package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;
import java.util.*;
import api.*;
import functions.*;
import models.*;

public class CadastrarPro extends AppCompatActivity {

    private Usuario usuario;
    private Controle controle;
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

        controle = new Controle(CadastrarPro.this);
        functions = new Functions(CadastrarPro.this);

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtTelefone = (EditText) findViewById(R.id.edtTelefone);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        requestQueue = Volley.newRequestQueue(CadastrarPro.this);
        progressDialog = new ProgressDialog(CadastrarPro.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cadastrando...");

        Bundle bundle = getIntent().getBundleExtra("Controle");

        if(bundle != null){
            usuario = (Usuario) bundle.getSerializable("Usuario");

        } else{
            functions.toast("Ops! Tente efetuar o login novamente !");
            Intent intent = new Intent(getApplicationContext(), MainLogin.class);
            startActivity(intent);
            finish();
        }

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.closeKeyboard(getCurrentFocus());

                if(validate()){
                    if(functions.statusInternet()){
                        cadastrarAPI("rsm", usuario.getToken());
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

    public void cadastrarAPI(final String login, final String token){
        progressDialog.show();
        String url = Api.BASE_URL+"api/vehicles/";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        controle.run3(usuario, response);
                        finish();
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
                                    try {
                                        JSONObject jsonError = new JSONObject(new String(networkResponse.data));
                                        int codError = jsonError.optInt("error");
                                        String msgError = jsonError.optString("message");

                                        switch (codError){
                                            case 1:
                                                functions.alertDialog("Atenção", msgError, R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 2:
                                                functions.alertDialog("Atenção", "Este veículo não está cadastrado em nossa base de dados.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 3:
                                                functions.alertDialog("Atenção", "Não foi possível cadastrar o veículo.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 4:
                                                final String jsonVehicle = jsonError.getString("vehicle");

                                                AlertDialog.Builder dlg = new AlertDialog.Builder(CadastrarPro.this);
                                                dlg
                                                        .setTitle("Download")
                                                        .setIcon(R.mipmap.ic_cloud)
                                                        .setCancelable(false)
                                                        .setMessage("Este veículo consta como cadastrado !\nSeu download foi realizado com sucesso.")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                controle.run3(usuario, jsonVehicle);
                                                                finish();
                                                            }
                                                        })
                                                        .show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 401:
                                    functions.toast("Você foi deslogado por medidas de segurança !");
                                    Intent intent = new Intent(getApplicationContext(), MainLogin.class);
                                    startActivity(intent);
                                    finish();
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String>  params = new HashMap<String, String>();
                params.put("name", edtNome.getText().toString().trim());
                params.put("phone", edtTelefone.getText().toString().trim());
                params.put("password", edtSenha.getText().toString().trim());
                params.put("model", " ");
                params.put("category", " ");

                return params;
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(CadastrarPro.this, MainLogin.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CadastrarPro.this, MainLogin.class);
        startActivity(intent);
        finish();
    }
}