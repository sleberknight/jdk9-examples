package jdk9.changes;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Static helpers for {@link SummarizeSince9ByClassFunctional} and {@link SummarizeSince9ByClassImperative}.
 */
class SummarizeSince9Helpers {

    static final String JDK9_SINCE9_REPORT_PATH = "etc/jdk9ea-since9-report.txt";

    static final String PACKAGE_PATTERN_REGEX = " {16,16}[a-z].*";

    static final Pattern PACKAGE_PATTERN = Pattern.compile(PACKAGE_PATTERN_REGEX);

    private static final String CLASS_PATTERN_REGEX = " {20,20}[A-Za-z\\-]+\\.java.*";

    private static final Pattern CLASS_PATTERN = Pattern.compile(CLASS_PATTERN_REGEX);

    private SummarizeSince9Helpers() {
    }

    /**
     * Two adjacent lines in the file are in the same package if:
     * a package line followed by class line; or
     * adjacent class lines
     */
    static boolean samePackage(String line1, String line2) {
        return (isPackageLine(line1) && isClassLine(line2))
                || (isClassLine(line1) && isClassLine(line2));
    }

    static boolean isPackageLine(String line) {
        return PACKAGE_PATTERN.matcher(line).matches();
    }

    static boolean isClassLine(String line) {
        return CLASS_PATTERN.matcher(line).matches();
    }

    /**
     * A run looks like: {@code P, C1, C2, ..., CN}
     */
    static Stream<String> toClassInfoStream(List<String> run) {
        String packageName = extractPackageName(run.get(0));
        List<String> classLines = run.subList(1, run.size());
        return toClassInfoStream(packageName, classLines);
    }

    /**
     * A package line in the file looks like: {@code <the.package.name> (n usage[s] found)}
     */
    static String extractPackageName(String line) {
        return line.trim().split(" ")[0];
    }

    /**
     * Output format: {@code P,C,N}
     * <p>
     * Where P = package name, C = class name, N = number of usages
     */
    private static Stream<String> toClassInfoStream(String packageName, List<String> classLines) {
        return classLines.stream()
                .map(classLine ->
                        String.format("%s,%s", packageName, extractClassInfo(classLine)));
    }

    /**
     * A class line in the file looks like: {@code <ClassName>.java  (n usage[s] found)}
     * <p>
     * This reduces each such line to : {@code <classname>,<# occurrences>, e.g. Optional,3}
     */
    static String extractClassInfo(String line) {
        return line.trim()
                .replaceAll("[\\(|\\)]|( usage found)|( usages found)", "")
                .replaceAll("  ", ",")
                .replaceAll(".java", "");
    }

}
