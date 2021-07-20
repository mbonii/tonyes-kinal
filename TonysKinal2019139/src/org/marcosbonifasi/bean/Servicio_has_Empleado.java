
package org.marcosbonifasi.bean;

import java.util.Date;

public class Servicio_has_Empleado {
    private int codigoServicio_has_Empleado;
    private int codigoServicio;
    private int codigoEmpleado;
    private Date fechaEvento;
    private String horaEvento;
    private String lugarEvento;

    public Servicio_has_Empleado() {
    }

    public Servicio_has_Empleado(int codigoServicio_has_Empleado, int codigoServicio, int codigoEmpleado, Date fechaEvento, String horaEvento, String lugarEvento) {
        this.codigoServicio_has_Empleado = codigoServicio_has_Empleado;
        this.codigoServicio = codigoServicio;
        this.codigoEmpleado = codigoEmpleado;
        this.fechaEvento = fechaEvento;
        this.horaEvento = horaEvento;
        this.lugarEvento = lugarEvento;
    }

    public int getCodigoServicio_has_Empleado() {
        return codigoServicio_has_Empleado;
    }

    public void setCodigoServicio_has_Empleado(int codigoServicio_has_Empleado) {
        this.codigoServicio_has_Empleado = codigoServicio_has_Empleado;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public int getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(int codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getHoraEvento() {
        return horaEvento;
    }

    public void setHoraEvento(String horaEvento) {
        this.horaEvento = horaEvento;
    }

    public String getLugarEvento() {
        return lugarEvento;
    }

    public void setLugarEvento(String lugarEvento) {
        this.lugarEvento = lugarEvento;
    }
}
