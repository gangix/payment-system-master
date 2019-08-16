package com.finreach.paymentservice.domain;

public class Payment {
	private String id;
	private Double amount;
	private String sourceAccountId;
	private String destinationAccountId;
	private PaymentState state;

	public Payment(Double amount, String sourceAccountId, String destinationAccountId) {
		this.id = String.valueOf(System.nanoTime());
		this.amount = amount;
		this.sourceAccountId = sourceAccountId;
		this.destinationAccountId = destinationAccountId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSourceAccountId() {
		return sourceAccountId;
	}

	public void setSourceAccountId(String sourceAccountId) {
		this.sourceAccountId = sourceAccountId;
	}

	public String getDestinationAccountId() {
		return destinationAccountId;
	}

	public void setDestinationAccountId(String destinationAccountId) {
		this.destinationAccountId = destinationAccountId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PaymentState getState() {
		return state;
	}

	public void setState(PaymentState state) {
		this.state = state;
	}

}
