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

/**
 * A simple {@link Fragment} subclass.
 */
public class EditarVeiculo extends Fragment implements View.OnClickListener {

    private AppDB appDB;
    private Usuario usuario;
    private Veiculo veiculo;
    private Config config;
    private Functions functions;

    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtSenha;
    private EditText edtNovaSenha;

    private Button btnEditar;
    private Button btnEditarSenha;

    private TextView txtCampoObrigatorio1;
    private TextView txtCampoObrigatorio2;
    private TextView txtCampoObrigatorio3;
    private TextView txtCampoObrigatorio4;

    private AlertDialog desbloquearAlertDialogSenha;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    // FragmentManager
    FragmentManager fm;
    //-----------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_veiculo, container, false);
        ((MainActivity)getActivity()).setVisibleFragments(false, false);

        // FragmentManager
        fm = getActivity().getSupportFragmentManager();
        //---------------------------------------------

        appDB = new AppDB(getActivity());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();
        veiculo = appDB.getVeiculo(config.getVeiculoEditar());
        functions = new Functions(getActivity());

        txtCampoObrigatorio1 = (TextView) view.findViewById(R.id.txtCampoObrigatorio1);
        txtCampoObrigatorio2 = (TextView) view.findViewById(R.id.txtCampoObrigatorio2);
        txtCampoObrigatorio3 = (TextView) view.findViewById(R.id.txtCampoObrigatorio3);
        txtCampoObrigatorio4 = (TextView) view.findViewById(R.id.txtCampoObrigatorio4);
        txtCampoObrigatorio1.setOnClickListener(this);
        txtCampoObrigatorio2.setOnClickListener(this);
        txtCampoObrigatorio3.setOnClickListener(this);
        txtCampoObrigatorio4.setOnClickListener(this);

        edtNome = (EditText) view.findViewById(R.id.edtNome);
        edtTelefone = (EditText) view.findViewById(R.id.edtTelefone);
        edtSenha = (EditText) view.findViewById(R.id.edtSenha);
        edtNovaSenha = (EditText) view.findViewById(R.id.edtNovaSenha);
        btnEditar = (Button) view.findViewById(R.id.btnEditar);
        btnEditarSenha = (Button) view.findViewById(R.id.btnEditarSenha);

        edtNome.setText(veiculo.getNome());
        edtTelefone.setText(veiculo.getTelefone());
        edtSenha.setText(veiculo.getSenha());

        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Editando...");

        btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editar();
                }
            });

        btnEditarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.alertDialog("Atenção", "Função desativada !", R.mipmap.ic_atencao, "Voltar");
                //editarSenha();
            }
        });

            return view;
        }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Editar Cadastro");
        ((MainActivity)getActivity()).btnAtalhoCadastrar(false);
    }

    private void editar(){
        functions.closeKeyboard(getActivity().getCurrentFocus());

        if(validate()){
            if(functions.statusInternet()){

                if(config.getConta() == 1){
                    veiculo.setNome(edtNome.getText().toString().trim());
                    veiculo.setTelefone(edtTelefone.getText().toString().trim());

                    editarAPI("rsm", usuario.getToken());

                } else{

                    if((!veiculo.getNome().equals(edtNome.getText().toString().trim())) || (!veiculo.getTelefone().equals(edtTelefone.getText().toString().trim()))) {

                        if(appDB.checkVeiculo(edtTelefone.getText().toString().trim()) == 0 || veiculo.getTelefone().equals(edtTelefone.getText().toString().trim())){
                            checkPhoneAPI("rsm", "8av1dy1a3h");
                        } else{
                            functions.alertDialog("Atenção", "Este veículo já está cadastrado.", R.mipmap.ic_atencao, "Voltar");
                        }

                    } else{
                        functions.alertDialog("Atenção", "Nenhuma informação foi alterada !", R.mipmap.ic_atencao, "Voltar");
                    }
                }
            }
        }
    }

    private boolean validate(){
        String nome = edtNome.getText().toString().trim();
        String telefone = edtTelefone.getText().toString().trim();

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

        return true;
    }

    public void editarAPI(final String login, final String token){
        progressDialog.show();
        String url = Api.BASE_URL+"api/vehicles/"+veiculo.getId();
        url = url.replace(" ","%20");

        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        aditVeiculo(response, 1);
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
                                                functions.alertDialog("Atenção", "Identificador inválido.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 2:
                                                functions.alertDialog("Atenção", msgError, R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 3:
                                                functions.alertDialog("Atenção", "Este veículo não está cadastrado em nossa base de dados.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 4:
                                                functions.alertDialog("Atenção", "Não foi possível editar o veículo.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 5:
                                                functions.alertDialog("Atenção", "Este veículo já está cadastrado nesta conta.", R.mipmap.ic_atencao, "Voltar");
                                                break;
                                            case 6:
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
                params.put("name", veiculo.getNome());
                params.put("phone", veiculo.getTelefone());
                params.put("password", veiculo.getSenha());
                params.put("model", veiculo.getModelo());
                params.put("category", veiculo.getCategoria());

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
                            aditVeiculo(null, 2);
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

    private void aditVeiculo(String jsonVehicle, int conta){

        if(conta == 1){
            try{
                JSONObject objVehicle = new JSONObject(jsonVehicle);

                veiculo.setImei(objVehicle.optString("imei"));
                veiculo.setNome(objVehicle.optString("name"));
                veiculo.setTelefone(objVehicle.optString("phone"));
                veiculo.setSenha(objVehicle.optString("password"));
                veiculo.setModelo(objVehicle.optString("model"));
                veiculo.setCategoria(objVehicle.optString("category"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else{
            veiculo.setNome(edtNome.getText().toString().trim());
            veiculo.setTelefone(edtTelefone.getText().toString().trim());
        }

        appDB.editVeiculo(veiculo);

        if(config.getVeiculoAtual() == veiculo.getId()){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();

        } else{
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

        functions.toast("Veículo Editado com Sucesso !");
    }

    private void editarSenha(){

            if (edtSenha.getText().toString().trim().isEmpty() || edtNovaSenha.getText().toString().trim().isEmpty()) {
                functions.alertDialog("Atenção", "Preencha todos os campos !", R.mipmap.ic_atencao, "Voltar");

            } else if (edtSenha.length() < 6 || edtNovaSenha.length() < 6) {
                functions.alertDialog("Atenção", "Senha composta por 6 dígitos !", R.mipmap.ic_atencao, "Voltar");

            } else {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(this.veiculo.getId());
                veiculo.setNome(edtNome.getText().toString().trim());
                veiculo.setTelefone(edtTelefone.getText().toString().trim());
                veiculo.setSenha(edtSenha.getText().toString().trim());
                veiculo.setNovaSenha(edtNovaSenha.getText().toString().trim());

                editarSenha(veiculo);
            }
    }

    private void editarSenha(Veiculo veiculo){
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.bloqueio_troca_senha, null);
        final Button btnCancelar = (Button) mView.findViewById(R.id.btnCancelar);

        dlg.setCancelable(false);
        dlg.setView(mView);
        desbloquearAlertDialogSenha = dlg.create();
        desbloquearAlertDialogSenha.show();
        ((MainActivity)getActivity()).alterarSenha(veiculo);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg
                   .setTitle("Atenção")
                   .setIcon(R.mipmap.ic_aviso)
                   .setMessage("Deseja Realmente Cancelar ?")
                   .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                       public void onClick(DialogInterface dialog, int which) {
                       Intent intent = new Intent(getActivity(), MainActivity.class);
                       startActivity(intent);
                       getActivity().finish();
                    }
                   }
                 )
                .setNegativeButton("Não", null)
                .show();
            }
        });
    }

    public void liberar(boolean controle){
        desbloquearAlertDialogSenha.dismiss();

        if(controle){
        if (appDB.getConfig("VEICULO_ATUAL") == veiculo.getId()) {

            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            dlg
               .setTitle("Informações")
               .setIcon(R.mipmap.ic_informacao)
               .setCancelable(false)
                    .setMessage("SMS Recebido, aguardando resposta!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .show();

        } else {

            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            dlg
               .setTitle("Informações")
               .setIcon(R.mipmap.ic_informacao)
               .setCancelable(false)
               .setMessage("SMS Recebido, aguardando resposta!")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                       ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

                       if(listarVeiculos != null){
                           fm.popBackStack("ListarVeiculos", 0);
                       }else {
                           FragmentTransaction ft = fm.beginTransaction();
                           ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
                           ft.addToBackStack("ListarVeiculos");
                           ft.commit();
                       }
                   }
               })
            .show();
        }
        } else{
            desbloquearAlertDialogSenha.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if( v == txtCampoObrigatorio1 || v == txtCampoObrigatorio2 || v == txtCampoObrigatorio3 || v == txtCampoObrigatorio4){
            functions.toast("Campo Obrigatório !");
        }
    }
}