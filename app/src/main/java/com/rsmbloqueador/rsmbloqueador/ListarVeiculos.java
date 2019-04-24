package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.view.*;
import android.widget.*;
import java.util.*;
import api.*;
import functions.*;
import models.*;

public class ListarVeiculos extends Fragment {

    private AppDB appDB;
    private Usuario usuario;
    private List<Veiculo> veiculos;
    private Config config;
    private Functions functions;

    private ListView lvListaVeiculos;
    private int posicaoChecked = 0;

    private RequestQueue requestQueue;
    private ProgressDialog progressDialog;

    // FragmentManager
    FragmentManager fm;
    //-----------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listar_usuarios, container, false);
        ((MainActivity)getActivity()).setVisibleFragments(false, false);

        // FragmentManager
        fm = getActivity().getSupportFragmentManager();
        //---------------------------------------------

        appDB = new AppDB(getActivity());
        usuario = appDB.getUsuario();
        veiculos = appDB.getVeiculo();
        config = appDB.getConfig();
        functions = new Functions(getActivity());

        lvListaVeiculos = (ListView) view.findViewById(R.id.lvListaVeiculos);

        requestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Excluindo...");

        ArrayAdapter<Veiculo> adp = new ArrayAdapter<Veiculo>(getActivity(), android.R.layout.simple_list_item_checked, veiculos);
        lvListaVeiculos.setAdapter(adp);

        checkChoice();

        lvListaVeiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.lista_veiculos_opcoes, null);
                TextView txtNomeVeiculo = (TextView) mView.findViewById(R.id.txtNomeVeiculo);
                Button btnAtualizar = (Button) mView.findViewById(R.id.btnAtualizar);
                Button btnEditar = (Button) mView.findViewById(R.id.btnEditar);
                Button btnExcluir = (Button) mView.findViewById(R.id.btnExcluir);

                dlg.setView(mView);

                final AlertDialog dialog = dlg.create();
                dialog.show();

                // Checked choice
                lvListaVeiculos.setItemChecked(posicaoChecked, true);

                // Nome do veículo
                txtNomeVeiculo.setText(veiculos.get(position).getNome().toUpperCase());

                // Visibilidade do Botão Atualizar
                if(veiculos.get(position).getId() == config.getVeiculoAtual()){
                    btnAtualizar.setVisibility(View.GONE);
                }

                btnAtualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        appDB.editConfig("VEICULO_ATUAL", veiculos.get(position).getId());

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        functions.toast("Veículo Atualizado com Sucesso !");
                    }
                });

                btnEditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        appDB.editConfig("VEICULO_EDITAR", veiculos.get(position).getId());

                        EditarVeiculo editarVeiculo = (EditarVeiculo) fm.findFragmentByTag("EditarVeiculo");

                        if(editarVeiculo != null){
                            fm.popBackStack("EditarVeiculo", 0);
                        } else{
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.container_padrao, new EditarVeiculo(), "EditarVeiculo");
                            ft.addToBackStack("EditarVeiculo");
                            ft.commit();
                        }
                    }
                });

                btnExcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(functions.statusInternet()){
                            dialog.dismiss();

                            int idVeiculoAtual = config.getVeiculoAtual();

                            int idVeiculo = veiculos.get(position).getId();

                            if(idVeiculoAtual == idVeiculo && veiculos.size() > 1){
                                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                                dlg
                                        .setTitle("Atenção")
                                        .setIcon(R.mipmap.ic_atencao)
                                        .setMessage("Atualize outro veículo no sistema para que este possa ser excluído !")
                                        .setCancelable(false)
                                        .setNeutralButton("Voltar", null)
                                        .show();

                            } else if(veiculos.size() == 1){

                                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                                dlg
                                        .setTitle("Atenção")
                                        .setIcon(R.mipmap.ic_atencao)
                                        .setMessage("Deseja Realmente Excluir ?")
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(config.getConta() == 1){
                                                    excluirAPI("rsm", usuario.getToken(), veiculos.get(position), false);
                                                } else{
                                                    excluirVeiculo(veiculos.get(position), false);
                                                }

                                            }
                                        })
                                        .setNegativeButton("Não", null)
                                        .show();

                            } else{

                                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                                dlg
                                        .setTitle("Atenção")
                                        .setIcon(R.mipmap.ic_atencao)
                                        .setMessage("Deseja Realmente Excluir ?")
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if(config.getConta() == 1){
                                                    excluirAPI("rsm", usuario.getToken(), veiculos.get(position), true);
                                                } else{
                                                    excluirVeiculo(veiculos.get(position), true);
                                                }

                                            }
                                        })
                                        .setNegativeButton("Não", null)
                                        .show();
                            }

                        }
                    }
                 });
              }
        });

        return view;
    }

    public void excluirAPI(final String login, final String token, final Veiculo veiculo, final boolean activity){
        progressDialog.show();
        String url = Api.BASE_URL+"api/vehicles/"+veiculo.getId();
        url = url.replace(" ","%20");

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();

                        appDB.deleteVeiculo(veiculo.getId());

                        if(activity){
                            ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

                            if(listarVeiculos != null){
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.detach(listarVeiculos);
                                ft.attach(listarVeiculos);
                                ft.commit();
                            }

                            checkChoice();
                            functions.toast("Veículo Excluído com Sucesso !");

                        } else{
                            appDB.cleanDB();
                            Intent intent = new Intent(getActivity(), MainLogin.class);
                            startActivity(intent);
                            getActivity().finish();
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
                                    functions.alertDialog("Atenção", "Identificador inválido.", R.mipmap.ic_atencao, "Voltar");
                                    break;
                                case 401:
                                    appDB.cleanDB();
                                    functions.toast("Você foi deslogado por medidas de segurança !");
                                    Intent intent = new Intent(getActivity(), MainLogin.class);
                                    getActivity().startActivity(intent);
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

    private void excluirVeiculo(Veiculo veiculo, boolean activity){

        appDB.deleteVeiculo(veiculo.getId());

        if(activity){
            ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

            if(listarVeiculos != null){
                FragmentTransaction ft = fm.beginTransaction();
                ft.detach(listarVeiculos);
                ft.attach(listarVeiculos);
                ft.commit();
            }

            checkChoice();
            Toast.makeText(getActivity(), "Veículo Excluído com Sucesso !", Toast.LENGTH_SHORT).show();

        } else{
            appDB.cleanDB();
            Intent intent = new Intent(getActivity(), MainLogin.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void checkChoice(){
        List<Veiculo> veiculos = appDB.getVeiculo();

        for(int x=0; x < veiculos.size(); x++){
            if(veiculos.get(x).getId() == config.getVeiculoAtual()){
                lvListaVeiculos.setItemChecked(x, true);
                posicaoChecked = x;
                break;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Veículos Cadastrados");
        ((MainActivity)getActivity()).btnAtalhoCadastrar(true);
    }
}