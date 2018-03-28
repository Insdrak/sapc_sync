package com.motivtelecom.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OracleDateFormatter {

    private static String dfDateFormatPattern = "dd.MM.yyyy HH:mm:ss";

    public static String formatWithDf(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat(dfDateFormatPattern);
        return dateFormat.format(date);
    }
    @SuppressWarnings("unused")
    public static Date parseWithDf(String in_string_date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(dfDateFormatPattern);
            return dateFormat.parse(in_string_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
