package com.rsmbloqueador.rsmbloqueador;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import funcoes.*;

public class Inicio_app extends AbsRuntimePermission {

    private AppDB appDB;
    private StatusInternet statusInternet;

    private static  final int REQUEST_PERMISSION = 10;
    private int tempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_app);

        requestAppPermissions(new String[]{
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.CALL_PHONE},
                R.string.msg_permissao, REQUEST_PERMISSION);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        // Do anything when permission granted
        appDB = new AppDB(Inicio_app.this);
        statusInternet = new StatusInternet(Inicio_app.this);

        int conta = appDB.getConfig("CONTA");

        if(conta == 1 && statusInternet.status()){
            tempo = 500;
        } else{
            tempo = 1800;
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Controle controle = new Controle(Inicio_app.this);
                controle.run();
            }
        },tempo);
    }

    public void finishActivity(){
        finish();
    }
}