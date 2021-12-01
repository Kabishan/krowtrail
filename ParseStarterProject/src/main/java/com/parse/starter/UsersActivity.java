package com.parse.starter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
  ArrayList<String> users = new ArrayList<>();
  ArrayAdapter adapter;

  // Adding menu for making a trail
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = new MenuInflater(this);
    menuInflater.inflate(R.menu.krow_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  // Responding to menu options
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.trail) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Start a trail");
      final EditText trailEditText = new EditText(this);
      builder.setView(trailEditText);

      // Start a trail
      builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          ParseObject trail = new ParseObject("Trail");
          trail.put("trail", trailEditText.getText().toString());
          trail.put("username", ParseUser.getCurrentUser().getUsername());

          // Save the trail object
          trail.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
              if (e == null) {
                Toast.makeText(UsersActivity.this, "Trail started", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(UsersActivity.this, "Trail failed", Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      });

      // Erase the trail
      builder.setNegativeButton("Erase", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Log.i("Info", "Trail erased");
        }
      });

      builder.show();

    } else if (item.getItemId() == R.id.logout) {
      // Logout and redirect back to sign in
      ParseUser.logOut();

      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(intent);
    } else if (item.getItemId() == R.id.feed) {
      // Go to Feed
      Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
      startActivity(intent);
    }


    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_users);
    setTitle("Users");

    // Displaying checkmarks if user is followed

    final ListView usersListView = findViewById(R.id.usersListView);
    usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
    usersListView.setAdapter(adapter);

    // Logic for following or un-following

    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;

        // Follow logic
        if (checkedTextView.isChecked()) {
          Log.i("Info", "Checked");
          ParseUser.getCurrentUser().add("isFollowing", users.get(position));
        }
        // Un-follow logic
        else {
          Log.i("Info", "Not checked");
          ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
          List remainingUsers = ParseUser.getCurrentUser().getList("isFollowing");
          ParseUser.getCurrentUser().remove("isFollowing");
          ParseUser.getCurrentUser().put("isFollowing", remainingUsers);
        }

        ParseUser.getCurrentUser().saveInBackground();
      }
    });

    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

    // Displaying checkmarks
    query.findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> objects, ParseException e) {
        if (e == null && objects.size() > 0) {
          for (ParseUser user : objects) users.add(user.getUsername());
          adapter.notifyDataSetChanged();

          for (String username : users) {
            if (ParseUser.getCurrentUser().getList("isFollowing").contains(username)) {
              usersListView.setItemChecked(users.indexOf(username), true);
            }
          }
        }
      }
    });

  }
}
