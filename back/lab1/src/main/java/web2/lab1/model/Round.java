package web2.lab1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoundDrawnNumber> drawnNumbers = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "round")
    private List<Ticket> tickets;
}
