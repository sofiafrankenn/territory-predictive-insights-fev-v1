package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PontoMonitoramentoTest {

    @Test
    void deveFicarParcialQuandoUmSensorEstaDesligado() throws DadosInvalidosException {
        // Arrange
        PontoMonitoramento ponto = new PontoMonitoramento("P1", "Ponto teste", "Região teste");
        SensorPluviometrico ligado = new SensorPluviometrico("PLU-01");
        SensorPluviometrico desligado = new SensorPluviometrico("PLU-02");
        desligado.desligar();
        ponto.adicionarSensor(ligado);
        ponto.adicionarSensor(desligado);

        // Act
        StatusComponente status = ponto.verificarStatus();

        // Assert
        assertEquals(StatusComponente.PARCIAL, status);
    }

    @Test
    void deveFicarInoperanteSemSensores() throws DadosInvalidosException {
        // Arrange
        PontoMonitoramento ponto = new PontoMonitoramento("P1", "Ponto teste", "Região teste");

        // Act
        StatusComponente status = ponto.verificarStatus();

        // Assert
        assertEquals(StatusComponente.INOPERANTE, status);
    }
}
