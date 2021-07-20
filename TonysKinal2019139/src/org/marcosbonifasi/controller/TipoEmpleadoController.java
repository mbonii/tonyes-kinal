
package org.marcosbonifasi.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.marcosbonifasi.bean.TipoEmpleado;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class TipoEmpleadoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    ObservableList<TipoEmpleado> listaTipoEmpleado;
    
    @FXML private TableView tblTipoEmpleado;
    @FXML private TableColumn colCodigoTipoEmpleado;
    @FXML private TableColumn colDescripcion;
    @FXML private TextField txtCodigoTipoEmpleado;
    @FXML private TextField txtDescripcion;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblTipoEmpleado.setItems(getEmpresa());
        colCodigoTipoEmpleado.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, Integer>("codigoTipoEmpleado"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoEmpleado, String>("descripcion"));
    }
    
    public ObservableList<TipoEmpleado> getEmpresa(){
        ArrayList<TipoEmpleado> lista = new ArrayList<TipoEmpleado>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTipoEmpleado()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new TipoEmpleado( resultado.getInt("codigoTipoEmpleado"),
                                            resultado.getString("descripcion")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipoEmpleado = FXCollections.observableArrayList(lista);
    }
    
   // ----------------- CRUD --------------------
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                deseleccionarElemento();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblTipoEmpleado.setDisable(true);
                activarControlers();
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(txtDescripcion)){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblTipoEmpleado.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }   
    }
    
    public void guardar(){
        TipoEmpleado registro = new TipoEmpleado();
        registro.setDescripcion(txtDescripcion.getText());
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_AgregarTipoEmpleado(?)");
            procedimiento.setString(1, registro.getDescripcion());
                
            procedimiento.execute();
            listaTipoEmpleado.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                deseleccionarElemento();
                limpiarControles();
                btnNuevo.setText("NUEVO");
                btnEliminar.setText("ELIMINAR");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblTipoEmpleado.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            case NINGUNO:
                if(tblTipoEmpleado.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar este registros?", "Eliminar Tipo Empleado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(validarEliminarTipoEmpleado(respuesta)){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarTipoEmpleado(?)}");
                            procedimiento.setInt(1, ((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado());
                            procedimiento.execute();

                            listaTipoEmpleado.remove(tblTipoEmpleado.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        limpiarControles();
                        deseleccionarElemento();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro");
                }
                break;
        }
    }
    
    public void editar(){
       switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTipoEmpleado.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControlers();
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:   
                if(validarCampos(txtDescripcion)){
                    actualizar();
                    desactivarControles();
                    deseleccionarElemento();
                    limpiarControles();
                    btnEditar.setText("EDITAR");
                    btnReporte.setText("REPORTE");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
       } 
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarTipoEmpleado(?,?)}");
            TipoEmpleado registro = (TipoEmpleado) tblTipoEmpleado.getSelectionModel().getSelectedItem();
            
            registro.setDescripcion(txtDescripcion.getText());
            
            procedimiento.setInt(1, registro.getCodigoTipoEmpleado());
            procedimiento.setString(2, registro.getDescripcion());
            procedimiento.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                deseleccionarElemento();
                limpiarControles();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    // ----------------- Utilidades -----------------
    public void seleccionarElemento(){
        if(tblTipoEmpleado.getSelectionModel().getSelectedItem() != null){
            txtCodigoTipoEmpleado.setText(String.valueOf(((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getCodigoTipoEmpleado()));
            txtDescripcion.setText(((TipoEmpleado)tblTipoEmpleado.getSelectionModel().getSelectedItem()).getDescripcion());
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblTipoEmpleado.getItems().indexOf(tblTipoEmpleado.getSelectionModel().getSelectedItem());
        if(index >= 0){
            tblTipoEmpleado.getSelectionModel().select(null);
        }
    }
    
    public void limpiarControles(){
        txtCodigoTipoEmpleado.setText("");
        txtDescripcion.setText("");
    }
    
    public void activarControlers(){
        txtCodigoTipoEmpleado.setEditable(false);
        txtDescripcion.setEditable(true);
    }
    
    public void desactivarControles(){
        txtCodigoTipoEmpleado.setEditable(false);
        txtDescripcion.setEditable(false);
    }
    
    // ------------ Otras Ventanas ------------
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaEmpleado(){
        this.escenarioPrincipal.ventanaEmpleado();
    }
    
    // -------------- Setters and Getters --------------
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    // ---------------- Validaciones ---------------
    private boolean validarCampos(TextField descripcion){
        boolean bandera = false;
        if(!descripcion.getText().equals(""))
            bandera = true;
        else
            JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE TODOS LOS CAMPOS", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
            
        return bandera;
    }
    
    private boolean validarEliminarTipoEmpleado(int respuesta){
        boolean bandera = false;
        if(respuesta == JOptionPane.YES_OPTION){
            int decision = JOptionPane.showConfirmDialog(null, "AL ELIMINAR ESTE REGISTRO, POSIBLEMENTE OTROS SE VERÁN AFECTADOS"
                    + "\n¿Está seguro de querer eliminar este registro?", "ADVERTENCIA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(decision == JOptionPane.YES_OPTION){
                bandera = true;
            }
        }
        return bandera;
    }
    
}
