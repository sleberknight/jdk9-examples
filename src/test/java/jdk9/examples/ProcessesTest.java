package jdk9.examples;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to exercise the new JDK 9 process API improvements.
 */
public class ProcessesTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetPid() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/sleep", "5");
        Process proc = builder.start();
        assertThat(proc.getPid()).isGreaterThan(0);
    }

    @Test
    public void testGetProcessInfo() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/sleep", "5");
        Process proc = builder.start();
        ProcessHandle.Info info = proc.info();
        softly.assertThat(info.arguments().orElse(new String[]{})).containsExactly("5");
        softly.assertThat(info.command().orElse(null)).isEqualTo("/bin/sleep");
        softly.assertThat(info.commandLine().orElse(null)).isEqualTo("/bin/sleep 5");
        softly.assertThat(info.user().orElse(null)).isEqualTo(System.getProperty("user.name"));
        softly.assertThat(info.startInstant().orElse(null)).isLessThanOrEqualTo(Instant.now());
    }

    @Test
    public void testGetProcessHandleForExistingProcess() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/sleep", "5");
        Process proc = builder.start();
        long pid = proc.getPid();

        ProcessHandle handle = ProcessHandle.of(pid).orElseThrow(IllegalStateException::new);
        softly.assertThat(handle.getPid()).isEqualTo(pid);
        softly.assertThat(handle.info().commandLine().orElse(null)).isEqualTo("/bin/sleep 5");
    }

    @Test
    public void testAllProcesses() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/sleep", "5");
        builder.start();

        String sleep = ProcessHandle.allProcesses()
                .map(handle -> handle.info().command().orElse(String.valueOf(handle.getPid())))
                .filter(cmd -> cmd.equals("/bin/sleep"))
                .findFirst()
                .orElse(null);
        assertThat(sleep).isNotNull();
    }

    @Test
    public void testParent() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/sleep", "5");
        Process proc = builder.start();
        ProcessHandle parent = proc.toHandle().parent().orElse(null);
        assertThat(parent).isNotNull();
        long currentPid = ProcessHandle.current().getPid();
        assertThat(parent.getPid()).isEqualTo(currentPid);
    }

    @Test
    public void testOnExit_ComparingIdenticalFiles_WhenIdentical()
            throws ExecutionException, InterruptedException, IOException, TimeoutException, URISyntaxException {

        String file1 = filePathForResource("file1.txt");
        String file2 = filePathForResource("file2.txt");

        Process proc = new ProcessBuilder("/usr/bin/cmp", file1, file2).start();
        Future<Boolean> areIdentical = proc.onExit().thenApply(proc1 -> proc1.exitValue() == 0);
        softly.assertThat(areIdentical.get(1, TimeUnit.SECONDS)).isTrue();
        softly.assertThat(areIdentical.isDone()).isTrue();
        softly.assertThat(areIdentical.isCancelled()).isFalse();
    }

    @Test
    public void testOnExit_ComparingIdenticalFiles_WhenNotIdentical()
            throws ExecutionException, InterruptedException, IOException, TimeoutException {

        String file1 = filePathForResource("file1.txt");
        String file3 = filePathForResource("file3.txt");

        Process proc = new ProcessBuilder("/usr/bin/cmp", file1, file3).start();
        Future<Boolean> areNotIdentical = proc.onExit().thenApply(proc1 -> proc1.exitValue() == 1);
        softly.assertThat(areNotIdentical.get(1, TimeUnit.SECONDS)).isTrue();
        softly.assertThat(areNotIdentical.isDone()).isTrue();
        softly.assertThat(areNotIdentical.isCancelled()).isFalse();
    }

    private String filePathForResource(String resourceName) {
        Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourceName));
        return resource.map(URL::getFile).orElseThrow(IllegalStateException::new);
    }

}
