package citystats.service;

import citystats.model.AnalysisResult;
import citystats.parser.AddressParseException;
import citystats.parser.AddressParser;
import citystats.parser.AddressParserFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileAnalyzer {
    private static final long NANOS_IN_MILLI = 1_000_000L;

    private final AddressParserFactory parserFactory;

    public FileAnalyzer(AddressParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public AnalysisResult analyze(Path file) throws AddressParseException {
        if (!Files.isRegularFile(file)) {
            throw new AddressParseException("Файл не найден: " + file);
        }
        long start = System.nanoTime();
        AddressParser parser = parserFactory.createFor(file);
        StatisticsCollector collector = new StatisticsCollector();
        parser.parse(file, collector);
        var duplicates = collector.getDuplicates();
        var floorsByCity = collector.getFloorsByCity();
        long elapsedMillis = (System.nanoTime() - start) / NANOS_IN_MILLI;
        return new AnalysisResult(duplicates, floorsByCity, elapsedMillis);
    }
}