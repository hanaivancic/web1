package web2.lab1.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import web2.lab1.model.Round;
import web2.lab1.model.Ticket;
import web2.lab1.model.TicketNumber;
import web2.lab1.repository.RoundRepository;
import web2.lab1.repository.TicketNumberRepository;
import web2.lab1.repository.TicketRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    public final TicketRepository ticketRepository;
    private final RoundRepository roundRepository;
    private final TicketNumberRepository ticketNumberRepository;

    public TicketService(TicketRepository ticketRepository, RoundRepository roundRepository, TicketNumberRepository ticketNumberRepository) {
        this.ticketRepository = ticketRepository;
        this.roundRepository = roundRepository;
        this.ticketNumberRepository = ticketNumberRepository;
    }

    public Ticket createTicket(String personalId, List<Integer> numbers) {
        if (personalId == null || personalId.isEmpty() || personalId.length() > 20)
            throw new IllegalArgumentException("Neispravan broj osobne iskaznice");

        if (numbers.size() < 6 || numbers.size() > 10)
            throw new IllegalArgumentException("Broj brojeva mora biti između 6 i 10");

        if (numbers.stream().anyMatch(n -> n < 1 || n > 45))
            throw new IllegalArgumentException("Svi brojevi moraju biti između 1 i 45");

        if (numbers.stream().distinct().count() != numbers.size())
            throw new IllegalArgumentException("Ne smije biti duplikata među brojevima");

        Round round = roundRepository.findByActiveTrue()
                .orElseThrow(() -> new IllegalStateException("Nema aktivnog kola"));

        Ticket ticket = new Ticket();
        ticket.setPersonalId(personalId);
        ticket.setRound(round);
        ticketRepository.save(ticket);

        for (Integer n : numbers) {
            TicketNumber tn = new TicketNumber();
            tn.setTicket(ticket);
            tn.setNumber(n);
            ticketNumberRepository.save(tn);
            ticket.getNumbers().add(tn);
        }

        return ticket;
    }

    public byte[] generateQRCode(String ticketUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(ticketUrl, BarcodeFormat.QR_CODE, 250, 250);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public List<Ticket> getTicketsByUserAndRound(String email, Round round) {
        return ticketRepository.getTicketsByUserAndRound(email, round);
    }
}
