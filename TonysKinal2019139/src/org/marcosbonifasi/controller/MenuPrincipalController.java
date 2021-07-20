
package org.marcosbonifasi.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.marcosbonifasi.system.Principal;

public class MenuPrincipalController implements Initializable{

    private Principal escenarioPrincipal;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaProgramador(){
        this.escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaEmpresa(){
        this.escenarioPrincipal.ventanaEmpresa();
    }
    
    public void ventanaTipoEmpleado(){
        this.escenarioPrincipal.ventanaTipoEmpleado();
    }
    
    public void ventanaPresupuesto(){
        this.escenarioPrincipal.ventanaPresupuesto();
    }
    
    public void ventanaEmpleado(){
        this.escenarioPrincipal.ventanaEmpleado();
    }
    
    public void ventanaTipoPlato(){
        this.escenarioPrincipal.ventanaTipoPlato();
    }
    
    public void ventanaProducto(){
        this.escenarioPrincipal.ventanaProducto();
    }
    
    public void ventanaServicio(){
        this.escenarioPrincipal.ventanaServicio();
    }
    
    public void ventanaServicio_has_Empleado(){
        this.escenarioPrincipal.ventanaServicio_has_Empleado();
    }
    
    public void ventanaPlato(){
        this.escenarioPrincipal.ventanaPlato();
    }
    
    public void ventanaServicio_has_Plato(){
        this.escenarioPrincipal.ventanaServicio_has_Plato();
    }
    
    public void ventanaProducto_has_Plato(){
        this.escenarioPrincipal.ventanaProducto_has_Plato();
    }
}
