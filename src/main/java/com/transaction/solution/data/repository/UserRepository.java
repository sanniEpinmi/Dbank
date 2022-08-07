package com.transaction.solution.data.repository;

import com.transaction.solution.data.model.Transactiondto;
import com.transaction.solution.data.model.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserDto, Long> {


    Optional<UserDto> findByAccNo(String accNo);

        Optional<UserDto> findByFullname(String name);

    }
