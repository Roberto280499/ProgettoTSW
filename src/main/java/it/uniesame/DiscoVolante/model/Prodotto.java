package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class Prodotto {
    //attributi
    private int id_prodotto;
    private String titolo;
    private String artista;
    private float prezzo;
    private int disponibilit‡;
    private String tipo;
    private boolean visibile;

    //costruttore
    public Prodotto() {

    }

    // Costruttore completo
    public Prodotto(int id_prodotto, String titolo, String artista, float prezzo, int disponibilit‡, String tipo) {
        this.id_prodotto = id_prodotto;
        this.titolo = titolo;
        this.artista = artista;
        this.prezzo = prezzo;
        this.disponibilit‡ = disponibilit‡;
        this.tipo = tipo;
    }

    //metodi getter e setter
    public int getId_prodotto() {
        return this.id_prodotto;
    }

    public int getDisponibilit‡() {
        return this.disponibilit‡;
    }

    public float getPrezzo() {
        return this.prezzo;
    }

    public String getTitolo() {
        return this.titolo;
    }

    public String getArtista() {
        return this.artista;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setId_prodotto(int id_prodotto) {
        this.id_prodotto = id_prodotto;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setDisponibilit‡(int disponibilit‡) {
        this.disponibilit‡ = disponibilit‡;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Il prodotto con ID = " + this.id_prodotto + "con titolo = " + this.titolo + "e  artista = " + "avente il prezzo = " + this.prezzo + "euro" + "con disponibilit√† = " + this.disponibilit‡ + "di tipo = " + this.tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Prodotto prodotto = (Prodotto) o;

        return id_prodotto == prodotto.id_prodotto;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id_prodotto); //cast inderetto a int
    }
}