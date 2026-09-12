package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class Account {
    //attributi
    private int id_account;
    private String nickname;
    private String email;
    private String password_hash;
    private boolean admin_flag;

    //costruttore
    public Account(){

    }

    // Costruttore completo
    public Account(int id_account, String nickname, String email, String password_hash, boolean admin_flag) {
        this.admin_flag = admin_flag;
        this.nickname = nickname;
        this.email = email;
        this.password_hash = password_hash;
        this.id_account = id_account;
    }

    //metodi getter e setter
    public int getId_account() {
        return this.id_account;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getEmail() { return email; }

    public String getPassword_hash() {
        return this.password_hash;
    }

    public boolean getAdmin_flag() {
        return this.admin_flag;
    }

    public void setId_account(int id_account) {
        this.id_account = id_account;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) { this.email = email; }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public void setAdmin_flag(boolean admin_flag) {
        this.admin_flag = admin_flag;
    }

    //altri metodi
    @Override
    public String toString(){
        return "Account{" + "id = " + this.id_account + "Nickname = " + this.nickname + " / admin = " + this.admin_flag;
    }

    public boolean isAdminFlag() {
         return this.getAdmin_flag();
    }



}