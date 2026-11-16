package territory.servico;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import territory.dominio.Alerta;
import territory.dominio.DadosTerritorio;
import territory.dominio.Limiares;
import territory.dominio.Medicao;
import territory.dominio.NivelAlerta;
import territory.dominio.PontoMonitoramento;
import territory.dominio.PrevisaoMediaMovel;
import territory.dominio.Sensor;
import territory.dominio.TipoSensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.persistencia.AlertaRepository;
import territory.persistencia.MedicaoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private Sensor sensor;

    private ServicoMonitoramento servico;

    @BeforeEach
    void configurar() throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio("Região teste", 1000, "1234");
        dados.definirLimiaresIniciais(TipoSensor.PLUVIOMETRICO, new Limiares(10, 25, 50));
        PontoMonitoramento ponto = new PontoMonitoramento("P1", "Ponto teste", "Região teste");
        ponto.adicionarSensor(sensor);
        servico = new ServicoMonitoramento(medicaoRepository, alertaRepository, dados, List.of(ponto),
                List.of(new PrevisaoMediaMovel(5)));
    }

    private List<Medicao> historicoDeChuva(double valor, int quantidade) throws DadosInvalidosException {
        List<Medicao> historico = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            historico.add(new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, valor, LocalDateTime.now().minusMinutes(i)));
        }
        return historico;
    }

    @Test
    void deveEmitirAlertaCriticoQuandoPrevisaoPassaDoLimite() throws DadosInvalidosException {
        // Arrange
        when(sensor.getId()).thenReturn("PLU-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.PLUVIOMETRICO);
        when(medicaoRepository.findBySensorIdOrderByDataHoraDesc("PLU-01")).thenReturn(historicoDeChuva(60, 5));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        servico.rodarPrevisoes();

        // Assert
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository).save(captor.capture());
        Alerta alerta = captor.getValue();
        assertEquals(NivelAlerta.CRITICO, alerta.getNivel());
        assertEquals(50.0, alerta.getLimite());
        assertTrue(alerta.getMensagem().contains("PLU-01"));
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevisaoFicaAbaixoDosLimiares() throws DadosInvalidosException {
        // Arrange
        when(sensor.getId()).thenReturn("PLU-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.PLUVIOMETRICO);
        when(medicaoRepository.findBySensorIdOrderByDataHoraDesc("PLU-01")).thenReturn(historicoDeChuva(5, 5));

        // Act
        List<AnaliseSensor> analises = servico.rodarPrevisoes();

        // Assert
        verify(alertaRepository, never()).save(any());
        assertTrue(analises.get(0).getAlerta().isEmpty());
    }

    @Test
    void deveSeCalarENaoEmitirAlertaComHistoricoInsuficiente() throws DadosInvalidosException {
        // Arrange
        when(sensor.getId()).thenReturn("PLU-01");
        when(medicaoRepository.findBySensorIdOrderByDataHoraDesc("PLU-01")).thenReturn(historicoDeChuva(60, 3));

        // Act
        List<AnaliseSensor> analises = servico.rodarPrevisoes();

        // Assert
        verify(alertaRepository, never()).save(any());
        assertTrue(analises.get(0).getPrevisao().isEmpty());
        assertTrue(analises.get(0).getMotivoSemPrevisao().contains("insuficiente"));
    }

    @Test
    void naoDeveGravarLeituraInvalida() throws FonteIndisponivelException {
        // Arrange
        when(sensor.getId()).thenReturn("PLU-01");
        when(sensor.getTipo()).thenReturn(TipoSensor.PLUVIOMETRICO);
        when(sensor.lerValor()).thenReturn(-8.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.lerEGravar(sensor));
        verify(medicaoRepository, never()).save(any());
    }

    @Test
    void naoDeveGravarQuandoSensorFalha() throws FonteIndisponivelException {
        // Arrange
        when(sensor.lerValor()).thenThrow(new FonteIndisponivelException("sensor PLU-01 não respondeu"));

        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.lerEGravar(sensor));
        verify(medicaoRepository, never()).save(any());
    }
}
