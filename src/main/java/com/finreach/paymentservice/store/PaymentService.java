package com.finreach.paymentservice.store;

import java.util.Optional;

import com.finreach.paymentservice.api.request.CreatePayment;
import com.finreach.paymentservice.domain.Payment;

public interface PaymentService {
	Payment create(CreatePayment createPayment);
	Payment excecute(String id);
	Payment cancel(String id);
	Optional<Payment> get(String trxId);
}