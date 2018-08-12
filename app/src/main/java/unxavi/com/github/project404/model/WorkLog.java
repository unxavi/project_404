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

    private Task task;

    private @ServerTimestamp
    Date timestamp;

    public WorkLog() {
    }

    public WorkLog(int action, Task task) {
        this.action = action;
        this.task = task;
    }

    // TODO: 8/12/18 add location

    public int getAction() {
        return action;
    }

    public Task getTask() {
        return task;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
