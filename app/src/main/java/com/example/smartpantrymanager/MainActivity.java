
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button openPantryButton;
    private Button suggestRecipesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openPantryButton = findViewById(R.id.openPantryButton);
        suggestRecipesButton = findViewById(R.id.suggestRecipesButton);

        openPantryButton.setOnClickListener(view ->
                Toast.makeText(
                        MainActivity.this,
                        "Welcome to My Pantry!",
                        Toast.LENGTH_SHORT
                ).show()
        );

        suggestRecipesButton.setOnClickListener(view ->
                Toast.makeText(
                        MainActivity.this,
                        "Welcome to Suggested Recipes!",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}
