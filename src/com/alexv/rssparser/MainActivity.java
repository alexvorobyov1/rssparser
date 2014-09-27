package com.alexv.rssparser;

import java.util.ArrayList;
import java.util.List;

import com.alexv.rssparser.adapter.PostItemAdapter;
import com.alexv.rssparser.db.RSSParserDB;
import com.alexv.rssparser.dto.PostDTO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private List<PostDTO> listData;
    private ProgressDialog pDialog;
    private ListView listView;
    private RSSParser rp;
    RSSParserDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);
        listView = (ListView) this.findViewById(R.id.postListView);
        /**
         * Calling a background thread which will cache
         * all posts in SQLite database, populate listData
         * and update listView with posts
         * */
        new PostsLoader().execute();
  
        listView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(), DisplayWebPageActivity.class);
                String postLinkLabel = ((TextView) view.findViewById(R.id.postLinkLabel)).getText().toString();
                Toast.makeText(getApplicationContext(), postLinkLabel, Toast.LENGTH_SHORT).show();
                in.putExtra("postLinkLabel", postLinkLabel);
                startActivity(in);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Background AsyncTask to get RSS data from URL
     * */
    class PostsLoader extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading posts...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * Caching all posts in SQLite and populating listData
         * with posts
         * */
        @Override
        protected String doInBackground(String... args) {
            db = new RSSParserDB(MainActivity.this);
            db.deletePosts();
            
            rp = new RSSParser();
            rp.updatePosts(MainActivity.this);
            
            listData = new ArrayList<PostDTO>();
            listData = db.getPosts();
            
            return null;
        }
 
        /**
         * After completing background task dismiss the progress dialog
         * and update list view with posts
         * **/
        protected void onPostExecute(String args) {
            PostItemAdapter itemAdapter = new PostItemAdapter(MainActivity.this, R.layout.postitem, listData);
            listView.setAdapter(itemAdapter);
            registerForContextMenu(listView);
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
 
    }
    
}