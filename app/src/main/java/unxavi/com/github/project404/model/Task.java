package unxavi.com.github.project404.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Task implements Parcelable {

    public static final String COLLECTION = "tasks";

    public static final String FIELD_NAME_LOWERCASE = "nameLowerCase";

    private String name;

    private String nameLowerCase;

    public Task() {
    }

    public Task(String name) {
        this.name = name;
        if (!TextUtils.isEmpty(name)) {
            this.nameLowerCase = name.toLowerCase();
        }
    }

    public String getName() {
        return name;
    }

    public String getNameLowerCase() {
        return nameLowerCase;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.nameLowerCase);
    }

    protected Task(Parcel in) {
        this.name = in.readString();
        this.nameLowerCase = in.readString();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
