package com.exam.fileanalyzer.util;

import java.time.Month;
import java.time.Year;

public class DateUtils {

    private static final String FIRST_ZERO = "^0+(?!$)";
    private static final String FILE_PATTERN_REGEX = "logs_\\d{4}-\\d{2}-\\d{2}-access\\.log";
    private static final int START_YEAR_INDEX = 5;
    private static final int LAST_YEAR_INDEX = 9;
    private static final int START_MONTH_INDEX = 10;
    private static final int LAST_MONTH_INDEX = 12;
    private static final int START_DAY_INDEX = 13;
    private static final int LAST_DAY_INDEX = 15;

    public static boolean validateFileName(String fileName) {
        if (!fileName.matches(FILE_PATTERN_REGEX)) {
            return false;
        }
        int year = Integer.parseInt(fileName.substring(START_YEAR_INDEX, LAST_YEAR_INDEX));
        int month = Integer.parseInt(fileName.substring(START_MONTH_INDEX, LAST_MONTH_INDEX)
                .replaceFirst(FIRST_ZERO, ""));
        int day = Integer.parseInt(fileName.substring(START_DAY_INDEX, LAST_DAY_INDEX)
                .replaceFirst(FIRST_ZERO, ""));
        return isValidDate(year, month, day);
    }

    public static boolean isValidDate(int year, int month, int day) {
        if (month < 1 || month > 12) {
            return false;
        }
        return day >= 1 && day <= Month.of(month).length(Year.isLeap(year));
    }
}
