package ru.vasic2000.eromemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private Button level0Button, level1Button, level2Button, level3Button, exitButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        // Инициализация SharedPreferences для сохранения прогресса
        preferences = getSharedPreferences("game_progress", MODE_PRIVATE);

        initializeViews();
        updateLevelButtons();

        // Обработка кнопки "Назад"
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем состояние кнопок при возвращении в меню
        updateLevelButtons();
    }

    private void updateLevelButtons() {
        // Получаем прогресс из SharedPreferences
        boolean level0Passed = preferences.getBoolean("level_0_passed", false);
        boolean level1Passed = preferences.getBoolean("level_1_passed", false);
        boolean level2Passed = preferences.getBoolean("level_2_passed", false);
//        boolean level3Passed = preferences.getBoolean("level_3_passed", false);

        // Уровень 1 всегда доступен
        level0Button.setEnabled(true);
        level0Button.setAlpha(1.0f);

        // Уровень 1 доступен после прохождения уровня 0
        if (level0Passed) {
            level1Button.setEnabled(true);
            level1Button.setAlpha(1.0f);
        } else {
            level1Button.setEnabled(false);
            level1Button.setAlpha(0.5f);
        }

        // Уровень 2 доступен после прохождения уровня 1
        if (level1Passed) {
            level2Button.setEnabled(true);
            level2Button.setAlpha(1.0f);
        } else {
            level2Button.setEnabled(false);
            level2Button.setAlpha(0.5f);
        }

        // Уровень 3 доступен после прохождения уровня 2
        if (level2Passed) {
            level3Button.setEnabled(true);
            level3Button.setAlpha(1.0f);
        } else {
            level3Button.setEnabled(false);
            level3Button.setAlpha(0.5f);
        }

    }

    private void initializeViews() {
        level0Button = findViewById(R.id.level0Button);
        level1Button = findViewById(R.id.level1Button);
        level2Button = findViewById(R.id.level2Button);
        level3Button = findViewById(R.id.level3Button);
        exitButton = findViewById(R.id.exitButton);

        // Обработчики кнопок - запускаем GameActivity с разными параметрами
        level0Button.setOnClickListener(v -> startGame(1, 6, 2, 3, 3));
        level1Button.setOnClickListener(v -> startGame(2, 12, 3, 4, 6));
        level2Button.setOnClickListener(v -> startGame(3, 16, 4, 4, 8));
        level3Button.setOnClickListener(v -> startGame(4, 20, 4, 5, 10));
        exitButton.setOnClickListener(v -> showExitConfirmation());
    }

    private void startGame(int level, int cardCount, int columns, int rows, int pairsCount) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL", level);
        intent.putExtra("CARD_COUNT", cardCount);
        intent.putExtra("COLUMNS", columns);
        intent.putExtra("ROWS", rows);
        intent.putExtra("PAIRS_COUNT", pairsCount);
        startActivity(intent);
        finish();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из игры")
                .setMessage("Вы действительно хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Завершаем все активности и выходим из приложения
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}
