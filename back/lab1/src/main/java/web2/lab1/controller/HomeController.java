package web2.lab1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web2.lab1.model.Round;
import web2.lab1.model.Ticket;
import web2.lab1.service.RoundService;
import web2.lab1.service.TicketService;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final RoundService roundService;
    private final TicketService ticketService;

    public HomeController(RoundService roundService, TicketService ticketService) {
        this.roundService = roundService;
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser user, Model model) {
        model.addAttribute("user", user);

        var currentRoundOpt = roundService.getCurrentRound();
        if (currentRoundOpt.isPresent()) {
            Round round = currentRoundOpt.get();
            model.addAttribute("paymentsActive", round.isActive());
            model.addAttribute("drawnNumbers", round.getDrawnNumbers());

            if (user != null) {
                List<Ticket> userTickets = ticketService.getTicketsByUserAndRound(user.getEmail(), round);
                model.addAttribute("userTickets", userTickets);
                model.addAttribute("ticketsCount", userTickets.size());
            }
        } else {
            model.addAttribute("paymentsActive", false);
            model.addAttribute("ticketsCount", 0);
        }

        return "Home";
    }


}
