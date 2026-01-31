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

    public TicketServiceTools(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // --- TOOL 1: Create Ticket ---

    @JsonClassDescription("Request to create a new support ticket.")
    public record CreateTicketRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("The user's problem description as a single text string. Do not use nested objects.")
            String issue
    ) {}

    public record TicketResponse(Long id, String status) {}

    @Bean("createTicket")
    @Description("Create a support ticket.")
    public Function<CreateTicketRequest, TicketResponse> createTicket() {
        return request -> {
            System.out.println(">>> TOOL CALLED: createTicket with issue: " + request.issue());
            Ticket ticket = new Ticket(request.issue());
            Ticket saved = ticketRepository.save(ticket);
            return new TicketResponse(saved.getId(), saved.getStatus());
        };
    }

    // --- TOOL 2: Check Status ---

    @JsonClassDescription("Request to check the status of an existing ticket.")
    public record TicketStatusRequest(
            @JsonProperty(required = true)
            @JsonPropertyDescription("The numeric ID of the ticket.")
            Long id
    ) {}

    @Bean("getTicketStatus")
    @Description("Get the status of a specific ticket.")
    public Function<TicketStatusRequest, String> getTicketStatus() {
        return request -> {
            System.out.println(">>> TOOL CALLED: getTicketStatus with ID: " + request.id());
            return ticketRepository.findById(request.id())
                    .map(Ticket::getStatus)
                    .orElse("Ticket not found.");
        };
    }
}