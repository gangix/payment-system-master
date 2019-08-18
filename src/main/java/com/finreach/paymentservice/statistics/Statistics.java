package com.finreach.paymentservice.statistics;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.finreach.paymentservice.domain.Account;
import com.finreach.paymentservice.domain.Transaction;
import com.finreach.paymentservice.statistics.dto.Statistic;
import com.finreach.paymentservice.store.AccountService;

@Service
public class Statistics {
	private static final int INITIAL_CAPACITY = 10;
	private static final int INITIAL_CAPACITY_SUB = 100;
	private static final long DURATION_MILIS = 10;
	private static final long INITIAL_DELAY = 10_000;

	private static Map<Integer, Map<String,Statistic>> STATS = new ConcurrentHashMap<>(INITIAL_CAPACITY);
	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	@Autowired
	private AccountService accountService;
	
	
	@Scheduled(initialDelay = INITIAL_DELAY, fixedRate = DURATION_MILIS)
	public void prepareStatistics() {
		List<Account> accounts = accountService.all();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				for (int i = 1; i < 11; i++) {
					Map<String,Statistic> secondStatistic = new ConcurrentHashMap<>(INITIAL_CAPACITY_SUB);
					int secondFor = i;
					for (Account account : accounts) {
						List<Double> transactionList = account.getTransactions().stream().filter(trx -> filterTimeAndAmount(trx, secondFor))
								.map(value -> value.getAmount()).collect(Collectors.toList());
						if(transactionList.isEmpty()) {
							continue;
						}
						secondStatistic.put(account.getId(), new Statistic(transactionList, account.getId()));
					}
					if(secondStatistic.isEmpty()) {
						if(STATS.containsKey(secondFor)) {
							STATS.get(secondFor).clear();
						}
						continue;
					}
					STATS.put(secondFor, secondStatistic);
				}
			}
		});
	}

	public static void setStatistics(Map<String, Statistic> statistics, Integer second) {
		STATS.put(second, statistics);
	}

	boolean filterTimeAndAmount(Transaction trx, Integer second) {
		LocalDateTime dt = trx.getCreated();
		ZonedDateTime zdt = dt.atZone(ZoneId.of("Europe/Berlin"));
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime zNow = now.atZone(ZoneId.of("Europe/Berlin"));
		final long diff = zNow.toInstant().toEpochMilli() - zdt.toInstant().toEpochMilli();
		long millis = TimeUnit.SECONDS.toMillis(second);
	
		return trx.getAmount()>0 && diff <= millis;
	}
	public Map<String, Statistic> getStatistics(Integer second) {
		return new HashMap<>(STATS).get(second);
	}

	public void add(Statistic statistic, int periodInSeconds) {
		Map<String, Statistic> accountMap = new HashMap<>();
		accountMap.put(statistic.getAccountId(), statistic);
		STATS.put(periodInSeconds, accountMap);
	}
}
