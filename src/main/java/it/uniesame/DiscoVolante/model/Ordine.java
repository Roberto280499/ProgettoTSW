package it.uniesame.DiscoVolante.model;
import java.lang.*;
import java.util.Date;

public class Ordine {
    //attributi
    private int id_ordine;
    private int id_indirizzo_associato;
    private float costo_totale;
    private Date data_ordine;
    private String stato;
    private Account utente;

    //costruttore
    public Ordine() {

    }

    // Costruttore completo
    public Ordine(Account utente, int id_ordine, int id_indirizzo_associato, float costo_totale, Date data_ordine, String stato) {
        this.utente = utente;
        this.id_ordine = id_ordine;
        this.id_indirizzo_associato = id_indirizzo_associato;
        this.costo_totale = costo_totale;
        this.data_ordine = data_ordine;
        this.stato = stato;
    }

    //metodi getter e setter

    public int getId_ordine() {
        return this.id_ordine;
    }

    public Date getData_ordine() {
        return this.data_ordine;
    }

    public float getCosto_totale() {
        return this.costo_totale;
    }

    public int getId_indirizzo_associato() {
        return id_indirizzo_associato;
    }

    public String getStato() {
        return stato;
    }

    public void setCosto_totale(float costo_totale) {
        this.costo_totale = costo_totale;
    }

    public void setId_ordine(int id_ordine) {
        this.id_ordine = id_ordine;
    }

    public void setData_ordine(Date data_ordine) {
        this.data_ordine = data_ordine;
    }

    public void setId_indirizzo_associato(int id_indirizzo_associato) {
        this.id_indirizzo_associato = id_indirizzo_associato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Account getUtente() {
        return utente;
    }

    public void setUtente(Account utente) {
        this.utente = utente;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Ordine con ID = " + this.id_ordine + ", fatto dal cliente con ID = " + this.utente.getId_account() + "associato all'indirizzo con ID = " + this.id_indirizzo_associato + ", ha costo totale = " + this.costo_totale + "euro, fatto in data" + this.data_ordine + ", Possiede il seguente stato = " + this.stato;
    }
}