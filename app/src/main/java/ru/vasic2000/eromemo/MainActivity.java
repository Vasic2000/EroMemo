package ru.vasic2000.eromemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        Button level1Button = findViewById(R.id.level1Button);
        Button level2Button = findViewById(R.id.level2Button);
        Button level3Button = findViewById(R.id.level3Button);
        Button exitButton = findViewById(R.id.exitButton);

        // Обработчики кнопок - запускаем GameActivity с разными параметрами
        level1Button.setOnClickListener(v -> startGame(1, 12, 4, 3));
        level2Button.setOnClickListener(v -> startGame(2, 16, 4, 4));
        level3Button.setOnClickListener(v -> startGame(3, 20, 4, 5));
        exitButton.setOnClickListener(v -> showExitConfirmation());

        // Обработка кнопки "Назад"
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void startGame(int level, int cardCount, int columns, int rows) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL", level);
        intent.putExtra("CARD_COUNT", cardCount);
        intent.putExtra("COLUMNS", columns);
        intent.putExtra("ROWS", rows);
        startActivity(intent);
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из игры")
                .setMessage("Вы действительно хотите выйти?")
//                .setPositiveButton("Да", (dialog, which) ->
//                        finish())
                .setPositiveButton("Да", (dialog, which) -> {
                    // Завершаем все активности и выходим из приложения
                    finishAffinity();
                    System.exit(0);
                })
                .setNegativeButton("Нет", null)
                .show();
    }
}
