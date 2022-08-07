package com.transaction.solution.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
@Slf4j
@SpringBootApplication
public class TransactionInfoApplication {



	public static String getRandomNumberString() {
		// It will generate 6 digit random Number.
		// from 0 to 999999
		Random rnd = new Random();
		int number = rnd.nextInt(999999);

		// this will convert any number sequence into 6 character.
		return String.format("%06d", number);
	}
	public static void main(String[] args) {

	String dg =	getRandomNumberString();
log.info("helloyyy "+dg);
		SpringApplication.run(TransactionInfoApplication.class, args);
	}

}

