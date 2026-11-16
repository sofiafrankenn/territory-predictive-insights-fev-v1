package territory.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import territory.dominio.Medicao;

import java.util.List;
import java.util.Optional;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    /** Histórico completo de um sensor, da leitura mais recente para a mais antiga. */
    List<Medicao> findBySensorIdOrderByDataHoraDesc(String sensorId);

    /** Leitura mais recente de um sensor, se houver. */
    Optional<Medicao> findFirstBySensorIdOrderByDataHoraDesc(String sensorId);
}
