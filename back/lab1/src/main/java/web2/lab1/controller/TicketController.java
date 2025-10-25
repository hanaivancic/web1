package web2.lab1.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import web2.lab1.model.Round;
import web2.lab1.model.Ticket;
import web2.lab1.repository.RoundRepository;
import web2.lab1.repository.TicketRepository;
import web2.lab1.service.TicketService;

import java.util.*;

@RestController
public class TicketController {
    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final RoundRepository roundRepository;

    public TicketController(TicketService ticketService, TicketRepository ticketRepository, RoundRepository roundRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.roundRepository = roundRepository;
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String,Object>> status(@AuthenticationPrincipal OidcUser user) {
        Map<String,Object> out = new HashMap<>();
        if (user != null) {
            out.put("user", Map.of(
                    "sub", user.getSubject(),
                    "name", user.getFullName(),
                    "email", user.getEmail()
            ));
        }
        roundRepository.findByActiveTrue().ifPresent(r -> {
            out.put("roundActive", r.isActive());
            out.put("ticketsCount", ticketRepository.countByRound(r));
            out.put("drawnNumbers", r.getDrawnNumbers());
        });
        return ResponseEntity.ok(out);
    }

    @PostMapping(value = "/tickets", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> createTicket(
            @RequestParam String personalId,
            @RequestParam String numbers,
            HttpServletRequest request,
            @AuthenticationPrincipal OidcUser user
    ) throws Exception {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        UUID id;
        try {
            id = ticketService.createTicket(personalId, numbers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage().getBytes());
        }

        byte[] png = ticketService.generateQrForTicket(id, getBaseUrl(request));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<Map<String,Object>> viewTicket(@PathVariable UUID id) {
        Optional<Ticket> maybe = ticketRepository.findById(id);
        if (maybe.isEmpty()) return ResponseEntity.notFound().build();
        Ticket t = maybe.get();
        Map<String,Object> out = new HashMap<>();
        out.put("ticketId", t.getId());
        out.put("personalId", t.getPersonalId());
        out.put("numbers", t.getNumbers());
        out.put("purchasedAt", t.getPurchasedAt());
        Round r = t.getRound();
        out.put("roundId", r != null ? r.getId() : null);
        out.put("drawnNumbers", r != null ? r.getDrawnNumbers() : null);
        return ResponseEntity.ok(out);
    }

    private String getBaseUrl(HttpServletRequest req) {
        StringBuffer url = req.getRequestURL();
        String uri = req.getRequestURI();
        return url.substring(0, url.length() - uri.length());
    }
}

