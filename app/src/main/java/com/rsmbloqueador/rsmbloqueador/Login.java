package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.*;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.*;
import java.util.*;
import api.*;
import functions.*;
import models.*;

public class Login extends Fragment {

    private AppDB appDB;
    private Config config;

    private EditText edtLogin;
    private EditText edtSenha;
    private Button btnLogar;
    private Functions functions;
    private AlertDialog dialog;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        appDB = new AppDB(getActivity());
        config = appDB.getConfig();

        edtLogin = (EditText) view.findViewById(R.id.edtLogin);
        edtSenha = (EditText) view.findViewById(R.id.edtSenha);
        btnLogar = (Button) view.findViewById(R.id.btnLogar);
        functions = new Functions(getActivity());

        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Autenticando...");

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.closeKeyboard(getActivity().getCurrentFocus());

                if(validate()){
                    if(functions.statusInternet()){
                        sessionAPI();
                    }
                }
            }
        });

        return view;
    }

    private boolean validate(){
        String login = edtLogin.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if(login.isEmpty()){
            edtLogin.setError("Este campo é obrigatório.");
            edtLogin.requestFocus();
            return false;
        }

        if(senha.isEmpty()){
            edtSenha.setError("Este campo é obrigatório.");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    private void sessionAPI(){
        progressDialog.show();
        String url = Api.BASE_URL+"api/session/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        cargaAPI("rsm", response.optString("token"));
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

                                        switch (codError){
                                            case 1:
                                                notification(2);
                                                break;
                                            case 2:
                                                notification(1);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 401:
                                    functions.alertDialog("Atenção", "Login e/ou senha inválidos !", R.mipmap.ic_atencao, "Voltar");
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
                String creds = String.format("%s:%s", edtLogin.getText().toString().trim(), edtSenha.getText().toString().trim());
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

    private void cargaAPI(final String login, final String token){
        String url = Api.BASE_URL+"api/carga/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();

                        if(config.getConta() > 0){ appDB.cleanDB(); }

                        Controle controle = new Controle(getActivity());
                        controle.run2(response);
                        getActivity().finish();
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
                                case 401:
                                    edtLogin.setText("");
                                    edtSenha.setText("");
                                    functions.alertDialog("Ops!", "Tente efetuar o login novamente !", R.mipmap.ic_atencao, "Voltar");
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

    private void notification(int type){
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_contato, null);
        final TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        final TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        final ImageView btnWhatsApp = (ImageView) view.findViewById(R.id.btnWhatsApp);
        final TextView txtPhone1 = (TextView) view.findViewById(R.id.txtPhone1);
        final TextView txtPhone2 = (TextView) view.findViewById(R.id.txtPhone2);
        final Button btnOk = (Button) view.findViewById(R.id.btnOk);

        switch(type){
            case 1:
                txtTitle.setText("Desculpe, sua conta expirou.");
                txtMessage.setText("Para renovar o serviço:");
                break;
            case 2:
                txtTitle.setText("Desculpe, sua conta está desativada.");
                txtMessage.setVisibility(View.GONE);
        }

        dlg.setView(view);
        dialog = dlg.create();
        dialog.show();

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521998259550"));
                startActivity(intent);
            }
        });

        txtPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521998259550"));
                startActivity(intent);
            }
        });

        txtPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521996396999"));
                startActivity(intent);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}