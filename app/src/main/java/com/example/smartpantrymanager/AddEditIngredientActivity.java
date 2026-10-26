package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Add a new ingredient, or edit or delete an existing one.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "pantry_item_id";

    private static final String[] UNITS = {
            "kg", "g", "mg", "L", "mL", "pcs",
            "tbsp", "tsp", "cups", "pack", "bottle",
            "can", "cloves", "slices", "pinch", "whole",
            "dozen"
    };

    private TextInputLayout tilName;
    private TextInputLayout tilQuantity;
    private TextInputLayout tilUnit;

    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private MaterialAutoCompleteTextView etUnit;
    private TextInputEditText etExpiry;

    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private PantryDatabaseHelper db;

    private int itemId = -1;
    private boolean isEditMode = false;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        db = PantryDatabaseHelper.getInstance(this);
        dateFormat.setLenient(false);

        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        tilUnit = findViewById(R.id.tilUnit);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiryDate);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        etUnit.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                UNITS
        ));

        etUnit.setOnClickListener(v -> etUnit.showDropDown());
        etExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        if (itemId != -1) {
            isEditMode = loadItem();
        }

        TextView title = findViewById(R.id.tvTitle);

        title.setText(isEditMode
                ? "Edit Ingredient"
                : "Add Ingredient");

        btnDelete.setVisibility(
                isEditMode ? View.VISIBLE : View.GONE
        );

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private boolean loadItem() {
        PantryItem item = db.getIngredient(itemId);

        if (item == null) {
            Toast.makeText(
                    this,
                    "Ingredient not found",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        etName.setText(item.name);

        etQuantity.setText(
                PantryAdapter.formatQuantity(item.quantity)
        );

        etUnit.setText(item.unit, false);

        if (item.expiryDate != null) {
            etExpiry.setText(item.expiryDate);
        }

        return true;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        String existing = textOf(etExpiry);

        if (!existing.isEmpty()) {
            try {
                Date date = dateFormat.parse(existing);

                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException ignored) {
                // Use today's date if the existing date is invalid.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> etExpiry.setText(
                        String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                day
                        )
                ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.setButton(
                DialogInterface.BUTTON_NEUTRAL,
                "Clear",
                (d, which) -> etExpiry.setText("")
        );

        dialog.show();
    }

    private void saveIngredient() {
        tilName.setError(null);
        tilQuantity.setError(null);
        tilUnit.setError(null);

        String name = textOf(etName);
        String quantityText = textOf(etQuantity);
        String unit = etUnit.getText() == null
                ? ""
                : etUnit.getText().toString().trim();
        String expiry = textOf(etExpiry);

        if (name.isEmpty()) {
            tilName.setError("Enter an ingredient name");
            etName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            tilQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            tilQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || quantity <= 0) {

            tilQuantity.setError(
                    "Quantity must be greater than zero"
            );

            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            tilUnit.setError("Choose a unit");
            etUnit.requestFocus();
            return;
        }

        if (!expiry.isEmpty()) {
            try {
                Date parsedDate = dateFormat.parse(expiry);

                if (parsedDate == null) {
                    tilName.setError(null);
                    Toast.makeText(
                            this,
                            "Enter a valid expiry date",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(
                        this,
                        "Expiry date must use yyyy-MM-dd",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
        }

        boolean saved;

        if (isEditMode) {
            saved = db.updateIngredient(
                    itemId,
                    name,
                    quantity,
                    unit,
                    expiry
            );
        } else {
            saved = db.addIngredient(
                    name,
                    quantity,
                    unit,
                    expiry
            );
        }

        if (saved) {
            Toast.makeText(
                    this,
                    isEditMode
                            ? "Ingredient updated"
                            : "Ingredient added",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Could not save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + textOf(etName) + "?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (db.deleteIngredient(itemId)) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null
                ? ""
                : field.getText().toString().trim();
    }
}