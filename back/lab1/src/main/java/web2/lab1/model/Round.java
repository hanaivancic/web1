package web2.lab1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private boolean active;

    private LocalDateTime startedAt = LocalDateTime.now();

    @Setter
    private LocalDateTime closedAt;

    @Setter
    @ElementCollection
    private List<Integer> drawnNumbers;

    @Setter
    @OneToMany(mappedBy = "round")
    private List<Ticket> tickets;

}
