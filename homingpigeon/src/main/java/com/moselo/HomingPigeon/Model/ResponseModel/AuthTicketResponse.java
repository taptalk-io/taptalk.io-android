package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTicketResponse {
    @JsonProperty("ticket") private String ticket;

    public AuthTicketResponse(String ticket) {
        this.ticket = ticket;
    }

    public AuthTicketResponse() {
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
