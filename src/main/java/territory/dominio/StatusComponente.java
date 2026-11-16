package territory.dominio;

public enum StatusComponente {
    OPERANDO("Operando"),
    PARCIAL("Operando parcialmente"),
    INOPERANTE("Inoperante");

    private final String descricao;

    StatusComponente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
