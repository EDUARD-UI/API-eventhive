package com.example.demo.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketPromedioDto {
    private String mes;
    private BigDecimal ticketPromedio;
    private Integer totalCompras;
}