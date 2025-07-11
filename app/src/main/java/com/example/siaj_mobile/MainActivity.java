package com.example.siaj_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.Handler;
import android.widget.Toast;

import com.example.siaj_mobile.adapters.ProductAdapter;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    private OkHttpClient okHttpClient;
    private Gson gson;
    private Handler mainHandler;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        okHttpClient = new OkHttpClient();
        gson = new Gson();
        mainHandler = new Handler(Looper.getMainLooper());

        loginButton.setOnClickListener(view -> loginWithServer());
    }

    private void loginWithServer() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            notificar("Error", "Por favor, complete todos los campos", false);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Iniciando sesi贸n...");

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);

        String json = gson.toJson(usuario);

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);
        Log.d("Request:" , body.toString());

        Request request = new Request.Builder()
                .url(VariablesEntorno.getServerURL() + "/api/login")
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Iniciar Sesi贸n");
                    notificar("Error Cr铆tico", "Error de conexi贸n: " + e.getMessage(), false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                mainHandler.post(() -> {
                    loginButton.setEnabled(true);
                    loginButton.setText("Iniciar Sesi贸n");

                    try {
                        if (responseBody.trim().startsWith("{")) {
                            LoginResponseDTO resultado = gson.fromJson(responseBody, LoginResponseDTO.class);

                            if (resultado != null && resultado.isSuccess()) {
                                UsuarioDTO usuarioLogueado = resultado.getUsuario();

                                notificar("Inicio de sesi贸n exitoso", resultado.getMessage(), true);
                                goToDashboard();

                            } else {
                                notificar("Incorrecto", resultado != null ? resultado.getMessage() : "Respuesta inv谩lida", false);
                            }
                        } else {
                            notificar("Error", responseBody, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        notificar("Error", "Error al procesar la respuesta: " + e.getMessage(), false);
                    }
                });
            }
        });
    }

    private void goToDashboard() {
        Intent i = new Intent(MainActivity.this, ProductAdapter.class);
        startActivity(i);
        finish();
    }

    private void notificar(String titulo, String mensaje, boolean esExito) {
        String textoCompleto = titulo + ": " + mensaje;
        int duracion = esExito ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast.makeText(this, textoCompleto, duracion).show();
    }
}