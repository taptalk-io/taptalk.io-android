package com.moselo.HomingPigeon.Model.ResponseModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpAuthTicketResponse {
    @JsonProperty("ticket") private String ticket;

    public HpAuthTicketResponse(String ticket) {
        this.ticket = ticket;
    }

    public HpAuthTicketResponse() {
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
