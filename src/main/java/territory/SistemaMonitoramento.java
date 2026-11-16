package territory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import territory.dominio.Alerta;
import territory.dominio.Medicao;
import territory.dominio.PontoMonitoramento;
import territory.dominio.Sensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.servico.AnaliseSensor;
import territory.servico.RelatorioBuilder;
import territory.servico.ServicoMonitoramento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * Menu principal de console do Territory Predictive Insights.
 */
@SpringBootApplication
public class SistemaMonitoramento implements CommandLineRunner {

    private final ServicoMonitoramento servico;
    private final Scanner scanner = new Scanner(System.in);

    public SistemaMonitoramento(ServicoMonitoramento servico) {
        this.servico = servico;
    }

    public static void main(String[] args) {
        SpringApplication.run(SistemaMonitoramento.class, args);
    }

    @Override
    public void run(String... args) {
        boolean executando = true;
        while (executando) {
            exibirMenu();
            String opcao = lerLinha("Escolha uma opção: ");
            if (opcao == null) {
                break; // fim da entrada
            }
            switch (opcao.trim()) {
                case "1" -> verificarSensores();
                case "2" -> rodarPrevisoes();
                case "3" -> menuHistorico();
                case "5" -> exibirStatusCompleto();
                case "0" -> executando = false;
                default -> System.out.println("Opção inválida: digite um dos números do menu.");
            }
        }
        System.out.println("Encerrando o sistema. Os dados ficam gravados no banco.");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== TERRITORY PREDICTIVE INSIGHTS - Clima Local =====");
        System.out.println("1 - Verificar sensores (ler e gravar medições)");
        System.out.println("2 - Rodar previsões e alertas");
        System.out.println("3 - Ver histórico (medições e alertas)");
        System.out.println("5 - Status completo");
        System.out.println("0 - Sair");
    }

    private void verificarSensores() {
        for (PontoMonitoramento ponto : servico.getPontos()) {
            System.out.printf("%n[%s] %s - status: %s%n", ponto.getId(), ponto.getNome(),
                    ponto.verificarStatus().getDescricao());
            for (Sensor sensor : ponto.getSensores()) {
                try {
                    Medicao medicao = servico.lerEGravar(sensor);
                    System.out.printf("  OK    %-7s %-16s %.2f %s%n", sensor.getId(),
                            sensor.getTipo().getDescricao(), medicao.getValor(), sensor.getTipo().getUnidade());
                } catch (FonteIndisponivelException e) {
                    System.out.printf("  FALHA %-7s fonte indisponível: %s. Nada foi gravado.%n",
                            sensor.getId(), e.getMessage());
                } catch (DadosInvalidosException e) {
                    System.out.printf("  INVÁLIDO %-7s leitura descartada: %s.%n", sensor.getId(), e.getMessage());
                }
            }
        }
    }

    private void rodarPrevisoes() {
        List<AnaliseSensor> analises = servico.rodarPrevisoes();
        int alertas = 0;
        for (AnaliseSensor analise : analises) {
            if (analise.getPrevisao().isEmpty()) {
                System.out.printf("  %-7s [%s] sem previsão: %s.%n", analise.getSensorId(),
                        analise.getEstrategia(), analise.getMotivoSemPrevisao());
                continue;
            }
            System.out.println("  " + analise.getPrevisao().get());
            if (analise.getAlerta().isPresent()) {
                System.out.println("    >>> " + analise.getAlerta().get().getMensagem());
                alertas++;
            } else {
                System.out.println("    Nenhum limiar atingido.");
            }
        }
        System.out.println(alertas + " alerta(s) emitido(s) e gravado(s) no banco.");
    }

    private void exibirStatusCompleto() {
        String relatorio = new RelatorioBuilder()
                .comTitulo("STATUS COMPLETO - " + servico.getDadosTerritorio().getRegiao())
                .comDataGeracao(LocalDateTime.now())
                .comDadosTerritorio(servico.getDadosTerritorio())
                .comPontos(servico.getPontos())
                .comUltimasMedicoes(servico.buscarUltimasMedicoes())
                .comAlertasEmitidos(servico.listarAlertasEmitidos(), 5)
                .construir()
                .formatar();
        System.out.println(relatorio);
    }

    private void menuHistorico() {
        System.out.println("1 - Medições por sensor");
        System.out.println("2 - Alertas já emitidos");
        String opcao = lerLinha("Escolha: ");
        switch (opcao == null ? "" : opcao.trim()) {
            case "1" -> verHistoricoPorSensor();
            case "2" -> verAlertasEmitidos();
            default -> System.out.println("Opção inválida no histórico.");
        }
    }

    private void verAlertasEmitidos() {
        List<Alerta> alertas = servico.listarAlertasEmitidos();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum alerta emitido até agora.");
            return;
        }
        System.out.println("Alertas emitidos (mais recente primeiro):");
        alertas.forEach(a -> System.out.println("  " + a));
    }

    private void verHistoricoPorSensor() {
        String sensorId = lerSensorId();
        try {
            List<Medicao> historico = servico.buscarHistoricoPorSensor(sensorId);
            if (historico.isEmpty()) {
                System.out.println("Ainda não há medições gravadas para " + sensorId + ".");
                return;
            }
            System.out.println("Histórico de " + sensorId + " (mais recente primeiro):");
            historico.forEach(m -> System.out.println("  " + m));
        } catch (DadosInvalidosException e) {
            System.out.println("Não foi possível consultar o histórico: " + e.getMessage());
        }
    }

    private String lerSensorId() {
        StringBuilder ids = new StringBuilder();
        for (Sensor sensor : servico.listarSensores()) {
            ids.append(sensor.getId()).append(" (").append(sensor.getTipo().getDescricao()).append(")  ");
        }
        System.out.println("Sensores: " + ids);
        String id = lerLinha("Informe o id do sensor: ");
        return id == null ? "" : id.trim();
    }

    private String lerLinha(String mensagem) {
        System.out.print(mensagem);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
