package it.uniesame.DiscoVolante.model;
import java.lang.*;
import java.util.Date;

public class Pagamento {
    //attributi
    private int id_pagamento;
    private int id_ordine_associato;
    private int id_carta_credito_usata;
    private float importo;
    private Date data_pagamento;
    private String stato;

    //costruttore
    public Pagamento() {

    }

    // Costruttore completo
    public Pagamento(int id_pagamento, int id_ordine_associato, int id_carta_credito_usata, float importo, Date data_pagamento, String stato) {
        this.id_pagamento = id_pagamento;
        this.id_ordine_associato = id_ordine_associato;
        this.id_carta_credito_usata = id_carta_credito_usata;
        this.importo = importo;
        this.data_pagamento = data_pagamento;
        this.stato = stato;
    }


    //metodi getter e setter
    public String getStato() {
        return this.stato;
    }

    public int getId_ordine_associato() {
        return this.id_ordine_associato;
    }

    public int getId_pagamento() {
        return this.id_pagamento;
    }

    public Date getData_pagamento() {
        return this.data_pagamento;
    }

    public int getId_carta_credito_usata() {
        return this.id_carta_credito_usata;
    }

    public float getImporto() {
        return this.importo;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public void setId_ordine_associato(int id_ordine_associato) {
        this.id_ordine_associato = id_ordine_associato;
    }

    public void setData_pagamento(Date data_pagamento) {
        this.data_pagamento = data_pagamento;
    }

    public void setId_pagamento(int id_pagamento) {
        this.id_pagamento = id_pagamento;
    }

    public void setId_carta_credito_usata(int id_carta_credito_usata) {
        this.id_carta_credito_usata = id_carta_credito_usata;
    }

    public void setImporto(float importo) {
        this.importo = importo;
    }

    //altri metodi
    @Override
    public String toString() {
        return "Pagamento avente ID = " + this.id_pagamento + " associato all'ordine con ID = " + this.id_ordine_associato + "e associata alla carta di credito con ID = " + this.id_carta_credito_usata + ", presenta un importo totale di " + this.importo + " creato in data " + this.data_pagamento + "con uno stato = " + this.stato;
    }
}