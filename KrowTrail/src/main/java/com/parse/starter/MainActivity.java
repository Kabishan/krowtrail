package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    setTitle("Login");
    redirectUser();

    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  protected void redirectUser() {
    // Redirect user after logging in
    if (ParseUser.getCurrentUser() != null) {
      Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
      startActivity(intent);
    }
  }

  public void signUpLogin (View view) {
    EditText userNameEditText = findViewById(R.id.userNameEditText);
    EditText passwordEditText = findViewById(R.id.passwordEditText);

    final String userName = userNameEditText.getText().toString();
    final String password = passwordEditText.getText().toString();

    // Login logic
    ParseUser.logInInBackground(userName, password, new LogInCallback () {
      @Override
      public void done (ParseUser user, ParseException e) {

        if (e == null) {
          Log.i(userName, password);
          redirectUser();
          Log.i("Login", "Success!");
        } else {
          ParseUser newUser = new ParseUser();

          newUser.setUsername(userName);
          newUser.setPassword(password);

          newUser.signUpInBackground(new SignUpCallback() {
              @Override
              public void done (ParseException e) {
                if (e == null) {
                  redirectUser();
                  Log.i("Sign Up", "Success!");
                }
                else Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          });
        }
      }
    });
  }
}