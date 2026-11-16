package territory.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Alerta emitido quando uma previsão estoura um limiar. Guarda o porquê:
 * qual sensor, qual valor previsto e qual limite foi estourado.
 */
@Entity
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSensor tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivel;

    @Column(nullable = false)
    private String estrategia;

    @Column(nullable = false)
    private double valorPrevisto;

    @Column(nullable = false)
    private double limite;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    protected Alerta() {
        // exigido pelo JPA
    }

    public Alerta(ResultadoPrevisao previsao, NivelAlerta nivel, double limite, LocalDateTime dataHora) {
        this.sensorId = previsao.getSensorId();
        this.tipo = previsao.getTipo();
        this.nivel = nivel;
        this.estrategia = previsao.getEstrategia();
        this.valorPrevisto = previsao.getValorPrevisto();
        this.limite = limite;
        this.dataHora = dataHora;
        this.mensagem = String.format(
                "%s - %s (%s): previsão de %.2f %s pela estratégia '%s' atingiu o limite de %s (%.2f %s)",
                nivel.getRotulo(), tipo.getDescricao(), sensorId, valorPrevisto, tipo.getUnidade(),
                estrategia, nivel.getRotulo(), limite, tipo.getUnidade());
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

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return dataHora.withNano(0) + " | " + mensagem;
    }
}
