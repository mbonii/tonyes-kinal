
package org.marcosbonifasi.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.marcosbonifasi.bean.Plato;
import org.marcosbonifasi.bean.TipoPlato;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class PlatoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NINGUNO, GUARDAR, ELIMINAR, EDITAR, CANCELAR, REPORTE, ACTUALIZAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Plato> listaPlato;
    private ObservableList<TipoPlato> listaTipoPlato;
    //Objetos para expreisones regulares
    private Pattern expresionRegularCantidad;
    private Pattern expresionRegularPrecio;
    private Matcher mat;
    
    @FXML private TextField txtCodigoPlato;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtNombrePlato;
    @FXML private TextField txtDescripcionPlato;
    @FXML private TextField txtPrecioPlato;
    @FXML private ComboBox cmbTipoPlato;
    @FXML private TableView tblPlatos;
    @FXML private TableColumn colCodigoPlato;
    @FXML private TableColumn colCantidad;
    @FXML private TableColumn colNombrePlato;
    @FXML private TableColumn colDescripcionPlato;
    @FXML private TableColumn colPrecioPlato;
    @FXML private TableColumn colCodigoTipoPlato;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbTipoPlato.setItems(getTipoPlato());
    }
    
    public void cargarDatos(){
        tblPlatos.setItems(getPlato());
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoPlato"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("cantidad"));
        colNombrePlato.setCellValueFactory(new PropertyValueFactory<Plato, String>("nombrePlato"));
        colDescripcionPlato.setCellValueFactory(new PropertyValueFactory<Plato, String>("descripcionPlato"));
        colPrecioPlato.setCellValueFactory(new PropertyValueFactory<Plato, Double>("precioPlato"));
        colCodigoTipoPlato.setCellValueFactory(new PropertyValueFactory<Plato, Integer>("codigoTipoPlato"));
    }
    
    public ObservableList<Plato> getPlato(){
        ArrayList<Plato> lista = new ArrayList<Plato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPlatos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Plato(resultado.getInt("codigoPlato"),
                                    resultado.getInt("cantidad"),
                                    resultado.getString("nombrePlato"),
                                    resultado.getString("descripcionPlato"),
                                    resultado.getDouble("precioPlato"),
                                    resultado.getInt("codigoTipoPlato") 
                ));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPlato = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<TipoPlato> getTipoPlato(){
        ArrayList<TipoPlato> lista = new ArrayList<TipoPlato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarTipoPlato()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new TipoPlato(resultado.getInt("codigoTipoPlato"),
                                    resultado.getString("descripcion")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipoPlato = FXCollections.observableArrayList(lista);
    }
    
    public TipoPlato buscarTipoPlato(int codigoTipoPlato){
        TipoPlato registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarTipoPlato(?)}");
            procedimiento.setInt(1, codigoTipoPlato);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                registro = new TipoPlato(resultado.getInt("codigoTipoPlato"),
                                            resultado.getString("descripcion")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    // ---------------------- CRUD -----------------------
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControler();
                activarControles();
                deseleccionarElemento();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblPlatos.setDisable(true);
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(txtCantidad, txtNombrePlato, txtDescripcionPlato, txtPrecioPlato, cmbTipoPlato)){
                    guardar();
                    desactivarControles();
                    limpiarControler();
                    deseleccionarElemento();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblPlatos.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void guardar(){
        Plato registro = new Plato();
        registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
        registro.setNombrePlato(txtNombrePlato.getText());
        registro.setDescripcionPlato(txtDescripcionPlato.getText());
        registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
        registro.setCodigoTipoPlato(((TipoPlato)cmbTipoPlato.getSelectionModel().getSelectedItem()).getCodigoTipoPlato());
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPlato(?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getCantidad());
            procedimiento.setString(2, registro.getNombrePlato());
            procedimiento.setString(3, registro.getDescripcionPlato());
            procedimiento.setDouble(4, registro.getPrecioPlato());
            procedimiento.setInt(5, registro.getCodigoTipoPlato());
            procedimiento.execute();
            
            listaPlato.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControler();
                deseleccionarElemento();
                btnNuevo.setText("NUEVO");
                btnEliminar.setText("ELIMINAR");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblPlatos.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro?", "ELIMINAR PLATO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(validarEliminarPlato(respuesta)){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarPlato(?)}");
                            procedimiento.setInt(1, ((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato());
                            procedimiento.execute();
                            
                            listaPlato.remove(tblPlatos.getSelectionModel().getSelectedIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    JOptionPane.showMessageDialog(null , "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPlatos.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                if(validarCampos(txtCantidad, txtNombrePlato, txtDescripcionPlato, txtPrecioPlato, cmbTipoPlato)){
                    actualizar();
                    desactivarControles();
                    limpiarControler();
                    deseleccionarElemento();
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarPlato(?,?,?,?,?)}");
            Plato registro = (Plato)tblPlatos.getSelectionModel().getSelectedItem();
            
            registro.setCantidad(Integer.parseInt(txtCantidad.getText()));
            registro.setNombrePlato(txtNombrePlato.getText());
            registro.setDescripcionPlato(txtDescripcionPlato.getText());
            registro.setPrecioPlato(Double.parseDouble(txtPrecioPlato.getText()));
            
            procedimiento.setInt(1, registro.getCodigoPlato());
            procedimiento.setInt(2, registro.getCantidad());
            procedimiento.setString(3, registro.getNombrePlato());
            procedimiento.setString(4, registro.getDescripcionPlato());
            procedimiento.setDouble(5, registro.getPrecioPlato());
            procedimiento.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControler();
                deseleccionarElemento();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    // ------------------- Utilidades -------------------
    public void desactivarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(false);
        txtNombrePlato.setEditable(false);
        txtDescripcionPlato.setEditable(false);
        txtPrecioPlato.setEditable(false);
        cmbTipoPlato.setDisable(false);
    }
    
    public void limpiarControler(){
        txtCodigoPlato.setText("");
        txtCantidad.setText("");
        txtNombrePlato.setText("");
        txtDescripcionPlato.setText("");
        txtPrecioPlato.setText("");
        cmbTipoPlato.getSelectionModel().select(null);
    }
    
    public void activarControles(){
        txtCodigoPlato.setEditable(false);
        txtCantidad.setEditable(true);
        txtNombrePlato.setEditable(true);
        txtDescripcionPlato.setEditable(true);
        txtPrecioPlato.setEditable(true);
        cmbTipoPlato.setDisable(false);
    }
    
    public void seleccionarElemento(){
        if(tblPlatos.getSelectionModel().getSelectedItem() != null){
            txtCodigoPlato.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
            txtCantidad.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCantidad()));
            txtNombrePlato.setText(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getNombrePlato());
            txtDescripcionPlato.setText(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getDescripcionPlato());
            txtPrecioPlato.setText(String.valueOf(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getPrecioPlato()));
            cmbTipoPlato.getSelectionModel().select(buscarTipoPlato(((Plato)tblPlatos.getSelectionModel().getSelectedItem()).getCodigoTipoPlato()));
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblPlatos.getItems().indexOf(tblPlatos.getSelectionModel().getSelectedItem());
        if(index >= 0){
            tblPlatos.getSelectionModel().select(null);
        }
    }
    
    // ------------------- Setters and Getters -----------------
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    public void tipoPlato(){
        this.escenarioPrincipal.ventanaTipoPlato();
    }
    
    // ------------------- Validaciones ---------------------
    private boolean validarCampos(TextField cantidad, TextField nombrePlato, TextField descripcionPlato, TextField precioPlato, ComboBox codTipoPlato){
        boolean bandera = false;
        
        if(!cantidad.getText().equals("") && !nombrePlato.getText().equals("") && !descripcionPlato.getText().equals("") && !precioPlato.getText().equals(bandera) && codTipoPlato.getSelectionModel().getSelectedItem() != null){
            if(validarCantidad(cantidad) && validarPrecio(precioPlato)){
                bandera = true;
            }else {
                JOptionPane.showMessageDialog(null, "POR FAVOR, LLEGE CORRECTAMENTE TODOS LOS DATOS");
            }
        }
        
        return bandera;
    }
    
    private boolean validarCantidad(TextField cantidad){
        boolean bandera = false;
        expresionRegularCantidad = Pattern.compile("^\\d+$");
        mat = expresionRegularCantidad.matcher(cantidad.getText());
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarPrecio(TextField precioPlato){
        boolean bandera = false;
        expresionRegularCantidad = Pattern.compile("^\\d{1,7}|\\d{1,7}\\.\\d{1,2}$");
        mat = expresionRegularCantidad.matcher(precioPlato.getText());
    
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarEliminarPlato(int respuesta){
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
