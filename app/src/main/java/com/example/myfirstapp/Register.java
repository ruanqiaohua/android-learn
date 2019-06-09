package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfirstapp.Networking.AppNetworking;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {

        EditText userText = findViewById(R.id.editText5);
        EditText passwordText = findViewById(R.id.editText6);
        EditText password2Text = findViewById(R.id.editText7);

        String user = userText.getText().toString();
        String password = passwordText.getText().toString();
        String password2 = password2Text.getText().toString();

        if (password.equals(password2) == false) {
            Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "register?"+"&user="+user+"&password="+password;

        AppNetworking.getInstance().get(path, new AppNetworking.CallBack() {
            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(String json) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
}
