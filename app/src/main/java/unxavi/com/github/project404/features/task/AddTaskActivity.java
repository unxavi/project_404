package unxavi.com.github.project404.features.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.Task;

public class AddTaskActivity extends MvpActivity<AddTaskView, AddTaskPresenter> implements AddTaskView {

    public static final int RC_ADD_TASK = 9002;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.taskNameET)
    EditText taskNameET;

    @BindView(R.id.taskNameIL)
    TextInputLayout taskNameIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            createTask();
            return true;
        }
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createTask() {
        taskNameIL.setError(null);
        Editable taskNameETText = taskNameET.getText();
        if (TextUtils.isEmpty(taskNameETText)) {
            taskNameIL.setError(getString(R.string.field_required));
        } else {
            presenter.createTask(taskNameETText.toString());
        }
    }

    @NonNull
    @Override
    public AddTaskPresenter createPresenter() {
        return new AddTaskPresenter();
    }

    @Override
    public void taskCreated(Task task) {
        Intent data = new Intent();
        data.putExtra(Task.TASK_TAG, task);
        setResult(CommonStatusCodes.SUCCESS, data);
        finish();
    }
}
