package territory.excecao;

/**
 * Lançada quando um dado do território é inválido: medição fora de faixa,
 * população negativa, limiar inválido etc.
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
