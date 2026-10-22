package com.financeapp.dto;

import com.financeapp.model.TransactionType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @Size(max = 50)
    private String icon;
}
