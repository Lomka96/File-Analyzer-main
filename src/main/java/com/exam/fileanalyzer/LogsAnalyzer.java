package com.exam.fileanalyzer;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipFile;

@Service
public class LogsAnalyzer {
    private static final String DATE = "yyyy-MM-dd";
    private static final String FILE_PATTERN_REGEX = "logs_\\d{4}-\\d{2}-\\d{2}-access\\.log";
    private static final String FIRST_ZERO = "^0+(?!$)";
    private static final Path DIR_PATH = Path.of("C:\\Users\\79627\\Documents\\GitHub\\File-Analyzer-main\\src\\test\\resources\\tempDir");

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
                        extractFilesFromZip(zip, DIR_PATH);
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
        if (!fileName.matches(FILE_PATTERN_REGEX)) {
            return false;
        }
        int year = Integer.parseInt(fileName.substring(5, 9));
        int month = Integer.parseInt(fileName.substring(10, 12).replaceFirst(FIRST_ZERO, ""));
        int day = Integer.parseInt(fileName.substring(13, 15).replaceFirst(FIRST_ZERO, ""));
        return isValidDate(year, month, day);
    }

    public static boolean isValidDate(int year, int month, int day) {
        if (month < 1 || month > 12) {
            return false;
        }
        return day >= 1 && day <= Month.of(month).length(Year.isLeap(year));
    }

    private static void extractFilesFromZip(ZipFile zip, Path tempDir) throws IOException {
        zip.stream().forEach(entry -> {
            Path outputFile = tempDir.resolve(entry.getName());
            try (InputStream inputStream = zip.getInputStream(entry);
                 FileOutputStream outputStream = new FileOutputStream(outputFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
