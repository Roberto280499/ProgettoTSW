package it.uniesame.DiscoVolante.model;

import java.sql.Timestamp;
import java.lang.*;
import java.util.*;

public class Carrello {

    //attributi
    private int id_carello;
    private Timestamp data_creazione;
    private Integer id_cliente_associato; //fk Riferimento all'account può essere null se l'utente non è loggato
    private Map<Prodotto, Integer> mappa_prodotti = new LinkedHashMap<>();

    //costruttore
    public Carrello(){

    }

    // Costruttore completo
    public Carrello(int id_carello, Timestamp data_creazione, Integer id_cliente_associato) {
        this.id_carello = id_carello;
        this.data_creazione = data_creazione;
        this.id_cliente_associato = id_cliente_associato;
    }

    //metodi getter e setter
    public int getId_carello() {
        return this.id_carello;
    }

    public Timestamp getData_creazione() {
        return this.data_creazione;
    }

    public Integer getId_cliente_associato() {
        return this.id_cliente_associato;
    }

    public void setId_carello(int id_carello) {
        this.id_carello = id_carello;
    }

    public void setData_creazione(Timestamp data_creazione) {
        this.data_creazione = data_creazione;
    }

    public void setId_cliente_associato(Integer id_cliente_associato) {
        this.id_cliente_associato = id_cliente_associato;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Carello con ID = " + this.id_carello + " creato in data " + this.data_creazione + "riferito al cliente con ID associato = " + this.id_cliente_associato;
    }

    public void aggiungiProdotto(Prodotto p) {
        if (mappa_prodotti.containsKey(p)) {
            int qtaAttuale = mappa_prodotti.get(p);
            mappa_prodotti.put(p, qtaAttuale + 1);
        }
        else {
            mappa_prodotti.put(p, 1);
        }
    }

    public void rimuoviProdotto(Prodotto p) {
        if (mappa_prodotti.containsKey(p)) {
            int qtaAttuale = mappa_prodotti.get(p);
            if (qtaAttuale > 1) {
                mappa_prodotti.put(p, qtaAttuale - 1);
            }
            else {
                mappa_prodotti.remove(p);
            }
        }
    }

    public void rimuoviTutto(Prodotto p) {
        mappa_prodotti.remove(p);
    }

    public Map<Prodotto, Integer> getProdotti() {
        return mappa_prodotti;
    }

    public float getTotale() {
        float totale = 0.0f;
        for (Map.Entry<Prodotto, Integer> entry : mappa_prodotti.entrySet()) {
            Prodotto p = entry.getKey();
            int qta = entry.getValue();

            totale += p.getPrezzo() * qta;
        }
        return totale;
    }

    public int getNumeroArticoli() {
        return mappa_prodotti.values().stream().mapToInt(Integer::intValue).sum();
    }
}