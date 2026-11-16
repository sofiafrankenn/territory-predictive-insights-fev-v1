package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ponto vulnerável do território onde ficam instalados os sensores.
 * O status depende de quantos sensores estão funcionando.
 */
public class PontoMonitoramento extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoMonitoramento(String id, String nome, String regiao) throws DadosInvalidosException {
        super(id, nome, regiao);
    }

    public void adicionarSensor(Sensor sensor) throws DadosInvalidosException {
        if (sensor == null) {
            throw new DadosInvalidosException("sensor nulo não pode ser instalado no ponto " + getId());
        }
        sensores.add(sensor);
    }

    @Override
    public StatusComponente verificarStatus() {
        long funcionando = sensores.stream().filter(Sensor::estaFuncionando).count();
        StatusComponente novoStatus;
        if (sensores.isEmpty() || funcionando == 0) {
            novoStatus = StatusComponente.INOPERANTE;
        } else if (funcionando < sensores.size()) {
            novoStatus = StatusComponente.PARCIAL;
        } else {
            novoStatus = StatusComponente.OPERANDO;
        }
        setStatus(novoStatus);
        return novoStatus;
    }

    public List<Sensor> getSensores() {
        return Collections.unmodifiableList(sensores);
    }
}
