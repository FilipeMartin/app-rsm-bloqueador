package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import models.*;

public class AnuncioPlataforma extends AppCompatActivity {

    private AppDB appDB;
    private Usuario usuario;

    private TextView txtNome;
    private Button btnContratar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anuncio_plataforma);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home);
        //-----------------------------------------------------------

        appDB = new AppDB(AnuncioPlataforma.this);
        usuario = appDB.getUsuario();

        txtNome = (TextView) findViewById(R.id.txtNome);
        btnContratar = (Button) findViewById(R.id.btnContratar);

        if(usuario.getId() > 0){
            txtNome.setText("Olá, "+usuario.getFirstName()+"!");
        }

        btnContratar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(AnuncioPlataforma.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_contato, null);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
