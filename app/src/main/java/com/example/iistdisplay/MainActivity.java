package com.example.iistdisplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String ATTRIBUTE_NAME_TITLE = "title";
    private static final String ATTRIBUTE_NAME_SUBTITLE = "subtitle";
    private static final String CONTENT_KEY = "values";

    private static final String ITEMS_DELTED_KEY = "ITEMS_DELETED";
    private static final String LOG_TAG = "Log612";

    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private ArrayList<Integer> deletedItems = new ArrayList<Integer>();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(CONTENT_KEY, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView list = findViewById(R.id.list);

        prepareContent();

        final BaseAdapter listContentAdapter = createAdapter(simpleAdapterContent);

        list.setAdapter(listContentAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                simpleAdapterContent.remove(position);
                deletedItems.add(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleAdapterContent.clear();
                prepareContent();
                listContentAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, getString(R.string.bundle));
            deletedItems = savedInstanceState.getIntegerArrayList(ITEMS_DELTED_KEY);
            for (Integer v : deletedItems
            ) {
                simpleAdapterContent.remove(v);
            }
            listContentAdapter.notifyDataSetChanged();
        } else {
            Log.d(LOG_TAG, getString(R.string.bundle));
        }
    }

    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        String[] from = {ATTRIBUTE_NAME_TITLE, ATTRIBUTE_NAME_SUBTITLE};
        int[] to = {R.id.tv_title, R.id.tv_subtitle};
        return new SimpleAdapter(this, values, R.layout.list_item, from, to);
    }

    private void prepareContent() {
        if (!preferences.contains(CONTENT_KEY)) {
            preferences.edit().putString(CONTENT_KEY, getString(R.string.large_text)).apply();
        }

        String savedStr = preferences.getString(CONTENT_KEY, "");

        fillAdapterContent(savedStr);
    }

    private void fillAdapterContent(String content) {
        String[] strings = content.split("\n\n");
        for (String str : strings) {
            Map<String, String> map = new HashMap<>();
            map.put(ATTRIBUTE_NAME_TITLE, str.length() + "");
            map.put(ATTRIBUTE_NAME_SUBTITLE, str);
            simpleAdapterContent.add(map);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putIntegerArrayList(ITEMS_DELTED_KEY, deletedItems);
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, getString(R.string.onSaveInstanceState));
        //      mText.append("\n" + getString(R.string.onSaveInstanceState));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, getString(R.string.onRestoreInstanceState));
//        mText.append("\n" + getString(R.string.onRestoreInstanceState));
    }
}
