package web2.lab1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import web2.lab1.service.RoundService;

import java.util.List;

@RestController
@RequestMapping("/")
public class RoundController {

    private final RoundService roundService;

    public RoundController(RoundService roundService) {
        this.roundService = roundService;
    }

    @PostMapping("/new-round")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void newRound() {
        roundService.startNewRound();
    }

    @PostMapping("/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeRound() {
        roundService.closeRound();
    }

    @PostMapping("/store-results")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void storeResults(@RequestBody StoreResultsRequest request) {
        boolean ok = roundService.storeResults(request.getNumbers());
        if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ne može pohraniti rezultate");
    }

    public static class StoreResultsRequest {
        private List<Integer> numbers;
        public List<Integer> getNumbers() { return numbers; }
        public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }
    }
}

