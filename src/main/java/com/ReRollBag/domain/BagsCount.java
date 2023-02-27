package com.ReRollBag.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class BagsCount {
    private final Map<String, Long> bagsCountMap = new HashMap<>();

    public boolean isExistWithRegion(String region) {
        return bagsCountMap.containsKey(region);
    }

    public Long getLastIndexWithRegion(String region) {
        return bagsCountMap.get(region);
    }

    public void increaseLastIndexWithRegion(String region) {
        bagsCountMap.put(region, getLastIndexWithRegion(region) + 1);
    }

    public void saveNewRegion(String region) {
        bagsCountMap.put(region, 0L);
    }
}
