package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrevisaoComparacaoTest {

    @Test
    void deveProjetarATendenciaDeAlta() throws DadosInvalidosException {
        // Arrange: período recente média 30, anterior média 20
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        ResultadoPrevisao resultado = previsao.prever(
                PrevisaoMediaMovelTest.historico(30, 30, 30, 20, 20, 20));

        // Assert: 30 + (30 - 20) = 40
        assertEquals(40.0, resultado.getValorPrevisto(), 0.0001);
        assertTrue(resultado.getSignificado().contains("alta"));
    }

    @Test
    void naoDevePreverValorAbaixoDaFaixaDoSensor() throws DadosInvalidosException {
        // Arrange: queda forte levaria a chuva negativa
        PrevisaoComparacao previsao = new PrevisaoComparacao(3);

        // Act
        ResultadoPrevisao resultado = previsao.prever(
                PrevisaoMediaMovelTest.historico(2, 2, 2, 40, 40, 40));

        // Assert
        assertEquals(0.0, resultado.getValorPrevisto(), 0.0001);
    }
}
