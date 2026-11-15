package citystats.ui;

import citystats.model.AnalysisResult;
import citystats.parser.AddressParseException;
import citystats.service.FileAnalyzer;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public class ConsoleUI {
    private static final String EXIT_COMMAND = "exit";

    private final Scanner scanner = new Scanner(System.in);
    private final FileAnalyzer analyzer;
    private final ResultPrinter printer;

    public ConsoleUI(FileAnalyzer analyzer, ResultPrinter printer) {
        this.analyzer = analyzer;
        this.printer = printer;
    }

    public void run() {
        while (true) {
            System.out.print("\nВведите путь к файлу .csv или .xml (" + EXIT_COMMAND
                    + " или Ctrl+D для выхода): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase(EXIT_COMMAND)) {
                break;
            }
            if (input.isEmpty()) {
                System.out.println("Путь не может быть пустым.");
                continue;
            }
            process(input);
        }
        System.out.println("Работа завершена.");
    }

    private void process(String input) {
        try {
            Path path = Path.of(removeQuotes(input));
            AnalysisResult result = analyzer.analyze(path);
            System.out.print(printer.format(result));
        } catch (InvalidPathException e) {
            System.out.println("Некорректный путь: " + input);
        } catch (AddressParseException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (OutOfMemoryError e) {
            System.out.println("Не хватило памяти. Запустите программу с параметром -Xmx2g или больше.");
        } catch (RuntimeException e) {
            System.out.println("Непредвиденная ошибка: " + e);
        }
    }

    private String removeQuotes(String path) {
        if (path.length() >= 2 && path.startsWith("\"") && path.endsWith("\"")) {
            return path.substring(1, path.length() - 1);
        }
        return path;
    }
}