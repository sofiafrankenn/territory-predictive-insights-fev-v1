package territory.dominio;

import java.util.Random;

/** Régua do rio/córrego: mede o nível da água em metros. */
public class SensorNivelRio extends SensorSimulado {

    public SensorNivelRio(String id) {
        this(id, new Random());
    }

    public SensorNivelRio(String id, Random random) {
        super(id, TipoSensor.NIVEL_RIO, 0.5, 6.0, random);
    }
}
