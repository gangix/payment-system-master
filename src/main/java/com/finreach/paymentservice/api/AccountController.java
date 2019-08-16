package com.finreach.paymentservice.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finreach.paymentservice.api.request.CreateAccount;
import com.finreach.paymentservice.domain.Account;
import com.finreach.paymentservice.store.AccountService;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping(path = "/account")
	public ResponseEntity<Account> create(@RequestBody CreateAccount request) {
		Account account = accountService.create(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(account);
	}
	
	@GetMapping(path = "/account/{id}")
	public ResponseEntity<Account> get(@PathVariable("id") String id) {
		final Optional<Account> accountOpt = accountService.get(id);
		if (!accountOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(accountOpt.get());
	}
}
