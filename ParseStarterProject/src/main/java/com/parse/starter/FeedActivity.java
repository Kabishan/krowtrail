package com.parse.starter;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
    setTitle("Feed");

    ActionBar actionBar = getSupportActionBar();

    actionBar.setDisplayHomeAsUpEnabled(true);

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

  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case android.R.id.home:
        this.finish();
        return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }
}
