package citystats.service;

import citystats.model.Address;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class StatisticsCollector implements Consumer<Address> {
    public static final int MIN_FLOOR = 1;
    public static final int MAX_FLOOR = 5;

    private final Map<Address, Integer> occurrences = new HashMap<>();
    private final Map<String, int[]> floorsByCity = new HashMap<>();

    @Override
    public void accept(Address address) {
        int count = occurrences.merge(address, 1, Integer::sum);
        if (count == 1) {
            countFloor(address);
        }
    }

    public Map<Address, Integer> getDuplicates() {
        Map<Address, Integer> duplicates = new LinkedHashMap<>();
        occurrences.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<Address, Integer>comparingByValue().reversed())
                .forEach(entry -> duplicates.put(entry.getKey(), entry.getValue()));
        return duplicates;
    }

    public Map<String, int[]> getFloorsByCity() {
        return new TreeMap<>(floorsByCity);
    }

    private void countFloor(Address address) {
        int[] floors = floorsByCity.computeIfAbsent(address.city(), city -> new int[MAX_FLOOR]);
        if (address.floor() >= MIN_FLOOR && address.floor() <= MAX_FLOOR) {
            floors[address.floor() - MIN_FLOOR]++;
        }
    }
}