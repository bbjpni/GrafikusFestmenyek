package com.company;

public class Festmeny {
    private int id;
    private String szerzo;
    private boolean display;
    private int ev;
    private String cim;

    public Festmeny(int id, String szerzo, boolean display, int ev, String cim) {
        this.id = id;
        this.szerzo = szerzo;
        this.display = display;
        this.ev = ev;
        this.cim = cim;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSzerzo(String szerzo) {
        this.szerzo = szerzo;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public void setEv(int ev) {
        this.ev = ev;
    }

    public void setCim(String cim) {
        this.cim = cim;
    }

    public int getId() {
        return id;
    }

    public String getSzerzo() {
        return szerzo;
    }

    public boolean isDisplay() {
        return display;
    }

    public int getEv() {
        return ev;
    }

    public String getCim() {
        return cim;
    }

    @Override
    public String toString() {
        return String.format("%4d %-25s %5d %-25s %-17s",id,szerzo,ev,cim,display ? "Kiállítva" : "Nem megtekínthető");
    }
}
