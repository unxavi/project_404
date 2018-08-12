package unxavi.com.github.project404.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class User {

    public static final String COLLECTION = "users";

    public static final String FIELD_DATE = "timestamp";

    private @ServerTimestamp
    Date timestamp;

    public User() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
