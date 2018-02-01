package com.kenyrim.MyRss.rss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kenyrim.MyRss.R;
import com.kenyrim.MyRss.db_main.ListData;
import com.kenyrim.MyRss.db_result.DBResult;
import com.kenyrim.MyRss.db_result.RssData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bunny on 01.02.2018.
 */

public class RssShort extends AppCompatActivity {


    private static final String TAG = "AAAAAAAAAAAAAAAAAAAAAAA";

    private ListView listResult;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<RssData> feedModels;
    RssShort.RssFeedListAdapter adapter;
    private long RssItemId;
    String feedName;
    String feedTitle;
    String feedLink;
    String feedDescription;
    DBResult dbResult;

    int UPDATE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short);

        listResult = findViewById(R.id.listResult);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        dbResult = new DBResult(this);

        update();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });

        this.listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(RssShort.this, RssActivity.class);
                    RssData rssData = dbResult.select(id);
                    if (rssData == null) {
                        Toast.makeText(getBaseContext(), "NOOOOOOOOO", Toast.LENGTH_SHORT).show();
                    } else
                        intent.putExtra("ListData", rssData);
                    startActivityForResult(intent, UPDATE_ACTIVITY);

            }
        });
    }

    public void update(){
        if(!isOnline(RssShort.this)){
            Log.e(TAG, "Нет интернета");
            ListData listData = (ListData) getIntent().getSerializableExtra("ListData");
            String res = listData.getUrl();
            Log.e(TAG, res);
            ArrayList<RssData> allFeeds = dbResult.selectItem(res);
            RssShort.RssFeedListAdapter mAdapter = new RssShort.RssFeedListAdapter(this, allFeeds);
            listResult.setAdapter(mAdapter);

            swipeRefreshLayout.setRefreshing(false);
        }else {
            Log.e(TAG, "Есть интернет");
            new RssShort.FetchFeedTask().execute((Void) null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            RssData rssData = (RssData) data.getExtras().getSerializable("ListData");
            if (requestCode == UPDATE_ACTIVITY){
                dbResult.update(rssData);
            }
            else{
                dbResult.insert(rssData);
            }
            updateList();
        }
    }
    private void updateList() {
        adapter.setArrayListData(dbResult.selectAll());
        adapter.notifyDataSetChanged();
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    public ArrayList<RssData> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        ArrayList<RssData> items = new ArrayList<>();
        DBResult dbResult = null;
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            dbResult = new DBResult(RssShort.this);

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }


                if (title != null && description != null && link != null ) {
                    if(isItem) {
                        ListData listData = (ListData) getIntent().getSerializableExtra("ListData");
                        RssData item = new RssData(RssItemId, listData.getUrl(), title, description, link);
                        items.add(item);
                        dbResult.insert(item);
                    }
                    else {
                        feedTitle = title;
                        feedDescription = description;
                        feedLink = link;

                    }

                    title = null;
                    description = null;
                    link = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }

    class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        ListData listData = (ListData) getIntent().getSerializableExtra("ListData");

        String result = listData.getUrl();

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            feedTitle = null;
            feedDescription = null;
            feedLink = null;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            if (TextUtils.isEmpty(result))
                return false;
            try {
                URL url = new URL(result);
                InputStream inputStream = url.openConnection().getInputStream();
                feedModels = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            swipeRefreshLayout.setRefreshing(false);

            if (success) {
                ListData listData = (ListData) getIntent().getSerializableExtra("ListData");
                String res = listData.getUrl();
                Log.e(TAG, res);
                ArrayList<RssData> allFeeds = dbResult.selectItem(res);
                RssShort.this.setTitle(listData.getTitle());
                listResult.setAdapter(new RssShort.RssFeedListAdapter(RssShort.this, allFeeds));


            } else {
                Toast.makeText(RssShort.this,
                        "Ошибка подключения!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class RssFeedListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;


        public RssFeedListAdapter(Context context, ArrayList<RssData> rssFeedModels2) {

            layoutInflater = LayoutInflater.from(context);
            setArrayListData(rssFeedModels2);
        }
        public void setArrayListData(ArrayList<RssData> arrayListData){
            feedModels = arrayListData;
        }

        @Override
        public int getCount() {
            return feedModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            RssData rssFeed = feedModels.get(position);
            if (rssFeed != null){
                return rssFeed.getId();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.item_rss_feed,null);

            TextView txtTitle = convertView.findViewById(R.id.titleText);

            RssData rssData = feedModels.get(position);
            txtTitle.setText(rssData.getTitle());

            return convertView;
        }

    }

}

