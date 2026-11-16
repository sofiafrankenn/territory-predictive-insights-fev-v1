package territory.dominio;

import territory.excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Base dos sensores simulados: gera valores com Random dentro de uma faixa realista.
 * Para exercitar a validação, às vezes simula uma falha de comunicação
 * ou uma leitura defeituosa (valor negativo).
 */
public abstract class SensorSimulado implements Sensor {

    private static final double CHANCE_FALHA = 0.05;
    private static final double CHANCE_LEITURA_DEFEITUOSA = 0.05;

    private final String id;
    private final TipoSensor tipo;
    private final double minimoSimulado;
    private final double maximoSimulado;
    private final Random random;
    private boolean ligado = true;

    protected SensorSimulado(String id, TipoSensor tipo, double minimoSimulado, double maximoSimulado,
                             Random random) {
        this.id = id;
        this.tipo = tipo;
        this.minimoSimulado = minimoSimulado;
        this.maximoSimulado = maximoSimulado;
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!estaFuncionando()) {
            throw new FonteIndisponivelException("sensor " + id + " está desligado");
        }
        if (random.nextDouble() < CHANCE_FALHA) {
            throw new FonteIndisponivelException("sensor " + id + " não respondeu à leitura");
        }
        double valor = minimoSimulado + random.nextDouble() * (maximoSimulado - minimoSimulado);
        if (random.nextDouble() < CHANCE_LEITURA_DEFEITUOSA) {
            return -valor; // leitura defeituosa: será barrada pela validação da Medicao
        }
        return valor;
    }

    @Override
    public boolean estaFuncionando() {
        return ligado;
    }

    public void ligar() {
        ligado = true;
    }

    public void desligar() {
        ligado = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }
}
