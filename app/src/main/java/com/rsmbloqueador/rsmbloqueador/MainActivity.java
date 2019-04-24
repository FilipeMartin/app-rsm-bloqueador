package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import functions.*;
import models.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private AppDB appDB;
    private Usuario usuario;
    private Config config;
    private Functions functions;

    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtExpirationTime;
    private View view;

    // ActionBar
    Toolbar toolbar;

    // NavigationView
    NavigationView navigationView;

    FloatingActionButton btnAtalhoCadastrar;

    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey("sessionTime")){
            Calendar sessionTime = (Calendar) savedInstanceState.getSerializable("sessionTime");
            Calendar date = Calendar.getInstance();

            if(sessionTime.getTime().before(date.getTime())){
                Intent intent = new Intent(this, Inicio_app.class);
                startActivity(intent);
                finish();
            }
        }

        if(savedInstanceState == null){
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container_inicio, new Inicio(), "Inicio");
            ft.commit();
        }

        appDB = new AppDB(getApplicationContext());
        usuario = appDB.getUsuario();
        config = appDB.getConfig();
        functions = new Functions(this);

        // Listener OnBackStack
        fm.addOnBackStackChangedListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnAtalhoCadastrar = (FloatingActionButton) findViewById(R.id.btnAtalhoCadastrar);
        btnAtalhoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cadastrar cadastrar = (Cadastrar) fm.findFragmentByTag("Cadastrar");

                if(cadastrar != null){
                    fm.popBackStack("Cadastrar", 0);
                }else {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.container_padrao, new Cadastrar(), "Cadastrar");
                    ft.addToBackStack("Cadastrar");
                    ft.commit();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Plataforma plataforma = (Plataforma) fm.findFragmentByTag("Plataforma");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(plataforma != null && plataforma.getVisibleFragment()){
            setVisibleFragments(true, false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_listar_usuarios) {
            navigationView.setCheckedItem(R.id.nav_listar_usuarios);

            ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

            if(listarVeiculos != null){
                fm.popBackStack("ListarVeiculos", 0);
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
                ft.addToBackStack("ListarVeiculos");
                ft.commit();
            }
            return true;

        } else if (id == R.id.action_cadastrar_usuarios) {
            navigationView.setCheckedItem(R.id.nav_cadastrar_usuarios);

            Cadastrar cadastrar = (Cadastrar) fm.findFragmentByTag("Cadastrar");

            if(cadastrar != null){
                fm.popBackStack("Cadastrar", 0);
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new Cadastrar(), "Cadastrar");
                ft.addToBackStack("Cadastrar");
                ft.commit();
            }
            return true;

        } else if (id == R.id.action_configuracao) {
            navigationView.setCheckedItem(R.id.nav_configuracao);

            Configuracao configuracao = (Configuracao) fm.findFragmentByTag("Configuracao");

            if(configuracao != null){
                fm.popBackStack("Configuracao", 0);
            }else{
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new Configuracao(), "Configuracao");
                ft.addToBackStack("Configuracao");
                ft.commit();
            }
            return true;

        } else if (id == R.id.action_sair) {

            caixaDialogoSair();
            return true;

        } else if (id == R.id.action_comando_voz) {

            Inicio inicio = (Inicio) fm.findFragmentByTag("Inicio");
            inicio.comandoVoz();
            return true;

        } else if (id == R.id.action_atualizar_gps_online) {

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container_plataforma, new Plataforma(), "Plataforma");
            ft.commit();
            functions.toast("Atualizando a Plataforma...");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_inicio) {

            // Remover todos os Fragments do Stack
            if(fm.getBackStackEntryCount() > 0){
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }else{
                setVisibleFragments(true, false);
            }

        } else if (id == R.id.nav_listar_usuarios) {

            ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

            if(listarVeiculos != null){
                fm.popBackStack("ListarVeiculos", 0);
            }else {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
                ft.addToBackStack("ListarVeiculos");
                ft.commit();
            }

        } else if (id == R.id.nav_cadastrar_usuarios) {

            Cadastrar cadastrar = (Cadastrar) fm.findFragmentByTag("Cadastrar");

            if(cadastrar != null){
                fm.popBackStack("Cadastrar", 0);
            }else {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new Cadastrar(), "Cadastrar");
                ft.addToBackStack("Cadastrar");
                ft.commit();
            }

        } else if (id == R.id.nav_tutoriais) {
            Intent intent = new Intent(this, ActivityTutoriais.class);
            startActivity(intent);

        } else if (id == R.id.nav_configuracao) {

            Configuracao configuracao = (Configuracao) fm.findFragmentByTag("Configuracao");

            if(configuracao != null){
                fm.popBackStack("Configuracao", 0);
            }else {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container_padrao, new Configuracao(), "Configuracao");
                ft.addToBackStack("Configuracao");
                ft.commit();
            }

        } else if (id == R.id.nav_login) {

            Intent intent = new Intent(getApplicationContext(), MainLogin.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_sair) {

            caixaDialogoSair();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void caixaDialogoSair(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg     .setIcon(R.mipmap.ic_interrogacao)
                .setTitle("Deseja realmente sair ?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(config.getConta() == 1){
                            appDB.cleanDB();
                        }
                        functions.toast("Tenha um Bom Dia !");
                        finish();
                    }
                })
                .setNegativeButton("Não", null);
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void setVisibleFragments(boolean statusInicio, boolean statusPlataforma){

        Inicio inicio = (Inicio) fm.findFragmentByTag("Inicio");
        if(inicio != null) {
            inicio.setVisibleFragment(statusInicio);
            if(statusInicio) {
                setActionBarTitle(getString(R.string.app_name));
            }
        }

        Plataforma plataforma = (Plataforma) fm.findFragmentByTag("Plataforma");
        if(plataforma != null){
            plataforma.setVisibleFragment(statusPlataforma);
            iconeAtualizarPlataforma(statusPlataforma);
            if(statusPlataforma){
                setActionBarTitle("Plataforma");
            }
        }

        Config config = appDB.getConfig();
        if(statusPlataforma && config.getStatusToolbar() == 1){
            toolbar.setVisibility(View.GONE);
        } else{
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    public void iconeAtualizarPlataforma(Boolean status){
        toolbar.getMenu().findItem(R.id.action_atualizar_gps_online).setVisible(status);
    }

    public void btnAtalhoCadastrar(boolean valor){

        if(valor){
            btnAtalhoCadastrar.setVisibility(View.VISIBLE);
        }else if(valor == false){
            btnAtalhoCadastrar.setVisibility(View.GONE);
        }
    }

    public void alterarSenha(Veiculo usuario){
        Inicio inicio = (Inicio) fm.findFragmentByTag("Inicio");
        inicio.editarSenha(usuario);
    }

    public void liberarAlterarSenha(boolean controle){
        EditarVeiculo editarVeiculo = (EditarVeiculo) fm.findFragmentByTag("EditarVeiculo");
        editarVeiculo.liberar(controle);
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    public void checkedItemNavMenu(int positionFragmentStack){

        FragmentManager.BackStackEntry position = fm.getBackStackEntryAt(positionFragmentStack);

        if(position != null){
            switch(position.getName()){
                case "ListarVeiculos": navigationView.setCheckedItem(R.id.nav_listar_usuarios); break;
                case "Cadastrar": navigationView.setCheckedItem(R.id.nav_cadastrar_usuarios); break;
                case "Configuracao": navigationView.setCheckedItem(R.id.nav_configuracao);
            }
        }
    }

    private void navHeader(){

        if(config.getConta() == 1){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            view = navigationView.getHeaderView(0);

            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_header));
            txtNome = (TextView) view.findViewById(R.id.txtNome);
            txtEmail = (TextView) view.findViewById(R.id.txtEmail);
            txtExpirationTime = (TextView) view.findViewById(R.id.txtExpirationTime);

            txtNome.setText(usuario.getName());
            txtEmail.setText(usuario.getEmail());
            try{
                txtExpirationTime.setText(sdf1.format(sdf.parse(usuario.getExpirationTime())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackStackChanged() {

        int positionFragmentStack = fm.getBackStackEntryCount()-1;
        if(positionFragmentStack >= 0){
            checkedItemNavMenu(positionFragmentStack);
        }else{
            setVisibleFragments(true, false);
            btnAtalhoCadastrar(false);
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Calendar sessionTime = Calendar.getInstance();
        sessionTime.add(Calendar.HOUR, 1);
        outState.putSerializable("sessionTime", sessionTime);
    }
}