package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MedicaoTest {

    @Test
    void deveCriarMedicaoComValorDentroDaFaixa() throws DadosInvalidosException {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        Medicao medicao = new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, 12.5, agora);

        // Assert
        assertEquals("PLU-01", medicao.getSensorId());
        assertEquals(12.5, medicao.getValor());
        assertEquals(TipoSensor.PLUVIOMETRICO, medicao.getTipo());
    }

    @Test
    void deveLancarExcecaoParaChuvaNegativa() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, -3.0, agora));

        // Assert
        assertTrue(erro.getMessage().contains("chuva"));
    }

    @Test
    void deveLancarExcecaoParaBueiroAcimaDeCemPorCento() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("BUE-01", TipoSensor.NIVEL_BUEIRO, 120.0, agora));
    }

    @Test
    void deveLancarExcecaoQuandoSensorNaoInformado() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act + Assert
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(" ", TipoSensor.NIVEL_RIO, 2.0, agora));
    }
}
