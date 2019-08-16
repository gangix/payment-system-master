package com.finreach.paymentservice.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.finreach.paymentservice.api.request.CreateAccount;
import com.finreach.paymentservice.domain.Account;
import com.finreach.paymentservice.domain.Payment;
import com.finreach.paymentservice.domain.PaymentState;
import com.finreach.paymentservice.domain.Transaction;
import com.finreach.paymentservice.exception.MyResourceNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Map<String, Account> ACCOUNTS = new HashMap<>();

	public Account create(CreateAccount request) {
		Account account = new Account();
		account.setBalance(request.getBalance());
		ACCOUNTS.put(account.getId(), account);

		return account;
	}
	
	@Override
	public PaymentState transactionCreated(Payment payment) {
		String destinationAccountId = payment.getDestinationAccountId();
		String sourceAccountId = payment.getSourceAccountId();
		Double transactionAmount = payment.getAmount();
		
		Optional<Account> optionalSource = get(sourceAccountId);
		Optional<Account> optionalDest = get(destinationAccountId);
		if (!optionalSource.isPresent() || !optionalDest.isPresent()) {
			throw new MyResourceNotFoundException();
		}
		Account sourceAccount = optionalSource.get();
		Double sourceCurrentAmount = sourceAccount.getBalance();
		
		double sourceCalcBalance = calcBalance(sourceCurrentAmount, transactionAmount, true);
		if (sourceCalcBalance < 0) {
			return PaymentState.REJECTED;
		}
		Transaction sourceTrx = new Transaction(sourceAccountId, transactionAmount*-1, LocalDateTime.now());
		sourceAccount.addTransaction(sourceTrx);
		sourceAccount.setBalance(sourceCalcBalance);
		
		Account destAccount = optionalDest.get();
		Double destCurrentAmount = destAccount.getBalance();
		double destCalcBalance = calcBalance(destCurrentAmount, transactionAmount, false);
		Transaction destTrx = new Transaction(destinationAccountId, transactionAmount, LocalDateTime.now());
		destAccount.addTransaction(destTrx);
		destAccount.setBalance(destCalcBalance);
		
		return PaymentState.EXECUTED;
	}
	
	public Transaction transaction(String accountId, double amount) {
		Optional<Account> optional = get(accountId);
		if(!optional.isPresent()) {
			return null;
		}
		Transaction trx = new Transaction(accountId, amount, LocalDateTime.now());
		Account account = optional.get();
		account.addTransaction(trx);
		return trx;
	}	
	
	public Optional<Account> get(String id) {
		return Optional.ofNullable(ACCOUNTS.get(id));
	}

	public List<Account> all() {
		return new ArrayList<>(ACCOUNTS.values());
	}

	public double calcBalance(Double amount, Double transactionAmount, boolean isSource) {
		if(isSource) {
			transactionAmount *= -1;
		}
		BigDecimal balance = BigDecimal.valueOf(amount)
										.add(BigDecimal.valueOf(transactionAmount));

		return balance.doubleValue();
	}

	@Override
	public boolean accountValidation(String sourceAccountId, String destinationAccountId) {
		return get(sourceAccountId).isPresent() && get(destinationAccountId).isPresent(); 
	}

}
