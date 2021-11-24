package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    ListView usersListView = (ListView) findViewById(R.id.usersListView);
    usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

    adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);

    usersListView.setAdapter(adapter);

    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;

        if (checkedTextView.isChecked()) Log.i("Info", "Checked");
        else Log.i("Info", "Not checked");
      }
    });

    ParseQuery<ParseUser> query = ParseUser.getQuery();

    query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

    query.findInBackground(new FindCallback<ParseUser>() {
      @Override
      public void done(List<ParseUser> objects, ParseException e) {
        if (e == null && objects.size() > 0) {
          for (ParseUser user : objects) users.add(user.getUsername());
          adapter.notifyDataSetChanged();
        }
      }
    });

  }
}
