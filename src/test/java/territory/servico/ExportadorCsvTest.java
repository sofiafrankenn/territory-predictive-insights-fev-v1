package territory.servico;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import territory.dominio.Medicao;
import territory.dominio.TipoSensor;
import territory.excecao.DadosInvalidosException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExportadorCsvTest {

    @TempDir
    Path pasta;

    @Test
    void deveGerarCabecalhoEUmaLinhaPorMedicao() throws DadosInvalidosException, IOException {
        // Arrange
        LocalDateTime data = LocalDateTime.of(2026, 3, 1, 10, 0);
        List<Medicao> historico = List.of(
                new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, 12.5, data),
                new Medicao("PLU-01", TipoSensor.PLUVIOMETRICO, 8.0, data.minusHours(1)));
        ExportadorCsv exportador = new ExportadorCsv(pasta);

        // Act
        Path arquivo = exportador.exportarHistorico("PLU-01", historico);

        // Assert
        List<String> linhas = Files.readAllLines(arquivo);
        assertEquals(3, linhas.size());
        assertEquals("2026-03-01T10:00;PLU-01;PLUVIOMETRICO;12.50;mm/h", linhas.get(1));
    }
}
