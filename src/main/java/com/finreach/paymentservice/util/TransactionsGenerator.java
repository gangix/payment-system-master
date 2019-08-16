package com.finreach.paymentservice.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.finreach.paymentservice.api.request.CreateAccount;
import com.finreach.paymentservice.domain.Account;
import com.finreach.paymentservice.statistics.dto.Statistic;
import com.finreach.paymentservice.store.AccountService;
import com.finreach.paymentservice.store.AccountServiceImpl;

@Component
public class TransactionsGenerator {
	private static final int ACCOUNTS = 100;
	private static final int DURATION = 10;
	private static final double MIN_AMOUNT = 10d;
	private static final double MAX_AMOUNT = 1000d;
	private static final double BALANCE = 1000000d;
	private static final int INITIAL_CAPACITY = 1000;

	private static List<String> genAccounts = new ArrayList<>();
	private Map<String, Statistic> genMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);
	private final AccountService accountService;

	public TransactionsGenerator() {
		accountService = new AccountServiceImpl();
	}

	public void generate() throws InterruptedException {
		generateAccounts();

		long start = System.currentTimeMillis();
		long later = start + TimeUnit.SECONDS.toMillis(DURATION);
		while (start < later) {
			start = System.currentTimeMillis();
			Thread.sleep(10);

			final RandomAccounts randomAccounts = randomAccounts();
			final double amount = randomAmount();
			accountService.transaction(randomAccounts.a1, -amount);
			accountService.transaction(randomAccounts.a2, amount);
		}
	}

	public Map<String, Statistic> calculate(int periodInSeconds) {
		Date when = Date.from(Instant.now().minusSeconds(periodInSeconds));
		accountService.all().forEach(account -> calculate(periodInSeconds, when, account));

		return new HashMap<>(genMap);
	}

	private void calculate(int periodInSeconds, Date when, Account account) {
		List<Double> filteredTransactions = account.getTransactions().stream()
				.filter(transaction -> within(transaction.getCreated(), when, periodInSeconds))
				.filter(transaction -> transaction.getAmount() > 0).map(t -> t.getAmount())
				.collect(Collectors.toList());

		if (filteredTransactions.isEmpty()) {
			return;
		}
		genMap.put(account.getId(), new Statistic(filteredTransactions, account.getId()));
	}

	private void generateAccounts() {
		for (int i = 0; i < ACCOUNTS; i++) {
			genAccounts.add(accountService.create(new CreateAccount(BALANCE)).getId());
		}
	}

	private double randomAmount() {
		return MathUtils.round(RandomUtils.nextDouble(MIN_AMOUNT, MAX_AMOUNT));
	}

	private RandomAccounts randomAccounts() {
		final int size = genAccounts.size();
		final String a1 = genAccounts.get(RandomUtils.nextInt(0, size));
		String a2 = genAccounts.get(RandomUtils.nextInt(0, size));
		while (a1.equals(a2)) {
			a2 = genAccounts.get(RandomUtils.nextInt(0, size));
		}

		return new RandomAccounts(a1, a2);
	}

	private class RandomAccounts {
		String a1;
		String a2;

		RandomAccounts(String a1, String a2) {
			this.a1 = a1;
			this.a2 = a2;
		}
	}

	private boolean within(LocalDateTime d1, Date d2, int seconds) {
		if (d2 == null) {
			return true;
		}
		ZonedDateTime zdt = d1.atZone(ZoneId.of("Europe/Berlin"));

		long diff = zdt.toInstant().toEpochMilli() - d2.toInstant().toEpochMilli();
		long millis = TimeUnit.SECONDS.toMillis(seconds);

		return diff >= 0 && diff < millis;
	}
}
