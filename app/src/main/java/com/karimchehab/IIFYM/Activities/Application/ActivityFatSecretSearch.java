package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fatsecret.platform.model.CompactFood;
import com.fatsecret.platform.model.Food;
import com.fatsecret.platform.services.Response;
import com.fatsecret.platform.services.android.Request;
import com.fatsecret.platform.services.android.ResponseListener;
import com.github.clans.fab.FloatingActionButton;
import com.karimchehab.IIFYM.Database.Credentials;
import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterFatsecretItem;
import com.karimchehab.IIFYM.Views.AdapterSavedItem;

import java.util.ArrayList;
import java.util.List;


public class ActivityFatSecretSearch extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextWatcher {

    // GUI
    private EditText                etxtSearch;
    private TextView                lblOfflineResults, lblOnlineResults;
    private AdapterSavedItem        adapterOffline;
    private AdapterFatsecretItem    adapterOnline;
    private ListView                listviewOfflineResults, listviewOnlineResults;
    private FloatingActionButton    fabCreateFood, fabCreateMeal;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    //Variables
    private Runnable        runnable;
    private Handler         handler;
    final private String    key = Credentials.FATSECRET_API_ACCESS_KEY;
    final private String    secret = Credentials.FATSECRET_SHARED_SECRET;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fat_secret_search);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        // GUI
        initializeGUI();

        runnable = () -> {
                String search = etxtSearch.getText().toString();
                filterOfflineItems(search);
                filterOnlineItems(search);
                Log.d("runnable", "timeout on etxtSearch");
        };
        handler = new Handler();
    }

    private void initializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.addTextChangedListener(this);

        // Labels
        lblOfflineResults = (TextView) findViewById(R.id.lblOfflineResults);
        lblOnlineResults = (TextView) findViewById(R.id.lblOnlineResults);

        // Adapters
        adapterOffline = new AdapterSavedItem(this);
        adapterOnline = new AdapterFatsecretItem(this);

        // ListViews
        listviewOfflineResults = (ListView) findViewById(R.id.listviewOfflineResults);
        listviewOfflineResults.setAdapter(adapterOffline);
        listviewOfflineResults.setOnItemClickListener(this);

        listviewOnlineResults = (ListView) findViewById(R.id.listviewOnlineResults);
        listviewOnlineResults.setAdapter(adapterOnline);
        listviewOnlineResults.setOnItemClickListener(this);

        // FABs
        fabCreateFood = (FloatingActionButton) findViewById(R.id.fabCreateNewFood);
        fabCreateFood.setOnClickListener(this);
        fabCreateMeal = (FloatingActionButton) findViewById(R.id.fabCreateNewMeal);
        fabCreateMeal.setOnClickListener(this);
    }

    @Override public void afterTextChanged(Editable s) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 500);
    }

    private void filterOfflineItems(final String search) {
        adapterOffline.clear();
        ArrayList<MyFood> arrOfflineItems;
        arrOfflineItems = DB_SQLite.searchFood(search, 20);

        if (arrOfflineItems.isEmpty()){
            lblOfflineResults.setText("No saved items found");
            listviewOfflineResults.setVisibility(View.GONE);

        }
        else {
            lblOfflineResults.setText("Saved items");
            listviewOfflineResults.setVisibility(View.VISIBLE);
            for (int i = 0; i < arrOfflineItems.size(); i++) {
                adapterOffline.add(arrOfflineItems.get(i));
            }
        }
    }

    private void filterOnlineItems(final String search) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Listener listener = new Listener();

        Request req = new Request(key, secret, listener);

        adapterOnline.clear();
        req.getFoods(requestQueue,search, 0); // Calls Response Listener when result is fetched
    }

    class Listener implements ResponseListener {
        @Override public void onFoodListRespone(Response<CompactFood> response) {
            System.out.println("TOTAL FOOD ITEMS: " + response.getTotalResults());

            List<CompactFood> foods = response.getResults();
            //This list contains summary information about the food items

            if (foods.isEmpty()){
                lblOnlineResults.setText("Online results not found.");
                listviewOnlineResults.setVisibility(View.GONE);
            }

            else {
                lblOnlineResults.setText("Online Results");
                listviewOnlineResults.setVisibility(View.VISIBLE);

                System.out.println("=========FOODS============");
                for (CompactFood food : foods) {
                    System.out.println(food.getName());

                    adapterOnline.add(food);
                }
            }
        }

        @Override public void onFoodResponse(Food food) {
            System.out.println("FOOD NAME: " + food.getName());
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getAdapter() instanceof AdapterSavedItem){
            MyFood food = (MyFood) parent.getItemAtPosition(position);
            long fid = food.getId();

            Intent intent = new Intent(context, ActivityAddDailyItem.class);
            intent.putExtra("fid", fid);
            startActivity(intent);
        }

        else if (parent.getAdapter() instanceof AdapterFatsecretItem){
            CompactFood compactFood = (CompactFood) parent.getItemAtPosition(position);
            long compactId = compactFood.getId();

            Intent intent = new Intent(context, ActivityAddDailyItem.class);
            intent.putExtra("compactId", compactId);
            startActivity(intent);
        }
    }

    public void goToCreateNewFood (){
        Intent intent = new Intent(context, ActivityCreateFood.class);
        startActivity(intent);
    }

    private void goToCreateNewMeal() {
        Intent intent = new Intent(context, ActivtitySelectMealIngredients.class);
        startActivity(intent);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabCreateNewFood:
                goToCreateNewFood();
                break;
            case R.id.fabCreateNewMeal:
                goToCreateNewMeal();
                break;
        }
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
}