package com.finreach.paymentservice.statistics.dto;

import java.util.List;

import com.finreach.paymentservice.util.MathUtils;

public class Statistic {
	private String accountId;
	private Double maxTrans;
	private Double minTrans;
	private Double avgTrans;

	public Statistic(List<Double> transactions, String accountId) {
		this.accountId = accountId;
		if (!transactions.isEmpty()) {
			double asDouble = transactions.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
			Double round = MathUtils.round(asDouble);
			this.setAvgTrans(round);
			this.setMaxTrans(transactions.stream().max(Double::compareTo).get());
			this.setMinTrans(transactions.stream().min(Double::compareTo).get());
		}
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Double getMaxTrans() {
		return maxTrans;
	}

	public void setMaxTrans(Double maxTrans) {
		this.maxTrans = maxTrans;
	}

	public Double getMinTrans() {
		return minTrans;
	}

	public void setMinTrans(Double minTrans) {
		this.minTrans = minTrans;
	}

	public Double getAvgTrans() {
		return avgTrans;
	}

	public void setAvgTrans(Double avgTrans) {
		this.avgTrans = avgTrans;
	}
}