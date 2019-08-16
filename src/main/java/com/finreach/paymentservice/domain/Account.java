package com.finreach.paymentservice.domain;

import java.util.HashSet;
import java.util.Set;

public class Account {
	private String id;
	private Double balance = 0d;
	private Set<Transaction> transactions = new HashSet<>();

	public Account() {
		this.id = String.valueOf(System.nanoTime());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}

	public void updateBalance(Double amount) {
		balance = Double.sum(balance, amount);
	}

	@Override
	public String toString() {
		return id;
	}

}
