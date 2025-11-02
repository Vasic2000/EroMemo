package ru.vasic2000.eromemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class Card extends androidx.appcompat.widget.AppCompatImageButton {
    private int imageResource;
    private boolean isFlipped = false;
    private boolean isMatched = false;
    private Drawable frontImage;
    private Drawable backImage;


    public Card(Context context) {
        super(context);
        init();
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Создаем градиент для заднего фона
        GradientDrawable back = new GradientDrawable();
        back.setColor(Color.parseColor("#2196F3")); // Синий цвет
        back.setCornerRadius(16f);
        back.setStroke(4, Color.BLACK);

        // Добавляем узор на рубашку
        GradientDrawable pattern = new GradientDrawable();
        pattern.setShape(GradientDrawable.OVAL);
        pattern.setColor(Color.parseColor("#1976D2"));
        pattern.setSize(30, 30);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{back, pattern});
        backImage = layerDrawable;

        setBackground(backImage);
        setScaleType(ImageView.ScaleType.CENTER_CROP);

    }

    public void setImageResource(int resourceId) {
        this.imageResource = resourceId;
        this.frontImage = ContextCompat.getDrawable(getContext(), resourceId);
    }

    public int getImageResource() {
        return imageResource;
    }

    public void flip() {
        if (isMatched) return;

        if (!isFlipped) {
            // Анимация переворота
            ObjectAnimator flip = ObjectAnimator.ofFloat(this, "rotationY", 0f, 90f);
            flip.setDuration(150);
            flip.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setImageDrawable(frontImage);
                    setBackground(null);

                    ObjectAnimator flipBack = ObjectAnimator.ofFloat(Card.this, "rotationY", 90f, 0f);
                    flipBack.setDuration(150);
                    flipBack.start();

                    isFlipped = true;
                }
            });
            flip.start();
        } else {
            // Анимация переворота обратно
            ObjectAnimator flip = ObjectAnimator.ofFloat(this, "rotationY", 0f, 90f);
            flip.setDuration(150);
            flip.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setImageDrawable(null);
                    setBackground(backImage);

                    ObjectAnimator flipBack = ObjectAnimator.ofFloat(Card.this, "rotationY", 90f, 0f);
                    flipBack.setDuration(150);
                    flipBack.start();

                    isFlipped = false;
                }
            });
            flip.start();
        }
    }

    public void setMatched() {
        isMatched = true;
        // Анимация исчезновения для совпавших карточек
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.25f);
        fadeOut.setDuration(250);
        fadeOut.start();
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public boolean isMatched() {
        return isMatched;
    }
}
