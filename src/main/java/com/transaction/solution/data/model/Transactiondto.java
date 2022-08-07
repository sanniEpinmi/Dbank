package com.transaction.solution.data.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transactiondto {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    private Timestamp timestamps;
    private String username;
    private String accNo;
    private String tranType;


}
