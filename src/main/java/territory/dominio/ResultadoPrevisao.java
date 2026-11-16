package territory.dominio;

/**
 * Resultado de uma previsão: o valor previsto e o que ele significa.
 */
public class ResultadoPrevisao {

    private final String estrategia;
    private final String sensorId;
    private final TipoSensor tipo;
    private final double valorPrevisto;
    private final int leiturasUsadas;
    private final String significado;

    public ResultadoPrevisao(String estrategia, String sensorId, TipoSensor tipo, double valorPrevisto,
                             int leiturasUsadas, String significado) {
        this.estrategia = estrategia;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valorPrevisto = valorPrevisto;
        this.leiturasUsadas = leiturasUsadas;
        this.significado = significado;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public int getLeiturasUsadas() {
        return leiturasUsadas;
    }

    public String getSignificado() {
        return significado;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f %s - %s", estrategia, sensorId, valorPrevisto,
                tipo.getUnidade(), significado);
    }
}
