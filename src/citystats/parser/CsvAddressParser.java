package citystats.parser;

import citystats.model.Address;
import citystats.util.StringPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class CsvAddressParser implements AddressParser {
    private static final char DELIMITER = ';';
    private static final char QUOTE = '"';
    private static final int COLUMN_COUNT = 4;
    private static final int CITY_COLUMN = 0;
    private static final int STREET_COLUMN = 1;
    private static final int HOUSE_COLUMN = 2;
    private static final int FLOOR_COLUMN = 3;

    @Override
    public void parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        StringPool pool = new StringPool();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            reader.readLine();
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isBlank()) {
                    consumer.accept(toAddress(line, lineNumber, pool));
                }
            }
        } catch (IOException e) {
            throw new AddressParseException("Не удалось прочитать CSV-файл: " + e.getMessage(), e);
        }
    }

    private Address toAddress(String line, int lineNumber, StringPool pool) throws AddressParseException {
        String[] fields = split(line, lineNumber);
        try {
            return new Address(
                    pool.get(fields[CITY_COLUMN]),
                    pool.get(fields[STREET_COLUMN]),
                    Integer.parseInt(fields[HOUSE_COLUMN].trim()),
                    Integer.parseInt(fields[FLOOR_COLUMN].trim()));
        } catch (NumberFormatException e) {
            throw new AddressParseException("Строка " + lineNumber + ": дом и этаж должны быть числами", e);
        }
    }

    private String[] split(String line, int lineNumber) throws AddressParseException {
        String[] fields = new String[COLUMN_COUNT];
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == QUOTE) {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == QUOTE) {
                    current.append(QUOTE);
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == DELIMITER && !inQuotes) {
                if (count >= COLUMN_COUNT - 1) {
                    throw wrongColumnCount(lineNumber);
                }
                fields[count++] = current.toString();
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (count != COLUMN_COUNT - 1) {
            throw wrongColumnCount(lineNumber);
        }
        fields[count] = current.toString();
        return fields;
    }

    private AddressParseException wrongColumnCount(int lineNumber) {
        return new AddressParseException("Строка " + lineNumber + ": ожидалось " + COLUMN_COUNT + " столбца");
    }
}