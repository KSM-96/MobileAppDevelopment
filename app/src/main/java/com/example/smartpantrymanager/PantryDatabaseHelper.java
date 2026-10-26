package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 5;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY = "expiry_date";

    private static PantryDatabaseHelper instance;

    public static synchronized PantryDatabaseHelper getInstance(
            Context context) {
        if (instance == null) {
            instance = new PantryDatabaseHelper(
                    context.getApplicationContext());
        }
        return instance;
    }

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createIngredientsTable(db);
    }

    private void createIngredientsTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_INGREDIENTS
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL, "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_EXPIRY + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_INGREDIENTS
                        + " ADD COLUMN " + COLUMN_EXPIRY + " TEXT");
            } catch (android.database.sqlite.SQLiteException e) {
                // The column may already exist.
            }
        }
        createIngredientsTable(db);
    }

    public boolean addIngredient(String name, double quantity,
                                 String unit, String expiry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY, expiry);

        return db.insert(TABLE_INGREDIENTS, null, values) != -1;
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_INGREDIENTS, null, null, null,
                null, null, COLUMN_NAME + " ASC");
    }

    public Cursor findIngredientByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_INGREDIENTS, null,
                "LOWER(" + COLUMN_NAME + ") = LOWER(?)",
                new String[]{name}, null, null, null);
    }

    public PantryItem getIngredient(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        PantryItem item = null;

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                int unitIndex = cursor.getColumnIndex(COLUMN_UNIT);
                int expiryIndex = cursor.getColumnIndex(COLUMN_EXPIRY);

                int itemId = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                double quantity = cursor.getDouble(quantityIndex);
                String unit = cursor.getString(unitIndex);

                String expiry = "";
                if (expiryIndex >= 0 && !cursor.isNull(expiryIndex)) {
                    expiry = cursor.getString(expiryIndex);
                }

                item = new PantryItem(
                        itemId,
                        name,
                        quantity,
                        unit,
                        expiry
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return item;
    }

    public boolean updateIngredient(int id, String name,
                                    double quantity, String unit,
                                    String expiry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY, expiry);

        return db.update(TABLE_INGREDIENTS, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_INGREDIENTS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }
}