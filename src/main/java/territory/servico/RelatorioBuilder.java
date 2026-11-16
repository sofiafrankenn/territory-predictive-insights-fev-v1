package territory.servico;

import territory.dominio.Alerta;
import territory.dominio.DadosTerritorio;
import territory.dominio.Limiares;
import territory.dominio.Medicao;
import territory.dominio.PontoMonitoramento;
import territory.dominio.Sensor;
import territory.dominio.TipoSensor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builder do relatório de status completo: cada método acrescenta uma seção
 * e devolve o próprio builder, permitindo montar o relatório passo a passo.
 */
public class RelatorioBuilder {

    private String titulo = "STATUS COMPLETO";
    private final List<String> secoes = new ArrayList<>();

    public RelatorioBuilder comTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public RelatorioBuilder comDataGeracao(LocalDateTime dataHora) {
        secoes.add("Gerado em: " + dataHora.withNano(0));
        return this;
    }

    public RelatorioBuilder comDadosTerritorio(DadosTerritorio dados) {
        StringBuilder secao = new StringBuilder("\n-- Território --\n");
        secao.append(String.format("Região: %s | População: %d%n", dados.getRegiao(), dados.getPopulacao()));
        for (Map.Entry<TipoSensor, Limiares> item : dados.getTodosLimiares().entrySet()) {
            Limiares l = item.getValue();
            secao.append(String.format("Limiares %-16s atenção %.2f | alerta %.2f | crítico %.2f %s%n",
                    item.getKey().getDescricao(), l.getAtencao(), l.getAlerta(), l.getCritico(),
                    item.getKey().getUnidade()));
        }
        secoes.add(secao.toString().stripTrailing());
        return this;
    }

    public RelatorioBuilder comPontos(List<PontoMonitoramento> pontos) {
        StringBuilder secao = new StringBuilder("\n-- Pontos de monitoramento --\n");
        for (PontoMonitoramento ponto : pontos) {
            secao.append(String.format("[%s] %s (%s) - %s%n", ponto.getId(), ponto.getNome(), ponto.getRegiao(),
                    ponto.verificarStatus().getDescricao()));
            for (Sensor sensor : ponto.getSensores()) {
                secao.append(String.format("   %s %-16s %s%n", sensor.getId(), sensor.getTipo().getDescricao(),
                        sensor.estaFuncionando() ? "funcionando" : "desligado"));
            }
        }
        secoes.add(secao.toString().stripTrailing());
        return this;
    }

    public RelatorioBuilder comUltimasMedicoes(List<Medicao> medicoes) {
        StringBuilder secao = new StringBuilder("\n-- Última medição de cada sensor --\n");
        if (medicoes.isEmpty()) {
            secao.append("Nenhuma medição gravada ainda.");
        }
        medicoes.forEach(m -> secao.append(m).append('\n'));
        secoes.add(secao.toString().stripTrailing());
        return this;
    }

    public RelatorioBuilder comAlertasEmitidos(List<Alerta> alertas, int limite) {
        StringBuilder secao = new StringBuilder("\n-- Últimos alertas emitidos --\n");
        if (alertas.isEmpty()) {
            secao.append("Nenhum alerta emitido.");
        }
        alertas.stream().limit(limite).forEach(a -> secao.append(a).append('\n'));
        secoes.add(secao.toString().stripTrailing());
        return this;
    }

    public Relatorio construir() {
        return new Relatorio(titulo, new ArrayList<>(secoes));
    }
}
