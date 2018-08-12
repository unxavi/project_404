package unxavi.com.github.project404.model;

import android.text.TextUtils;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Task {

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
}
