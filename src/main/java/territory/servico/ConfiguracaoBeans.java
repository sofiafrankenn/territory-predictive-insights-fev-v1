package territory.servico;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import territory.dominio.DadosTerritorio;
import territory.dominio.EstrategiaPrevisao;
import territory.dominio.Limiares;
import territory.dominio.PontoMonitoramento;
import territory.dominio.PrevisaoComparacao;
import territory.dominio.PrevisaoMediaMovel;
import territory.dominio.SensorNivelBueiro;
import territory.dominio.SensorNivelRio;
import territory.dominio.SensorPluviometrico;
import territory.dominio.TipoSensor;
import territory.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Monta os objetos do território (dados, limiares, pontos e sensores) na inicialização.
 */
@Configuration
public class ConfiguracaoBeans {

    @Bean
    public DadosTerritorio dadosTerritorio(@Value("${territorio.regiao}") String regiao,
                                           @Value("${territorio.populacao}") int populacao,
                                           @Value("${territorio.codigo-acesso}") String codigoAcesso)
            throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio(regiao, populacao, codigoAcesso);
        // Limiares de referência (atenção / alerta / crítico)
        dados.definirLimiaresIniciais(TipoSensor.PLUVIOMETRICO, new Limiares(10.0, 25.0, 50.0));
        dados.definirLimiaresIniciais(TipoSensor.NIVEL_RIO, new Limiares(3.0, 4.0, 5.0));
        dados.definirLimiaresIniciais(TipoSensor.NIVEL_BUEIRO, new Limiares(60.0, 80.0, 95.0));
        return dados;
    }

    @Bean
    public List<PontoMonitoramento> pontosMonitoramento(DadosTerritorio dadosTerritorio)
            throws DadosInvalidosException {
        PontoMonitoramento ponto = new PontoMonitoramento("P1", "Margem do Rio Tietê",
                dadosTerritorio.getRegiao());
        ponto.adicionarSensor(new SensorPluviometrico("PLU-01"));
        ponto.adicionarSensor(new SensorNivelRio("RIO-01"));
        ponto.adicionarSensor(new SensorNivelBueiro("BUE-01"));
        return List.of(ponto);
    }

    /** Estratégias que rodam sobre o histórico; o serviço usa todas de forma polimórfica. */
    @Bean
    public List<EstrategiaPrevisao> estrategiasPrevisao() {
        return List.of(new PrevisaoMediaMovel(5), new PrevisaoComparacao(3));
    }
}
