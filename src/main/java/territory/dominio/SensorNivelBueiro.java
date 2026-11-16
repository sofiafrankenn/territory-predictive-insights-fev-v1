package territory.dominio;

import java.util.Random;

/** Sensor de galeria/bueiro: mede o percentual da capacidade ocupada pela água. */
public class SensorNivelBueiro extends SensorSimulado {

    public SensorNivelBueiro(String id) {
        this(id, new Random());
    }

    public SensorNivelBueiro(String id, Random random) {
        super(id, TipoSensor.NIVEL_BUEIRO, 0.0, 100.0, random);
    }
}
