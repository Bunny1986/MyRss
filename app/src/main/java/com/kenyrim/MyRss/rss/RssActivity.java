package com.kenyrim.MyRss.rss;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kenyrim.MyRss.R;
import com.kenyrim.MyRss.db_result.DBResult;
import com.kenyrim.MyRss.db_result.RssData;

public class RssActivity extends AppCompatActivity {

    private long RssItemId;
    DBResult dbResult;
    int UPDATE_ACTIVITY = 1;
    Button btnLink;
    TextView textRssTitle, textRssDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss);

        textRssTitle = findViewById(R.id.titleRssText);
        textRssDescription = findViewById(R.id.descriptionRssText);
        dbResult = new DBResult(this);
        final RssData rssData = (RssData) getIntent().getSerializableExtra("ListData");
        String title = rssData.getTitle();
        String description = rssData.getDescription();
        RssItemId = rssData.getId();

        textRssTitle.setText(title);
        textRssDescription.setText(description);

        btnLink = findViewById(R.id.buttonLink);
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent link = new Intent(Intent.ACTION_VIEW);
                link.setData(Uri.parse(rssData.getLink()));
                startActivity(link);
            }
        });

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
        }
    }
}
