package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.Optional;

/**
 * Limites de um tipo de sensor para cada nível de alerta.
 * Precisam ser não negativos e crescentes: atenção &lt; alerta &lt; crítico.
 */
public class Limiares {

    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiares(double atencao, double alerta, double critico) throws DadosInvalidosException {
        if (atencao < 0 || alerta < 0 || critico < 0) {
            throw new DadosInvalidosException("limiar inválido: valores não podem ser negativos");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(String.format(
                    "limiar inválido: é preciso atenção < alerta < crítico (recebido %.2f / %.2f / %.2f)",
                    atencao, alerta, critico));
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    /** Devolve o nível mais alto estourado pelo valor, ou vazio se nenhum foi atingido. */
    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= critico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= alerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= atencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    public double limiteDe(NivelAlerta nivel) {
        switch (nivel) {
            case CRITICO:
                return critico;
            case ALERTA:
                return alerta;
            default:
                return atencao;
        }
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }
}
