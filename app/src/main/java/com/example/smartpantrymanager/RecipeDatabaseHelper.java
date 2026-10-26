package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_INSTRUCTIONS = "instructions";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "ingredients TEXT NOT NULL, " +
                        "instructions TEXT)"
        );
    }

    private void createRecipesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_INGREDIENTS + " TEXT NOT NULL, " +
                        COLUMN_INSTRUCTIONS + " TEXT)"
        );
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        createRecipesTable(db);
    }


    public long addRecipe(
            String name,
            String ingredients,
            String instructions) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put(COLUMN_INSTRUCTIONS, instructions);

        return db.insert(TABLE_RECIPES, null, values);
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

    public Cursor getRecipeById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public int deleteRecipe(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_RECIPES,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int updateRecipe(
            int id,
            String name,
            String ingredients,
            String instructions) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put(COLUMN_INSTRUCTIONS, instructions);

        return db.update(
                TABLE_RECIPES,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}