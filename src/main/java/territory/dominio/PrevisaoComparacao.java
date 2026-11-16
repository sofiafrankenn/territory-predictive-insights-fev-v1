package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Previsão pela comparação com o período anterior: compara a média do período
 * recente com a do período anterior e projeta a mesma variação para o próximo período.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int tamanhoPeriodo;

    public PrevisaoComparacao(int tamanhoPeriodo) {
        super("Comparação com período anterior", tamanhoPeriodo * 2);
        this.tamanhoPeriodo = tamanhoPeriodo;
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) throws DadosInvalidosException {
        ResultadoPrevisao base = super.prever(historico); // valida o histórico
        double mediaRecente = media(historico, 0, tamanhoPeriodo);
        double mediaAnterior = media(historico, tamanhoPeriodo, tamanhoPeriodo * 2);
        double variacao = mediaRecente - mediaAnterior;
        double previsto = limitarFaixa(base.getTipo(), mediaRecente + variacao);

        String tendencia = variacao > 0 ? "alta" : variacao < 0 ? "queda" : "estabilidade";
        String significado = String.format(
                "tendência de %s (%+.2f %s entre os dois últimos períodos de %d leituras); "
                        + "projeta a mesma variação para o próximo período",
                tendencia, variacao, base.getTipo().getUnidade(), tamanhoPeriodo);
        return new ResultadoPrevisao(getNome(), base.getSensorId(), base.getTipo(), previsto,
                tamanhoPeriodo * 2, significado);
    }
}
