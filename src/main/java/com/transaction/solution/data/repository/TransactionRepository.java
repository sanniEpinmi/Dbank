package com.transaction.solution.data.repository;

import com.transaction.solution.data.model.Transactiondto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactiondto, Long> {

    @Query(
            value = "SELECT * FROM tbl_transactions u WHERE u.timestamps between :one and :two",
            nativeQuery = true)
    List<Transactiondto> findTimeLimit(Timestamp one , Timestamp two);


    List<Transactiondto> findByUsername(String username);



    List<Transactiondto> findByAccNoAndTranType(String accNo,String tranType);

    List<Transactiondto> findByAccNo(String accNo);

}
