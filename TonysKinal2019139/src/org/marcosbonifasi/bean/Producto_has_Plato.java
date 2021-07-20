
package org.marcosbonifasi.bean;

public class Producto_has_Plato {
    private int codigoProducto;
    private int codigoPlato;

    public Producto_has_Plato() {
    }

    public Producto_has_Plato(int codigoProducto, int codigoPlato) {
        this.codigoProducto = codigoProducto;
        this.codigoPlato = codigoPlato;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }
}
