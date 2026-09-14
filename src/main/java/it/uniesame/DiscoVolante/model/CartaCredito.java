package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class CartaCredito {
    //attributi
    private int id_carta;
    private String numero_carta;
    private String circuito;

    //costruttore
    public CartaCredito() {

    }

    // Costruttore completo
    public CartaCredito(int id_carta, String numero_carta, String circuito) {
        this.circuito = circuito;
        this.numero_carta = numero_carta;
        this.id_carta = id_carta;
    }

    //metodi getter e setter
    public int getId_carta() {
        return this.id_carta;
    }

    public String getNumero_carta() {
        return this.numero_carta;
    }

    public String getCircuito() {
        return this.circuito;
    }

    public void setId_carta(int id_carta) {
        this.id_carta = id_carta;
    }

    public void setNumero_carta(String numero_carta) {
        this.numero_carta = numero_carta;
    }

    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Carta di credito con ID = " + this.id_carta + "avente numero " + this.numero_carta + "facente parte del circuito " + this.circuito;
    }
}