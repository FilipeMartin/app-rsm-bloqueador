package com.rsmbloqueador.rsmbloqueador;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.*;
import models.*;

public class AppDB extends SQLiteOpenHelper{

    private static final String NOME_BASE = "app_rsm";
    private static final int VERSAO_BASE = 2;

    public AppDB(Context context){
        super(context, NOME_BASE, null, VERSAO_BASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlUsuarios = "CREATE TABLE usuarios (" +
                "id INTEGER(11) NOT NULL," +
                "name VARCHAR(128) NOT NULL," +
                "datebirth VARCHAR(10) NOT NULL," +
                "cpf VARCHAR(14) NOT NULL," +
                "email VARCHAR(128) NOT NULL," +
                "login VARCHAR(128) NOT NULL," +
                "cellphone VARCHAR(20)," +
                "phone VARCHAR(20)," +
                "zipcode VARCHAR(10)," +
                "state VARCHAR(50)," +
                "city VARCHAR(50)," +
                "neighborhood VARCHAR(50)," +
                "address TEXT," +
                "addressnumber VARCHAR(20)," +
                "complement VARCHAR(128)," +
                "serviceterms TINYINT(1) NOT NULL," +
                "expirationtime TIMESTAMP NOT NULL," +
                "admin TINYINT(1) NOT NULL," +
                "date TIMESTAMP NOT NULL," +
                "status TINYINT(1) NOT NULL," +
                "lastupdate VARCHAR(128) NOT NULL," +
                "token VARCHAR(128) NOT NULL," +
                "session VARCHAR(128) NOT NULL," +
                "platformtoken VARCHAR(128)" +
                ")";

        String sqlVeiculos = "CREATE TABLE veiculos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "imei VARCHAR(128) NOT NULL," +
                "nome VARCHAR(128) NOT NULL," +
                "telefone VARCHAR(20) NOT NULL," +
                "senha VARCHAR(128) NOT NULL," +
                "modelo VARCHAR(128) NOT NULL," +
                "categoria VARCHAR(128) NOT NULL," +
                "velocidadelimite INTEGER(2) DEFAULT 15," +
                "painelstatus TINYINT(1) DEFAULT 0," +
                "painelignicao TINYINT(1) DEFAULT 0," +
                "painelvelocidade VARCHAR(3)," +
                "painelurlmapa VARCHAR(300)," +
                "paineldata TIMESTAMP," +
                "data TIMESTAMP NOT NULL," +
                "status TINYINT(1) NOT NULL" +
                ")";

        String sqlConfig = "CREATE TABLE config (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "veiculoatual INTEGER(11) NOT NULL DEFAULT 0," +
                "veiculoeditar INTEGER(11) NOT NULL DEFAULT 0," +
                "statusaudio TINYINT(1) NOT NULL DEFAULT 0," +
                "statustoolbar TINYINT(1) NOT NULL DEFAULT 0," +
                "loginsenha TINYINT(1) NOT NULL DEFAULT 0," +
                "conta TINYINT(1) NOT NULL DEFAULT 0" +
                ")";

        db.execSQL(sqlUsuarios);
        db.execSQL(sqlVeiculos);
        db.execSQL(sqlConfig);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sqlUsuarios = "DROP TABLE usuarios";
        String sqlVeiculos = "DROP TABLE veiculos";
        String sqlConfig = "DROP TABLE config";
        db.execSQL(sqlUsuarios);
        db.execSQL(sqlVeiculos);
        db.execSQL(sqlConfig);

        onCreate(db);
    }

    public void addUsuario(Usuario usuario){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("id", usuario.getId());
        cv.put("name", usuario.getName());
        cv.put("datebirth", usuario.getDateBirth());
        cv.put("cpf", usuario.getCpf());
        cv.put("email", usuario.getEmail());
        cv.put("login", usuario.getLogin());
        cv.put("cellphone", usuario.getCellPhone());
        cv.put("phone", usuario.getPhone());
        cv.put("zipcode", usuario.getZipCode());
        cv.put("state", usuario.getState());
        cv.put("city", usuario.getCity());
        cv.put("neighborhood", usuario.getNeighborhood());
        cv.put("address", usuario.getAddress());
        cv.put("addressnumber", usuario.getAddressNumber());
        cv.put("complement", usuario.getComplement());
        cv.put("serviceterms", usuario.getServiceTerms());
        cv.put("expirationtime", usuario.getExpirationTime());
        cv.put("admin", usuario.getAdmin());
        cv.put("date", usuario.getDate());
        cv.put("status", usuario.getStatus());
        cv.put("lastupdate", usuario.getLastUpdate());
        cv.put("token", usuario.getToken());
        cv.put("session", usuario.getSession());
        cv.put("platformtoken", usuario.getPlatformToken());

        db.insert("usuarios", null, cv);

        db.close();
    }

    public Usuario getUsuario(){
        Usuario usuario = new Usuario();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "SELECT * FROM usuarios LIMIT 1";

        Cursor c = db.rawQuery(sqlSelect, null);

        if(c.moveToFirst()){

            usuario.setId(c.getInt(0));
            usuario.setName(c.getString(1));
            usuario.setDateBirth(c.getString(2));
            usuario.setCpf(c.getString(3));
            usuario.setEmail(c.getString(4));
            usuario.setLogin(c.getString(5));
            usuario.setCellPhone(c.getString(6));
            usuario.setPhone(c.getString(7));
            usuario.setZipCode(c.getString(8));
            usuario.setState(c.getString(9));
            usuario.setCity(c.getString(10));
            usuario.setNeighborhood(c.getString(11));
            usuario.setAddress(c.getString(12));
            usuario.setAddressNumber(c.getString(13));
            usuario.setComplement(c.getString(14));
            usuario.setServiceTerms(c.getInt(15));
            usuario.setExpirationTime(c.getString(16));
            usuario.setAdmin(c.getInt(17));
            usuario.setDate(c.getString(18));
            usuario.setStatus(c.getInt(19));
            usuario.setLastUpdate(c.getString(20));
            usuario.setToken(c.getString(21));
            usuario.setSession(c.getString(22));
            usuario.setPlatformToken(c.getString(23));
        }

        db.close();

        return usuario;
    }

    public void editUsuario(String atributo, String valor){
        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "UPDATE usuarios SET " + atributo + " = '" + valor + "'";

        db.execSQL(sqlSelect);

        db.close();
    }

    public void addVeiculo(Veiculo veiculo, boolean type){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        if(type){
            cv.put("id", veiculo.getId());
        }
        cv.put("imei", veiculo.getImei());
        cv.put("nome", veiculo.getNome());
        cv.put("telefone", veiculo.getTelefone());
        cv.put("senha", veiculo.getSenha());
        cv.put("modelo", veiculo.getModelo());
        cv.put("categoria", veiculo.getCategoria());
        cv.put("data", veiculo.getData());
        cv.put("status", veiculo.getStatus());

        db.insert("veiculos", null, cv);

        db.close();
    }

    public List<Veiculo> getVeiculo(){

        SQLiteDatabase db = getReadableDatabase();

        List<Veiculo> veiculos = new ArrayList<Veiculo>();

        String sqlSelect = "SELECT * FROM veiculos";

        Cursor c = db.rawQuery(sqlSelect, null);

        if(c.moveToFirst()){
            do {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(c.getInt(0));
                veiculo.setImei(c.getString(1));
                veiculo.setNome(c.getString(2));
                veiculo.setTelefone(c.getString(3));
                veiculo.setSenha(c.getString(4));
                veiculo.setModelo(c.getString(5));
                veiculo.setCategoria(c.getString(6));
                veiculo.setVelocidadeLimite(c.getInt(7));
                veiculo.setPainelStatus(c.getInt(8));
                veiculo.setPainelIgnicao(c.getInt(9));
                veiculo.setPainelVelocidade(c.getString(10));
                veiculo.setPainelUrlMapa(c.getString(11));
                veiculo.setPainelData(c.getString(12));
                veiculo.setData(c.getString(13));
                veiculo.setStatus(c.getInt(14));

                veiculos.add(veiculo);
            }while(c.moveToNext());
        }
        db.close();

        return veiculos;
    }

    public Veiculo getVeiculo(int idVeiculo){
        Veiculo veiculo = new Veiculo();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = null;

        if(idVeiculo > 0){
            sqlSelect = "SELECT * FROM veiculos WHERE id = " + idVeiculo;
        } else{
            sqlSelect = "SELECT * FROM veiculos LIMIT 1";
        }

        Cursor c = db.rawQuery(sqlSelect, null);

        if(c.moveToFirst()){
            veiculo.setId(c.getInt(0));
            veiculo.setImei(c.getString(1));
            veiculo.setNome(c.getString(2));
            veiculo.setTelefone(c.getString(3));
            veiculo.setSenha(c.getString(4));
            veiculo.setModelo(c.getString(5));
            veiculo.setCategoria(c.getString(6));
            veiculo.setVelocidadeLimite(c.getInt(7));
            veiculo.setPainelStatus(c.getInt(8));
            veiculo.setPainelIgnicao(c.getInt(9));
            veiculo.setPainelVelocidade(c.getString(10));
            veiculo.setPainelUrlMapa(c.getString(11));
            veiculo.setPainelData(c.getString(12));
            veiculo.setData(c.getString(13));
            veiculo.setStatus(c.getInt(14));

            if(idVeiculo == 0){
                editConfig("VEICULO_ATUAL", veiculo.getId());
            }
        }

        db.close();

        return veiculo;
    }

    public void editVeiculo(Veiculo veiculo){

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "UPDATE veiculos SET " +
                                "imei = '" + veiculo.getImei() + "', " +
                                "nome = '" + veiculo.getNome() + "', " +
                                "telefone = '" + veiculo.getTelefone() + "', " +
                                "senha = '" + veiculo.getSenha() + "', " +
                                "modelo = '" + veiculo.getModelo() + "', " +
                                "categoria = '" + veiculo.getCategoria() + "' " +
                                "WHERE id = " + veiculo.getId();

        db.execSQL(sqlSelect);

        db.close();
    }

    public void deleteVeiculo(int idVeiculo){

        SQLiteDatabase db = getWritableDatabase();

        String sqlSelect = "DELETE FROM veiculos WHERE id = " + idVeiculo;

        db.execSQL(sqlSelect);

        db.close();
    }

    public int checkVeiculo(String telefone){
        int idVeiculo = 0;

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "SELECT id FROM veiculos WHERE telefone = '" + telefone + "'";

        Cursor c = db.rawQuery(sqlSelect, null);

        if(c.moveToFirst()){
            idVeiculo = c.getInt(0);
        }
        return idVeiculo;
    }

    public void editSenha(Veiculo veiculo){

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "UPDATE veiculos SET senha = '" + veiculo.getSenha() + "' WHERE id = " + veiculo.getId();

        db.execSQL(sqlSelect);

        db.close();

    }

    public int editPainel(Veiculo veiculo){

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "UPDATE veiculos SET " +
                                "painelstatus = 1, " +
                                "painelignicao = " + veiculo.getPainelIgnicao() + ", " +
                                "painelvelocidade = '" + veiculo.getPainelVelocidade() + "', " +
                                "painelurlmapa = '" + veiculo.getPainelUrlMapa() + "', " +
                                "paineldata = '" + veiculo.getPainelData() + "' " +
                                "WHERE id = " + veiculo.getId();

        db.execSQL(sqlSelect);

        db.close();

        return 1;
    }

    public void editVelocidadeLimite(int idVeiculo, int posicao){

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "UPDATE veiculos SET velocidadelimite = " + posicao + " WHERE id = " + idVeiculo;

        db.execSQL(sqlSelect);

        db.close();
    }

    public void addConfig(Config config){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("veiculoatual", config.getVeiculoAtual());
        cv.put("veiculoeditar", config.getVeiculoEditar());
        cv.put("statusaudio", config.getStatusAudio());
        cv.put("statustoolbar", config.getStatusToolbar());
        cv.put("loginsenha", config.getLoginSenha());
        cv.put("conta", config.getConta());

        db.insert("config", null, cv);

        db.close();
    }

    public Config getConfig(){
        Config config = new Config();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "SELECT * FROM config WHERE id = 1";

        Cursor c = db.rawQuery(sqlSelect, null);

        if(c.moveToFirst()){
            config.setId(c.getInt(0));
            config.setVeiculoAtual(c.getInt(1));
            config.setVeiculoEditar(c.getInt(2));
            config.setStatusAudio(c.getInt(3));
            config.setStatusToolbar(c.getInt(4));
            config.setLoginSenha(c.getInt(5));
            config.setConta(c.getInt(6));
        }

        db.close();

        return config;
    }

    public int getConfig(String type){

        SQLiteDatabase db = getReadableDatabase();

        int valor = 0;

        String atributo = null;
        switch(type){
            case "VEICULO_ATUAL": atributo = "veiculoatual"; break;
            case "VEICULO_EDITAR": atributo = "veiculoeditar"; break;
            case "STATUS_AUDIO": atributo = "statusaudio"; break;
            case "STATUS_TOOLBAR": atributo = "statustoolbar"; break;
            case "LOGIN_SENHA": atributo = "loginsenha"; break;
            case "CONTA": atributo = "conta";
        }

        if(atributo != null){
            String sqlSelect = "SELECT " + atributo + " FROM config WHERE id = 1";
            Cursor c = db.rawQuery(sqlSelect, null);

            if(c.moveToFirst()){
                valor = c.getInt(0);
            }
        }

        db.close();

        return valor;
    }

    public void editConfig(String type, int valor){

        SQLiteDatabase db = getReadableDatabase();

        String atributo = null;
        switch(type){
            case "VEICULO_ATUAL": atributo = "veiculoatual"; break;
            case "VEICULO_EDITAR": atributo = "veiculoeditar"; break;
            case "STATUS_AUDIO": atributo = "statusaudio"; break;
            case "STATUS_TOOLBAR": atributo = "statustoolbar"; break;
            case "LOGIN_SENHA": atributo = "loginsenha"; break;
            case "CONTA": atributo = "conta";
        }

        if(atributo != null){
            String sqlSelect = "UPDATE config SET " + atributo + " = " + valor + " WHERE id = 1";
            db.execSQL(sqlSelect);
        }

        db.close();
    }

    public void cleanDB(){

        SQLiteDatabase db = getWritableDatabase();

        String sqlUsuarios = "DROP TABLE IF EXISTS usuarios";
        String sqlVeiculos = "DROP TABLE IF EXISTS veiculos";
        String sqlConfig = "DROP TABLE IF EXISTS config";
        db.execSQL(sqlUsuarios);
        db.execSQL(sqlVeiculos);
        db.execSQL(sqlConfig);

        onCreate(db);

        db.close();
    }
}