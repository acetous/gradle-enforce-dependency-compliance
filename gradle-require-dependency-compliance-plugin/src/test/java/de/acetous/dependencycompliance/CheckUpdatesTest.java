package de.acetous.dependencycompliance;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckUpdatesTest extends AbstractTest {

    @Before
    public void setup() throws IOException {
        copyFile("check-updates/testcase.gradle", "build.gradle");
    }

    @Test
    public void shouldSucceedWithGeneratedReport() {
        createGradleRunner().withArguments("dependencyComplianceExport", "--stacktrace").build();
        BuildResult result = createGradleRunner().withArguments("dependencyComplianceCheck", "--stacktrace").build();
        assertThat(result.task(":dependencyComplianceCheck").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }

    @Test
    public void shouldReportExistingVersions() throws IOException {
        copyFile("check-updates/report-old.json", "dependency-compliance-report.json");
        BuildResult result = createGradleRunner().withArguments("dependencyComplianceCheck", "--stacktrace").buildAndFail();
        assertThat(result.task(":dependencyComplianceCheck").getOutcome()).isEqualTo(TaskOutcome.FAILED);
        assertThat(result.getOutput()).contains("Dependencies are not listed in dependency compliance export.");
        assertThat(result.getOutput()).contains("com.google.code.gson:gson:2.8.5 - existing versions: 1.2.3");
        assertThat(result.getOutput()).contains("commons-io:commons-io:2.4 - existing versions: 2.3");
        assertTaskFailSummary(result);
    }

    private void assertTaskFailSummary(BuildResult result) {
        assertThat(result.getOutput()).contains("Build contains violating dependencies or repositories.");
    }
}
