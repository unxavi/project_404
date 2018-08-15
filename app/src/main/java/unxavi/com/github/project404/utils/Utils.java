package unxavi.com.github.project404.utils;

import android.support.annotation.DrawableRes;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.WorkLog;

public class Utils {

    public static String dateToString(Date date, Locale locale) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", locale);
            return sdf.format(date);
        } else {
            return "";
        }
    }
}
