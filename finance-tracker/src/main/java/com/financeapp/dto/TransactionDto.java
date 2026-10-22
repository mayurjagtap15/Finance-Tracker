package com.financeapp.dto;

import com.financeapp.model.TransactionType;
import javax.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {

    private Long id;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate transactionDate;
}
