package territory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import territory.dominio.Medicao;
import territory.dominio.PontoMonitoramento;
import territory.dominio.Sensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.servico.ServicoMonitoramento;

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
                case "3" -> verHistoricoPorSensor();
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
        System.out.println("3 - Ver histórico de medições por sensor");
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
