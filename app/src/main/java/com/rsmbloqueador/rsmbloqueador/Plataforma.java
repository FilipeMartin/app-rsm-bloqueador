package com.rsmbloqueador.rsmbloqueador;

import android.content.*;
import android.net.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import api.*;
import funcoes.*;
import models.*;

public class Plataforma extends Fragment {

    private AppDB appDB;
    private Usuario usuario;
    private Config config;

    private String URL_SITE = Api.BASE_URL_PLATFORM;
    private RelativeLayout layout_progress;
    private WebView webView;
    private FloatingActionButton floatingToolbar;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.getBoolean("status")){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Plataforma plataforma = (Plataforma) fm.findFragmentByTag("Plataforma");

            if(plataforma != null){
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(plataforma);
                ft.commit();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plataforma, container, false);

        appDB = new AppDB(getActivity());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();

        if(usuario.getPlatformToken().length() > 0){
            URL_SITE += "?token="+usuario.getPlatformToken();
        }

        layout_progress = (RelativeLayout) view.findViewById(R.id.layout_progress);
        webView = view.findViewById(R.id.webview);
        floatingToolbar = (FloatingActionButton) view.findViewById(R.id.floatingToolbar);
        toolbar = ((MainActivity)getActivity()).getToolbar();

        if(config.getStatusToolbar() == 1){
            floatingToolbar.setImageResource(R.mipmap.ic_fullscreen_exit);
        }

        floatingToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toolbar.getVisibility() > 0){
                    appDB.editConfig("STATUS_TOOLBAR", 0);
                    floatingToolbar.setImageResource(R.mipmap.ic_fullscreen);
                    toolbar.setVisibility(View.VISIBLE);
                } else{
                    appDB.editConfig("STATUS_TOOLBAR", 1);
                    floatingToolbar.setImageResource(R.mipmap.ic_fullscreen_exit);
                    toolbar.setVisibility(View.GONE);
                }
            }
        });

        // Habilitar Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        // Set Render Priority To High
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // Load URL
        webView.loadUrl(URL_SITE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains(Api.BASE_URL_PLATFORM)) {
                    webView.loadUrl(url);
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                layout_progress.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Verificar status da Internet
        statusInternet();
    }

    private void statusInternet(){

        StatusInternet statusInternet = new StatusInternet(getActivity());

        if((!statusInternet.status()) && (getVisibleFragment())){
            ((MainActivity)getActivity()).setVisibleFragments(true, false);
        }
    }

    public void setVisibleFragment(Boolean controle){
        if(controle) {
            getView().setVisibility(View.VISIBLE);
        }else{
            getView().setVisibility(View.GONE);
        }
    }

    public boolean getVisibleFragment(){
        int visible = getView().getVisibility();

        if(visible > 0){
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("status", true);
    }
}