package citystats.model;

import java.util.Map;

public record AnalysisResult(
        Map<Address, Integer> duplicates,
        Map<String, int[]> floorsByCity,
        long elapsedMillis) {
}