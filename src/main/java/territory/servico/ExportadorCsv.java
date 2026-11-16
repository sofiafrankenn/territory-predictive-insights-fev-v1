package territory.servico;

import territory.dominio.Medicao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Bônus: exporta o histórico consultado de um sensor para um arquivo CSV.
 */
public class ExportadorCsv {

    private final Path pastaDestino;

    public ExportadorCsv(Path pastaDestino) {
        this.pastaDestino = pastaDestino;
    }

    public Path exportarHistorico(String sensorId, List<Medicao> historico) throws IOException {
        Files.createDirectories(pastaDestino);
        Path arquivo = pastaDestino.resolve("historico_" + sensorId + ".csv");
        try (BufferedWriter escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
            escritor.write("data_hora;sensor;tipo;valor;unidade");
            escritor.newLine();
            for (Medicao medicao : historico) {
                escritor.write(String.format(Locale.ROOT, "%s;%s;%s;%.2f;%s",
                        medicao.getDataHora().withNano(0), medicao.getSensorId(), medicao.getTipo(),
                        medicao.getValor(), medicao.getTipo().getUnidade()));
                escritor.newLine();
            }
        }
        return arquivo;
    }
}
