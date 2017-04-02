package models;

import com.sun.istack.internal.Nullable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shrralis on 3/14/17.
 */
public class DateWorker {
    private static SimpleDateFormat simpleDateTimeFormatter;
    private static SimpleDateFormat simpleDateFormatter;

    static {
        simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public static String convertDateToString(Date src) {
        return simpleDateFormatter.format(src);
    }

    public static String convertDateToString(DatePicker src) {
        return simpleDateFormatter.format(convertToDate(src));
    }
    @Nullable
    public static Date convertToDate(String src) {
        Date result = null;

        try {
            result = simpleDateFormatter.parse(src);
        } catch (ParseException ignored) {}
        return result;
    }

    public static Date convertToDate(DatePicker src) {
        return Date.from(src.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String convertDateTimeToString(Date src) {
        return simpleDateTimeFormatter.format(src);
    }

    public static String convertDateTimeToString(DatePicker src) {
        return simpleDateTimeFormatter.format(convertToDate(src));
    }
    @Nullable
    public static Date convertToDateTime(String src) {
        Date result = null;

        try {
            result = simpleDateTimeFormatter.parse(src);
        } catch (ParseException ignored) {}
        return result;
    }

    public static Time convertToTime(String src) {
        Time result = null;

        try {
            result = Time.valueOf(src);
        } catch (IllegalArgumentException ignored) {}
        return result;
    }

    public static Time convertToTime(TextField src) {
        return convertToTime(src.getText().trim());
    }
}
