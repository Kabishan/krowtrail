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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

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
                Toast.makeText(FeedActivity.this, "Trail started", Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(FeedActivity.this, "Trail failed", Toast.LENGTH_SHORT).show();
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
    } else if (item.getItemId() == R.id.users) {
      // Go to Feed
      Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
      startActivity(intent);
    }


    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    setTitle("Feed");

    final ListView listView = findViewById(R.id.listView);

    // Sending query for retrieving feed from followed users ordered by the date of creation
    ParseQuery<ParseObject> trailQuery = ParseQuery.getQuery("Trail");
    trailQuery.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
    trailQuery.orderByDescending("createdAt");
    trailQuery.setLimit(20);

    final List<Map<String, String>> trailData = new ArrayList<>();

    trailQuery.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> objects, ParseException e) {
        if (e == null) {
          for (ParseObject trail : objects) {
            Map<String, String> trailInfo = new HashMap<>();

            trailInfo.put("content", trail.getString("trail"));
            trailInfo.put("username", trail.getString("username"));
            trailData.add(trailInfo);
          }

          SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this, trailData, android.R.layout.simple_expandable_list_item_2, new String[] { "content", "username" }, new int[] { android.R.id.text1, android.R.id.text2 });

          listView.setAdapter(simpleAdapter);
        }
      }
    });
  }
}
