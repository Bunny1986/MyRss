package com.kenyrim.MyRss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kenyrim.MyRss.db_main.ListData;

/**
 * Created by kenyr on 26.01.2018.
 */

public class AddActivity extends AppCompatActivity{
    Button btnSave;
    EditText etTitle, etText, etUrl;
    private long ListItemId;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        etTitle = findViewById(R.id.editTitle);
        etText = findViewById(R.id.editText);
        etUrl = findViewById(R.id.editUrl);

        if (getIntent().hasExtra("ListData")){
            ListData listData = (ListData) getIntent().getSerializableExtra("ListData");
            etTitle.setText(listData.getTitle());
            etText.setText(listData.getText());
            etUrl.setText(listData.getUrl());
            ListItemId = listData.getId();
        }else {
            ListItemId = -1;
        }


        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListData listData = new ListData(ListItemId,
                        etTitle.getText().toString(),
                        etText.getText().toString(),
                        etUrl.getText().toString());
                Intent intent = getIntent();
                intent.putExtra("ListData", listData);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
