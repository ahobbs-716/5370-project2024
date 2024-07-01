package edu.upenn.cis573.project;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ChangePasswordActivity extends AppCompatActivity {


    private DataManager dataManager = new DataManager(new WebClient("10.0.2.2", 3001));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }


    public void onChangePasswordButtonClick(View view) {


        EditText passwordCurrentField = findViewById(R.id.passwordCurrentField);
        String passwordCurrent = passwordCurrentField.getText().toString();


        EditText passwordNewField = findViewById(R.id.passwordNewField);
        String passwordNew = passwordNewField.getText().toString();


        EditText passwordNewRepeatField = findViewById(R.id.passwordNewRepeatField);
        String passwordNewRepeat = passwordNewRepeatField.getText().toString();


        Contributor contributor = MainActivity.contributor;
        if (contributor == null) {
            Toast.makeText(this, "An Error Occurred! Please restart the app and try again", Toast.LENGTH_LONG).show();
            return;
        }


        if (!passwordNewRepeat.equals(passwordNew)) {
            Toast.makeText(this, "Please check both inputs of the new password are the same", Toast.LENGTH_LONG).show();
            return;
        }


        try {
            dataManager.updatePassword(contributor.getId(), passwordCurrent, passwordNew);
            Intent i = new Intent(this, MenuActivity.class);


            startActivity(i);


        } catch (IllegalStateException | IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

