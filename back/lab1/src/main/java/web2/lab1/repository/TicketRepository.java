package web2.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import web2.lab1.model.Round;
import web2.lab1.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    long countByRound(Round round);
    List<Ticket> findByRound(Round round);
}

