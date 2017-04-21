package jdk9.changes;

import one.util.streamex.StreamEx;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static jdk9.changes.SummarizeSince9Helpers.JDK9_SINCE9_REPORT_PATH;
import static jdk9.changes.SummarizeSince9Helpers.isClassLine;
import static jdk9.changes.SummarizeSince9Helpers.isPackageLine;

/**
 * Summarize the number of "@since 9" occurrences by class in the JDK 9 javadoc using a functional style.
 * <p>
 * Reads from the file at path {@link SummarizeSince9Helpers#JDK9_SINCE9_REPORT_PATH}.
 *
 * @see SummarizeSince9ByClassImperative
 */
public class SummarizeSince9ByClassFunctional {

    public static void main(String[] args) throws IOException {

        //
        // stream: P1, C1, P2, C2, C3, P4, C4, P5, C5, C6, P6...
        //         |----|  |---------| |----|  |--------|  |-...
        //
        // need to compare successive values in the stream, and start new accumulation
        // when package changes, e.g. groups from above would be:
        // (P1, C1)
        // (P2, C2, C3)
        // (P4, C4)
        // (P5, C5, C6)

        summarizeFunctional();
    }

    /**
     * @implNote Uses {@link StreamEx#groupRuns(BiPredicate)} from the StreamEx (Enhancing Java 8 Streams)
     * library ( found at https://github.com/amaembo/streamex ) in order to determine the package for each
     * class, since the file is organized in a hierarchical fashion with each package occurring only one time
     * following by one to many classes within that package.
     */
    static void summarizeFunctional() {
        try {
            Stream<String> filtered = Files
                    .lines(Paths.get(JDK9_SINCE9_REPORT_PATH))
                    .filter(line -> isPackageLine(line) || isClassLine(line));

            StreamEx.of(filtered)
                    .groupRuns(SummarizeSince9Helpers::samePackage)
                    .flatMap(SummarizeSince9Helpers::toClassInfoStream)
                    .forEach(System.out::println);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
