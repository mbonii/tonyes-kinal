/*
Programador: 
    Bonifasi de León, Marcos Daniel

    Carné: 2019-139

    Código Técnico:
        IN5AM

    Fecha Creación:
        23/04/2020
Modificaciones:
    Creación de la imagen de fondo
    28/04/2020

 */
package org.marcosbonifasi.system;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.marcosbonifasi.controller.DatosPersonalesController;
import org.marcosbonifasi.controller.EmpleadoController;
import org.marcosbonifasi.controller.EmpresaController;
import org.marcosbonifasi.controller.MenuPrincipalController;
import org.marcosbonifasi.controller.PlatoController;
import org.marcosbonifasi.controller.PresupuestoController;
import org.marcosbonifasi.controller.ProductoController;
import org.marcosbonifasi.controller.Producto_has_PlatoController;
import org.marcosbonifasi.controller.ServicioController;
import org.marcosbonifasi.controller.Servicio_has_EmpleadoController;
import org.marcosbonifasi.controller.Servicio_has_PlatoController;
import org.marcosbonifasi.controller.TipoEmpleadoController;
import org.marcosbonifasi.controller.TipoPlatoController;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/marcosbonifasi/view/";// a donde va ir a buscar las vistas
    private Stage escenarioPrincipal;
    private Scene escena;
    
    
    @Override
    public void start(Stage escenarioPrincipal) throws Exception{
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("Tony's Kinal App");
        //Parent root = FXMLLoader.load(getClass().getResource("/org/marcosbonifasi/view/MenuPrincipalView.fxml"));
        //Scene escena = new Scene(root);
        //escenarioPrincipal.setScene(escena);
        menuPrincipal();
        escenarioPrincipal.setResizable(false);
        
        escenarioPrincipal.getIcons().add(new Image("/org/marcosbonifasi/image/icono.png"));
        escenarioPrincipal.setResizable(false);
        escenarioPrincipal.show();
    }

    public void menuPrincipal(){
        try {
            MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",900,600);
            menuPrincipal.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try {
            DatosPersonalesController datosProgramador = (DatosPersonalesController)cambiarEscena("VentanaProgramadorView.fxml", 787, 525);
            datosProgramador.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpresa(){
        try {
            EmpresaController empresaController = (EmpresaController)cambiarEscena("EmpresasView.fxml", 984, 650);
            empresaController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoEmpleado(){
        try {
            TipoEmpleadoController tipoEmpleado = (TipoEmpleadoController)cambiarEscena("TipoEmpleadoView.fxml", 960, 549);
            tipoEmpleado.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaEmpleado(){
        try {
            EmpleadoController empleadoController = (EmpleadoController) cambiarEscena("EmpleadosView.fxml", 1093, 658);
            empleadoController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaPresupuesto(){
        try {
            PresupuestoController presupuesto = (PresupuestoController) cambiarEscena("PresupuestoView.fxml", 915, 595);
            presupuesto.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaTipoPlato(){
        try {
            TipoPlatoController tipoPlatoController = (TipoPlatoController) cambiarEscena("TipoPlatoView.fxml", 953, 549);
            tipoPlatoController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaProducto(){
        try {
            ProductoController productoController = (ProductoController) cambiarEscena("ProductosView.fxml", 957, 567);
            productoController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServicio(){
        try {
            ServicioController servicioController = (ServicioController) cambiarEscena("ServiciosView.fxml", 1086, 658);
            servicioController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServicio_has_Empleado(){
        try {
            Servicio_has_EmpleadoController servicioHasEmpleado = (Servicio_has_EmpleadoController) cambiarEscena("Servicios_has_EmpleadosView.fxml", 1153, 606);
            servicioHasEmpleado.setMenuPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaPlato(){
        try {
            PlatoController platoController = (PlatoController) cambiarEscena("PlatosView.fxml", 989, 600);
            platoController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaServicio_has_Plato(){
        try {
            Servicio_has_PlatoController servicioHasPlato = (Servicio_has_PlatoController) cambiarEscena("Servicios_has_PlatosView.fxml", 864, 453);
            servicioHasPlato.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaProducto_has_Plato(){
        try {
            Producto_has_PlatoController productoHasPlato = (Producto_has_PlatoController) cambiarEscena("Productos_has_PlatosView.fxml", 870, 453);
            productoHasPlato.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto)throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        
        return resultado;
    }
    
}
