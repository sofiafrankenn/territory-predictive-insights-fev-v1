package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Base das estratégias de previsão. Recebe o histórico do banco
 * (leitura mais recente primeiro) e devolve um valor previsto.
 *
 * A versão base valida o histórico e faz a previsão mais ingênua possível:
 * repetir a última leitura. As filhas chamam super.prever() para reaproveitar
 * a validação e então aplicam o próprio cálculo.
 */
public abstract class EstrategiaPrevisao {

    /** Abaixo disso o sistema se cala: poucas leituras não sustentam previsão. */
    public static final int MINIMO_LEITURAS = 5;

    private final String nome;
    private final int leiturasNecessarias;

    protected EstrategiaPrevisao(String nome, int leiturasNecessarias) {
        this.nome = nome;
        this.leiturasNecessarias = Math.max(MINIMO_LEITURAS, leiturasNecessarias);
    }

    public ResultadoPrevisao prever(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.size() < leiturasNecessarias) {
            int total = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException(String.format(
                    "histórico insuficiente para %s: %d leitura(s) no banco, mínimo %d",
                    nome, total, leiturasNecessarias));
        }
        Medicao ultima = historico.get(0);
        return new ResultadoPrevisao(nome, ultima.getSensorId(), ultima.getTipo(), ultima.getValor(), 1,
                "repete a última leitura");
    }

    /** Média dos valores das posições [inicio, fim) do histórico. */
    protected double media(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += historico.get(i).getValor();
        }
        return soma / (fim - inicio);
    }

    /** Mantém o valor previsto dentro da faixa física do sensor. */
    protected double limitarFaixa(TipoSensor tipo, double valor) {
        return Math.max(tipo.getValorMinimo(), Math.min(tipo.getValorMaximo(), valor));
    }

    public String getNome() {
        return nome;
    }

    public int getLeiturasNecessarias() {
        return leiturasNecessarias;
    }
}
