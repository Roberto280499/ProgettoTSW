package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class DettaglioOrdine {
    //attributi
    private int id_ordine_associato;
    private int id_prodotto_associato;
    private int quantità;
    private float prezzo_intero; // Il prezzo al momento dell'acquisto (storico)
    private Prodotto prodotto;

    //costruttore
    public DettaglioOrdine() {

    }

    // Costruttore completo
    public DettaglioOrdine(int id_ordine_associato, int id_prodotto_associato, int quantità, float prezzo_intero) {
        this.id_ordine_associato = id_ordine_associato;
        this.id_prodotto_associato = id_prodotto_associato;
        this.quantità= quantità;
        this.prezzo_intero = prezzo_intero;
    }

    //metodi getter e setter

    public int getId_prodotto_associato() {
        return this.id_prodotto_associato;
    }

    public int getId_ordine_associato() {
        return this.id_ordine_associato;
    }

    public int getQuantità() {
        return this.quantità;
    }

    public float getPrezzo_intero() {
        return this.prezzo_intero;
    }

    public void setQuantità(int quantità) {
        this.quantità= quantità;
    }

    public void setId_ordine_associato(int id_ordine_associato) {
        this.id_ordine_associato = id_ordine_associato;
    }

    public void setId_prodotto_associato(int id_prodotto_associato) {
        this.id_prodotto_associato = id_prodotto_associato;
    }

    public void setPrezzo_intero(float prezzo_intero) {
        this.prezzo_intero = prezzo_intero;
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
        return "L'ordine con ID = " + this.id_ordine_associato + "contenente il prodotto con ID = " + this.id_ordine_associato + "con quantità  = " + this.quantità + "e prezzo intero = " + this.prezzo_intero;
    }
}