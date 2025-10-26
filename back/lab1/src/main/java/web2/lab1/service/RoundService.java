package web2.lab1.service;

import org.springframework.stereotype.Service;
import web2.lab1.model.Round;
import web2.lab1.repository.RoundRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class RoundService {

    private final RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
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
            System.out.println("nema kola u bazi");
            return false;
        }

        Round round = roundOpt.get();
        System.out.println("Trenutno kolo: ID=" + round.getId() + ", active=" + round.isActive() + ", drawn=" + round.getDrawnNumbers());

        if (round.isActive() || round.getDrawnNumbers() != null) return false;

        round.setDrawnNumbers(new ArrayList<>(numbers));
        roundRepository.save(round);
        System.out.println("Uspješno pohranjeno");
        return true;
    }


    public Optional<Round> getCurrentRound() {
        return roundRepository.findByActiveTrue();
    }
}
