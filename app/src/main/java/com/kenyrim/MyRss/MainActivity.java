package com.kenyrim.MyRss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kenyrim.MyRss.db_main.DBHelper;
import com.kenyrim.MyRss.db_main.ListData;
import com.kenyrim.MyRss.rss.RssShort;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    Context context;
    ListView listView;
    myListAdapter adapter;
    ArrayList<ListData> listItem;

    int ADD_ACTIVITY = 0;
    int UPDATE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.listView);
        adapter = new myListAdapter(context, dbHelper.selectAll());
        listView.setAdapter(adapter);
        registerForContextMenu(listView);


        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, RssShort.class);
                ListData listData = dbHelper.select(id);
                intent.putExtra("ListData", listData);
                startActivityForResult(intent, UPDATE_ACTIVITY);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(context, AddActivity.class);
                ListData listData = dbHelper.select(info.id);
                intent.putExtra("ListData", listData);
                startActivityForResult(intent, UPDATE_ACTIVITY);
                updateList();
                return true;
            case R.id.delete:
                dbHelper.delete(info.id);
                updateList();
                return true;
                default:
            return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(intent, ADD_ACTIVITY);
                updateList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ListData listData = (ListData) data.getExtras().getSerializable("ListData");
            if (requestCode == UPDATE_ACTIVITY)
                dbHelper.update(listData);
            else
                dbHelper.insert(listData);
            updateList();
        }
    }

    private void updateList() {
        adapter.setArrayListData(dbHelper.selectAll());
        adapter.notifyDataSetChanged();
    }

    class myListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ArrayList<ListData> arrayListData;

        public myListAdapter (Context context, ArrayList<ListData> arr){
            layoutInflater = LayoutInflater.from(context);
            setArrayListData(arr);
        }

        public ArrayList<ListData> getArrayListData(){
            return arrayListData;
        }

        public void setArrayListData(ArrayList<ListData> arrayListData){
            this.arrayListData = arrayListData;
        }

        public int getCount() {
            return arrayListData.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            ListData listData = arrayListData.get(position);
            if (listData != null){
                return listData.getId();
            }
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = layoutInflater.inflate(R.layout.list_item_1, null);

            TextView txtTitle = convertView.findViewById(R.id.textTitle);
            TextView txtText = convertView.findViewById(R.id.textText);
            TextView txtUrl = convertView.findViewById(R.id.textUrl);

            ListData listData = arrayListData.get(position);
            txtTitle.setText(listData.getTitle());
            txtText.setText(listData.getText());
            txtUrl.setText(listData.getUrl());

            return convertView;
        }
    }
}
