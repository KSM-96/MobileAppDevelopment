package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameInput;
    private TextView welcomeText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
                "SmartPantryProfile", MODE_PRIVATE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 40, 32, 32);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("My Profile");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        welcomeText = new TextView(this);
        welcomeText.setTextSize(20);
        welcomeText.setPadding(0, 30, 0, 30);
        layout.addView(welcomeText);

        TextView nameLabel = new TextView(this);
        nameLabel.setText("Enter your name:");
        nameLabel.setTextSize(16);
        layout.addView(nameLabel);

        nameInput = new EditText(this);
        nameInput.setHint("e.g. Thato");
        nameInput.setSingleLine(true);
        layout.addView(nameInput);

        Button saveButton = new Button(this);
        saveButton.setText("Save Name");
        layout.addView(saveButton);

        setContentView(layout);

        String savedName = preferences.getString("username", "");

        if (!savedName.isEmpty()) {
            nameInput.setText(savedName);
            welcomeText.setText("Welcome back, " + savedName + "!");
        } else {
            welcomeText.setText("Welcome! Please enter your name.");
        }

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Please enter your name");
                return;
            }

            preferences.edit()
                    .putString("username", name)
                    .apply();

            welcomeText.setText("Welcome back, " + name + "!");

            Toast.makeText(this,
                    "Name saved successfully!",
                    Toast.LENGTH_SHORT).show();
        });
    }
}
