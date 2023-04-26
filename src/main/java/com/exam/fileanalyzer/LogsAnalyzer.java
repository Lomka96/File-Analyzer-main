package com.exam.fileanalyzer;

import com.exam.fileanalyzer.util.DateUtils;
import com.exam.fileanalyzer.util.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipFile;

@Service
public class LogsAnalyzer {
    private static final String DATE = "yyyy-MM-dd";
    private static final String FILE_PATTERN_REGEX = "logs_\\d{4}-\\d{2}-\\d{2}-access\\.log";
    private static final Path DIR_PATH = Paths.get("src/test/resources/logs-27_02_2018-03_03_2018.zip");

    public static Map<String, Integer> countEntriesInZipFile(
            String searchQuery, File zipFile, LocalDate startDate, Integer numberOfDays)
            throws IOException {
        if (searchQuery.isBlank() || zipFile == null || startDate == null || numberOfDays == null || numberOfDays < 0) {
            throw new IllegalArgumentException();
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        LocalDate endDate = startDate.plusDays(numberOfDays);
        try (ZipFile zip = new ZipFile(zipFile)) {
            zip.stream().forEach(entry -> {
                String fileName = entry.getName();
                if (validateFileName(fileName)) {
                    try {
                        FileUtils.extractFilesFromZip(zip, DIR_PATH);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String fileDateStr = fileName.substring(5, 15);
                    LocalDate fileDate = LocalDate.parse(fileDateStr, DateTimeFormatter.ofPattern(DATE));
                    if (fileDate.isAfter(startDate.minusDays(1)) && fileDate.isBefore(endDate)) {
                        int count = 0;
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                if (line.contains(searchQuery)) {
                                    count++;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        result.put(fileName, count);
                    }
                }
            });
        }
        return result;
    }

    public static boolean validateFileName(String fileName) {
        return DateUtils.validateFileName(fileName, FILE_PATTERN_REGEX);
    }
}
