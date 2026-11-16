package territory.dominio;

public enum NivelAlerta {
    ATENCAO("ATENÇÃO"),
    ALERTA("ALERTA"),
    CRITICO("CRÍTICO");

    private final String rotulo;

    NivelAlerta(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
