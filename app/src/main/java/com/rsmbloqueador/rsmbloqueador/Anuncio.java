package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import functions.*;
import models.*;

public class Anuncio extends Fragment {

    private AppDB appDB;
    private Usuario usuario;
    private Config config;
    private Functions functions;

    private TextView txtNome;
    private Button btnVersaoPaga;
    private Button btnVersaoGratuita;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anuncio, container, false);

        appDB = new AppDB(getActivity());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();
        functions = new Functions(getActivity());

        txtNome = (TextView) view.findViewById(R.id.txtNome);
        btnVersaoPaga = (Button) view.findViewById(R.id.btnVersaoPaga);
        btnVersaoGratuita = (Button) view.findViewById(R.id.btnVersaoGratuita);

        if(config.getConta() > 0 && usuario.getId() > 0){
            txtNome.setText("Olá, "+usuario.getFirstName()+"!");
        }

        btnVersaoPaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_contato, null);
                final TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                final TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
                final ImageView btnWhatsApp = (ImageView) view.findViewById(R.id.btnWhatsApp);
                final TextView txtPhone1 = (TextView) view.findViewById(R.id.txtPhone1);
                final TextView txtPhone2 = (TextView) view.findViewById(R.id.txtPhone2);
                final Button btnOk = (Button) view.findViewById(R.id.btnOk);

                txtTitle.setText("PLATAFORMA ONLINE");
                txtMessage.setText("Para contratar o serviço:");

                dlg.setView(view);
                final AlertDialog dialog = dlg.create();
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
        });

        btnVersaoGratuita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(config.getConta() > 0){
                    if(config.getConta() == 3){
                        appDB.editConfig("CONTA", 2);
                    }
                    functions.toast("Seja Bem-Vindo");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else{
                    Intent intent = new Intent(getActivity(), CadastrarFree.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}