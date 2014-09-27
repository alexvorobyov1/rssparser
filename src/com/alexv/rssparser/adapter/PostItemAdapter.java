package com.alexv.rssparser.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.alexv.rssparser.R;
import com.alexv.rssparser.dto.PostDTO;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PostItemAdapter extends ArrayAdapter<PostDTO> {

    private Activity context;
    private List<PostDTO> posts;
    
    public PostItemAdapter(Context context, int textViewResourceId, List<PostDTO> posts) {
        super(context, textViewResourceId, posts);
        this.context = (Activity) context;
        this.posts = posts;
    }
    
    static class ViewHolder {
        TextView postTitleView;
        TextView postLinkView;
        TextView postDateView;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.postitem, null);
        
            viewHolder = new ViewHolder();
            viewHolder.postTitleView = (TextView) convertView.findViewById(R.id.postTitleLabel);
            viewHolder.postLinkView = (TextView) convertView.findViewById(R.id.postLinkLabel);
            viewHolder.postDateView = (TextView) convertView.findViewById(R.id.postDateLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            
        viewHolder.postTitleView.setText(posts.get(position).getTitle());
        viewHolder.postLinkView.setText(posts.get(position).getUrl());
        viewHolder.postDateView.setText(new SimpleDateFormat("EEE, MMMM d, yyyy, HH:mm:ss", Locale.ENGLISH)
                                        .format(posts.get(position).getDate()));
        
        return convertView;     
    }
        
}