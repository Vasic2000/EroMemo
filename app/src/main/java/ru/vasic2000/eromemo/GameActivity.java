package ru.vasic2000.eromemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

// GameActivity.java
public class GameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private LinearLayout victoryScreen;
    private TextView levelInfo;
    private TextView victoryTitle;
    private Button restartButton;
    private Button menuButton;
    private Button nextLevelButton;

    private int level;
    private int cardCount;
    private int columns;
    private int rows;

    private int[] allCardImages = {
            R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_4,
            R.drawable.card_5, R.drawable.card_6, R.drawable.card_7, R.drawable.card_8,
            R.drawable.card_9, R.drawable.card_10
    };
    private int[] cardImages;
    private Card[] cards;
    private int flippedCards = 0;
    private Card firstCard, secondCard;
    private Handler handler = new Handler();
    private boolean isBoardLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_game);

        // Получаем параметры уровня
        Intent intent = getIntent();
        level = intent.getIntExtra("LEVEL", 1);
        cardCount = intent.getIntExtra("CARD_COUNT", 12);
        columns = intent.getIntExtra("COLUMNS", 4);
        rows = intent.getIntExtra("ROWS", 3);

        initializeViews();
        initializeGame();
    }

    private void initializeViews() {
        gridLayout = findViewById(R.id.gridLayout);
        victoryScreen = findViewById(R.id.victoryScreen);
        levelInfo = findViewById(R.id.levelInfo);
        victoryTitle = findViewById(R.id.victoryTitle);
        restartButton = findViewById(R.id.restartButton);
        menuButton = findViewById(R.id.menuButton);
        nextLevelButton = findViewById(R.id.nextLevelButton);

        // Настраиваем GridLayout
        gridLayout.setColumnCount(columns);
        gridLayout.setRowCount(rows);

        // Устанавливаем информацию об уровне
        levelInfo.setText("Уровень " + level + " (" + cardCount + " карточек)");

        restartButton.setOnClickListener(v -> restartGame());
        menuButton.setOnClickListener(v -> goToMenu());
        nextLevelButton.setOnClickListener(v -> goToNextLevel());

        // Показываем кнопку следующего уровня только если есть следующий уровень
        if (level < 3) {
            nextLevelButton.setVisibility(View.VISIBLE);
        }

        // Обработка кнопки "Назад"
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initializeGame() {
        victoryScreen.setVisibility(View.GONE);
        prepareCardImages();
        shuffleCards();
        createCards();
    }

    private void prepareCardImages() {
        int pairsNeeded = cardCount / 2;
        cardImages = new int[cardCount];

        // Создаем пары карточек
        for (int i = 0; i < pairsNeeded; i++) {
            int imageIndex = i % allCardImages.length;
            cardImages[i * 2] = allCardImages[imageIndex];
            cardImages[i * 2 + 1] = allCardImages[imageIndex];
        }

        cards = new Card[cardCount];
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

    private void createCards() {
        gridLayout.removeAllViews();

        for (int i = 0; i < cardImages.length; i++) {
            cards[i] = new Card(this);
            cards[i].setId(View.generateViewId());
            cards[i].setImageResource(cardImages[i]);
            cards[i].setOnClickListener(v -> onCardClick((Card) v));

            GridLayout.Spec row = GridLayout.spec(i / columns);
            GridLayout.Spec col = GridLayout.spec(i % columns);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, col);
            params.width = getResources().getDisplayMetrics().widthPixels / columns - 20;
            params.height = params.width;
            params.setMargins(10, 10, 10, 10);

            gridLayout.addView(cards[i], params);
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
            isBoardLocked = true;
            checkForMatch();
        }
    }

    private void checkForMatch() {
        if (firstCard.getImageResource() == secondCard.getImageResource()) {
            handler.postDelayed(() -> {
                firstCard.setMatched();
                secondCard.setMatched();
                resetFlippedCards();
                isBoardLocked = false;
                checkGameCompletion();
            }, 500);
        } else {
            handler.postDelayed(() -> {
                firstCard.flip();
                secondCard.flip();
                resetFlippedCards();
                isBoardLocked = false;
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
            handler.postDelayed(this::showVictoryScreen, 500);
        }
    }

    private void showVictoryScreen() {
        victoryTitle.setText("Уровень " + level + " пройден!");
        victoryScreen.setAlpha(0f);
        victoryScreen.setVisibility(View.VISIBLE);
        victoryScreen.animate()
                .alpha(1f)
                .setDuration(500)
                .start();
    }

    private void restartGame() {
        victoryScreen.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    victoryScreen.setVisibility(View.GONE);
                    flippedCards = 0;
                    firstCard = null;
                    secondCard = null;
                    isBoardLocked = false;
                    initializeGame();
                })
                .start();
    }

    private void goToMenu() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToNextLevel() {
        int nextLevel = level + 1;
        int nextCardCount = 0;
        int nextColumns = 0;
        int nextRows = 0;

        // Определяем параметры следующего уровня
        switch (nextLevel) {
            case 2:
                nextCardCount = 16;
                nextColumns = 4;
                nextRows = 4;
                break;
            case 3:
                nextCardCount = 20;
                nextColumns = 4;
                nextRows = 5;
                break;
            default:
                goToMenu();
                return;
        }

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL", nextLevel);
        intent.putExtra("CARD_COUNT", nextCardCount);
        intent.putExtra("COLUMNS", nextColumns);
        intent.putExtra("ROWS", nextRows);
        startActivity(intent);
        finish();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход в меню")
                .setMessage("Вы действительно хотите выйти в главное меню?")
                .setPositiveButton("Да", (dialog, which) -> goToMenu())
                .setNegativeButton("Нет", null)
                .setCancelable(false)
                .show();
    }
}
