package com.transaction.solution.data.controller;


import com.transaction.solution.data.model.Transactiondto;
import com.transaction.solution.data.model.UserDto;
import com.transaction.solution.data.repository.TransactionRepository;
import com.transaction.solution.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
@RestController
public class TransactionController {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;
    private ReadWriteLock accountLock;
    @GetMapping("/transaction/{accno}")
    public ResponseEntity<?> getTransactions(@PathVariable ("accno")String accno) {
        Timestamp requestTimeStamp = Timestamp.from(Instant.now());
        System.out.println("TIME TRANS "+requestTimeStamp);
        Timestamp requestTimeStamp2 = Timestamp.from(Instant.now().minusSeconds(60));
        List<Transactiondto> tu= transactionRepository.findByAccNo(accno);
        List<Transactiondto> all= transactionRepository.findAll();
        Map<String, Object> tranmap = new HashMap<>();
        Long count = tu.stream().count();

        BigDecimal sum = tu.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal min = tu
                .stream()
                .map(Transactiondto::getAmount)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = tu
                .stream()
                .map(Transactiondto::getAmount)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        {

            Double avc = sum.doubleValue() / count;

            tranmap.put("transaction", tu);
            tranmap.put("totalBalance", sum);
            tranmap.put("Maximum Deposit", max);
            tranmap.put("Min Deposit", min);

            if (tu.size() > 0) {
                return new ResponseEntity<>(tranmap, HttpStatus.OK);
            } else {
                //BASE ON REQUIREMENT BUT WRONG IN REAL LIFE!!!
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
        }
    }




    @GetMapping("/transactionListforlast60seconds")
    public ResponseEntity<?> getBookByIdTime() {
        Timestamp requestTimeStamp = Timestamp.from(Instant.now());
        System.out.println("TIME TRANS "+requestTimeStamp);
        Timestamp requestTimeStamp2 = Timestamp.from(Instant.now().minusSeconds(60));
        List<Transactiondto> tutorialData = transactionRepository.findTimeLimit(requestTimeStamp, requestTimeStamp2);
        Map<String, Object> tranmap = new HashMap<>();
        Long count = tutorialData.stream().count();

        BigDecimal sum = tutorialData.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal min = tutorialData
                .stream()
                .map(Transactiondto::getAmount)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal max = tutorialData
                .stream()
                .map(Transactiondto::getAmount)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        {

            Double avc = sum.doubleValue() / count;

            tranmap.put("totalBalance", sum);
            tranmap.put("Maximum Deposit", max);
            tranmap.put("Min Deposit", min);

            if (tutorialData.size() > 0) {
                return new ResponseEntity<>(tranmap, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }

    @GetMapping("/transactionList/{id}")
    public ResponseEntity<?> getBookByIdTime(@PathVariable ("id") Long id) {
        Timestamp requestTimeStamp = null;

        Optional<Transactiondto> tu = transactionRepository.findById(id);

            if (tu.isPresent()) {

                Timestamp chk = tu.get().getTimestamps();

                if(chk.after(requestTimeStamp) ){
                    return new ResponseEntity<>(tu, HttpStatus.NO_CONTENT);
                }

                    return new ResponseEntity<>(tu, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }








    @PostMapping("/transaction/deposit")
    public ResponseEntity<?> createTransaction(@RequestBody Transactiondto transactiondto) {
        try {

            transactiondto.setTranType("C");
          Timestamp requestTimeStamp = null;
            if(transactiondto.getTimestamps() == null) {
                requestTimeStamp = Timestamp.from(Instant.now());
                transactiondto.setTimestamps(requestTimeStamp);

            }

            Optional<UserDto> user = userRepository
                    .findByAccNo(transactiondto.getAccNo());

            if( user ==null){
                throw new Exception("No Record Found");
            }
            Map<String, Object> tranmap = new HashMap<>();

            if (transactiondto.getAmount() == null){
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }
            Transactiondto _tutorial = transactionRepository
                    .save(transactiondto);
            List<Transactiondto> creditSum = transactionRepository.findByAccNoAndTranType(transactiondto.getAccNo(),"C");
            BigDecimal sumCredit = creditSum.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<Transactiondto> debitSum = transactionRepository.findByAccNoAndTranType(transactiondto.getAccNo(),"D");
            BigDecimal sumdebit = debitSum.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        double bal    =  sumCredit.doubleValue() - sumdebit.doubleValue();
            tranmap.put("Closing Balance",bal);
            return new ResponseEntity<>(tranmap, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






    @PostMapping("/transaction/withdraw")
    public ResponseEntity<?> withDrawalTransaction(@RequestBody Transactiondto transactiondto) {
        try {
            transactiondto.setTranType("D");
          Timestamp requestTimeStamp = null;
            if(transactiondto.getTimestamps() == null) {
                requestTimeStamp = Timestamp.from(Instant.now());
                transactiondto.setTimestamps(requestTimeStamp);

            }

            Optional<UserDto> user = userRepository
                    .findByAccNo(transactiondto.getAccNo());

            if( user ==null){
                throw new Exception("No Record Found");
            }
            Map<String, Object> tranmap = new HashMap<>();
            this.accountLock.writeLock().lock();
            List<Transactiondto> creditSum = transactionRepository.findByAccNoAndTranType(transactiondto.getAccNo(),"C");
            BigDecimal sumCredit = creditSum.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<Transactiondto> debitSum = transactionRepository.findByAccNoAndTranType(transactiondto.getAccNo(),"D");
            BigDecimal sumdebit = debitSum.stream().map(transaction -> transaction.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
            double bal    =  sumCredit.doubleValue() - sumdebit.doubleValue();
            if( (bal)< (transactiondto.getAmount().doubleValue())){
    throw new Exception("Insufficient Funds");
}
            if (transactiondto.getAmount() == null){
                return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
            }
            Transactiondto _tutorial = transactionRepository
                    .save(transactiondto);
            tranmap.put("Closing Balance",bal);
            this.accountLock.writeLock().unlock();
            return new ResponseEntity<>(tranmap, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @DeleteMapping("/transaction/delete")
    public ResponseEntity<HttpStatus> deleteAllTransaction() {
        try {
            transactionRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping ("/generateAccno")
    public ResponseEntity<?> generateAccno(@RequestBody UserDto userDto) {
        try {
          //  int int_random = ThreadLocalRandom.current().nextInt();
            Timestamp requestTimeStamp = null;
            if(userDto.getTimestamps() == null) {
                requestTimeStamp = Timestamp.from(Instant.now());
                userDto.setTimestamps(requestTimeStamp);

            }

            long accno = (long) (Math.random()*Math.pow(10,10));
            log.info("accno: "+accno);
//userDto.setAddress(u);
            userDto.setAccNo(String.valueOf(accno));
            UserDto _tutorial = userRepository
                    .save(userDto);
            return new ResponseEntity<>(userDto, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping ("/getaccno/byaccno/{accno}")
    public ResponseEntity<?> getAccno(@PathVariable("accno")String accno) {
        try {

            Optional<UserDto> user = userRepository
                    .findByAccNo(accno);

            if(user ==null){
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }

            else {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping ("/getaccno/byname/{name}")
    public ResponseEntity<?> getAccnobyName(@PathVariable("name")String name) {
        try {

            Optional<UserDto> user = userRepository
                    .findByFullname(name);

            if(user ==null){
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }

            else {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    @GetMapping ("/getallaccno")
    public ResponseEntity<?> getallAccno() {
        try {

            List<UserDto> user = userRepository
                    .findAll();

            if(user ==null){
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }

            else {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}