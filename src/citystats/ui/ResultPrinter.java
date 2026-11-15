package citystats.ui;

import citystats.model.Address;
import citystats.model.AnalysisResult;
import citystats.service.StatisticsCollector;

import java.util.Map;

public class ResultPrinter {
    public String format(AnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        appendDuplicates(sb, result.duplicates());
        appendFloors(sb, result.floorsByCity());
        sb.append("\nВремя обработки: ").append(result.elapsedMillis()).append(" мс\n");
        return sb.toString();
    }

    private void appendDuplicates(StringBuilder sb, Map<Address, Integer> duplicates) {
        sb.append("\n=== Дублирующиеся записи ===\n");
        if (duplicates.isEmpty()) {
            sb.append("Дубликатов нет.\n");
            return;
        }
        for (Map.Entry<Address, Integer> entry : duplicates.entrySet()) {
            Address address = entry.getKey();
            sb.append(address.city()).append(", ")
                    .append(address.street()).append(", д. ")
                    .append(address.house()).append(", этажей: ")
                    .append(address.floor()).append(" - повторов: ")
                    .append(entry.getValue()).append('\n');
        }
    }

    private void appendFloors(StringBuilder sb, Map<String, int[]> floorsByCity) {
        sb.append("\n=== Количество зданий по этажности ===\n");
        for (Map.Entry<String, int[]> entry : floorsByCity.entrySet()) {
            sb.append(entry.getKey()).append(':');
            int[] counts = entry.getValue();
            for (int i = 0; i < counts.length; i++) {
                sb.append(' ').append(StatisticsCollector.MIN_FLOOR + i)
                        .append("-эт.: ").append(counts[i]);
                if (i < counts.length - 1) {
                    sb.append(',');
                }
            }
            sb.append('\n');
        }
    }
}