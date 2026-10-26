package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RecipeActivity extends AppCompatActivity {

    private RecipeDatabaseHelper database;
    private PantryDatabaseHelper pantryDatabase;
    private LinearLayout recipeContainer;

    private final Map<String, IngredientAmount> pantry =
            new HashMap<>();

    private static class IngredientAmount {
        double quantity;
        String unit;

        IngredientAmount(double quantity, String unit) {
            this.quantity = quantity;
            this.unit = unit == null ? "" : unit.trim();
        }
    }

    private static class RecipeData {
        int id;
        String name;
        String ingredients;
        String instructions;
        List<String> missing = new ArrayList<>();

        RecipeData(int id, String name, String ingredients,
                   String instructions) {
            this.id = id;
            this.name = name;
            this.ingredients = ingredients;
            this.instructions = instructions;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new RecipeDatabaseHelper(this);
        pantryDatabase = PantryDatabaseHelper.getInstance(this);

        ScrollView scroll = new ScrollView(this);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(24, 24, 24, 24);
        main.setBackgroundColor(0xFFFFF7E6);

        TextView title = new TextView(this);
        title.setText("Recipe Suggestions");
        title.setTextSize(26);
        title.setTextColor(0xFF800020);
        title.setPadding(0, 0, 0, 8);
        main.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(
                "Find recipes using the ingredients in your pantry."
        );
        subtitle.setTextSize(16);
        subtitle.setPadding(0, 0, 0, 20);
        main.addView(subtitle);

        recipeContainer = new LinearLayout(this);
        recipeContainer.setOrientation(LinearLayout.VERTICAL);
        main.addView(recipeContainer);

        scroll.addView(main);
        setContentView(scroll);

        addDefaultRecipesIfNeeded();
        refreshSuggestions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recipeContainer != null && database != null) {
            refreshSuggestions();
        }
    }

    private void addDefaultRecipesIfNeeded() {
        Set<String> existingNames = new HashSet<>();
        Cursor cursor = null;

        try {
            cursor = database.getAllRecipes();

            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex("name");

                if (nameIndex >= 0) {
                    while (cursor.moveToNext()) {
                        existingNames.add(
                                normalize(cursor.getString(nameIndex))
                        );
                    }
                }
            }
        } catch (Exception e) {
            showMessage("Could not check the recipe database.");
            return;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        addDefaultRecipe(
                existingNames,
                "Tomato Fried Rice",
                "rice: 1 cup; tomato: 2 pieces; onion: 1 piece",
                "1. Cook the rice.\n"
                        + "2. Chop the tomato and onion.\n"
                        + "3. Fry the vegetables in a little oil.\n"
                        + "4. Add the rice and mix well."
        );

        addDefaultRecipe(
                existingNames,
                "Vegetable Omelette",
                "egg: 2 pieces; tomato: 1 piece; onion: 1 piece",
                "1. Beat the eggs.\n"
                        + "2. Chop the tomato and onion.\n"
                        + "3. Mix the vegetables with the eggs.\n"
                        + "4. Cook in a lightly oiled pan."
        );

        addDefaultRecipe(
                existingNames,
                "Simple Potato Curry",
                "potato: 3 pieces; tomato: 2 pieces; onion: 1 piece",
                "1. Wash and cut the potatoes.\n"
                        + "2. Chop the tomato and onion.\n"
                        + "3. Cook the onion and tomato in a pan.\n"
                        + "4. Add potatoes, curry spices and water.\n"
                        + "5. Simmer until the potatoes are soft."
        );

        addDefaultRecipe(
                existingNames,
                "Tomato Pasta",
                "pasta: 1 cup; tomato: 3 pieces; onion: 1 piece",
                "1. Cook the pasta according to its packet.\n"
                        + "2. Chop the tomato and onion.\n"
                        + "3. Cook the vegetables in a pan.\n"
                        + "4. Add the pasta and mix."
        );

        addDefaultRecipe(
                existingNames,
                "Egg and Potato",
                "egg: 2 pieces; potato: 2 pieces",
                "1. Wash and cut the potatoes.\n"
                        + "2. Cook the potatoes until soft.\n"
                        + "3. Add beaten eggs to the pan.\n"
                        + "4. Cook until the eggs are done."
        );
    }

    private void addDefaultRecipe(
            Set<String> existingNames,
            String name,
            String ingredients,
            String instructions) {

        if (existingNames.contains(normalize(name))) {
            return;
        }

        long result = database.addRecipe(
                name, ingredients, instructions
        );

        if (result != -1) {
            existingNames.add(normalize(name));
        }
    }

    private void refreshSuggestions() {
        recipeContainer.removeAllViews();
        pantry.clear();

        loadPantry();

        if (pantry.isEmpty()) {
            showMessage(
                    "Your pantry is empty. Add ingredients in the Pantry "
                            + "section to receive recipe suggestions."
            );
            return;
        }

        List<RecipeData> availableRecipes = new ArrayList<>();
        List<RecipeData> otherRecipes = new ArrayList<>();

        Cursor cursor = null;

        try {
            cursor = database.getAllRecipes();

            if (cursor == null) {
                showMessage("Recipes could not be loaded.");
                return;
            }

            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int ingredientsIndex = cursor.getColumnIndex("ingredients");
            int instructionsIndex = cursor.getColumnIndex("instructions");

            if (idIndex < 0 || nameIndex < 0
                    || ingredientsIndex < 0 || instructionsIndex < 0) {
                showMessage("Recipe database columns are missing.");
                return;
            }

            while (cursor.moveToNext()) {
                RecipeData recipe = new RecipeData(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(ingredientsIndex),
                        cursor.getString(instructionsIndex)
                );

                checkRecipe(recipe);

                if (recipe.missing.isEmpty()) {
                    availableRecipes.add(recipe);
                } else {
                    otherRecipes.add(recipe);
                }
            }

        } catch (Exception e) {
            showMessage("Could not load recipes: " + e.getMessage());
            return;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        addSectionTitle("Recipes You Can Make");

        if (availableRecipes.isEmpty()) {
            showMessage(
                    "No recipes can be made completely with your current "
                            + "ingredients."
            );
        } else {
            for (RecipeData recipe : availableRecipes) {
                addRecipeCard(recipe, true);
            }
        }

        addSectionTitle("Other Recipes");

        if (otherRecipes.isEmpty()) {
            showMessage("You have all the ingredients for these recipes!");
        } else {
            for (RecipeData recipe : otherRecipes) {
                addRecipeCard(recipe, false);
            }
        }
    }

    private void loadPantry() {
        Cursor cursor = null;

        try {
            cursor = pantryDatabase.getAllIngredients();

            if (cursor == null) {
                return;
            }

            int nameIndex = cursor.getColumnIndex("name");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int unitIndex = cursor.getColumnIndex("unit");

            if (nameIndex < 0 || quantityIndex < 0 || unitIndex < 0) {
                showMessage("Pantry database columns are missing.");
                return;
            }

            while (cursor.moveToNext()) {
                String name = normalize(cursor.getString(nameIndex));
                double quantity = cursor.getDouble(quantityIndex);
                String unit = cursor.getString(unitIndex);

                if (name.isEmpty() || quantity <= 0) {
                    continue;
                }

                String key = name + "|" + unitType(unit);

                IngredientAmount current = pantry.get(key);

                if (current == null) {
                    pantry.put(key, new IngredientAmount(quantity, unit));
                } else {
                    current.quantity += quantity;
                }
            }

        } catch (Exception e) {
            showMessage("Could not read pantry: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void checkRecipe(RecipeData recipe) {
        recipe.missing.clear();

        if (recipe.ingredients == null
                || recipe.ingredients.trim().isEmpty()) {
            recipe.missing.add("Recipe ingredients are not listed.");
            return;
        }

        String[] lines = recipe.ingredients.split("[;\\r\\n]+");

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(":", 2);
            String ingredientName = normalize(parts[0]);

            if (ingredientName.isEmpty()) {
                continue;
            }

            double requiredQuantity = 1;
            String requiredUnit = "";

            if (parts.length == 2) {
                String amountText = parts[1].trim();
                String[] amountParts = amountText.split("\\s+", 2);

                try {
                    requiredQuantity = Double.parseDouble(amountParts[0]);
                } catch (NumberFormatException e) {
                    recipe.missing.add(line + " (check recipe amount)");
                    continue;
                }

                if (amountParts.length > 1) {
                    requiredUnit = amountParts[1];
                }
            }

            if (requiredQuantity <= 0) {
                recipe.missing.add(line + " (invalid amount)");
                continue;
            }

            double availableQuantity = 0;
            String requiredType = unitType(requiredUnit);

            for (Map.Entry<String, IngredientAmount> entry
                    : pantry.entrySet()) {

                String[] keyParts = entry.getKey().split("\\|", 2);

                if (keyParts.length != 2) {
                    continue;
                }

                if (!keyParts[0].equals(ingredientName)) {
                    continue;
                }

                IngredientAmount item = entry.getValue();

                if (!requiredType.equals(unitType(item.unit))) {
                    continue;
                }

                double converted = convertToBase(
                        item.quantity, item.unit
                );

                availableQuantity += converted;
            }

            double requiredBase = convertToBase(
                    requiredQuantity, requiredUnit
            );

            if (availableQuantity + 0.000001 < requiredBase) {
                recipe.missing.add(
                        parts[0].trim() + ": need "
                                + requiredQuantity + " " + requiredUnit
                );
            }
        }
    }

    private String unitType(String unit) {
        String value = normalize(unit);

        switch (value) {
            case "kg":
            case "kilogram":
            case "kilograms":
            case "g":
            case "gram":
            case "grams":
                return "weight";

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return "volume";

            case "cup":
            case "cups":
                return "cup";

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "teaspoon";

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tablespoon";

            case "":
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
            case "item":
            case "items":
            case "egg":
            case "eggs":
                return "count";

            default:
                return "other:" + value;
        }
    }

    private double convertToBase(double quantity, String unit) {
        String value = normalize(unit);

        switch (value) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return quantity * 1000;

            case "g":
            case "gram":
            case "grams":
                return quantity;

            case "l":
            case "liter":
            case "liters":
            case "litre":
            case "litres":
                return quantity * 1000;

            case "ml":
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
                return quantity;

            default:
                // Cups, spoons, pieces and unknown units are
                // compared only with the same unit type.
                return quantity;
        }
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private void addSectionTitle(String text) {
        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextSize(20);
        heading.setTextColor(0xFF800020);
        heading.setPadding(0, 12, 0, 12);
        recipeContainer.addView(heading);
    }

    private void addRecipeCard(RecipeData recipe, boolean canMake) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundColor(0xFFFFFFFF);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);

        TextView name = new TextView(this);
        name.setText(recipe.name);
        name.setTextSize(20);
        name.setTextColor(0xFF800020);
        card.addView(name);

        TextView status = new TextView(this);
        status.setText(
                canMake
                        ? "You have the required ingredients."
                        : "You may need more ingredients."
        );
        status.setTextSize(14);
        status.setPadding(0, 8, 0, 8);
        card.addView(status);

        if (!canMake) {
            TextView missing = new TextView(this);
            missing.setText("Missing or insufficient:\n"
                    + joinLines(recipe.missing));
            missing.setTextSize(15);
            card.addView(missing);
        }

        TextView details = new TextView(this);
        details.setText("Tap to view ingredients and instructions.");
        details.setTextSize(15);
        details.setPadding(0, 8, 0, 0);
        card.addView(details);

        card.setOnClickListener(v -> showRecipeDetails(recipe));

        recipeContainer.addView(card);
    }

    private String joinLines(List<String> lines) {
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append("- ").append(line);
        }

        return result.toString();
    }

    private void showRecipeDetails(RecipeData recipe) {
        String message = "Ingredients:\n"
                + recipe.ingredients
                + "\n\nInstructions:\n"
                + (recipe.instructions == null
                || recipe.instructions.trim().isEmpty()
                ? "No instructions available."
                : recipe.instructions);

        new AlertDialog.Builder(this)
                .setTitle(recipe.name)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showMessage(String message) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(16);
        text.setPadding(0, 12, 0, 16);
        recipeContainer.addView(text);
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }

        super.onDestroy();
    }
}
