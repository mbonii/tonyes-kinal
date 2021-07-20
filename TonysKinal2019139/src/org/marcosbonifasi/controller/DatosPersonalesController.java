
package org.marcosbonifasi.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.marcosbonifasi.system.Principal;

public class DatosPersonalesController implements Initializable{

    private Principal escenarioPrincipal;
  
    @FXML private Button btnDesarrollador;
    @FXML private Button btnAdministracion;
    @FXML private AnchorPane anchorPaneDesarrollador;
    @FXML private ImageView imgKinal;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {}
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void desarrollador(){
        anchorPaneDesarrollador.setVisible(!(anchorPaneDesarrollador.isVisible()));
    }
    
    public void administracion(){
        imgKinal.setVisible(!(imgKinal.isVisible()));
    }
}
