package web2.lab1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web2.lab1.model.Round;
import web2.lab1.model.Ticket;
import web2.lab1.repository.RoundRepository;
import web2.lab1.repository.TicketRepository;
import web2.lab1.util.QrUtil;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final RoundRepository roundRepository;

    public TicketService(TicketRepository ticketRepository, RoundRepository roundRepository) {
        this.ticketRepository = ticketRepository;
        this.roundRepository = roundRepository;
    }

    @Transactional
    public UUID createTicket(String personalId, String numbersCsv) {
        // validation
        if (personalId == null || personalId.isBlank() || personalId.length() > 20) {
            throw new IllegalArgumentException("Personal ID must be 1..20 characters");
        }
        List<Integer> nums = Arrays.stream(numbersCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try { return Integer.valueOf(s); }
                    catch (NumberFormatException e) { throw new IllegalArgumentException("Numbers must be integers"); }
                })
                .collect(Collectors.toList());

        if (nums.size() < 6 || nums.size() > 10) {
            throw new IllegalArgumentException("Numbers count must be between 6 and 10");
        }
        Set<Integer> set = new HashSet<>(nums);
        if (set.size() != nums.size()) {
            throw new IllegalArgumentException("Duplicate numbers are not allowed");
        }
        for (int n : nums) {
            if (n < 1 || n > 45) throw new IllegalArgumentException("All numbers must be in range 1..45");
        }

        Round round = roundRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("No active round"));

        Ticket t = new Ticket();
        t.setId(UUID.randomUUID());
        t.setPersonalId(personalId);
        t.setNumbers(nums);
        t.setRound(round);
        t.setPurchasedAt(Instant.now());
        ticketRepository.save(t);
        return t.getId();
    }

    public byte[] generateQrForTicket(UUID ticketId, String baseUrl) throws Exception {
        String url = baseUrl.endsWith("/") ? baseUrl + "ticket/" + ticketId : baseUrl + "/ticket/" + ticketId;
        return QrUtil.generateQrPng(url, 400, 400);
    }
}

