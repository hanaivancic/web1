package web2.lab1.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    private UUID id;

    @Column(length = 20, nullable = false)
    private String personalId;

    @ElementCollection
    @CollectionTable(name = "ticket_numbers", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "number")
    private List<Integer> numbers;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    private Instant purchasedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPersonalId() { return personalId; }
    public void setPersonalId(String personalId) { this.personalId = personalId; }
    public List<Integer> getNumbers() { return numbers; }
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }
    public Instant getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Instant purchasedAt) { this.purchasedAt = purchasedAt; }
}

