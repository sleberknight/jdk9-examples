package jdk9.changes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static jdk9.changes.SummarizeSince9Helpers.JDK9_SINCE9_REPORT_PATH;
import static jdk9.changes.SummarizeSince9Helpers.PACKAGE_PATTERN_REGEX;

/**
 * Summarize the number of "@since 9" occurrences by package in the JDK 9 javadoc.
 * <p>
 * Reads from the file at path {@link SummarizeSince9Helpers#JDK9_SINCE9_REPORT_PATH}.
 */
public class SummarizeSince9ByPackage {

    private SummarizeSince9ByPackage() {
    }

    public static void main(String[] args) throws IOException {

        try (Stream<String> lines = Files.lines(Paths.get(JDK9_SINCE9_REPORT_PATH))) {
            lines
                    .filter(line -> line.matches(PACKAGE_PATTERN_REGEX))
                    .map(String::trim)
                    .map(line -> line.replaceAll("[\\(|\\)]|( usage found)|( usages found)", ""))
                    .map(line -> line.replaceAll("  ", ","))
                    .forEach(System.out::println);
        }
    }

}
