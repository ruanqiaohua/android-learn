package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myfirstapp.Networking.AppNetworking;
import com.example.myfirstapp.model.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.TextView.BufferType.NORMAL;

public class UserActivity extends AppCompatActivity {

    public User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getUserInfo();
    }

    public void getUserInfo() {

        AppNetworking.getInstance().post("user/getUserInfo", null, new AppNetworking.CallBack() {
            @Override
            public void onFailure(int code, String msg) {

            }

            @Override
            public void onSuccess(String json) {

                JSONObject object = null;
                try {
                    object = new JSONObject(json).getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                user= gson.fromJson(object.toString(), User.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
    }

    public void updateUI() {

        ImageView imageView = findViewById(R.id.imageView2);
        Picasso.get().load(user.avatar).into(imageView);

        EditText editText = findViewById(R.id.username);
        editText.setText(user.user, NORMAL);
    }

}
