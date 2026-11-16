package territory.excecao;

/**
 * Lançada quando um sensor ou gerador de dados falha e não consegue entregar uma leitura.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
