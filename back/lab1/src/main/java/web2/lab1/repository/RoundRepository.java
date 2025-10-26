package web2.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web2.lab1.model.Round;

import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    Optional<Round> findByActiveTrue();
    Optional<Round> findTopByOrderByStartedAtDesc();
}

