package web2.lab1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
public class Ticket {
    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    @Column(length = 20)
    private String personalId;

    private LocalDateTime purchasedAt = LocalDateTime.now();

    @Setter
    @ElementCollection
    private List<Integer> numbers;

    @Setter
    @ManyToOne
    private Round round;

}

