package com.exam.fileanalyzer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class FileAnalyzerApplicationTests {
    Map<String, Integer> result = new HashMap<>();
    Path path = Paths.get("src/test/resources/logs-27_02_2018-03_03_2018.zip");
    File zipPath = path.toFile();

    @Test
    public void testCountEntriesInZipFile() throws IOException {
        result = LogsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, LocalDate.of(2018, 2, 27), 3);
        Map<String, Integer> expected = Map.of("logs_2018-02-27-access.log", 40, "logs_2018-02-28-access.log", 18, "logs_2018-03-01-access.log", 23);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testCountEntriesInZipFileWhenNoMatchSearchQuery() throws IOException {
        result = LogsAnalyzer.countEntriesInZipFile("abcdef11", zipPath, LocalDate.of(2018, 2, 27), 3);
        Map<String, Integer> expected = Map.of("logs_2018-02-27-access.log", 0, "logs_2018-02-28-access.log", 0, "logs_2018-03-01-access.log", 0);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testCountEntriesInZipFileWhenFilesNotFound() throws IOException {
        result = LogsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, LocalDate.of(2020, 2, 27), 3);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testValidateFileNameWithInvalidFileName() {
        Assertions.assertFalse(LogsAnalyzer.validateFileName("logs_2018-02-29-access.log"));
        Assertions.assertTrue(LogsAnalyzer.validateFileName("logs_2020-02-29-access.log"));
        Assertions.assertTrue(LogsAnalyzer.validateFileName("logs_2018-01-31-access.log"));
        Assertions.assertTrue(LogsAnalyzer.validateFileName("logs_2018-08-31-access.log"));
        Assertions.assertFalse(LogsAnalyzer.validateFileName("logs_2018-09-31-access.log"));
        Assertions.assertFalse(LogsAnalyzer.validateFileName("logs_2018-13-31-access.log"));
        Assertions.assertFalse(LogsAnalyzer.validateFileName("log_2018-13-31-access.log"));
        Assertions.assertFalse(LogsAnalyzer.validateFileName("logs_2018-01-31-access"));
    }

    @Test
    public void testCountEntriesInZipFileWithNull() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> LogsAnalyzer.countEntriesInZipFile("", zipPath, LocalDate.of(2018, 2, 27), 3));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> LogsAnalyzer.countEntriesInZipFile("Mozilla", null, LocalDate.of(2018, 2, 27), 3));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> LogsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, null, 3));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> LogsAnalyzer.countEntriesInZipFile("Mozilla", zipPath, LocalDate.of(2018, 2, 27), null));
    }
}
