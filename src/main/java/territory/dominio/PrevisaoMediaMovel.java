package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Previsão pela média das últimas N medições do histórico.
 */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel", janela);
        this.janela = janela;
    }

    @Override
    public ResultadoPrevisao prever(List<Medicao> historico) throws DadosInvalidosException {
        ResultadoPrevisao base = super.prever(historico); // valida o histórico
        double media = media(historico, 0, janela);
        String significado = String.format(
                "média das últimas %d leituras; é o valor esperado se o padrão recente continuar", janela);
        return new ResultadoPrevisao(getNome(), base.getSensorId(), base.getTipo(), media, janela, significado);
    }
}
