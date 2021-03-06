package com.example.henry.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    Button btnLogin;
    Button btnRegister;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        // Redirect to LoginActivity
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // Register for new User
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                if (!username.equals("") && !password.equals("")) {
                        new RegisterUser().execute(username, password);
                }

            }
        });
    }

    class RegisterUser extends AsyncTask<String, Void, Response> {

        @Override
        protected Response doInBackground(String... params) {

            try {
                String username = params[0];
                String password = params[1];
                URL url = new URL("http://10.0.3.2:8080/WebChat/api/users/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
                String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<user>" +
                        "<username>" + username + "</username>" +
                        "<password>" + password + "</password>" +
                        "</user>";
                OutputStream output = new BufferedOutputStream(conn.getOutputStream());
                output.write(body.getBytes());
                output.flush();
                output.close();
                InputStream is = conn.getInputStream();
                // receive a Response object from the server and parse it
                Response response = new ResponseParser().parse(is);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // If the response is success, redirect to ChatActivity
        @Override
        protected void onPostExecute(Response response) {

            try {
                if (response.getResult().equals("success")) {
                    Intent intent = new Intent();
                    intent.putExtra("username", response.getUsername());
                    intent.putExtra("sessionId", response.getSessionId());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_LONG).show();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
