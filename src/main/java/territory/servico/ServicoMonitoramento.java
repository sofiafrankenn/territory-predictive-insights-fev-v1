package territory.servico;

import org.springframework.stereotype.Service;
import territory.dominio.Alerta;
import territory.dominio.DadosTerritorio;
import territory.dominio.EstrategiaPrevisao;
import territory.dominio.Limiares;
import territory.dominio.Medicao;
import territory.dominio.NivelAlerta;
import territory.dominio.PontoMonitoramento;
import territory.dominio.ResultadoPrevisao;
import territory.dominio.Sensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.persistencia.AlertaRepository;
import territory.persistencia.MedicaoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Regras de negócio do monitoramento: leitura de sensores, histórico, previsões e alertas.
 */
@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final DadosTerritorio dadosTerritorio;
    private final List<PontoMonitoramento> pontos;
    private final List<EstrategiaPrevisao> estrategias;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository,
                                DadosTerritorio dadosTerritorio, List<PontoMonitoramento> pontos,
                                List<EstrategiaPrevisao> estrategias) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.dadosTerritorio = dadosTerritorio;
        this.pontos = pontos;
        this.estrategias = estrategias;
    }

    /** Lê um sensor, valida o valor e grava a medição no banco. */
    public Medicao lerEGravar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor, LocalDateTime.now());
        return medicaoRepository.save(medicao);
    }

    /**
     * Roda uma estratégia sobre o histórico do sensor, compara a previsão com os limiares
     * e grava um alerta se algum limiar for atingido.
     */
    public AnaliseSensor analisar(Sensor sensor, EstrategiaPrevisao estrategia) throws DadosInvalidosException {
        List<Medicao> historico = medicaoRepository.findBySensorIdOrderByDataHoraDesc(sensor.getId());
        ResultadoPrevisao previsao = estrategia.prever(historico);
        Limiares limiares = dadosTerritorio.getLimiares(sensor.getTipo());
        Optional<NivelAlerta> nivel = limiares.classificar(previsao.getValorPrevisto());
        if (nivel.isEmpty()) {
            return AnaliseSensor.comPrevisao(previsao, null);
        }
        Alerta alerta = new Alerta(previsao, nivel.get(), limiares.limiteDe(nivel.get()), LocalDateTime.now());
        return AnaliseSensor.comPrevisao(previsao, alertaRepository.save(alerta));
    }

    /** Roda todas as estratégias em todos os sensores. Sem previsão possível, registra o motivo. */
    public List<AnaliseSensor> rodarPrevisoes() {
        List<AnaliseSensor> analises = new ArrayList<>();
        for (Sensor sensor : listarSensores()) {
            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    analises.add(analisar(sensor, estrategia));
                } catch (DadosInvalidosException e) {
                    analises.add(AnaliseSensor.semPrevisao(sensor.getId(), estrategia.getNome(), e.getMessage()));
                }
            }
        }
        return analises;
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId) throws DadosInvalidosException {
        Sensor sensor = buscarSensor(sensorId);
        return medicaoRepository.findBySensorIdOrderByDataHoraDesc(sensor.getId());
    }

    public List<Alerta> listarAlertasEmitidos() {
        return alertaRepository.findAllByOrderByDataHoraDesc();
    }

    public Sensor buscarSensor(String sensorId) throws DadosInvalidosException {
        for (Sensor sensor : listarSensores()) {
            if (sensor.getId().equalsIgnoreCase(sensorId == null ? "" : sensorId.trim())) {
                return sensor;
            }
        }
        throw new DadosInvalidosException("sensor não encontrado: " + sensorId);
    }

    public List<Sensor> listarSensores() {
        List<Sensor> sensores = new ArrayList<>();
        for (PontoMonitoramento ponto : pontos) {
            sensores.addAll(ponto.getSensores());
        }
        return sensores;
    }

    public List<PontoMonitoramento> getPontos() {
        return pontos;
    }

    public List<EstrategiaPrevisao> getEstrategias() {
        return estrategias;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }
}
