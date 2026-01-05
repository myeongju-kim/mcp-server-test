package com.example.demo.git;

import com.example.demo.git.dto.GitExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class GitCommandRunnerTest {

    private GitCommandRunner gitCommandRunner;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        gitCommandRunner = new GitCommandRunner();
    }

    @Test
    void testGitInit() {
        // given
        String repoPath = tempDir.toString();

        // when
        GitExecResult result = gitCommandRunner.exec(repoPath, "init");

        // then
        assertThat(result.ok()).isTrue();
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(new File(repoPath, ".git")).exists();
    }

    @Test
    void testGitAddAndStatus() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");

        // 테스트 파일 생성
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // when - git add -A
        GitExecResult addResult = gitCommandRunner.exec(repoPath, "add", "-A");

        // then
        assertThat(addResult.ok()).isTrue();
        assertThat(addResult.exitCode()).isEqualTo(0);

        // git status로 확인
        GitExecResult statusResult = gitCommandRunner.exec(repoPath, "status", "--short");
        assertThat(statusResult.ok()).isTrue();
        assertThat(statusResult.stdout()).contains("test.txt");
    }

    @Test
    void testGitCommit() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");
        gitCommandRunner.exec(repoPath, "config", "user.email", "test@example.com");
        gitCommandRunner.exec(repoPath, "config", "user.name", "Test User");

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");
        gitCommandRunner.exec(repoPath, "add", "-A");

        // when
        GitExecResult commitResult = gitCommandRunner.exec(repoPath, "commit", "-m", "Initial commit");

        // then
        assertThat(commitResult.ok()).isTrue();
        assertThat(commitResult.exitCode()).isEqualTo(0);

        // git log로 확인
        GitExecResult logResult = gitCommandRunner.exec(repoPath, "log", "--oneline");
        assertThat(logResult.ok()).isTrue();
        assertThat(logResult.stdout()).contains("Initial commit");
    }

    @Test
    void testGitStatus() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");

        // when
        GitExecResult statusResult = gitCommandRunner.exec(repoPath, "status");

        // then
        assertThat(statusResult.ok()).isTrue();
        assertThat(statusResult.exitCode()).isEqualTo(0);
        assertThat(statusResult.stdout()).contains("test.txt");
    }

    @Test
    void testGitDiff() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");
        gitCommandRunner.exec(repoPath, "config", "user.email", "test@example.com");
        gitCommandRunner.exec(repoPath, "config", "user.name", "Test User");

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "original content");
        gitCommandRunner.exec(repoPath, "add", "-A");
        gitCommandRunner.exec(repoPath, "commit", "-m", "Initial commit");

        // 파일 수정
        Files.writeString(testFile, "modified content");

        // when
        GitExecResult diffResult = gitCommandRunner.exec(repoPath, "diff");

        // then
        assertThat(diffResult.ok()).isTrue();
        assertThat(diffResult.exitCode()).isEqualTo(0);
        assertThat(diffResult.stdout()).contains("modified content");
    }

    @Test
    void testGitCommandWithInvalidPath() {
        // given
        String invalidPath = "/invalid/path/that/does/not/exist";

        // when
        GitExecResult result = gitCommandRunner.exec(invalidPath, "status");

        // then
        assertThat(result.ok()).isFalse();
        assertThat(result.exitCode()).isNotEqualTo(0);
    }

    @Test
    void testGitCommandWithInvalidCommand() {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");

        // when
        GitExecResult result = gitCommandRunner.exec(repoPath, "invalid-command");

        // then
        assertThat(result.ok()).isFalse();
        assertThat(result.exitCode()).isNotEqualTo(0);
        assertThat(result.stderr()).isNotEmpty();
    }

    @Test
    void testGitAddMultipleFiles() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");

        // 여러 파일 생성
        Files.writeString(tempDir.resolve("file1.txt"), "content 1");
        Files.writeString(tempDir.resolve("file2.txt"), "content 2");
        Files.writeString(tempDir.resolve("file3.txt"), "content 3");

        // when - git add -A로 모든 파일 추가
        GitExecResult addResult = gitCommandRunner.exec(repoPath, "add", "-A");

        // then
        assertThat(addResult.ok()).isTrue();
        assertThat(addResult.exitCode()).isEqualTo(0);

        // status로 확인
        GitExecResult statusResult = gitCommandRunner.exec(repoPath, "status", "--short");
        assertThat(statusResult.ok()).isTrue();
        assertThat(statusResult.stdout()).contains("file1.txt", "file2.txt", "file3.txt");
    }

    @Test
    void testGitLog() throws IOException {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");
        gitCommandRunner.exec(repoPath, "config", "user.email", "test@example.com");
        gitCommandRunner.exec(repoPath, "config", "user.name", "Test User");

        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");
        gitCommandRunner.exec(repoPath, "add", "-A");
        gitCommandRunner.exec(repoPath, "commit", "-m", "Test commit message");

        // when
        GitExecResult logResult = gitCommandRunner.exec(repoPath, "log", "--oneline");

        // then
        assertThat(logResult.ok()).isTrue();
        assertThat(logResult.exitCode()).isEqualTo(0);
        assertThat(logResult.stdout()).contains("Test commit message");
    }

    @Test
    void testGitBranch() {
        // given
        String repoPath = tempDir.toString();
        gitCommandRunner.exec(repoPath, "init");

        // when
        GitExecResult branchResult = gitCommandRunner.exec(repoPath, "branch");

        // then
        assertThat(branchResult.ok()).isTrue();
        assertThat(branchResult.exitCode()).isEqualTo(0);
    }
}

