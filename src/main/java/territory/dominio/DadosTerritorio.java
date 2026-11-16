package territory.dominio;

import territory.excecao.DadosInvalidosException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Dados gerais do território monitorado. Todos os atributos são privados;
 * os limiares só podem ser alterados com o código de acesso.
 */
public class DadosTerritorio {

    private String regiao;
    private int populacao;
    private final String codigoAcesso;
    private final Map<TipoSensor, Limiares> limiares = new EnumMap<>(TipoSensor.class);

    public DadosTerritorio(String regiao, int populacao, String codigoAcesso) throws DadosInvalidosException {
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException("código de acesso do território não pode ser vazio");
        }
        setRegiao(regiao);
        setPopulacao(populacao);
        this.codigoAcesso = codigoAcesso;
    }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("região do território não pode ser vazia");
        }
        this.regiao = regiao;
    }

    public void setPopulacao(int populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException("população negativa não é permitida: " + populacao);
        }
        this.populacao = populacao;
    }

    /** Define os limiares iniciais na montagem do sistema (sem código de acesso). */
    public void definirLimiaresIniciais(TipoSensor tipo, Limiares novosLimiares) throws DadosInvalidosException {
        if (tipo == null || novosLimiares == null) {
            throw new DadosInvalidosException("tipo de sensor e limiares são obrigatórios");
        }
        if (limiares.containsKey(tipo)) {
            throw new DadosInvalidosException("limiares de " + tipo.getDescricao()
                    + " já definidos; use o código de acesso para alterar");
        }
        limiares.put(tipo, novosLimiares);
    }

    /** Altera os limiares de um tipo de sensor, exigindo o código de acesso. */
    public void alterarLimiares(String codigoInformado, TipoSensor tipo, Limiares novosLimiares)
            throws DadosInvalidosException {
        if (!codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException("código de acesso incorreto: limiares não foram alterados");
        }
        if (tipo == null || novosLimiares == null) {
            throw new DadosInvalidosException("tipo de sensor e limiares são obrigatórios");
        }
        limiares.put(tipo, novosLimiares);
    }

    public Limiares getLimiares(TipoSensor tipo) throws DadosInvalidosException {
        Limiares encontrados = limiares.get(tipo);
        if (encontrados == null) {
            throw new DadosInvalidosException("não há limiares cadastrados para " + tipo.getDescricao());
        }
        return encontrados;
    }

    public Map<TipoSensor, Limiares> getTodosLimiares() {
        return Collections.unmodifiableMap(limiares);
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPopulacao() {
        return populacao;
    }
}
