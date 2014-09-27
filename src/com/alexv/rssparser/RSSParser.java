package com.alexv.rssparser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

import com.alexv.rssparser.db.RSSParserDB;
import com.alexv.rssparser.dto.PostDTO;

public class RSSParser extends DefaultHandler {
    
    // Data source
    public static final String DATA_SOURCE_STRING = "http://www.npr.org/rss/rss.php?id=1006";
    private static URL DATA_SOURCE;
    
    // Used to define what elements we are currently in
    private boolean inItem = false;
    private boolean inTitle = false;
    private boolean inLink = false;
    private boolean inDate = false;

    // PostDTO for temporary storage
    private PostDTO currentPost = new PostDTO();

    private RSSParserDB db = null;
    
    private StringBuilder sb;
    
    public RSSParser() {
        try {
            DATA_SOURCE = new URL(DATA_SOURCE_STRING);
        } catch (MalformedURLException e) {
            Log.e("Rss Parser", e.toString());
        }
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
        sb = new StringBuilder();
        
        if (name.trim().equals("item")) {
            inItem = true;
        } else if (name.trim().equals("title")) {
            inTitle = true;
        } else if (name.trim().equals("link")) {
            inLink = true;
        } else if (name.trim().equals("pubDate")) {
            inDate = true;
        }
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        if (name.trim().equals("item")) {
            inItem = false;
        } else if (name.trim().equals("title")) {
            inTitle = false;
        } else if (name.trim().equals("link")) {
            inLink = false;
        } else if (name.trim().equals("pubDate")) {
            inDate = false;
        }

        // Check if current post is complete
        if (currentPost.getTitle() != null && currentPost.getUrl() != null && 
            currentPost.getDate() != null) {
            db.addPost(currentPost.getTitle(), currentPost.getUrl(), currentPost.getDate());
            currentPost.setTitle(null);
            currentPost.setUrl(null);
            currentPost.setDate(null);
        }           
    }
    
    @Override
    public void characters(char ch[], int start, int length) {
        if (sb != null) {
            for (int i = start; i < start + length; i++) {
                sb.append(ch[i]);
            }
        }
        
        if (inItem) {
            try {
                if (inTitle) {
                    currentPost.setTitle(sb.toString());
                }
                if (inLink) {
                    currentPost.setUrl(sb.toString());
                }
                if (inDate) {
                    currentPost.setDate(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                                        .parse(sb.toString()));
                }   
            } catch (ParseException e) {
                Log.e("RssParser", e.toString());
            }
        }
    }

    public void updatePosts(Context context) {
        db = new RSSParserDB(context);
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(new InputSource(DATA_SOURCE.openStream()));
        } catch (ParserConfigurationException e) {
            Log.e("Rss Parser", e.toString());
        } catch (SAXException e) {
            Log.e("Rss Parser", e.toString());
        } catch (IOException e) {
            Log.e("Rss Parser", e.toString());
        }
    }
    
}
