package com.example.scanbrogpstrackerapp.utilities;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtilities {

    // This is a utility method for getting the current date and time in a format that the API likes.
    public static String getCurrentDateTimeWithExpectedFormat() {

        String EXPECTED_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        DateTimeFormatter API_TIMESTAMP_FORMATTER
                = DateTimeFormatter.ofPattern(EXPECTED_TIMESTAMP_PATTERN);
        String dateWithExpectedPattern = API_TIMESTAMP_FORMATTER.format(ZonedDateTime.now());

        return dateWithExpectedPattern;
    }

}
