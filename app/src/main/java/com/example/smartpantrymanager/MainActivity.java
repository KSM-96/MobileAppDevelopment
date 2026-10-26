package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button openPantryButton;
    private Button suggestRecipesButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openPantryButton = findViewById(R.id.openPantryButton);
        suggestRecipesButton = findViewById(R.id.suggestRecipesButton);
        settingsButton = findViewById(R.id.settingsButton);

        openPantryButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryActivity.class
            );
            startActivity(intent);
        });

        suggestRecipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecipeActivity.class
            );
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });
    }
}