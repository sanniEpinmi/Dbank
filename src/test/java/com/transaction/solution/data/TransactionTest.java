package com.transaction.solution.data;

import com.transaction.solution.data.model.Transactiondto;
import com.transaction.solution.data.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//import static org.junit.Assert.*;


@SpringBootTest

class TransactionTest {

    @Autowired
    TransactionRepository pRepo;

    @Test
    public void testCreate () {
        Transactiondto p = new Transactiondto();
        p.setId(1L);
        p.setAmount(new BigDecimal(1000.00));
        pRepo.save(p);
        assertNotNull(pRepo.findById(1L).get());
    }

    @Test
    public void testReadAll () {
        List list = pRepo.findAll();
        assertThat(list).size().isGreaterThan(0);
    }

    @Test
    public void testRead () {
        Transactiondto te = pRepo.findById(1L).get();
        assertEquals("sanni", te.getUsername());
    }

    @Test
    public void testUpdate () {
        Transactiondto p = pRepo.findById(1L).get();
        p.setAmount(new BigDecimal(800.00));
        pRepo.save(p);
        assertNotEquals(700.00, pRepo.findById(1L).get().getAmount());
    }

    @Test
    public void testDelete () {
        pRepo.deleteById(1L);
        assertThat(pRepo.existsById(1L)).isFalse();
    }
}