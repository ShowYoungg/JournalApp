/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();

    // Fields for views
    EditText mHeaderText;
    EditText mDescription;
    Button mAddButton;
    AppDatabase mDb;

    private int mTaskId = DEFAULT_TASK_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        savedJournal();

        // COMPLETED (4) Initialize member variable for the data base
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mAddButton.setText("Update");
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final TaskEntry task = mDb.taskDao().loadTaskById(mTaskId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateUI(task);
                            }
                        });

                    }
                });

            }
        }

    }

    private void savedJournal(){
        mHeaderText = findViewById(R.id.heading) ;
        mDescription = findViewById(R.id.description) ;
        mAddButton = findViewById(R.id.add_journal) ;

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }
        mHeaderText.setText(task.getHeader());
        mDescription.setText(task.getDescription());

    }

    private void onSaveButtonClicked() {
        String header = mHeaderText.getText().toString();
        String description = mDescription.getText().toString();

        // COMPLETED (4) Make taskEntry final so it is visible inside the run method
        final TaskEntry taskEntry = new TaskEntry(header, description);
        // COMPLETED (2) Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    // COMPLETED (3) Move the remaining logic inside the run method
                    mDb.taskDao().insertTask(taskEntry);
                } else {
                    taskEntry.setId(mTaskId);
                    mDb.taskDao().updateTask(taskEntry);
                }
                finish();
            }
        });
    }
}