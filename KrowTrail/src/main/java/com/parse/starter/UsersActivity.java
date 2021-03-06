package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
  ArrayList<String> users = new ArrayList<>();
  ArrayAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_users);
    setTitle("Users");

    ActionBar actionBar = getSupportActionBar();

    actionBar.setDisplayHomeAsUpEnabled(true);

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

  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if (menuItem.getItemId() == android.R.id.home) {
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    return super.onOptionsItemSelected(menuItem);
  }
}
