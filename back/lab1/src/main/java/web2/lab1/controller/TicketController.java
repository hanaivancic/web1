package web2.lab1.controller;

import com.google.zxing.WriterException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import web2.lab1.model.Round;
import web2.lab1.model.Ticket;
import web2.lab1.service.TicketService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/new")
    public String newTicketForm(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("user", user);
        return "NewTicket";
    }

    @PostMapping("/new")
    public String createTicket(@AuthenticationPrincipal OidcUser user,
                               @RequestParam String personalId,
                               @RequestParam String numbers,
                               Model model) {
        try {
            List<Integer> numbersList = Arrays.stream(numbers.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            Ticket ticket = ticketService.createTicket(personalId, numbersList);

            String ticketUrl = "https://web1-zcj6.onrender.com/tickets/" + ticket.getId();
            byte[] qrCode = ticketService.generateQRCode(ticketUrl);
            String base64QR = java.util.Base64.getEncoder().encodeToString(qrCode);

            model.addAttribute("ticket", ticket);
            model.addAttribute("qrCode", base64QR);

            return "TicketSuccessful";
        } catch (IllegalArgumentException | IllegalStateException | WriterException | IOException e) {
            model.addAttribute("error", e.getMessage());
            return "NewTicket";
        }
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable("id") String id, Model model) {
        Ticket ticket = ticketService.ticketRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() -> new IllegalArgumentException("Ticket ne postoji"));
        Round round = ticketService.roundRepository.findById(ticket.getRound().getId())
                .orElseThrow(() -> new IllegalArgumentException("Round ne postoji"));
        model.addAttribute("ticket", ticket);
        model.addAttribute("round", round);
        model.addAttribute("drawnNumbers", round.getDrawnNumbers());
        return "TicketDetails";
    }
}
