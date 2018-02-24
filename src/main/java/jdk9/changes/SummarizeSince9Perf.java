package jdk9.changes;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

/**
 * Performs a simple and naive performance test for imperative vs. functional styles of finding "@since 9"
 * occurrences in the file at path {@link SummarizeSince9Helpers#JDK9_SINCE9_REPORT_PATH}.
 *
 * @implNote I would have used the {@link System#console()} except that it always returns null when running
 * from within an IDE (IntelliJ and Eclipse both return null, probably NetBeans too).
 */
public class SummarizeSince9Perf {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int trials = promptForTrials(reader);
        int style = promptForStyle(reader);

        switch (style) {
            case 1:
                measure(trials, SummarizeSince9ByClassImperative::summarizeImperative);
                break;

            case 2:
                measure(trials, SummarizeSince9ByClassFunctional::summarizeFunctional);
                break;

            default:
                throw new IllegalArgumentException("Invalid style: " + style);
        }
    }

    private static int promptForTrials(BufferedReader reader) throws IOException {
        System.out.print("Enter number of trials [100]:");
        String line = Strings.emptyToNull(reader.readLine());
        return isNull(line) ? 100 : Integer.parseInt(line);
    }

    private static int promptForStyle(BufferedReader reader) throws IOException {
        System.out.println("Choose style:");
        System.out.println("  1. Imperative");
        System.out.println("  2. Functional");
        System.out.println();
        System.out.print("Choice [1]: ");
        String line = Strings.emptyToNull(reader.readLine());
        return isNull(line) ? 1 : Integer.parseInt(line);
    }

    private static void measure(int trials, Runnable runnable) {
        long startTime = System.nanoTime();
        for (int i = 0; i < trials; i++) {
            runnable.run();
        }

        long elapsedNanos = System.nanoTime() - startTime;
        double elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
        double avgElapsedMillis = elapsedMillis / trials;
        System.out.println("Average Elapsed Milliseconds = " + avgElapsedMillis);
    }

}
