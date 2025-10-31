package ru.vasic2000.eromemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private LinearLayout victoryScreen;

    private int[] cardImages = {
            R.drawable.card_1, R.drawable.card_1,
            R.drawable.card_2, R.drawable.card_2,
            R.drawable.card_3, R.drawable.card_3,
            R.drawable.card_4, R.drawable.card_4,
            R.drawable.card_5, R.drawable.card_5,
            R.drawable.card_6, R.drawable.card_6
    };
    private Card[] cards = new Card[12];
    private int flippedCards = 0;
    private Card firstCard, secondCard;
    private Handler handler = new Handler();
    private boolean isBoardLocked = false; // флаг блокировки доски

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        victoryScreen = findViewById(R.id.victoryScreen);
        Button restartButton = findViewById(R.id.restartButton);
        Button exitButton = findViewById(R.id.exitButton);

        restartButton.setOnClickListener(v -> restartGame());
        exitButton.setOnClickListener(v -> exitGame());

        gridLayout = findViewById(R.id.gridLayout);

        // Современная обработка кнопки "Назад"
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        initializeGame();
    }

    private void initializeGame() {
        // Скрываем экран победы
        victoryScreen.setVisibility(View.GONE);

        // Перемешиваем карточки
        shuffleCards();

        // Создаем карточки и добавляем их в GridLayout
        for (int i = 0; i < cardImages.length; i++) {
            cards[i] = new Card(this);
            cards[i].setId(View.generateViewId());
            cards[i].setImageResource(cardImages[i]);
            cards[i].setOnClickListener(v -> onCardClick((Card) v));

            GridLayout.Spec row = GridLayout.spec(i / 3);
            GridLayout.Spec col = GridLayout.spec(i % 3);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);

            params.width = getResources().getDisplayMetrics().widthPixels / 4 - 20;
            params.height = params.width;
            params.setMargins(10, 10, 10, 10);
            gridLayout.addView(cards[i], params);
        }
    }

    private void shuffleCards() {
        Random random = new Random();
        for (int i = 0; i < cardImages.length; i++) {
            int j = random.nextInt(cardImages.length);
            int temp = cardImages[i];
            cardImages[i] = cardImages[j];
            cardImages[j] = temp;
        }
    }

    private void onCardClick(Card card) {

        if (isBoardLocked || card.isFlipped() || card.isMatched()) return;

        card.flip();
        flippedCards++;

        if (flippedCards == 1) {
            firstCard = card;
        } else if (flippedCards == 2) {
            secondCard = card;
            isBoardLocked = true; // Блокируем доску до завершения проверки
            checkForMatch();
        }
    }

    private void checkForMatch() {
        if (firstCard.getImageResource() == secondCard.getImageResource()) {
            handler.postDelayed(() -> {
                firstCard.setMatched();
                secondCard.setMatched();
                resetFlippedCards();
                isBoardLocked = false; // Разблокируем доску
                checkGameCompletion();
            }, 400);
        } else {
            handler.postDelayed(() -> {
                firstCard.flip();
                secondCard.flip();
                resetFlippedCards();
                isBoardLocked = false; // Разблокируем доску
                // Проверяем завершение игры
            }, 1000);
        }
    }

    private void resetFlippedCards() {
        flippedCards = 0;
        firstCard = null;
        secondCard = null;
    }

    private void checkGameCompletion() {
        boolean allMatched = true;
        for (Card card : cards) {
            if (!card.isMatched()) {
                allMatched = false;
                break;
            }
        }

        if (allMatched) {
//            showWinMessage();
            // Показываем экран победы с небольшой задержкой
            handler.postDelayed(this::showVictoryScreen, 500);
        }
    }

    private void showVictoryScreen() {
        // Анимация появления экрана победы
        victoryScreen.setAlpha(0f);
        victoryScreen.setVisibility(View.VISIBLE);
        victoryScreen.animate()
                .alpha(1f)
                .setDuration(500)
                .start();
    }

//    private void showWinMessage() {
//        new AlertDialog.Builder(this)
//                .setTitle("Поздравляем!")
//                .setMessage("Вы нашли все пары!")
//                .setPositiveButton("Новая игра", (dialog, which) -> restartGame())
//                .setCancelable(false)
//                .show();
//    }

    private void restartGame() {
        // Анимация скрытия экрана победы
        victoryScreen.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    victoryScreen.setVisibility(View.GONE);

        // Перезапускаем игру после скрытия экрана
        gridLayout.removeAllViews();
        flippedCards = 0;
        firstCard = null;
        secondCard = null;
        isBoardLocked = false;
        initializeGame();
    })
                .start();
    }

    //    Подтверждение выхода из игры
    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы действительно хотите выйти из игры?")
                .setPositiveButton("Да", (dialog, which) -> finish())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void exitGame() {
        showExitConfirmation();
    }

//    @Override
//    public void onBackPressed() {
//        showExitConfirmation();
//    }
}
