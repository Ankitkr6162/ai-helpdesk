package com.ai.helpdesk.service;




import com.ai.helpdesk.entity.Ticket;
import com.ai.helpdesk.repository.TicketRepository;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class TicketServiceTools {

    private final TicketRepository ticketRepository;
    private final EmailService emailService; // 1. Inject EmailService

    public TicketServiceTools(TicketRepository ticketRepository, EmailService emailService) {
        this.ticketRepository = ticketRepository;
        this.emailService = emailService;
    }

    // --- TOOL 1: Create Ticket ---

    @JsonClassDescription("Request to create a new support ticket.")
    public record CreateTicketRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("The user's problem description as a single text string.")
            String issue
    ) {}

    public record TicketResponse(Long id, String status) {}

    @Bean("createTicket")
    @Description("Create a support ticket.")
    public Function<CreateTicketRequest, TicketResponse> createTicket() {
        return request -> {
            // Logic
            Ticket ticket = new Ticket(request.issue());
            Ticket saved = ticketRepository.save(ticket);

            // 2. Trigger Notification
            String subject = "NEW TICKET Created: #" + saved.getId();
            String body = "A new ticket has been created.\n\nDescription: " + request.issue();
            emailService.sendNotification(subject, body);

            return new TicketResponse(saved.getId(), saved.getStatus());
        };
    }

    // --- TOOL 2: Check Status ---

    @JsonClassDescription("Request to check the status of an existing ticket.")
    public record TicketStatusRequest(
            @JsonProperty(required = true)
            Long id
    ) {}

    @Bean("getTicketStatus")
    @Description("Get the status of a specific ticket.")
    public Function<TicketStatusRequest, String> getTicketStatus() {
        return request -> {
            String status = ticketRepository.findById(request.id())
                    .map(Ticket::getStatus)
                    .orElse("Ticket not found");

            // 3. Trigger Notification
            String subject = "STATUS CHECK: Ticket #" + request.id();
            String body = "A user just queried the status of Ticket #" + request.id() + "\nCurrent Status: " + status;
            emailService.sendNotification(subject, body);

            return status;
        };
    }
}