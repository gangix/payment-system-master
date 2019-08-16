package com.finreach.paymentservice.store;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finreach.paymentservice.api.request.CreatePayment;
import com.finreach.paymentservice.domain.Payment;
import com.finreach.paymentservice.domain.PaymentState;
import com.finreach.paymentservice.exception.MyResourceNotFoundException;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Map<String, Payment> PAYMENTS = new HashMap<>();
	
	private final AccountService accountService;

	@Autowired
	public PaymentServiceImpl(AccountService accountService) {
		super();
		this.accountService = accountService;
	}

	@Override
	public Payment create(CreatePayment createPayment) {
		String sourceAccountId = createPayment.getSourceAccountId();
		String destinationAccountId = createPayment.getDestinationAccountId();
		boolean accountsAreValid = accountService.accountValidation(sourceAccountId,destinationAccountId);
		if (!accountsAreValid) {
			throw new MyResourceNotFoundException();
		}
		Double amount = createPayment.getAmount();
		BigDecimal transactionAmount = BigDecimal.valueOf(amount);
		if(sourceAccountId.equals(destinationAccountId)) {
			throw new RuntimeException();
		}
		
		boolean isValid = validateTransactionAmount(transactionAmount);
		if(!isValid) {
			throw new RuntimeException();
		}		
		Payment payment = new Payment(amount, sourceAccountId, destinationAccountId);
		payment.setState(PaymentState.CREATED);
		
		PAYMENTS.put(payment.getId(), payment);
		return payment;
	}

	@Override
	public Payment excecute(String paymentId) {
		Optional<Payment> optional = get(paymentId);
		if(!optional.isPresent()) {
			throw new MyResourceNotFoundException();
		}		
		Payment payment = optional.get();
		if(!payment.getState().equals(PaymentState.CREATED)) {
			throw new RuntimeException();
		}
		PaymentState state = accountService.transactionCreated(payment);
		payment.setState(state);
		PAYMENTS.put(paymentId, payment);
		
		return payment;
	}
	
	@Override
	public Payment cancel(String paymentId) {
		Optional<Payment> optional = get(paymentId);
		if(!optional.isPresent()) {
			throw new MyResourceNotFoundException();
		}		
		Payment payment = optional.get();
		if(!payment.getState().equals(PaymentState.CREATED)) {
			throw new RuntimeException();
		}
		payment.setState(PaymentState.CANCELED);
		PAYMENTS.put(paymentId, payment);
		
		return payment;
	}
	
	@Override
	public Optional<Payment> get(String id) {
		return Optional.ofNullable(PAYMENTS.get(id));
	}
	
	private boolean validateTransactionAmount(BigDecimal transactionAmount) {
		return	transactionAmount.compareTo(BigDecimal.ZERO) > -1;
	}

}
