package com.example.triviaapp.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.triviaapp.controller.AppController;
import com.example.triviaapp.model.Question;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    private ArrayList<Question> questions = new ArrayList<>();

    String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public List<Question> getQuestions(final AsyncResponse callBack) {
        JsonArrayRequest getQuestions = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        Question question = new Question();
                        Log.d("test", question.toString());
                        try {
                            question.setName(response.getJSONArray(i).getString(0));
                            question.setTrue(response.getJSONArray(i).getBoolean(1));
                            questions.add(question);
                            Log.d("test", question.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != callBack) callBack.processFinished(questions);

                    },
                error -> {
                    Log.d("question_error", "getQuestions: ERROR");

                });

        AppController.getInstance().addToRequestQueue(getQuestions);
        return questions;
    }
}
