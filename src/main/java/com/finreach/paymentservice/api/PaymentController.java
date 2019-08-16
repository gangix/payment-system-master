package com.finreach.paymentservice.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finreach.paymentservice.api.request.CreatePayment;
import com.finreach.paymentservice.domain.Payment;
import com.finreach.paymentservice.store.PaymentService;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(path = "/payment")
	public ResponseEntity<Payment> create(@RequestBody CreatePayment createPaymentRequest) {
		Payment payment = paymentService.create(createPaymentRequest);

		return ResponseEntity.status(HttpStatus.CREATED).body(payment);
	}

	@GetMapping(path = "/payment/{id}")
	public ResponseEntity<Payment> get(@PathVariable("id") String id) {
		Optional<Payment> paymentOptinal = paymentService.get(id);
		if (!paymentOptinal.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(paymentOptinal.get());
	}

	@PutMapping(path = "/payment/execute/{id}")
	public ResponseEntity<Payment> excecute(@PathVariable("id") String id) {
		Payment payment = paymentService.excecute(id);

		return ResponseEntity.ok(payment);
	}

	@PutMapping(path = "/payment/cancel/{id}")
	public ResponseEntity<Payment> cancel(@PathVariable("id") String id) {
		Payment payment = paymentService.cancel(id);

		return ResponseEntity.ok(payment);
	}

}
