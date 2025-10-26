package web2.lab1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import web2.lab1.model.Round;
import web2.lab1.service.RoundService;

import java.util.Optional;

@Controller
public class HomeController {

    private final RoundService roundService;

    public HomeController(RoundService roundService) {
        this.roundService = roundService;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser user, Model model) {
        if (user == null) {
            return "redirect:/oauth2/authorization/auth0";
        }

        model.addAttribute("user", user);

        Optional<Round> currentRoundOpt = roundService.getCurrentRound();
        if (currentRoundOpt.isPresent()) {
            Round round = currentRoundOpt.get();
            model.addAttribute("paymentsActive", round.isActive());
            model.addAttribute("drawnNumbers", round.getDrawnNumbers());
            model.addAttribute("ticketsCount", round.getTickets() != null ? round.getTickets().size() : 0);
        } else {
            model.addAttribute("paymentsActive", false);
            model.addAttribute("ticketsCount", 0);
        }

        return "Home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        request.logout();
        return "redirect:/oauth2/authorization/auth0";
    }


}
