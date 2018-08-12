package unxavi.com.github.project404.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class WorkLog {

    public static final String COLLECTION = "worklogs";

    public static final String FIELD_DATE = "timestamp";

    public static final int ACTION_START = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RETURN = 2;
    public static final int ACTION_STOP = 3;

    private int action;

    private @ServerTimestamp
    Date timestamp;

    public WorkLog() {
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
