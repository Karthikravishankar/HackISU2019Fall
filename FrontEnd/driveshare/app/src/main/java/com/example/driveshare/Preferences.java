package com.example.driveshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

public class Preferences extends AppCompatActivity {
    private Spinner age, sport, pet, music, drink, food;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        age = findViewById(R.id.age);
        sport = findViewById(R.id.sport);
        pet = findViewById(R.id.pet);
        music = findViewById(R.id.music);
        drink = findViewById(R.id.drink);
        food = findViewById(R.id.food);
    }
}
