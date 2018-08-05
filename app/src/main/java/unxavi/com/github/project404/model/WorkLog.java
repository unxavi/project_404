package unxavi.com.github.project404.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class WorkLog {

    public static final String COLLECTION = "worklogs";

    public static final String FIELD_DATE = "timestamp";

    private @ServerTimestamp
    Date timestamp;

    public WorkLog() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
