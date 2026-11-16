package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimiaresTest {

    @Test
    void deveClassificarValorNoNivelMaisAltoAtingido() throws DadosInvalidosException {
        // Arrange
        Limiares limiares = new Limiares(10, 25, 50);

        // Act
        Optional<NivelAlerta> nivel = limiares.classificar(30);

        // Assert
        assertEquals(Optional.of(NivelAlerta.ALERTA), nivel);
    }

    @Test
    void naoDeveClassificarValorAbaixoDaAtencao() throws DadosInvalidosException {
        // Arrange
        Limiares limiares = new Limiares(10, 25, 50);

        // Act
        Optional<NivelAlerta> nivel = limiares.classificar(9.9);

        // Assert
        assertTrue(nivel.isEmpty());
    }

    @Test
    void deveRejeitarLimiaresForaDeOrdem() {
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new Limiares(30, 20, 50));
    }
}
