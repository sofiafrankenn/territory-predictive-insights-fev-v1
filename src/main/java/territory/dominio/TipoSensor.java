package territory.dominio;

import territory.excecao.DadosInvalidosException;

/**
 * Tipos de sensor do recorte Clima Local, com unidade e faixa física válida.
 * Qualquer leitura fora da faixa é considerada inválida.
 */
public enum TipoSensor {

    PLUVIOMETRICO("Chuva", "mm/h", 0.0, 200.0),
    NIVEL_RIO("Nível do rio", "m", 0.0, 15.0),
    NIVEL_BUEIRO("Nível do bueiro", "% ocupado", 0.0, 100.0);

    private final String descricao;
    private final String unidade;
    private final double valorMinimo;
    private final double valorMaximo;

    TipoSensor(String descricao, String unidade, double valorMinimo, double valorMaximo) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.valorMinimo = valorMinimo;
        this.valorMaximo = valorMaximo;
    }

    public void validarValor(double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || valor < valorMinimo || valor > valorMaximo) {
            throw new DadosInvalidosException(String.format(
                    "medição de %s fora da faixa: %.2f %s (válido de %.1f a %.1f)",
                    descricao.toLowerCase(), valor, unidade, valorMinimo, valorMaximo));
        }
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getValorMinimo() {
        return valorMinimo;
    }

    public double getValorMaximo() {
        return valorMaximo;
    }
}
