package com.transaction.solution.data.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "tbl_transaction_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    private String foneNo;
    private Timestamp timestamps;
    private String fullname;
    private String accNo;
    private String address;
    private String sex;
}
