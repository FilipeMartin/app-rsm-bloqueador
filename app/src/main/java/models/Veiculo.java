package models;

import java.io.Serializable;

public class Veiculo implements Serializable {

    private int id;
    private String imei;
    private String nome;
    private String telefone;
    private String senha;
    private String novaSenha;
    private String modelo;
    private String categoria;
    private int velocidadeLimite;
    private int painelStatus;
    private int painelIgnicao;
    private String painelVelocidade;
    private String painelUrlMapa;
    private String painelData;
    private String data;
    private int status;

    public Veiculo(){
        this.id = 0;
        this.imei = "";
        this.nome = "";
        this.telefone = "";
        this.senha = "";
        this.novaSenha = "";
        this.modelo = "";
        this.categoria = "";
        this.velocidadeLimite = 0;
        this.painelStatus = 0;
        this.painelIgnicao = 0;
        this.painelVelocidade = "";
        this.painelUrlMapa = "";
        this.painelData = "";
        this.data = "";
        this.status = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getVelocidadeLimite() {
        return velocidadeLimite;
    }

    public void setVelocidadeLimite(int velocidadeLimite) {
        this.velocidadeLimite = velocidadeLimite;
    }

    public int getPainelStatus() {
        return painelStatus;
    }

    public void setPainelStatus(int painelStatus) {
        this.painelStatus = painelStatus;
    }

    public int getPainelIgnicao() {
        return painelIgnicao;
    }

    public void setPainelIgnicao(int painelIgnicao) {
        this.painelIgnicao = painelIgnicao;
    }

    public String getPainelVelocidade() {
        return painelVelocidade;
    }

    public void setPainelVelocidade(String painelVelocidade) {
        this.painelVelocidade = painelVelocidade;
    }

    public String getPainelUrlMapa() {
        return painelUrlMapa;
    }

    public void setPainelUrlMapa(String painelUrlMapa) {
        this.painelUrlMapa = painelUrlMapa;
    }

    public String getPainelData() {
        return painelData;
    }

    public void setPainelData(String painelData) {
        this.painelData = painelData;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString(){
        return this.nome+"\n"+telefoneFormatado();
    }

    // Método para formatar número de Telefone
    private String telefoneFormatado(){
        String telefone = getTelefone();
        int totalNumero = getTelefone().length();
        String telefoneFormatado = "(" + telefone.substring(0, totalNumero - 9) + ") " + telefone.substring(totalNumero - 9, totalNumero-4) +"-"+ telefone.substring(totalNumero - 4, totalNumero);

        return telefoneFormatado;
    }
}