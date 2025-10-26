package web2.lab1.service;

import org.springframework.stereotype.Service;
import web2.lab1.model.Round;
import web2.lab1.model.RoundDrawnNumber;
import web2.lab1.model.TicketNumber;
import web2.lab1.repository.RoundDrawnNumberRepository;
import web2.lab1.repository.RoundRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RoundService {

    private final RoundRepository roundRepository;
    private final RoundDrawnNumberRepository roundDrawnNumberRepository;

    public RoundService(RoundRepository roundRepository, RoundDrawnNumberRepository roundDrawnNumberRepository) {
        this.roundRepository = roundRepository;
        this.roundDrawnNumberRepository = roundDrawnNumberRepository;
    }

    public Round startNewRound() {
        roundRepository.findByActiveTrue().ifPresent(r -> {
            r.setActive(false);
            r.setClosedAt(LocalDateTime.now());
            roundRepository.save(r);
        });

        Round round = new Round();
        round.setActive(true);
        return roundRepository.save(round);
    }

    public void closeRound() {
        roundRepository.findByActiveTrue().ifPresent(r -> {
            r.setActive(false);
            r.setClosedAt(LocalDateTime.now());
            roundRepository.save(r);
        });
    }

    public boolean storeResults(List<Integer> numbers) {
        Optional<Round> roundOpt = roundRepository.findTopByOrderByStartedAtDesc();
        if (roundOpt.isEmpty()) {
            return false;
        }

        Round round = roundOpt.get();

        if (round.isActive() || !round.getDrawnNumbers().isEmpty()) return false;

        for (Integer n : numbers) {
            RoundDrawnNumber rn = new RoundDrawnNumber();
            rn.setRound(round);
            rn.setNumber(n);
            roundDrawnNumberRepository.save(rn);
            round.getDrawnNumbers().add(rn);
        }
        roundRepository.save(round);
        return true;
    }


    public Optional<Round> getCurrentRound() {
        return roundRepository.findByActiveTrue();
    }
}
