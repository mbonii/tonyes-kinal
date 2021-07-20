
package org.marcosbonifasi.bean;

public class Servicio_has_Plato {
    private int codigoServicio;
    private int codigoPlato;
    
    public Servicio_has_Plato() {
    }

    public Servicio_has_Plato(int codigoServicio, int codigoPlato) {
        this.codigoServicio = codigoServicio;
        this.codigoPlato = codigoPlato;
    }
    
    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }
}
