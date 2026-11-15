package citystats;

import citystats.parser.AddressParserFactory;
import citystats.service.FileAnalyzer;
import citystats.ui.ConsoleUI;
import citystats.ui.ResultPrinter;

public class Main {
    public static void main(String[] args) {
        new ConsoleUI(new FileAnalyzer(new AddressParserFactory()), new ResultPrinter()).run();
    }
}