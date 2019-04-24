package com.rsmbloqueador.rsmbloqueador;

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

public class Controle {

    private Context context;

    private AppDB appDB;
    private Config config;
    private Functions functions;

    private RequestQueue requestQueue;

    public Controle(Context context){
        this.context = context;
        appDB = new AppDB(context);
        config = appDB.getConfig();
        functions = new Functions(context);
        requestQueue = Volley.newRequestQueue(context);
    }

    public void run(){

        if(config.getConta() == 1){
            checkAPI();

        } else if(config.getConta() == 2){
            inicio();
            ((Inicio_app)context).finishActivity();

        } else{
            Intent intent = new Intent(context, MainLogin.class);
            context.startActivity(intent);
            ((Inicio_app)context).finishActivity();
        }
    }

    public void run2(JSONObject response){
        JSONArray jsonUser = response.optJSONArray("user");
        JSONArray jsonVehicles = response.optJSONArray("vehicles");

        try{
            JSONObject objUsuario = jsonUser.getJSONObject(0);

            Usuario usuario = new Usuario();

            // Criar Usuário
            usuario.setId(objUsuario.optInt("id"));
            usuario.setName(objUsuario.optString("name"));
            usuario.setDateBirth(objUsuario.optString("datebirth"));
            usuario.setCpf(objUsuario.optString("cpf"));
            usuario.setEmail(objUsuario.optString("email"));
            usuario.setLogin(objUsuario.optString("login"));
            usuario.setCellPhone(objUsuario.optString("cellphone"));
            usuario.setPhone(objUsuario.optString("phone"));
            usuario.setZipCode(objUsuario.optString("zipcode"));
            usuario.setState(objUsuario.optString("state"));
            usuario.setCity(objUsuario.optString("city"));
            usuario.setNeighborhood(objUsuario.optString("neighborhood"));
            usuario.setAddress(objUsuario.optString("address"));
            usuario.setAddressNumber(objUsuario.optString("addressnumber"));
            usuario.setComplement(objUsuario.optString("complement"));
            usuario.setServiceTerms(objUsuario.optInt("serviceterms"));
            usuario.setExpirationTime(objUsuario.optString("expirationtime"));
            usuario.setAdmin(objUsuario.optInt("admin"));
            usuario.setDate(objUsuario.optString("date"));
            usuario.setStatus(objUsuario.optInt("status"));
            usuario.setLastUpdate(objUsuario.optString("lastupdate"));
            usuario.setToken(objUsuario.optString("token"));
            usuario.setSession(objUsuario.optString("session"));
            usuario.setPlatformToken(objUsuario.optString("platformtoken"));

            if(jsonVehicles.length() > 0){

                List<Veiculo> veiculos = new ArrayList<Veiculo>();

                for(int i=0; i < jsonVehicles.length(); i++){

                    JSONObject objVeiculos = jsonVehicles.getJSONObject(i);

                    Veiculo veiculo = new Veiculo();

                    // Criar Veículo
                    veiculo.setId(objVeiculos.optInt("id"));
                    veiculo.setImei(objVeiculos.optString("imei"));
                    veiculo.setNome(objVeiculos.optString("name"));
                    veiculo.setTelefone(objVeiculos.optString("phone"));
                    veiculo.setSenha(objVeiculos.optString("password"));
                    veiculo.setModelo(objVeiculos.optString("model"));
                    veiculo.setCategoria(objVeiculos.optString("category"));
                    veiculo.setData(objVeiculos.optString("date"));
                    veiculo.setStatus(objVeiculos.optInt("status"));

                    veiculos.add(veiculo);
                }

                addCarga(usuario, veiculos);

            } else{
                Bundle bundle = new Bundle();
                bundle.putSerializable("Usuario", usuario);

                Intent intent = new Intent(context, CadastrarPro.class);
                intent.putExtra("Controle", bundle);
                context.startActivity(intent);
            }

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void run3(Usuario usuario, String response){

        try {
            JSONObject objVehicle = new JSONObject(response);

            Veiculo veiculo = new Veiculo();

            veiculo.setId(objVehicle.optInt("id"));
            veiculo.setImei(objVehicle.optString("imei"));
            veiculo.setNome(objVehicle.optString("name"));
            veiculo.setTelefone(objVehicle.optString("phone"));
            veiculo.setSenha(objVehicle.optString("password"));
            veiculo.setModelo(objVehicle.optString("model"));
            veiculo.setCategoria(objVehicle.optString("category"));
            veiculo.setData(objVehicle.optString("date"));
            veiculo.setStatus(objVehicle.optInt("status"));

            List<Veiculo> veiculos = new ArrayList<Veiculo>();
            veiculos.add(veiculo);

            addCarga(usuario, veiculos);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void run4(JSONObject response){

        long lastUpdate = response.optLong("lastUpdate");
        String session = response.optString("session");

        Usuario usuario = appDB.getUsuario();

        if(!session.equals(usuario.getSession())){
            appDB.cleanDB();

            Intent intent = new Intent(context, MainLogin.class);
            context.startActivity(intent);
            ((Inicio_app)context).finishActivity();

        } else if(lastUpdate > Long.parseLong(usuario.getLastUpdate())){

            cargaAPI("rsm", usuario.getToken());

        } else{
            inicio();
            ((Inicio_app)context).finishActivity();
        }
    }

    public void addCarga(Usuario usuario, List<Veiculo> veiculos){

        appDB.addUsuario(usuario);

        for(int i=0; i < veiculos.size(); i++){
            appDB.addVeiculo(veiculos.get(i), true);
        }

        if(config.getId() == 0){
            config.setConta(1);
            appDB.addConfig(config);
        } else{
            appDB.addConfig(config);
        }
        inicio();
    }

    private void cargaAPI(final String login, final String token){
        String url = Api.BASE_URL+"api/carga/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        appDB.cleanDB();
                        run2(response);
                        ((Inicio_app)context).finishActivity();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.data != null){
                            switch(networkResponse.statusCode){
                                case 401:
                                    appDB.cleanDB();
                                    functions.toast("Você foi deslogado por medidas de segurança !");
                                    Intent intent = new Intent(context, MainLogin.class);
                                    context.startActivity(intent);
                                    ((Inicio_app)context).finishActivity();
                                    break;
                                default:
                                    inicio();
                                    ((Inicio_app)context).finishActivity();
                            }
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

    public void checkAPI(){
        String url = Api.BASE_URL+"api/check/";

        Usuario usuario = appDB.getUsuario();

        final String login = "rsm";
        final String token = usuario.getToken();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        run4(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.data != null){
                            switch(networkResponse.statusCode){
                                case 401:
                                    appDB.editConfig("CONTA", 3);

                                    Intent intent = new Intent(context, MainLogin.class);
                                    context.startActivity(intent);
                                    ((Inicio_app)context).finishActivity();
                                    break;
                                default:
                                    inicio();
                                    ((Inicio_app)context).finishActivity();
                            }
                        } else{
                            inicio();
                            ((Inicio_app)context).finishActivity();
                        }
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", login, token);
                String auth = "Basic " + android.util.Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(8000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private void inicio(){
        if(Integer.toString(config.getLoginSenha()).length() > 1){
            Intent intent = new Intent(context, LoginSeguranca.class);
            context.startActivity(intent);
        } else{
            functions.toast("Seja Bem-Vindo");
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}