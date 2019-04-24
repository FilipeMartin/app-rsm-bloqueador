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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;
import java.util.*;
import api.*;
import funcoes.*;
import functions.*;
import models.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class Configuracao extends Fragment {

    private AppDB appDB;
    private Usuario usuario;
    private Config config;
    private Functions functions;

    private Switch btnSecurityLogin;
    private Button btnPlatformToken;
    private Button btnCheckAppUpdate;
    private Button btnAppVersion;
    private View btnBar;

    private AlertDialog dialog;
    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    // FragmentManager
    private FragmentManager fm;
    //-------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracao, container, false);
        ((MainActivity)getActivity()).setVisibleFragments(false, false);

        // FragmentManager
        fm = getActivity().getSupportFragmentManager();
        //---------------------------------------------

        appDB = new AppDB(getActivity());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();
        functions = new Functions(getActivity());

        btnSecurityLogin = (Switch) view.findViewById(R.id.btnSecurityLogin);
        btnPlatformToken = (Button) view.findViewById(R.id.btnPlatformToken);
        btnCheckAppUpdate = (Button) view.findViewById(R.id.btnCheckAppUpdate);
        btnAppVersion = (Button) view.findViewById(R.id.btnAppVersion);
        btnBar = (View) view.findViewById(R.id.btnBar);

        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando...");

        if(config.getConta() == 2){
            btnPlatformToken.setVisibility(View.GONE);
            btnBar.setVisibility(View.GONE);
        }

        btnSecurityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSecurityLogin();
            }
        });

        // Start Switch
        setCheckedSwitch();
        //-----------------

        btnSecurityLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedSwitch();
            }
        });

        btnPlatformToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(functions.statusInternet()){
                    platformToken();
                }
            }
        });

        btnCheckAppUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAppUpdate checkAppUpdate = new CheckAppUpdate(getActivity(), true);
                if(!checkAppUpdate.check()){
                    functions.alertDialog("Atenção", "Sem conexão com a Internet !", R.mipmap.ic_atencao, "Voltar");
                }
            }
        });

        btnAppVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityBanner.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setCheckedSwitch() {
        if(Integer.toString(config.getLoginSenha()).length() > 1){
            btnSecurityLogin.setChecked(true);
        } else{
            btnSecurityLogin.setChecked(false);
        }
    }

    private void btnSecurityLogin(){
        if(Integer.toString(config.getLoginSenha()).length() > 1){
            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.login_seguranca, null);
            final Button btnAlterarLogin = (Button) view.findViewById(R.id.btnAlterarLogin);
            final Button btnCancelarLogin = (Button) view.findViewById(R.id.btnCancelarLogin);

            dlg.setView(view);
            final AlertDialog dialog = dlg.create();
            dialog.show();

            btnAlterarLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getActivity().getLayoutInflater().inflate(R.layout.login_seguranca_alterar, null);
                    final EditText edtSenhaAntiga = (EditText) view.findViewById(R.id.edtSenhaAntiga);
                    final EditText edtSenha = (EditText) view.findViewById(R.id.edtSenha);
                    final EditText edtConfirmarSenha = (EditText) view.findViewById(R.id.edtConfirmarSenha);
                    final Button btnAlterar = (Button) view.findViewById(R.id.btnAlterar);
                    final TextView txtAlerta = (TextView) view.findViewById(R.id.txtAlerta);
                    txtAlerta.setVisibility(View.GONE);

                    dlg.setView(view);
                    final AlertDialog dialoge = dlg.create();
                    dialoge.show();

                    edtSenhaAntiga.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            txtAlerta.setVisibility(View.GONE);
                        }
                    });

                    edtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            txtAlerta.setVisibility(View.GONE);
                        }
                    });

                    edtConfirmarSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            txtAlerta.setVisibility(View.GONE);
                        }
                    });

                    btnAlterar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senhaBD = Integer.toString(config.getLoginSenha());
                            String senhaAntiga = edtSenhaAntiga.getText().toString().trim();
                            String senha = edtSenha.getText().toString().trim();
                            String confirmarSenha = edtConfirmarSenha.getText().toString().trim();

                            if(senhaAntiga.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()){
                                txtAlerta.setText("Campos incompletos !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else if(!senhaAntiga.equals(senhaBD)){
                                txtAlerta.setText("Senha antiga inválida !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else if(senha.length() < 4) {
                                txtAlerta.setText("A nova senha deve ter no mínimo 4 dígitos !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else if(!senha.equals(confirmarSenha)){
                                txtAlerta.setText("A confirmação da senha não confere !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else{
                                appDB.editConfig("LOGIN_SENHA", Integer.parseInt(edtSenha.getText().toString().trim()));

                                functions.toast("Senha Alterada com Sucesso !");
                                Intent intent = new Intent(getActivity(), LoginSeguranca.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    });
                }
            });

            btnCancelarLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getActivity().getLayoutInflater().inflate(R.layout.login_seguranca_cancelar, null);
                    final EditText edtSenha = (EditText) view.findViewById(R.id.edtSenha);
                    final Button btnCancelar = (Button) view.findViewById(R.id.btnCancelar);
                    final TextView txtAlerta = (TextView) view.findViewById(R.id.txtAlerta);
                    txtAlerta.setVisibility(View.GONE);

                    dlg.setView(view);
                    final AlertDialog dialog = dlg.create();
                    dialog.show();

                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String senhaBD = Integer.toString(config.getLoginSenha());
                            String senha = edtSenha.getText().toString().trim();

                            if(senha.isEmpty()){
                                txtAlerta.setText("Campo incompleto !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else if(!senha.equals(senhaBD)){
                                txtAlerta.setText("Senha inválida !");
                                txtAlerta.setVisibility(View.VISIBLE);

                            } else{
                                dialog.dismiss();

                                appDB.editConfig("LOGIN_SENHA", 0);

                                functions.toast("Login Cancelado !");
                                Configuracao configuracao = (Configuracao) fm.findFragmentByTag("Configuracao");

                                if(configuracao != null){
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.detach(configuracao);
                                    ft.attach(configuracao);
                                    ft.commit();
                                }
                            }
                        }
                    });
                }
            });

        } else{
            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.login_seguranca_cadastrar, null);
            final EditText edtSenha = (EditText) view.findViewById(R.id.edtSenha);
            final EditText edtConfirmarSenha = (EditText) view.findViewById(R.id.edtConfirmarSenha);
            final Button btnCadastrar = (Button) view.findViewById(R.id.btnCadastrar);
            final TextView txtAlerta = (TextView) view.findViewById(R.id.txtAlerta);
            txtAlerta.setVisibility(View.GONE);

            dlg.setView(view);
            final AlertDialog dialog = dlg.create();
            dialog.show();

            btnCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String senha = edtSenha.getText().toString().trim();
                    String confirmarSenha = edtConfirmarSenha.getText().toString().trim();

                    edtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            txtAlerta.setVisibility(View.GONE);
                        }
                    });

                    edtConfirmarSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            txtAlerta.setVisibility(View.GONE);
                        }
                    });

                    if(senha.isEmpty() || confirmarSenha.isEmpty()){
                        txtAlerta.setText("Campos incompletos !");
                        txtAlerta.setVisibility(View.VISIBLE);

                    } else if(senha.length() < 4){
                        txtAlerta.setText("A senha deve ter no mínimo 4 dígitos !");
                        txtAlerta.setVisibility(View.VISIBLE);

                    } else if(!senha.equals(confirmarSenha)){
                        txtAlerta.setText("A confirmação da senha não confere !");
                        txtAlerta.setVisibility(View.VISIBLE);

                    } else{
                        appDB.editConfig("LOGIN_SENHA", Integer.parseInt(senha));

                        Intent intent = new Intent(getActivity(), LoginSeguranca.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    public void platformToken(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_plataforma_token, null);
        final EditText editToken = (EditText) view.findViewById(R.id.editToken);
        final Button btnSalvar = (Button) view.findViewById(R.id.btnSalvar);
        final ImageButton btnDuvida = (ImageButton) view.findViewById(R.id.btnDuvida);
        final Button btnOkDuvida = (Button) view.findViewById(R.id.btnOkDuvida);
        final LinearLayout layoutDuvida = (LinearLayout) view.findViewById(R.id.layoutDuvida);

        dlg.setView(view);
        dialog = dlg.create();
        dialog.show();

        editToken.setText(usuario.getPlatformToken());

        btnDuvida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDuvida.setVisibility(View.VISIBLE);
            }
        });

        btnOkDuvida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDuvida.setVisibility(View.GONE);
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(functions.statusInternet()){
                    if(editToken.getText().toString().trim().length() != 0 && editToken.getText().toString().trim().length() != 32){
                        functions.alertDialog("Atenção", "Token Inválido !", R.mipmap.ic_atencao, "Voltar");
                    } else{
                        platformTokenAPI(editToken.getText().toString().trim());
                    }
                }
            }
        });
    }

    public void platformTokenAPI(final String token){
        progressDialog.show();
        String url = Api.BASE_URL+"api/users/platform_token/";

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        progressDialog.hide();

                        try{
                            JSONObject objUsuario = new JSONObject(response);

                            usuario.setPlatformToken(objUsuario.optString("platformToken"));
                            appDB.editUsuario("platformtoken", usuario.getPlatformToken());

                            if(usuario.getPlatformToken().length() > 0){
                                functions.toast("Token Salvo com Sucesso !");
                            } else{
                                functions.toast("Token Removido com Sucesso !");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                                    try {
                                        JSONObject jsonError = new JSONObject(new String(networkResponse.data));
                                        int codError = jsonError.optInt("error");
                                        String msgError = jsonError.optString("message");

                                        switch (codError){
                                            case 1:
                                                functions.alertDialog("Atenção", msgError, R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 2:
                                                functions.alertDialog("Atenção", "Token Inválido !", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 3:
                                                functions.alertDialog("Atenção", "Nenhuma informação foi alterada !", R.mipmap.ic_atencao, "Voltar");
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
                params.put("platformToken", token);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", "rsm", usuario.getToken());
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
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Configurações");
        ((MainActivity)getActivity()).btnAtalhoCadastrar(false);
    }
}