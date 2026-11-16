package territory.dominio;

import territory.excecao.FonteIndisponivelException;

/**
 * Contrato de qualquer sensor do território.
 */
public interface Sensor {

    String getId();

    /** Lê o valor atual. Lança exceção se o sensor não responder. */
    double lerValor() throws FonteIndisponivelException;

    /** Indica se o sensor está ligado e apto a fazer leituras. */
    boolean estaFuncionando();

    TipoSensor getTipo();
}
