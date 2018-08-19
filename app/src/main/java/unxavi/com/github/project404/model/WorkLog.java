package unxavi.com.github.project404.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

import unxavi.com.github.project404.R;

@IgnoreExtraProperties
public class WorkLog implements Parcelable {

    public static final String WORK_LOG_TAG = "WORK_LOG_TAG";

    public static final String COLLECTION = "worklogs";

    public static final String FIELD_DATE = "timestamp";

    public static final int ACTION_START = 0;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RETURN = 2;
    public static final int ACTION_STOP = 3;

    private static final String START_WORKING = "Start working";
    private static final String BREAK = "Break";
    private static final String RETURN = "Return";
    private static final String FINISH_WORKING = "Finish working";

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
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }
    }

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

    public int getImageResource() {
        switch (getAction()) {
            case WorkLog.ACTION_START:
                return R.drawable.ic_play_arrow_black_24dp;
            case WorkLog.ACTION_PAUSE:
                return R.drawable.ic_pause_black_24dp;
            case WorkLog.ACTION_RETURN:
                return R.drawable.ic_replay_black_24dp;
            case WorkLog.ACTION_STOP:
                return R.drawable.ic_stop_black_24dp;
            default:
                return R.drawable.ic_play_arrow_black_24dp;
        }
    }

    public String getActionString() {
        switch (getAction()) {
            case WorkLog.ACTION_START:
                return START_WORKING;
            case WorkLog.ACTION_PAUSE:
                return BREAK;
            case WorkLog.ACTION_RETURN:
                return RETURN;
            case WorkLog.ACTION_STOP:
                return FINISH_WORKING;
            default:
                return START_WORKING;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeParcelable(this.task, flags);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
    }

    protected WorkLog(Parcel in) {
        this.action = in.readInt();
        this.task = in.readParcelable(Task.class.getClassLoader());
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<WorkLog> CREATOR = new Parcelable.Creator<WorkLog>() {
        @Override
        public WorkLog createFromParcel(Parcel source) {
            return new WorkLog(source);
        }

        @Override
        public WorkLog[] newArray(int size) {
            return new WorkLog[size];
        }
    };
}
