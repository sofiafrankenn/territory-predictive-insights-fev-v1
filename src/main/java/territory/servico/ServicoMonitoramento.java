package territory.servico;

import org.springframework.stereotype.Service;
import territory.dominio.Medicao;
import territory.dominio.PontoMonitoramento;
import territory.dominio.Sensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.persistencia.MedicaoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras de negócio do monitoramento: leitura de sensores e consulta ao histórico.
 */
@Service
public class ServicoMonitoramento {

    private final MedicaoRepository medicaoRepository;
    private final List<PontoMonitoramento> pontos;

    public ServicoMonitoramento(MedicaoRepository medicaoRepository, List<PontoMonitoramento> pontos) {
        this.medicaoRepository = medicaoRepository;
        this.pontos = pontos;
    }

    /** Lê um sensor, valida o valor e grava a medição no banco. */
    public Medicao lerEGravar(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        double valor = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor, LocalDateTime.now());
        return medicaoRepository.save(medicao);
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId) throws DadosInvalidosException {
        Sensor sensor = buscarSensor(sensorId);
        return medicaoRepository.findBySensorIdOrderByDataHoraDesc(sensor.getId());
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
}
