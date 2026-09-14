package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class Cliente {
    //attributi
    private int id_cliente;
    private String nome;
    private String cognome;
    private int id_account_associato; //fk Riferimento all'account
    private int id_carta_credito_associata; //fk Riferimento alla carta

    //costruttore
    public Cliente() {

    }

    // Costruttore completo
    public Cliente(int idCliente, String nome, String cognome, int id_account_associato, int id_carta_credito_associata) {
        this.id_cliente = idCliente;
        this.nome = nome;
        this.cognome = cognome;
        this.id_account_associato = id_account_associato;
        this.id_carta_credito_associata = id_carta_credito_associata;
    }

    //metodi getter e setter
    public int getId_cliente() {
        return this.id_cliente;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public int getId_account_associato() {
        return this.id_account_associato;
    }

    public int getId_carta_credito_associata() {
        return this.id_carta_credito_associata;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId_account_associato(int id_account_associato) {
        this.id_account_associato = id_account_associato;
    }

    public void setId_carta_credito_associata(int id_carta_credito_associata) {
        this.id_carta_credito_associata = id_carta_credito_associata;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Cliente con nome " + this.nome + "e cognome" + this.cognome + "Associato all'account con ID = " + this.id_account_associato + "e asscociato all'id dell carta uguale a = " + this.id_carta_credito_associata;
    }
}