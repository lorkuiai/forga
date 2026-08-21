package com.luokuiai.forga.core.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StandaloneBoundaryTest {

  private static final List<String> MODULES =
      List.of(
          "forga-core",
          "forga-query",
          "forga-mybatis",
          "forga-sa-token",
          "forga-spring-security",
          "forga-spring-boot-starter",
          "forga-scope",
          "forga-spring-web");

  private static final List<String> FORBIDDEN_CORE_IMPORT_PREFIXES =
      List.of(
          "org.springframework.",
          "cn.dev33.satoken.",
          "org.mybatis.",
          "java.sql.",
          "javax.sql.",
          "jakarta.persistence.");

  @Test
  void coreDoesNotDependOnFrameworkPersistenceOrHostPackages() throws IOException {
    assertThat(forbiddenCoreImports()).isEmpty();
  }

  private static List<String> forbiddenCoreImports() throws IOException {
    Path coreMain = rootDir().resolve("forga-core/src/main/java");
    if (Files.notExists(coreMain)) {
      return List.of();
    }
    try (Stream<Path> paths = Files.walk(coreMain)) {
      return paths
          .filter(path -> path.toString().endsWith(".java"))
          .flatMap(StandaloneBoundaryTest::forbiddenImportViolations)
          .sorted()
          .toList();
    }
  }

  private static Stream<String> forbiddenImportViolations(Path source) {
    String relativeSource = rootDir().relativize(source).toString();
    return read(source)
        .lines()
        .map(String::trim)
        .filter(line -> line.startsWith("import "))
        .filter(StandaloneBoundaryTest::isForbiddenCoreImport)
        .map(line -> relativeSource + " has forbidden import " + line);
  }

  private static boolean isForbiddenCoreImport(String importLine) {
    String importedType =
        importLine
            .replace("import static ", "")
            .replace("import ", "")
            .replace(";", "")
            .trim();
    return FORBIDDEN_CORE_IMPORT_PREFIXES.stream().anyMatch(importedType::startsWith);
  }

  private static String read(Path path) {
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to read " + path, exception);
    }
  }

  private static Path rootDir() {
    Path userDir = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
    if (MODULES.stream().anyMatch(module -> userDir.endsWith(module))) {
      return userDir.getParent();
    }
    return userDir;
  }
}
