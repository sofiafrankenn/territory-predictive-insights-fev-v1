package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DadosTerritorioTest {

    @Test
    void deveRejeitarPopulacaoNegativa() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Região teste", 1000, "1234");

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> dados.setPopulacao(-1));
        assertEquals(1000, dados.getPopulacao());
    }

    @Test
    void naoDeveAlterarLimiaresComCodigoErrado() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Região teste", 1000, "1234");
        Limiares originais = new Limiares(10, 25, 50);
        dados.definirLimiaresIniciais(TipoSensor.PLUVIOMETRICO, originais);

        // Act
        assertThrows(DadosInvalidosException.class,
                () -> dados.alterarLimiares("0000", TipoSensor.PLUVIOMETRICO, new Limiares(1, 2, 3)));

        // Assert
        assertEquals(originais, dados.getLimiares(TipoSensor.PLUVIOMETRICO));
    }

    @Test
    void deveAlterarLimiaresComCodigoCorreto() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Região teste", 1000, "1234");
        dados.definirLimiaresIniciais(TipoSensor.PLUVIOMETRICO, new Limiares(10, 25, 50));
        Limiares novos = new Limiares(5, 15, 30);

        // Act
        dados.alterarLimiares("1234", TipoSensor.PLUVIOMETRICO, novos);

        // Assert
        assertEquals(novos, dados.getLimiares(TipoSensor.PLUVIOMETRICO));
    }
}
