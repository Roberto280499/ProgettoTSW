/*TABELLA DI RELAZIONE PER LA LISTA DEI DESIDERI DELL'UTENTE*/
package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class ListaDesiderio {
    //attributi
    private int id_cliente_associato;
    private int id_prodotto_associato;
    private Account account;
    private Prodotto prodotto;

    //costruttore
    public ListaDesiderio() {

    }

    // Costruttore completo
    public ListaDesiderio(int id_cliente_associato, int id_prodotto_associato) {
        this.id_cliente_associato = id_cliente_associato;
        this.id_prodotto_associato = id_prodotto_associato;
    }

    //metodi getter e setter
    public int getId_cliente_associato() {
        return this.id_cliente_associato;
    }

    public int getId_prodotto_associato() {
        return this.id_prodotto_associato;
    }

    public void setId_cliente_associato(int id_cliente_associato) {
        this.id_cliente_associato = id_cliente_associato;
    }

    public void setId_prodotto_associato(int id_prodotto_associato) {
        this.id_prodotto_associato = id_prodotto_associato;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Il CLiente con ID = " + this.id_cliente_associato + "desidera il prodotto con ID = " + this.id_prodotto_associato;
    }
}

