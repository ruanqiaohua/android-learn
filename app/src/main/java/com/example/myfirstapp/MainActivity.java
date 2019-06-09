package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfirstapp.Networking.AppNetworking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myFirstApp.MESSAGE";
    public String user = "";
    public String password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("Main", MODE_PRIVATE);
        user = sharedPreferences.getString("user","");
        password = sharedPreferences.getString("password","");
        String token = sharedPreferences.getString("token","");
        AppNetworking.getInstance().token = token;

        autoLogin();
    }

    public void login(View view) {

        EditText usernameText = findViewById(R.id.editText);
        EditText passwordText = findViewById(R.id.editText2);
        user = usernameText.getText().toString();
        password = passwordText.getText().toString();

        if (user.length() == 0 || password.length() == 0) {
            Toast.makeText(MainActivity.this, "用户名密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        autoLogin();
    }

    public void autoLogin() {

        if (user.length() == 0 || password.length() == 0) {
            return;
        }

        HashMap map = new HashMap();
        map.put("user", user);
        map.put("password", password);

        AppNetworking.getInstance().post("login", map, new AppNetworking.CallBack() {
            @Override
            public void onFailure(int code, String msg) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onSuccess(final String json) {

                try {
                    JSONObject object = new JSONObject(json).getJSONObject("data");
                    String token = object.getString("token");
                    Log.d("TAG", token);
                    AppNetworking.getInstance().token = token;
                    // save token
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("Main", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user",user);
                    editor.putString("password",password);
                    editor.putString("token",token);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessage();
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    public void sendMessage() {
        Intent intent = new Intent(this, UserActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void register(View view) {
        startActivity(new Intent(this, Register.class));
    }
}
