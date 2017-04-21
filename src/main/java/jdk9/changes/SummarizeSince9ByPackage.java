package jdk9.changes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Summarize the number of "@since 9" occurrences by package in the JDK 9 javadoc.
 * <p>
 * Reads from the file at path {@link SummarizeSince9Helpers#JDK9_SINCE9_REPORT_PATH}.
 */
public class SummarizeSince9ByPackage {

    private static final String PACKAGE_PATTERN = " {12,12}[a-z].*";

    private SummarizeSince9ByPackage() {
    }

    public static void main(String[] args) throws IOException {

        Files.lines(Paths.get("etc/jdk9-since9-report.txt"))
                .filter(line -> line.matches(PACKAGE_PATTERN))
                .map(String::trim)
                .map(line -> line.replaceAll("[\\(|\\)]|( usage found)|( usages found)", ""))
                .map(line -> line.replaceAll("  ", ","))
                .forEach(System.out::println);
    }

}
