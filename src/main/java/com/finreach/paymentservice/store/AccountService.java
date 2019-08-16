package com.finreach.paymentservice.store;

import java.util.List;
import java.util.Optional;

import com.finreach.paymentservice.api.request.CreateAccount;
import com.finreach.paymentservice.domain.Account;
import com.finreach.paymentservice.domain.Payment;
import com.finreach.paymentservice.domain.PaymentState;
import com.finreach.paymentservice.domain.Transaction;

public interface AccountService {
	Account create(CreateAccount request);

	Optional<Account> get(String accountId);

	PaymentState transactionCreated(Payment payment);

	List<Account> all();

	boolean accountValidation(String sourceAccountId, String destinationAccountId);
	
	Transaction transaction(String accountId, double amount);
}
