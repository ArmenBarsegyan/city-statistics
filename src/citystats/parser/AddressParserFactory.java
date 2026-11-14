package citystats.parser;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

public class AddressParserFactory {
    private static final Map<String, Supplier<AddressParser>> PARSERS = Map.of(
            "csv", CsvAddressParser::new,
            "xml", XmlAddressParser::new);

    public AddressParser createFor(Path file) throws AddressParseException {
        String name = file.getFileName().toString().toLowerCase();
        int dotIndex = name.lastIndexOf('.');
        String extension = dotIndex < 0 ? "" : name.substring(dotIndex + 1);
        Supplier<AddressParser> supplier = PARSERS.get(extension);
        if (supplier == null) {
            throw new AddressParseException("Поддерживаются только файлы .csv и .xml");
        }
        return supplier.get();
    }
}