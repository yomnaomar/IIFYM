package com.karimchehab.IIFYM.Activities.Application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karimchehab.IIFYM.Database.SQLiteConnector;
import com.karimchehab.IIFYM.Models.MyFood;
import com.karimchehab.IIFYM.R;
import com.karimchehab.IIFYM.Views.AdapterSavedItem;

import java.util.ArrayList;

public class ActivtitySelectMealIngredients extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // GUI
    private EditText                etxtSearch;
    private AdapterSavedItem        adapterSelectedFoods, adapterSavedItem;
    private ListView                listviewSelectedFoods, listviewSavedItems;

    // Database
    private SQLiteConnector DB_SQLite;
    private Context context;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_ingredients);

        // Database
        context = getApplicationContext();
        DB_SQLite = new SQLiteConnector(context);

        initializeGUI();
    }

    private void initializeGUI() {
        // Search Functionality
        etxtSearch = (EditText) findViewById(R.id.etxtSearch);
        etxtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Search();
                return true;
            }
            return false;
        });

        // List View
        adapterSelectedFoods = new AdapterSavedItem(this);
        listviewSelectedFoods = (ListView) findViewById(R.id.listviewSelectedFoods);
        listviewSelectedFoods.setAdapter(adapterSelectedFoods);
        //TODO Implement Swipe to delete

        adapterSavedItem = new AdapterSavedItem(this);
        listviewSavedItems = (ListView) findViewById(R.id.listviewSavedItems);
        listviewSavedItems.setAdapter(adapterSavedItem);
        listviewSavedItems.setOnItemClickListener(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_meal_ingredients, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar list_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_next) {
            goToActivityCreateMeal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToActivityCreateMeal() {
        int ingredientCount = adapterSelectedFoods.getCount();
        long[] ingredients = new long[ingredientCount];
        for (int i = 0; i < ingredientCount; i ++){
            ingredients[i] = adapterSelectedFoods.getItem(i).getId();
        }
        Intent newIntent = new Intent(getApplicationContext(), ActivityCreateMeal.class);
        newIntent.putExtra("ingredients", ingredients);
        startActivity(newIntent);
    }

    private void Search() {
        adapterSavedItem.clear();
        ArrayList<MyFood> arrSavedItems;
        String search = etxtSearch.getText().toString();

        arrSavedItems = DB_SQLite.searchFood(search);

        for (int i = 0; i < arrSavedItems.size(); i++) {
            adapterSavedItem.add(arrSavedItems.get(i));
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyFood food = (MyFood) parent.getItemAtPosition(position);
        adapterSelectedFoods.add(food);
    }
}
