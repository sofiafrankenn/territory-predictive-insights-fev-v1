package territory.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import territory.excecao.DadosInvalidosException;

import java.time.LocalDateTime;

/**
 * Uma leitura de sensor. Só é criada se o valor estiver dentro da faixa do tipo de sensor.
 */
@Entity
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSensor tipo;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Medicao() {
        // exigido pelo JPA
    }

    public Medicao(String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("medição sem identificação do sensor");
        }
        if (tipo == null) {
            throw new DadosInvalidosException("medição sem tipo de sensor");
        }
        if (dataHora == null) {
            throw new DadosInvalidosException("medição sem data/hora");
        }
        tipo.validarValor(valor);
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %.2f %s", dataHora.withNano(0), sensorId, valor, tipo.getUnidade());
    }
}
