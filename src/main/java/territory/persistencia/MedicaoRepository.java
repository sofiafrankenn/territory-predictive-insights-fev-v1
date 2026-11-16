package territory.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import territory.dominio.Medicao;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    /** Histórico completo de um sensor, da leitura mais recente para a mais antiga. */
    List<Medicao> findBySensorIdOrderByDataHoraDesc(String sensorId);
}
