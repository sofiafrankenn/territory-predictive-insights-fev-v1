package territory.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import territory.dominio.Alerta;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    /** Todos os alertas emitidos, do mais recente para o mais antigo. */
    List<Alerta> findAllByOrderByDataHoraDesc();
}
