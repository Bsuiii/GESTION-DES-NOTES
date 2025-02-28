package com.example.school_managment_system.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    // This method formats a java.sql.Date to yyyy-MM-dd
    public static Date dateFormatter(Date date) {
        // Format the java.sql.Date to a string with the desired format
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(date);

        // Convert the formatted string back to a java.sql.Date and return it
        return java.sql.Date.valueOf(formattedDate);
    }
}
