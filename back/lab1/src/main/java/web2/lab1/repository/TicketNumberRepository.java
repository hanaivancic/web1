package web2.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web2.lab1.model.TicketNumber;

public interface TicketNumberRepository extends JpaRepository<TicketNumber, Long> {
}

