
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_STEPS = "steps";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS "
                + TABLE_RECIPES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_INGREDIENTS + " TEXT NOT NULL, "
                + COLUMN_STEPS + " TEXT NOT NULL)";

        db.execSQL(createTable);
        insertStarterRecipes(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            String createTable = "CREATE TABLE IF NOT EXISTS "
                    + TABLE_RECIPES + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_INGREDIENTS + " TEXT NOT NULL, "
                    + COLUMN_STEPS + " TEXT NOT NULL)";

            db.execSQL(createTable);
            insertStarterRecipes(db);
        }
    }

    private void insertStarterRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "egg:2;milk:30 ml;butter:10 g",
                "1. Beat the eggs with milk.\n"
                        + "2. Melt butter in a pan.\n"
                        + "3. Add eggs and stir until cooked.");

        addRecipe(db, "Tomato Pasta",
                "pasta:200 g;tomato:2;onion:1",
                "1. Boil pasta until tender.\n"
                        + "2. Chop and cook tomato and onion.\n"
                        + "3. Mix the sauce with the pasta.");

        addRecipe(db, "Banana Pancakes",
                "banana:2;egg:2;flour:100 g",
                "1. Mash the bananas.\n"
                        + "2. Mix with eggs and flour.\n"
                        + "3. Cook small pancakes in a pan.");

        addRecipe(db, "Cheese Sandwich",
                "bread:2 slice;cheese:2 slice;butter:10 g",
                "1. Butter the bread.\n"
                        + "2. Add cheese between the slices.\n"
                        + "3. Toast until golden.");

        addRecipe(db, "Garlic Rice",
                "rice:200 g;garlic:2 clove;oil:15 ml",
                "1. Cook the rice.\n"
                        + "2. Fry chopped garlic in oil.\n"
                        + "3. Stir in the cooked rice.");

        addRecipe(db, "Vegetable Omelette",
                "egg:2;onion:1;tomato:1",
                "1. Beat the eggs.\n"
                        + "2. Chop the onion and tomato.\n"
                        + "3. Cook the vegetables with the eggs.");

        addRecipe(db, "Chicken Rice",
                "chicken:200 g;rice:200 g;onion:1",
                "1. Cook the rice.\n"
                        + "2. Cook the chicken thoroughly with onion.\n"
                        + "3. Serve the chicken over the rice.");

        addRecipe(db, "Potato Mash",
                "potato:3;butter:20 g;milk:50 ml",
                "1. Peel and boil the potatoes.\n"
                        + "2. Drain and mash them.\n"
                        + "3. Mix in butter and milk.");

        addRecipe(db, "Tomato Egg Curry",
                "egg:3;tomato:2;onion:1",
                "1. Boil and peel the eggs.\n"
                        + "2. Cook chopped onion and tomato.\n"
                        + "3. Add eggs and simmer.");

        addRecipe(db, "Peanut Butter Toast",
                "bread:2 slice;peanut butter:30 g",
                "1. Toast the bread.\n"
                        + "2. Spread peanut butter on each slice.\n"
                        + "3. Serve immediately.");

        addRecipe(db, "Simple Bean Stew",
                "beans:200 g;tomato:2;onion:1",
                "1. Cook the beans until tender.\n"
                        + "2. Cook tomato and onion in a pot.\n"
                        + "3. Add beans and simmer.");

        addRecipe(db, "Apple Oatmeal",
                "oats:100 g;milk:200 ml;apple:1",
                "1. Heat oats and milk in a pot.\n"
                        + "2. Stir until creamy.\n"
                        + "3. Chop the apple and add it.");

        addRecipe(db, "Tuna Pasta",
                "pasta:200 g;tuna:1 can;onion:1",
                "1. Boil the pasta.\n"
                        + "2. Cook chopped onion.\n"
                        + "3. Mix with drained tuna and pasta.");

        addRecipe(db, "Chicken Sandwich",
                "bread:2 slice;chicken:100 g;mayonnaise:20 g",
                "1. Cook the chicken thoroughly.\n"
                        + "2. Slice or shred the chicken.\n"
                        + "3. Add mayonnaise and chicken to bread.");

        addRecipe(db, "Flour Flatbread",
                "flour:200 g;water:120 ml;salt:2 g",
                "1. Mix flour, water and salt.\n"
                        + "2. Knead into a dough and divide.\n"
                        + "3. Roll flat and cook in a hot pan.");
    }

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String ingredients,
            String steps) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put(COLUMN_STEPS, steps);

        db.insert(TABLE_RECIPES, null, values);
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }
}
