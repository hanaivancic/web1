package web2.lab1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web2.lab1.model.Round;
import web2.lab1.repository.RoundRepository;

import java.time.Instant;
import java.util.Optional;

@Service
public class RoundService {
    private final RoundRepository roundRepository;

    public RoundService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    @Transactional
    public Round createNewRoundIfNoneActive() {
        Optional<Round> active = roundRepository.findByActiveTrue();
        if (active.isPresent()) {
            return active.get();
        }
        Round r = new Round();
        r.setActive(true);
        r.setStartedAt(Instant.now());
        return roundRepository.save(r);
    }

    @Transactional
    public void closeActiveRoundIfAny() {
        roundRepository.findByActiveTrue().ifPresent(r -> {
            r.setActive(false);
            roundRepository.save(r);
        });
    }

    @Transactional
    public boolean storeDrawnNumbersIfAllowed(java.util.List<Integer> numbers) {
        Optional<Round> maybe = roundRepository.findByActiveTrue();
        if (maybe.isEmpty()) return false; // no round -> bad request per spec
        Round r = maybe.get();
        if (r.isActive()) return false; // can't store while active
        if (r.getDrawnNumbers() != null && !r.getDrawnNumbers().isEmpty()) return false; // already stored
        r.setDrawnNumbers(numbers);
        roundRepository.save(r);
        return true;
    }
}
