package iam.bookme.config;

import java.time.format.DateTimeFormatter;

/**
 * This class contains constant values used throughout the application.
 */
@SuppressWarnings("unused")
public class AppConstants {

    private  AppConstants() {
        // Private constructor to prevent instantiation!!
        // Explicitly declaring private constructor to prevent Java from adding a default public constructor
    }

    /// This pattern (XXX) includes the 3-digit zone offset (e.g. +05:30 for India Standard Time).
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    public static final String CACHE_BOOKINGS = "bookings";
}
