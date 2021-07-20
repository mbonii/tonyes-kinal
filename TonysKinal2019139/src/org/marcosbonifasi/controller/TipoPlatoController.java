
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
import org.marcosbonifasi.bean.TipoPlato;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class TipoPlatoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NINGUNO, NUEVO, GUARDAR, CANCELAR, EDITAR, ACTUALIZAR, REPORTE};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<TipoPlato> listaTipoPlato;
    
    @FXML private TextField txtCodigoTipoPlato;
    @FXML private TextField txtDescripcion;
    @FXML private TableView tblTipoPlato;
    @FXML private TableColumn colCodigoTipoPlato;
    @FXML private TableColumn colDescripcion;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblTipoPlato.setItems(getTipoPlato());
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<TipoPlato, Integer>("codigoTipoPlato"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<TipoPlato, String>("descripcion"));
    }
    
    public ObservableList<TipoPlato> getTipoPlato(){
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
    
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTipoPlato()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new TipoPlato (resultado.getInt("codigoTipoPlato"),
                                            resultado.getString("descripcion")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipoPlato = FXCollections.observableArrayList(lista);
    }
    
    // ----------------- CRUD ----------------------
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                deseleccionarElemento();
                tblTipoPlato.setDisable(true);
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(txtDescripcion)){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    tblTipoPlato.setDisable(false);
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void guardar(){
        TipoPlato registro = new TipoPlato();
        registro.setDescripcion(txtDescripcion.getText());
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarTipoPlato(?)}");
            procedimiento.setString(1, registro.getDescripcion());
            
            procedimiento.execute();
            listaTipoPlato.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                tblTipoPlato.setDisable(false);
                btnNuevo.setText("NUEVO");
                btnEliminar.setText("ELIMINAR");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblTipoPlato.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar este registro?", "ELIMINAR TIPO PLATO",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(validarEliminarTipoPlato(respuesta)){
                        try {
                         PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarTipoPlato(?)}");   
                         procedimiento.setInt(1, ((TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
                         procedimiento.execute();
                         
                         listaTipoPlato.remove(tblTipoPlato.getSelectionModel().getSelectedIndex());
                         limpiarControles();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    deseleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null , "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTipoPlato.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);

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
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarTipoPlato(?,?)}");
            TipoPlato registro = (TipoPlato) tblTipoPlato.getSelectionModel().getSelectedItem();

            registro.setDescripcion(txtDescripcion.getText());

            procedimiento.setInt(1, registro.getCodigoTipoPlato());
            procedimiento.setString(2, registro.getDescripcion());
            procedimiento.execute();
        } catch (Exception e) {
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
        if(tblTipoPlato.getSelectionModel().getSelectedItem() != null){
            txtCodigoTipoPlato.setText(String.valueOf(((TipoPlato)(tblTipoPlato.getSelectionModel().getSelectedItem())).getCodigoTipoPlato()));
            txtDescripcion.setText(((TipoPlato)tblTipoPlato.getSelectionModel().getSelectedItem()).getDescripcion());
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblTipoPlato.getItems().indexOf(tblTipoPlato.getSelectionModel().getSelectedItem());
        if(index >= 0){
            tblTipoPlato.getSelectionModel().select(null);
        }
    }

    public void limpiarControles(){
        txtCodigoTipoPlato.setText("");
        txtDescripcion.setText("");
    }
    
    public void desactivarControles(){
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcion.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoTipoPlato.setEditable(false);
        txtDescripcion.setEditable(true);
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    // ---------------- Setters and Getters ----------------
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaPlato(){
        this.escenarioPrincipal.ventanaPlato();
    }
    
    // ------------------ Validaciones -----------------
    private boolean validarCampos(TextField descripcion){
        boolean bandera = false;
        if(!descripcion.getText().equals(""))
            bandera = true;
        else
            JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE TODOS LOS CAMPOS", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
            
        return bandera;
    }
    
    private boolean validarEliminarTipoPlato(int respuesta){
        boolean bandera = false;
        
        if(respuesta == JOptionPane.YES_OPTION){
            int decision = JOptionPane.showConfirmDialog(null, "AL ELIMINAR ESTE REGISTRO, POSIBLEMENTE OTROS SE VARÁN AFECTADOS"
                    + "\n>¿Está seguro de querer eliminar este registro?", "ADVERTENCIA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(decision == JOptionPane.YES_OPTION){
                bandera = true;
            }
        }
        return bandera;
    }
}
