package jdk9.changes;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static jdk9.changes.SummarizeSince9Helpers.JDK9_SINCE9_REPORT_PATH;
import static jdk9.changes.SummarizeSince9Helpers.extractClassInfo;
import static jdk9.changes.SummarizeSince9Helpers.extractPackageName;
import static jdk9.changes.SummarizeSince9Helpers.isClassLine;
import static jdk9.changes.SummarizeSince9Helpers.isPackageLine;

/**
 * Summarize the number of "@since 9" occurrences by class in the JDK 9 javadoc using an imperative style.
 * <p>
 * Reads from the file at path {@link SummarizeSince9Helpers#JDK9_SINCE9_REPORT_PATH}.
 *
 * @see SummarizeSince9ByClassFunctional
 */
public class SummarizeSince9ByClassImperative {

    public static void main(String[] args) {

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

        summarizeImperative();
    }

    static void summarizeImperative() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(JDK9_SINCE9_REPORT_PATH));
            Map<String, List<String>> packageToClassInfos = new LinkedHashMap<>();
            String currentPackage = null;
            for (String line : lines) {
                if (isPackageLine(line)) {
                    String packageName = extractPackageName(line);
                    packageToClassInfos.put(packageName, new ArrayList<>());
                    currentPackage = packageName;
                } else if (isClassLine(line)) {
                    String classInfo = extractClassInfo(line);
                    packageToClassInfos.get(currentPackage).add(classInfo);
                }
            }

            for (Map.Entry<String, List<String>> entry : packageToClassInfos.entrySet()) {
                for (String classInfo : entry.getValue()) {
                    String outputLine = entry.getKey() + "," + classInfo;
                    System.out.println(outputLine);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
