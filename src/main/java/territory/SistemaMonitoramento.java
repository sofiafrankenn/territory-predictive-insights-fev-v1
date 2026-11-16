package territory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import territory.dominio.Alerta;
import territory.dominio.DadosTerritorio;
import territory.dominio.Limiares;
import territory.dominio.Medicao;
import territory.dominio.PontoMonitoramento;
import territory.dominio.Sensor;
import territory.dominio.TipoSensor;
import territory.excecao.DadosInvalidosException;
import territory.excecao.FonteIndisponivelException;
import territory.servico.AnaliseSensor;
import territory.servico.ExportadorCsv;
import territory.servico.RelatorioBuilder;
import territory.servico.ServicoMonitoramento;

import java.io.IOException;
import java.nio.file.Path;
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
    private final ExportadorCsv exportadorCsv = new ExportadorCsv(Path.of("exportacoes"));

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
                case "4" -> gerenciarDadosTerritorio();
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
        System.out.println("4 - Gerenciar dados do território");
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

    private void gerenciarDadosTerritorio() {
        DadosTerritorio dados = servico.getDadosTerritorio();
        System.out.printf("Região: %s | População: %d%n", dados.getRegiao(), dados.getPopulacao());
        System.out.println("1 - Alterar região");
        System.out.println("2 - Alterar população");
        System.out.println("3 - Alterar limiares de um sensor (exige código de acesso)");
        String opcao = lerLinha("Escolha: ");
        try {
            switch (opcao == null ? "" : opcao.trim()) {
                case "1" -> {
                    dados.setRegiao(lerLinha("Nova região: "));
                    System.out.println("Região atualizada.");
                }
                case "2" -> {
                    dados.setPopulacao(Integer.parseInt(lerTexto("Nova população: ")));
                    System.out.println("População atualizada.");
                }
                case "3" -> alterarLimiares(dados);
                default -> System.out.println("Opção inválida em gerenciar dados.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println("Dados não alterados: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Dados não alterados: digite um número válido (ex.: 1500 ou 12.5).");
        }
    }

    private void alterarLimiares(DadosTerritorio dados) throws DadosInvalidosException {
        TipoSensor[] tipos = TipoSensor.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.printf("%d - %s (%s)%n", i + 1, tipos[i].getDescricao(), tipos[i].getUnidade());
        }
        int indice = Integer.parseInt(lerTexto("Tipo de sensor: ")) - 1;
        if (indice < 0 || indice >= tipos.length) {
            throw new DadosInvalidosException("tipo de sensor inexistente: " + (indice + 1));
        }
        TipoSensor tipo = tipos[indice];
        double atencao = Double.parseDouble(lerTexto("Limiar de ATENÇÃO: ").replace(',', '.'));
        double alerta = Double.parseDouble(lerTexto("Limiar de ALERTA: ").replace(',', '.'));
        double critico = Double.parseDouble(lerTexto("Limiar de CRÍTICO: ").replace(',', '.'));
        String codigo = lerTexto("Código de acesso: ");
        dados.alterarLimiares(codigo, tipo, new Limiares(atencao, alerta, critico));
        System.out.println("Limiares de " + tipo.getDescricao() + " atualizados.");
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
        System.out.println("3 - Exportar histórico de um sensor em CSV");
        String opcao = lerLinha("Escolha: ");
        switch (opcao == null ? "" : opcao.trim()) {
            case "1" -> verHistoricoPorSensor();
            case "2" -> verAlertasEmitidos();
            case "3" -> exportarHistoricoCsv();
            default -> System.out.println("Opção inválida no histórico.");
        }
    }

    private void exportarHistoricoCsv() {
        String sensorId = lerSensorId();
        try {
            List<Medicao> historico = servico.buscarHistoricoPorSensor(sensorId);
            Path arquivo = exportadorCsv.exportarHistorico(servico.buscarSensor(sensorId).getId(), historico);
            System.out.println(historico.size() + " medição(ões) exportada(s) para " + arquivo.toAbsolutePath());
        } catch (DadosInvalidosException e) {
            System.out.println("Não foi possível exportar: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Falha ao gravar o arquivo CSV: " + e.getMessage());
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

    /** Lê uma linha sem espaços nas pontas; fim da entrada vira texto vazio. */
    private String lerTexto(String mensagem) {
        String linha = lerLinha(mensagem);
        return linha == null ? "" : linha.trim();
    }

    private String lerLinha(String mensagem) {
        System.out.print(mensagem);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }
}
