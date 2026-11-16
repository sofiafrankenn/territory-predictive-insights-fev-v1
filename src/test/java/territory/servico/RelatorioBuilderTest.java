package territory.servico;

import org.junit.jupiter.api.Test;
import territory.dominio.DadosTerritorio;
import territory.dominio.Limiares;
import territory.dominio.TipoSensor;
import territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelatorioBuilderTest {

    @Test
    void deveMontarRelatorioComAsSecoesNaOrdemPedida() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio dados = new DadosTerritorio("Região teste", 500, "1234");
        dados.definirLimiaresIniciais(TipoSensor.PLUVIOMETRICO, new Limiares(10, 25, 50));

        // Act
        Relatorio relatorio = new RelatorioBuilder()
                .comTitulo("Teste")
                .comDadosTerritorio(dados)
                .comAlertasEmitidos(List.of(), 5)
                .construir();

        // Assert
        assertEquals("Teste", relatorio.getTitulo());
        assertEquals(2, relatorio.getSecoes().size());
        assertTrue(relatorio.getSecoes().get(0).contains("Região teste"));
        assertTrue(relatorio.getSecoes().get(1).contains("Nenhum alerta"));
    }

    @Test
    void deveIncluirDataDeGeracao() {
        // Arrange
        LocalDateTime data = LocalDateTime.of(2026, 1, 10, 8, 30);

        // Act
        Relatorio relatorio = new RelatorioBuilder().comDataGeracao(data).construir();

        // Assert
        assertTrue(relatorio.formatar().contains("2026-01-10T08:30"));
    }
}
