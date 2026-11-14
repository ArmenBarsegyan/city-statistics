package citystats.parser;

import citystats.model.Address;
import citystats.util.StringPool;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;


public class XmlAddressParser implements AddressParser {
    private static final String ITEM_TAG = "item";
    private static final String CITY_ATTRIBUTE = "city";
    private static final String STREET_ATTRIBUTE = "street";
    private static final String HOUSE_ATTRIBUTE = "house";
    private static final String FLOOR_ATTRIBUTE = "floor";

    @Override
    public void parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        StringPool pool = new StringPool();
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(file))) {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            try {
                while (reader.hasNext()) {
                    if (reader.next() == XMLStreamConstants.START_ELEMENT
                            && ITEM_TAG.equals(reader.getLocalName())) {
                        consumer.accept(readAddress(reader, pool));
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException | XMLStreamException e) {
            throw new AddressParseException("Не удалось прочитать XML-файл: " + e.getMessage(), e);
        }
    }

    private Address readAddress(XMLStreamReader reader, StringPool pool) throws AddressParseException {
        int line = reader.getLocation().getLineNumber();
        try {
            return new Address(
                    pool.get(requireAttribute(reader, CITY_ATTRIBUTE, line)),
                    pool.get(requireAttribute(reader, STREET_ATTRIBUTE, line)),
                    Integer.parseInt(requireAttribute(reader, HOUSE_ATTRIBUTE, line).trim()),
                    Integer.parseInt(requireAttribute(reader, FLOOR_ATTRIBUTE, line).trim()));
        } catch (NumberFormatException e) {
            throw new AddressParseException("Строка " + line + ": дом и этаж должны быть числами", e);
        }
    }

    private String requireAttribute(XMLStreamReader reader, String name, int line) throws AddressParseException {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            throw new AddressParseException("Строка " + line + ": нет атрибута " + name);
        }
        return value;
    }
}