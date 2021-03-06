/*
 * Copyright 2015-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.cli;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.facebook.buck.testutil.integration.TemporaryPaths;
import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TestDataHelper;
import com.google.common.base.Joiner;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class ExternalTestRunnerIntegrationTest {

  @Rule
  public TemporaryPaths tmp = new TemporaryPaths();

  private ProjectWorkspace workspace;

  @Before
  public void setUp() throws IOException {
    workspace =
        TestDataHelper.createProjectWorkspaceForScenario(
            this,
            "external_test_runner",
            tmp);
    workspace.setUp();
  }

  @Test
  public void runPass() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner.py"),
            "//:pass");
    result.assertSuccess();
    assertThat(
        result.getStdout(),
        is(equalTo("TESTS PASSED!\n")));
  }

  @Test
  public void runCoverage() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner_coverage.py"),
            "//dir:python-coverage");
    result.assertSuccess();
    assertThat(
        result.getStdout(),
        is(equalTo("[[0.0, [u'dir/simple.py']], " +
            "[0.75, [u'dir/also_simple.py', u'dir/simple.py']], " +
            "[1.0, [u'dir/also_simple.py']]]\n")));
  }

  @Test
  public void runFail() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner.py"),
            "//:fail");
    result.assertSuccess();
    assertThat(
        result.getStderr(),
        Matchers.endsWith("TESTS FAILED!\n"));
  }

  @Test
  public void extraArgs() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner_echo.py"),
            "//:pass",
            "--",
            "bobloblawlobslawbomb");
    result.assertSuccess();
    assertThat(
        result.getStdout().trim(),
        is(equalTo("bobloblawlobslawbomb")));
  }

  @Test
  public void runJavaTest() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner.py"),
            "//:simple");
    result.assertSuccess();
    assertThat(
        result.getStdout(),
        Matchers.matchesPattern(
            Joiner.on(System.lineSeparator()).join(
                "<\\?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"\\?>",
                "<testcase name=\"SimpleTest\" runner_capabilities=\"simple_test_selector\">",
                "  <test name=\"passingTest\" success=\"true\" suite=\"SimpleTest\" " +
                    "time=\"\\d*\" type=\"SUCCESS\">",
                "    <stdout>passed!",
                "</stdout>",
                "  </test>",
                "</testcase>",
                "<\\?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"\\?>",
                "<testcase name=\"SimpleTest2\" runner_capabilities=\"simple_test_selector\">",
                "  <test name=\"passingTest\" success=\"true\" suite=\"SimpleTest2\" " +
                    "time=\"\\d*\" type=\"SUCCESS\">",
                "    <stdout>passed!",
                "</stdout>",
                "  </test>",
                "</testcase>") + System.lineSeparator()));
  }

  @Test
  public void numberOfJobsIsPassedToExternalRunner() throws IOException {
    ProjectWorkspace.ProcessResult result =
        workspace.runBuckCommand(
            "test",
            "-c", "test.external_runner=" + workspace.getPath("test_runner_echo_jobs.py"),
            "//:pass",
            "-j", "13");
    result.assertSuccess();
    assertThat(
        result.getStdout().trim(),
        is(equalTo("13")));
  }

}
