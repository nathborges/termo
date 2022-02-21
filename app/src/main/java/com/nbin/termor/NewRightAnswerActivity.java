package com.nbin.termor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Locale;

public class NewRightAnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_user_input);

        ImageView confirmButton = findViewById(R.id.confirm_a_new_word_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText =  findViewById(R.id.new_word_input);
                CharSequence newWordChar = editText.getText();
                String newWord = String.valueOf(newWordChar).toUpperCase(Locale.ROOT);
                if (!validateWord(newWord)) {
                    return;
                }
                Intent i = new Intent(NewRightAnswerActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("WORD", newWord);
                i.putExtras(bundle);
                startActivity(i);
                finish();
            }
        });

        ImageView newWordButton = findViewById(R.id.new_word_button);
        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate();
            }
        });

        ImageView helpButton = findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate();
            }
        });
    }

    protected boolean validateWord(String word) {
        if (5 != word.length()) {
            animate();
            return false;
        }
        return true;
    }

    protected void animate(){
        View editText =  (View)findViewById(R.id.new_word_input);
        View confirmButton =  (View)findViewById(R.id.confirm_a_new_word_button);
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        editText.startAnimation(shake);
        confirmButton.startAnimation(shake);
    }
}