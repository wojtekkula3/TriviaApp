package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.triviaapp.data.AsyncResponse;
import com.example.triviaapp.data.Repository;
import com.example.triviaapp.databinding.ActivityMainBinding;
import com.example.triviaapp.model.Question;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // zmienna klucz, która zapisuje wyniki oraz osatani stan aplikacji (pytanie)
    private static final String SCORES_ID = "scores";
    ActivityMainBinding binding;
    int currentQuestionNumber = 0;
    int score = 0;
    int highestScore = 0;
    int applicationState = 0;

    Repository repo = new Repository();
    List<Question> questionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set latest score and question from database;
        SharedPreferences sharedPreferences = getSharedPreferences(SCORES_ID, MODE_PRIVATE);
        highestScore=sharedPreferences.getInt("highest_score", 0);
        currentQuestionNumber=sharedPreferences.getInt("last_state", 0);
        score=sharedPreferences.getInt("last_score",0);

        setScore();
        questionList = repo.getQuestions(new AsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                Log.d("Main", "onCreate: " + questionArrayList);
                changeQuestion(questionArrayList);

            }
        });

        binding.nextButton.setOnClickListener( (view -> {
            currentQuestionNumber = (currentQuestionNumber + 1) % (questionList.size());
            changeQuestion(questionList);
        }));

        binding.trueButton.setOnClickListener(view -> {
            if(questionList.get(currentQuestionNumber).isAnwsered())
            {
                Snackbar.make(binding.questionCardView, "You already answered",Snackbar.LENGTH_LONG)
                        .setTextColor(Color.RED)
                        .show();
            }
            else
            {
                checkAnwser(true);
                changeQuestion(questionList);
                questionList.get(currentQuestionNumber).setAnwsered(true);
            }
        });

        binding.falseButton.setOnClickListener( view -> {
            if(questionList.get(currentQuestionNumber).isAnwsered())
            {
                Snackbar.make(binding.questionCardView, "You already answered",Snackbar.LENGTH_LONG)
                        .setTextColor(Color.RED)
                        .show();            }
            else
            {
                checkAnwser(false);
                changeQuestion(questionList);
                questionList.get(currentQuestionNumber).setAnwsered(true);
            }
        });

        binding.resetScoreButton.setOnClickListener(view -> {
            currentQuestionNumber = 0;
            score = 0;
            setScore();
            changeQuestion(questionList);
            for (Question question: questionList) {
                question.setAnwsered(false);
            }
        });

        binding.previousButton.setOnClickListener(view -> {
            if (currentQuestionNumber != 0)
                currentQuestionNumber = (currentQuestionNumber-1) % (questionList.size());
            changeQuestion(questionList);

        });
    }

    private void setScore() {
        binding.scoreTextView.setText(getString(R.string.score, score));
        binding.bestScoreTextView.setText(getString(R.string.bestScore, highestScore));

    }

    private void changeQuestion(List<Question> questionList) {
        binding.questionTextView.setText(questionList.get(currentQuestionNumber).getName());
        binding.questionNumberTextView.setText(String.format(getString(R.string.questionNumber), currentQuestionNumber + 1, questionList.size()));
    }

    private void checkAnwser(boolean userAnwser) {
        boolean isTrue = questionList.get(currentQuestionNumber).isTrue();
        if(isTrue == userAnwser)
        {
            score = score +10;
            if(highestScore<score)
                highestScore=score;
            fadeCardView();

        }
        else if(isTrue != userAnwser)
        {
            if(score!=0)
                score = score -10;
            shakeCardView();
        }

        setScore();
    }

    private void fadeCardView()
    {
        AlphaAnimation fade = new AlphaAnimation(1.0f,0.0f);
        fade.setDuration(500);
        fade.setRepeatCount(1);
        fade.setRepeatMode(Animation.REVERSE);
        binding.questionCardView.setAnimation(fade);

        fade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                currentQuestionNumber++;
                changeQuestion(questionList);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeCardView()
    {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        binding.questionCardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                currentQuestionNumber++;
                changeQuestion(questionList);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences(SCORES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putInt("highest_score", highestScore);
        editor.putInt("last_score", score);
        editor.putInt("last_state", currentQuestionNumber);
        editor.apply();
        super.onStop();
    }
}