package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class Indirizzo {
    //attributi
    private int id_indirizzo;
    private String via;
    private String citt‡;
    private String cap;
    private String paese;
    private String civico;
    private int id_cliente_associato;

    //costruttore
    public Indirizzo() {

    }

    // Costruttore completo
    public Indirizzo(int id_indirizzo, String via, String citt‡, String cap, String paese, String civico, int id_cliente_associato) {
        this.id_indirizzo = id_indirizzo;
        this.via = via;
        this.citt‡ = citt‡;
        this.cap = cap;
        this.paese = paese;
        this.civico = civico;
        this.id_cliente_associato = id_cliente_associato;
    }

    //metodi getter e setter
    public int getId_indirizzo() {
        return this.id_indirizzo;
    }

    public String getVia() {
        return this.via;
    }

    public String getCitt‡() {
        return this.citt‡;
    }

    public String getCap() {
        return this.cap;
    }

    public String getPaese() {
        return this.paese;
    }

    public String getCivico() {
        return this.civico;
    }

    public int getId_cliente_associato() {
        return this.id_cliente_associato;
    }

    public void setId_cliente_associato(int id_cliente_associato) {
        this.id_cliente_associato = id_cliente_associato;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public void setId_indirizzo(int id_indirizzo) {
        this.id_indirizzo = id_indirizzo;
    }

    public void setCitt‡(String citt‡) {
        this.citt‡ = citt‡;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public void setVia(String via) {
        this.via = via;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Cliente con ID = " + this.id_cliente_associato + "ha associato il seguente indirizzo con ID = " + this.id_indirizzo + "via = " + this.via + ", citt√† = " + this.citt‡ + ", cap = " + this.cap + ", paese = " + this.paese + "infine numero civico = " + this.civico;
    }
}