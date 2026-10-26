package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {

    private PantryDatabaseHelper db;
    private PantryAdapter adapter;

    private final List<PantryItem> allItems = new ArrayList<>();
    private final List<PantryItem> displayedItems = new ArrayList<>();

    private RecyclerView rvPantry;
    private TextView tvEmpty;
    private TextView tvItemCount;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        db = PantryDatabaseHelper.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        rvPantry = findViewById(R.id.rvPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvItemCount = findViewById(R.id.tvItemCount);
        etSearch = findViewById(R.id.etSearch);

        MaterialButton btnAdd = findViewById(R.id.btnAddIngredient);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        toolbar.setTitle("My Pantry");
        rvPantry.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PantryAdapter(displayedItems, item -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    AddEditIngredientActivity.EXTRA_ITEM_ID,
                    item.id
            );

            startActivity(intent);
        });

        rvPantry.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> openAddIngredient());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filterIngredients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        bottomNav.setSelectedItemId(R.id.nav_pantry);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_add) {
                openAddIngredient();
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, RecipeActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return false;
        });
    }

    private void openAddIngredient() {
        startActivity(new Intent(
                this,
                AddEditIngredientActivity.class
        ));
    }

    private void loadIngredients() {
        allItems.clear();

        Cursor cursor = db.getAllIngredients();

        if (cursor != null) {
            try {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int quantityIndex = cursor.getColumnIndex("quantity");
                int unitIndex = cursor.getColumnIndex("unit");
                int expiryIndex = cursor.getColumnIndex("expiry_date");

                while (cursor.moveToNext()) {
                    if (idIndex < 0 || nameIndex < 0
                            || quantityIndex < 0 || unitIndex < 0) {
                        break;
                    }

                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    double quantity = cursor.getDouble(quantityIndex);
                    String unit = cursor.getString(unitIndex);

                    String expiry = "";
                    if (expiryIndex >= 0 && !cursor.isNull(expiryIndex)) {
                        expiry = cursor.getString(expiryIndex);
                    }

                    allItems.add(new PantryItem(
                            id, name, quantity, unit, expiry
                    ));
                }
            } finally {
                cursor.close();
            }
        }

        filterIngredients(etSearch.getText().toString());
    }

    private void filterIngredients(String query) {
        displayedItems.clear();

        String search = query.trim().toLowerCase(Locale.ROOT);

        for (PantryItem item : allItems) {
            if (item.name != null
                    && item.name.toLowerCase(Locale.ROOT).contains(search)) {
                displayedItems.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        tvItemCount.setText(
                displayedItems.size() + " ingredient"
                        + (displayedItems.size() == 1 ? "" : "s")
        );

        if (displayedItems.isEmpty()) {
            rvPantry.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);

            if (allItems.isEmpty()) {
                tvEmpty.setText(
                        "Your pantry is empty.\n"
                                + "Tap + Add Ingredient to get started."
                );
            } else {
                tvEmpty.setText("No ingredients match your search.");
            }
        } else {
            rvPantry.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null && adapter != null) {
            loadIngredients();
        }
    }
}