package com.finreach.paymentservice.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finreach.paymentservice.statistics.Statistics;
import com.finreach.paymentservice.statistics.dto.Statistic;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {


    @Autowired
    private Statistics statisticsService;

    @GetMapping(path = "/{second}")
    public ResponseEntity<Map<String, Statistic>> get(@PathVariable("second") @Max(value = 10) Integer second) {
    	Map<String, Statistic> statisticsMap = statisticsService.getStatistics(second);
    	Map<String, Statistic> inOrderMap = 
    			statisticsMap.entrySet().stream()
    								.sorted(Map.Entry.comparingByKey())
    								.collect(Collectors.toMap(Map.Entry:: getKey, Map.Entry:: getValue,(entry1,entry2)-> entry2, LinkedHashMap:: new ));
    	return ResponseEntity.status(HttpStatus.OK).body(inOrderMap);
    }
}
