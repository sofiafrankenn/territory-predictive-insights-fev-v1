package territory.servico;

import territory.dominio.Alerta;
import territory.dominio.ResultadoPrevisao;

import java.util.Optional;

/**
 * Resultado de rodar uma estratégia sobre um sensor: a previsão e, se houver, o alerta emitido.
 * Quando não foi possível prever, guarda o motivo (o sistema se cala e explica por quê).
 */
public class AnaliseSensor {

    private final String sensorId;
    private final String estrategia;
    private final ResultadoPrevisao previsao;
    private final Alerta alerta;
    private final String motivoSemPrevisao;

    private AnaliseSensor(String sensorId, String estrategia, ResultadoPrevisao previsao, Alerta alerta,
                          String motivoSemPrevisao) {
        this.sensorId = sensorId;
        this.estrategia = estrategia;
        this.previsao = previsao;
        this.alerta = alerta;
        this.motivoSemPrevisao = motivoSemPrevisao;
    }

    public static AnaliseSensor comPrevisao(ResultadoPrevisao previsao, Alerta alerta) {
        return new AnaliseSensor(previsao.getSensorId(), previsao.getEstrategia(), previsao, alerta, null);
    }

    public static AnaliseSensor semPrevisao(String sensorId, String estrategia, String motivo) {
        return new AnaliseSensor(sensorId, estrategia, null, null, motivo);
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public Optional<ResultadoPrevisao> getPrevisao() {
        return Optional.ofNullable(previsao);
    }

    public Optional<Alerta> getAlerta() {
        return Optional.ofNullable(alerta);
    }

    public String getMotivoSemPrevisao() {
        return motivoSemPrevisao;
    }
}
