package com.finreach.paymentservice;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.finreach.paymentservice.util.TransactionsGenerator;

@Configuration
public class TransactionSimulator implements ApplicationRunner{

	@Override
	public void run(ApplicationArguments args) throws Exception {
		TransactionsGenerator generator = new TransactionsGenerator();
		generator.generate();
	}
	
}
