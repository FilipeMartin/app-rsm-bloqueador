package com.rsmbloqueador.rsmbloqueador;

import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import functions.*;
import models.*;

public class LoginSeguranca extends AppCompatActivity {

    private AppDB appDB;
    private Config config;
    private Functions functions;

    private EditText edtSenha;
    private Button btnLogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_seguranca);

        appDB = new AppDB(LoginSeguranca.this);
        config = appDB.getConfig();
        functions = new Functions(LoginSeguranca.this);

        edtSenha = (EditText) findViewById(R.id.edtSenha);
        btnLogar = (Button) findViewById(R.id.btnLogar);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.closeKeyboard(getCurrentFocus());

                String senhaBD = Integer.toString(config.getLoginSenha());
                String senha = edtSenha.getText().toString().trim();

                if(senha.isEmpty()){
                    functions.alertDialog("Atenção", "Campo incompleto !", R.mipmap.ic_atencao, "Voltar");

                } else if(!senha.equals(senhaBD)){
                    functions.alertDialog("Atenção", "Senha inválida !", R.mipmap.ic_atencao, "Voltar");

                } else{
                    functions.toast("Seja Bem-Vindo");
                    Intent intent = new Intent(LoginSeguranca.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}