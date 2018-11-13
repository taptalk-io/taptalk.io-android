package com.moselo.TapTalk.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TAPAuthTicketResponse {
    @JsonProperty("ticket") private String ticket;

    public TAPAuthTicketResponse(String ticket) {
        this.ticket = ticket;
    }

    public TAPAuthTicketResponse() {
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
