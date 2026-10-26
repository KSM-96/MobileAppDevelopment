package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "recipe_name";
    public static final String EXTRA_INGREDIENTS = "recipe_ingredients";
    public static final String EXTRA_STEPS = "recipe_steps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String ingredients = getIntent().getStringExtra(EXTRA_INGREDIENTS);
        String steps = getIntent().getStringExtra(EXTRA_STEPS);

        tvTitle.setText(name == null || name.trim().isEmpty()
                ? "Recipe" : name.trim());
        tvIngredients.setText(formatIngredients(ingredients));
        tvSteps.setText(formatSteps(steps));
    }

    private String formatIngredients(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "No ingredients available.";
        }

        StringBuilder result = new StringBuilder();
        for (String part : text.split("[;\\r\\n]+")) {
            String item = part.trim();
            if (item.isEmpty()) {
                continue;
            }
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append("• ").append(item);
        }

        return result.length() > 0 ? result.toString() : "No ingredients available.";
    }

    private String formatSteps(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "No preparation instructions available.";
        }

        StringBuilder result = new StringBuilder();
        int number = 1;

        for (String part : text.trim().split("\\r?\\n+")) {
            // Remove any existing "1." / "Step 1:" so numbers are not doubled.
            String step = part.trim()
                    .replaceFirst("(?i)^(step\\s*)?\\d+[.):\\-]?\\s*", "");
            if (step.isEmpty()) {
                continue;
            }
            if (result.length() > 0) {
                result.append("\n\n");
            }
            result.append(number++).append(". ").append(step);
        }

        return result.length() > 0
                ? result.toString()
                : "No preparation instructions available.";
    }
}
