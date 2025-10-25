package web2.lab1.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "rounds")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active;

    @ElementCollection
    @CollectionTable(name = "round_drawn_numbers", joinColumns = @JoinColumn(name = "round_id"))
    @Column(name = "number")
    private List<Integer> drawnNumbers;

    private Instant startedAt;

    public Long getId() { return id; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Integer> getDrawnNumbers() { return drawnNumbers; }
    public void setDrawnNumbers(List<Integer> drawnNumbers) { this.drawnNumbers = drawnNumbers; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
}
