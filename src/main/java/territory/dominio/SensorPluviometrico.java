package territory.dominio;

import java.util.Random;

/** Pluviômetro: mede a intensidade da chuva em mm/h. */
public class SensorPluviometrico extends SensorSimulado {

    public SensorPluviometrico(String id) {
        this(id, new Random());
    }

    public SensorPluviometrico(String id, Random random) {
        super(id, TipoSensor.PLUVIOMETRICO, 0.0, 60.0, random);
    }
}
