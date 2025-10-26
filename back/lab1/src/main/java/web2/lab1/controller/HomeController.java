package web2.lab1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web2.lab1.service.RoundService;

@Controller
public class HomeController {

    private final RoundService roundService;

    public HomeController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser user) {
        model.addAttribute("user", user);

        roundService.getCurrentRound().ifPresent(r -> {
            model.addAttribute("ticketsCount", r.getTickets() != null ? r.getTickets().size() : 0);
            model.addAttribute("drawnNumbers", r.getDrawnNumbers());
            model.addAttribute("paymentsActive", r.isActive());
        });

        return "Home";
    }
}
