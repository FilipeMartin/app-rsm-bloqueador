package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;
import java.util.*;
import api.*;
import functions.*;
import models.*;

public class Cadastrar extends Fragment {

    private AppDB appDB;
    private Usuario usuario;
    private Config config;
    private Functions functions;

    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtSenha;
    private Button btnCadastrar;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    // FragmentManager
    FragmentManager fm;
    //-----------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.cadastrar_veiculo, container, false);
        ((MainActivity)getActivity()).setVisibleFragments(false, false);

        // FragmentManager
        fm = getActivity().getSupportFragmentManager();
        //---------------------------------------------

        appDB = new AppDB(getContext());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();
        functions = new Functions(getActivity());

        edtNome = (EditText)view.findViewById(R.id.edtNome);
        edtTelefone = (EditText)view.findViewById(R.id.edtTelefone);
        edtSenha = (EditText)view.findViewById(R.id.edtSenha);
        btnCadastrar = (Button)view.findViewById(R.id.btnCadastrar);

        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cadastrando...");

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.closeKeyboard(getActivity().getCurrentFocus());

                if(validate()){
                    if(functions.statusInternet()){

                        if(config.getConta() == 1){
                            cadastrarAPI("rsm", usuario.getToken());

                        } else{
                            if(appDB.checkVeiculo(edtTelefone.getText().toString().trim()) == 0){
                                checkPhoneAPI("rsm", "8av1dy1a3h");
                            } else{
                                functions.alertDialog("Atenção", "Este veículo já está cadastrado.", R.mipmap.ic_atencao, "Voltar");
                            }
                        }
                    }
                }
            }
        });

        return view;
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

                        int idVeiculo = appDB.checkVeiculo(edtTelefone.getText().toString().trim());
                        if(idVeiculo > 0){
                            appDB.deleteVeiculo(idVeiculo);
                        }

                        addVeiculo(response, 1);
                        functions.toast("Veículo Cadastrado com Sucesso !");
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
                                                if(appDB.checkVeiculo(edtTelefone.getText().toString().trim()) == 0){
                                                    addVeiculo(jsonError.getString("vehicle"), 1);
                                                    functions.alertDialog("Download", "Este veículo consta como cadastrado !\nSeu download foi realizado com sucesso.", R.mipmap.ic_cloud, "Ok");
                                                } else{
                                                    functions.alertDialog("Atenção", "Este veículo já está cadastrado nesta conta.", R.mipmap.ic_atencao, "Voltar");
                                                }
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 401:
                                    appDB.cleanDB();
                                    functions.toast("Você foi deslogado por medidas de segurança !");
                                    Intent intent = new Intent(getActivity(), MainLogin.class);
                                    startActivity(intent);
                                    getActivity().finish();
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
                HashMap<String, String> params = new HashMap<String, String>();
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
                            addVeiculo(null, 2);
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

    private void addVeiculo(String jsonVehicle, int conta){
        Veiculo veiculo = new Veiculo();

        if(conta == 1){
            try{
                JSONObject objVehicle = new JSONObject(jsonVehicle);

                veiculo.setId(objVehicle.optInt("id"));
                veiculo.setImei(objVehicle.optString("imei"));
                veiculo.setNome(objVehicle.optString("name"));
                veiculo.setTelefone(objVehicle.optString("phone"));
                veiculo.setSenha(objVehicle.optString("password"));
                veiculo.setModelo(objVehicle.optString("model"));
                veiculo.setCategoria(objVehicle.optString("category"));
                veiculo.setData(objVehicle.optString("date"));
                veiculo.setStatus(objVehicle.optInt("status"));

                appDB.addVeiculo(veiculo, true);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else{
            veiculo.setNome(edtNome.getText().toString().trim());
            veiculo.setTelefone(edtTelefone.getText().toString().trim());
            veiculo.setSenha(edtSenha.getText().toString().trim());

            appDB.addVeiculo(veiculo, false);
        }

        limparCampos();
        ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

        if(listarVeiculos != null){
            fm.popBackStack("ListarVeiculos", 0);
        } else{
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
            ft.addToBackStack("ListarVeiculos");
            ft.commit();
        }
    }

    private void limparCampos(){
        edtNome.getText().clear();
        edtTelefone.getText().clear();
        edtSenha.getText().clear();
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Cadastrar Veículo");
        ((MainActivity)getActivity()).btnAtalhoCadastrar(false);
    }
}