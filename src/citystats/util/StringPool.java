package citystats.util;

import java.util.HashMap;
import java.util.Map;

public class StringPool {
    private final Map<String, String> pool = new HashMap<>();

    public String get(String value) {
        String existing = pool.putIfAbsent(value, value);
        return existing == null ? value : existing;
    }
}