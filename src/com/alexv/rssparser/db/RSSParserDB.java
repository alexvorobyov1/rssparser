package com.alexv.rssparser.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alexv.rssparser.dto.PostDTO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RSSParserDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rssparser";
    private static final int DATABASE_VERSION = 1;
    private static final String POSTS_TABLE = "posts";
    private static final String CREATE_TABLE_POSTS = "CREATE TABLE " + POSTS_TABLE 
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, url TEXT NOT NULL, "
            + "date TEXT NOT NULL);";
    private static final String SELECT_ALL_POSTS = "SELECT * FROM " + POSTS_TABLE + ";";

    public RSSParserDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_POSTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE);
        onCreate(db);
    }
    
    public void addPost(String title, String url, Date date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);
        values.put("date", date.toString());
        db.insert(POSTS_TABLE, null, values);
        db.close();
    }

    public List<PostDTO> getPosts() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<PostDTO> posts = new ArrayList<PostDTO>();
        Cursor cursor = db.rawQuery(SELECT_ALL_POSTS, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    PostDTO post = new PostDTO();
                    post.setTitle(cursor.getString(1));
                    post.setUrl(cursor.getString(2));
                    post.setDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
                                .parse(cursor.getString(3)));          
                    posts.add(post);
                } catch (ParseException e) {
                    Log.e("Rss Parser", e.toString());
                }      
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return posts;
    }
    
    public void deletePosts() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(POSTS_TABLE, null, null);
        db.close();
    }
    
}
