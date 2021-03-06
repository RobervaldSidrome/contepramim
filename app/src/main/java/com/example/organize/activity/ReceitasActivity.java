package com.example.organize.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organize.R;
import com.example.organize.config.ConfiguracaoFirebase;
import com.example.organize.helper.Base64Custom;
import com.example.organize.helper.DateCustom;
import com.example.organize.model.Movimentacao;
import com.example.organize.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private EditText editValor;
    private TextInputEditText editData;
    private TextInputEditText editCategoria;
    private TextInputEditText editDescricao;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        editValor = findViewById(R.id.editValor);
        editData = findViewById(R.id.editData);
        editCategoria = findViewById(R.id.editCategoria);
        editDescricao = findViewById(R.id.editDescricao);


        editData.setText(DateCustom.dataAtual());
        recuperarReceitaTotal();

    }

    public void salvarReceita(View view){

        if(validarCamposReceitas()){

            movimentacao = new Movimentacao();
            String data = editData.getText().toString();
            Double valorRecuperado =  Double.parseDouble(editValor.getText().toString());
            movimentacao.setValor( valorRecuperado );
            movimentacao.setCategoria(editCategoria.getText().toString());
            movimentacao.setDescricao(editDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita( receitaAtualizada );

            movimentacao.salvar(data);
            finish();

        }

    }

    public Boolean validarCamposReceitas(){

        String textoValor = editValor.getText().toString();
        String textoData = editData.getText().toString();
        String textoCategoria = editCategoria.getText().toString();
        String textoDescricao = editDescricao.getText().toString();

        if( !textoValor.isEmpty()){
            if( !textoData.isEmpty()){
                if( !textoCategoria.isEmpty()){
                    if( !textoDescricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(ReceitasActivity.this,
                                "Preencha o campo Descrição!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(ReceitasActivity.this,
                            "Preencha o campo Categoria!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(ReceitasActivity.this,
                        "Preencha o campo Data!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        }else{
            Toast.makeText(ReceitasActivity.this,
                    "Preencha o campo Valor!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    public void recuperarReceitaTotal(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseref
                .child("usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizarReceita(Double receita){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        DatabaseReference usuarioRef = firebaseref
                .child("usuarios")
                .child(idUsuario);


        usuarioRef.child("receitaTotal").setValue(receita);
    }
}
