package territory.dominio;

import territory.excecao.DadosInvalidosException;

/**
 * Qualquer elemento físico do território acompanhado pelo sistema.
 * Cada tipo concreto decide como verificar o próprio status.
 */
public abstract class ComponenteTerritorial {

    private final String id;
    private final String nome;
    private final String regiao;
    private StatusComponente status = StatusComponente.OPERANDO;

    protected ComponenteTerritorial(String id, String nome, String regiao) throws DadosInvalidosException {
        if (id == null || id.isBlank()) {
            throw new DadosInvalidosException("componente territorial sem id");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("componente territorial sem nome");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("componente territorial sem região");
        }
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
    }

    /** Recalcula, guarda e devolve o status atual do componente. */
    public abstract StatusComponente verificarStatus();

    protected void setStatus(StatusComponente status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }
}
