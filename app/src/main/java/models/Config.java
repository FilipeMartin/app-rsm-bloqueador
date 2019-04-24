package models;

import java.io.Serializable;

public class Config implements Serializable {

    private int id;
    private int veiculoAtual;
    private int veiculoEditar;
    private int statusAudio;
    private int statusToolbar;
    private int loginSenha;
    private int conta;

    public Config(){
        this.id = 0;
        this.veiculoAtual = 0;
        this.veiculoEditar = 0;
        this.statusAudio = 0;
        this.statusToolbar = 0;
        this.loginSenha = 0;
        this.conta = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVeiculoAtual() {
        return veiculoAtual;
    }

    public void setVeiculoAtual(int veiculoAtual) {
        this.veiculoAtual = veiculoAtual;
    }

    public int getVeiculoEditar() {
        return veiculoEditar;
    }

    public void setVeiculoEditar(int veiculoEditar) {
        this.veiculoEditar = veiculoEditar;
    }

    public int getStatusAudio() {
        return statusAudio;
    }

    public void setStatusAudio(int statusAudio) {
        this.statusAudio = statusAudio;
    }

    public int getStatusToolbar() {
        return statusToolbar;
    }

    public void setStatusToolbar(int statusToolbar) {
        this.statusToolbar = statusToolbar;
    }

    public int getLoginSenha() {
        return loginSenha;
    }

    public void setLoginSenha(int loginSenha) {
        this.loginSenha = loginSenha;
    }

    public int getConta() {
        return conta;
    }

    public void setConta(int conta) {
        this.conta = conta;
    }
}