package unxavi.com.github.project404.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String dateToString(Date date, Locale locale) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy", locale);
            return sdf.format(date);
        }else{
            return "";
        }
    }
}
