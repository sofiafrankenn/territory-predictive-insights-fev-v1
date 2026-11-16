package territory.dominio;

import org.junit.jupiter.api.Test;
import territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoMediaMovelTest {

    /** Monta um histórico com a leitura mais recente primeiro. */
    static List<Medicao> historico(double... valoresMaisRecentePrimeiro) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();
        for (int i = 0; i < valoresMaisRecentePrimeiro.length; i++) {
            lista.add(new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, valoresMaisRecentePrimeiro[i],
                    agora.minusMinutes(i)));
        }
        return lista;
    }

    @Test
    void deveCalcularMediaDasUltimasCincoLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);
        List<Medicao> dados = historico(10, 20, 30, 40, 50, 150);

        // Act
        ResultadoPrevisao resultado = previsao.prever(dados);

        // Assert: a leitura antiga (150) fica fora da janela
        assertEquals(30.0, resultado.getValorPrevisto(), 0.0001);
        assertEquals(5, resultado.getLeiturasUsadas());
    }

    @Test
    void deveSeCalarComMenosDeCincoLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel previsao = new PrevisaoMediaMovel(5);
        List<Medicao> dados = historico(10, 20, 30, 40);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> previsao.prever(dados));
    }
}
