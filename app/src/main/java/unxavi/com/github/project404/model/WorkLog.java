package unxavi.com.github.project404.model;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

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

    private Date timestamp;

    private Double latitude;

    private Double longitude;

    public WorkLog() {
    }

    public WorkLog(int action, Task task, @Nullable Location location) {
        this.action = action;
        this.task = task;
        this.timestamp = new Date();
        if (location != null){
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
