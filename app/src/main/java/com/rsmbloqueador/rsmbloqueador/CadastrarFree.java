package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.os.Bundle;
import android.util.Base64;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;
import java.util.*;
import api.*;
import functions.*;
import models.*;

public class CadastrarFree extends AppCompatActivity {

    private AppDB appDB;
    private Config config;
    private Functions functions;

    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtSenha;
    private Button btnCadastrar;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastrar_veiculo);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home);
        //-----------------------------------------------------------

        appDB = new AppDB(CadastrarFree.this);
        config = new Config();
        functions = new Functions(CadastrarFree.this);

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        requestQueue = Volley.newRequestQueue(CadastrarFree.this);
        progressDialog = new ProgressDialog(CadastrarFree.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cadastrando...");

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.closeKeyboard(getCurrentFocus());

                if(validate()){
                    if(functions.statusInternet()){
                        checkPhoneAPI("rsm", "8av1dy1a3h");
                    }
                }
            }
        });
    }

    private boolean validate(){
        String nome = edtNome.getText().toString().trim();
        String telefone = edtTelefone.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if(nome.isEmpty()){
            edtNome.setError("Este campo é obrigatório.");
            edtNome.requestFocus();
            return false;
        }

        if(telefone.isEmpty()){
            edtTelefone.setError("Este campo é obrigatório.");
            edtTelefone.requestFocus();
            return false;

        } else if(telefone.length() < 10){
            edtTelefone.setError("Digite um telefone válido.");
            edtTelefone.requestFocus();
            return false;
        }

        if(senha.isEmpty()){
            edtSenha.setError("Este campo é obrigatório.");
            edtSenha.requestFocus();
            return false;

        } else if(senha.length() < 6){
            edtSenha.setError("Senha composta por 6 dígitos.");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    public void checkPhoneAPI(final String login, final String token){
        progressDialog.show();
        String url = Api.BASE_URL+"api/check_phone/"+edtTelefone.getText().toString().trim();
        url = url.replace(" ","%20");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();

                        if(response.optBoolean("status")){
                            addVeiculo();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        boolean serviceStatus = false;

                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.data != null){

                            switch(networkResponse.statusCode){
                                case 400:
                                    functions.alertDialog("Atenção", "Telefone inválido.", R.mipmap.ic_atencao, "Voltar");
                                    break;
                                case 404:
                                    functions.alertDialog("Atenção", "Este veículo não está cadastrado em nossa base de dados.", R.mipmap.ic_atencao, "Voltar");
                                    break;
                                case 401:
                                    serviceStatus = true;
                                    break;
                                default:
                                    serviceStatus = true;
                            }
                        } else{
                            serviceStatus = true;
                        }

                        if(serviceStatus){
                            functions.alertDialog("Atenção", "O serviço está indisponível no momento.\nPor favor, tente novamente mais tarde.", R.mipmap.ic_atencao, "Voltar");
                        }
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", login, token);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private void addVeiculo(){
        Veiculo veiculo = new Veiculo();
        veiculo.setNome(edtNome.getText().toString().trim());
        veiculo.setTelefone(edtTelefone.getText().toString().trim());
        veiculo.setSenha(edtSenha.getText().toString().trim());
        appDB.addVeiculo(veiculo, false);

        config.setConta(2);
        appDB.addConfig(config);

        functions.toast("Seja Bem-Vindo");
        Intent intent = new Intent(CadastrarFree.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(CadastrarFree.this, MainLogin.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CadastrarFree.this, MainLogin.class);
        startActivity(intent);
        finish();
    }
}