package com.example.organize.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organize.R;
import com.example.organize.config.ConfiguracaoFirebase;
import com.example.organize.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.core.Context;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String textEmail = editEmail.getText().toString();
                String textPassword = editPassword.getText().toString();

                if( !textEmail.isEmpty()){
                    if( !textPassword.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail( textEmail );
                        usuario.setPassword( textPassword );
                        validarLogin();

                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Preencha o password",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o email",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void validarLogin(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,
                            "Autenticação realizada com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    abriTelaPrincipal();
                }else{

                    String excecao = "";
                    try{

                        throw task.getException();

                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Email e senha não correspondem a um usuário cadastrado";
                    } catch (FirebaseNetworkException e){
                        //isConnected();
                        Toast.makeText(LoginActivity.this,
                                "Precisa se conectar dados móveis ou wifi",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        excecao = "Erro no server..." + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //Método para verificar a conectividadse
    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if ( activeNetwork != null ) {
           //Verifica internet pela WIFI
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                Toast.makeText(LoginActivity.this,
                        "Conectado via WIFI",
                        Toast.LENGTH_SHORT).show();
                return true;
            }else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(LoginActivity.this,
                        "Conectado via Dados móveis",
                        Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(LoginActivity.this,
                        "Ative seu pacote de dados ou wifi",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return false;
    }

    public void abriTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

}
