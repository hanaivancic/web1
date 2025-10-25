package web2.lab1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import web2.lab1.service.RoundService;

import java.util.List;

@RestController
public class AdminController {
    private final RoundService roundService;

    public AdminController(RoundService roundService) {
        this.roundService = roundService;
    }

    @PostMapping("/new-round")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Void> newRound() {
        roundService.createNewRoundIfNoneActive();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Void> closeRound() {
        roundService.closeActiveRoundIfAny();
        return ResponseEntity.noContent().build();
    }

    public static class NumbersDto {
        public List<Integer> numbers;
    }

    @PostMapping("/store-results")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Void> storeResults(@RequestBody NumbersDto dto) {
        if (dto == null || dto.numbers == null) return ResponseEntity.badRequest().build();
        boolean ok = roundService.storeDrawnNumbersIfAllowed(dto.numbers);
        if (ok) return ResponseEntity.noContent().build();
        return ResponseEntity.badRequest().build();
    }
}

