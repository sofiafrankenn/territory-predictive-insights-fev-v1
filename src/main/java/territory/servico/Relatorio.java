package territory.servico;

import java.util.Collections;
import java.util.List;

/**
 * Relatório final (status completo). Só é criado pelo RelatorioBuilder.
 */
public class Relatorio {

    private final String titulo;
    private final List<String> secoes;

    Relatorio(String titulo, List<String> secoes) {
        this.titulo = titulo;
        this.secoes = Collections.unmodifiableList(secoes);
    }

    public String getTitulo() {
        return titulo;
    }

    public List<String> getSecoes() {
        return secoes;
    }

    public String formatar() {
        StringBuilder texto = new StringBuilder();
        texto.append("========== ").append(titulo).append(" ==========\n");
        for (String secao : secoes) {
            texto.append(secao).append('\n');
        }
        return texto.toString();
    }
}
