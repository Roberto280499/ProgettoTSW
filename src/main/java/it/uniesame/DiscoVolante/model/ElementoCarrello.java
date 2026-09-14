/*--TABELLA DI RELAZIONE TRA CARRELLO E PRODOTTI PER CAPIRE QUALI PRODOTTI HA INSERITO L'UTENTE--*/
package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class ElementoCarrello {
    //attributi
    private int id_carello;
    private int id_prodotto;
    private int quantità;

    //costruttore
    public ElementoCarrello() {

    }

    // Costruttore completo
    public ElementoCarrello(int id_carello, int id_prodotto, int quantità) {
        this.id_carello = id_carello;
        this.id_prodotto = id_prodotto;
        this.quantità= quantità;
    }

    //metodi getter e setter

    public int getId_carello() {
        return this.id_carello;
    }

    public int getId_prodotto() {
        return this.id_prodotto;
    }

    public int getQuantità() {
        return this.quantità;
    }

    public void setId_carello(int id_carello) {
        this.id_carello = id_carello;
    }

    public void setId_prodotto(int id_prodotto) {
        this.id_prodotto = id_prodotto;
    }

    public void setQuantità(int quantità) {
        this.quantità= quantità;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Il carello con ID = " + this.id_carello + "à¨ associato al prodotto con ID = " + this.id_prodotto + "con quantità  = " + this.quantità;
    }
}