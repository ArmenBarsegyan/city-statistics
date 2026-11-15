package citystats.parser;

import citystats.model.Address;

import java.nio.file.Path;
import java.util.function.Consumer;


public interface AddressParser {
    void parse(Path file, Consumer<Address> consumer) throws AddressParseException;
}