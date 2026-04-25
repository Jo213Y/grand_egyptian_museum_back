package com.gem.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequest {
    private String           visitDate;
    private Long             hallId;
    private List<TicketItem> tickets;
    private PaymentInfo      payment;

    @Data
    public static class TicketItem {
        private String  type;
        private Integer quantity;
        private Double  price;
    }

    @Data
    public static class PaymentInfo {
        private String nameOnCard;
        private String cardNumber;
        private String expirationDate;
        private String securityCode;
    }
}
