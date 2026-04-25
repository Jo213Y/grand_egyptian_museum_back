package com.gem.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingResponse {
    private Long               id;
    private String             visitDate;
    private Double             totalPrice;
    private String             status;
    private String             hallName;
    private List<TicketDetail> tickets;

    @Data
    public static class TicketDetail {
        private String  ticketType;
        private Integer quantity;
        private Double  unitPrice;
        private Double  subtotal;
    }
}
