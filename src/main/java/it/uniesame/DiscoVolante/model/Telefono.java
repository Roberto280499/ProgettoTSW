package it.uniesame.DiscoVolante.model;
import java.lang.*;

public class Telefono {
    //attributi
    private String prefisso;
    private String numero;
    private int id_telefono;
    private int id_cliente_associato;

    //costruttore
    public Telefono() {

    }

    // Costruttore completo
    public Telefono(String prefisso, String numero, int id_telefono, int id_cliente_associato) {
        this.prefisso = prefisso;
        this.numero = numero;
        this.id_telefono = id_telefono;
        this.id_cliente_associato = id_cliente_associato;
    }

    //metodi getter e setter
    public String getPrefisso() {
        return this.prefisso;
    }

    public String getNumero() {
        return this.numero;
    }

    public int getId_cliente_associato() {
        return this.id_cliente_associato;
    }

    public int getId_telefono() {
        return this.id_telefono;
    }

    public void setId_cliente_associato(int id_cliente_associato) {
        this.id_cliente_associato = id_cliente_associato;
    }

    public void setId_telefono(int id_telefono) {
        this.id_telefono = id_telefono;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setPrefisso(String prefisso) {
        this.prefisso = prefisso;
    }

    //altri metodi
    @Override
    public String toString() {
        return "ID =" + this.id_telefono + "Il numero di telefono con prefisso = " + this.prefisso + "e numero = " + this.numero + "è associato all'account con ID = " + this.id_cliente_associato;    }
}