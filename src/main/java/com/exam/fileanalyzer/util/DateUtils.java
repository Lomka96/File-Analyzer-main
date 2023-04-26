package com.exam.fileanalyzer.util;

import java.time.Month;
import java.time.Year;

public class DateUtils {

    private static final String FIRST_ZERO = "^0+(?!$)";

    public static boolean validateFileName(String fileName, String filePatternRegex) {
        if (!fileName.matches(filePatternRegex)) {
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
}
