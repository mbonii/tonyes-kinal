
package org.marcosbonifasi.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.marcosbonifasi.bean.Empresa;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.report.GenerarReporte;
import org.marcosbonifasi.system.Principal;

public class EmpresaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};    
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Empresa> listaEmpresa;
    // ----- Objetos para validaciones -----
    private Pattern expresionRegularTelefono;
    private Pattern expresionRegularTxt;
    private Matcher mat;
    
    // Injeccion de elementos de la vista
    @FXML private TextField txtCodigoEmpresa;
    @FXML private TextField txtNombreEmpresa;
    @FXML private TextField txtDireccionEmpresa;
    @FXML private TextField txtTelefonoEmpresa;
    @FXML private TableView tblEmpresas;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private TableColumn colNombreEmpresa;
    @FXML private TableColumn colDireccionEmpresa;
    @FXML private TableColumn colTelefonoEmpresa;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override// Sobre escritura del metodo abstracto.
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblEmpresas.setItems(getEmpresa());
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, Integer>("codigoEmpresa"));
        colNombreEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("nombreEmpresa"));
        colDireccionEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("direccion"));
        colTelefonoEmpresa.setCellValueFactory(new PropertyValueFactory<Empresa, String>("telefono"));
    }
        
    public ObservableList<Empresa> getEmpresa(){
        ArrayList<Empresa> lista = new ArrayList<Empresa>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpresas()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Empresa( resultado.getInt("codigoEmpresa"),
                                        resultado.getString("nombreEmpresa"),
                                        resultado.getString("direccion"),
                                        resultado.getString("telefono")
                                        ));  
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaEmpresa = FXCollections.observableArrayList(lista);
    }
    
    // --------------------- CRUD ------------------------
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEliminar.setDisable(false);
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblEmpresas.setDisable(true);
                
                deseleccionarElemento();
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(txtNombreEmpresa, txtDireccionEmpresa, txtTelefonoEmpresa)){
                    guardar();     
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblEmpresas.setDisable(false);
                    
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos(); 
                }
                break;
        }
    }
    
    public void guardar(){
        Empresa registro = new Empresa();
        registro.setNombreEmpresa(txtNombreEmpresa.getText());
        registro.setDireccion(txtDireccionEmpresa.getText());
        registro.setTelefono(txtTelefonoEmpresa.getText());

        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEmpresa(?,?,?)}");
            procedimiento.setString(1, registro.getNombreEmpresa());
            procedimiento.setString(2, registro.getDireccion());
            procedimiento.setString(3, registro.getTelefono());

            procedimiento.execute();
            listaEmpresa.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("NUEVO");
                btnNuevo.setDisable(false);
                btnEliminar.setText("ELIMINAR");
                btnEliminar.setDisable(false);
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblEmpresas.setDisable(false);
                
                deseleccionarElemento();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblEmpresas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro?", "Eliminar Empresa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(validarEliminarEmpresa(respuesta)){
                        try {
                            PreparedStatement procedimento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarEmpresa(?)}");
                            procedimento.setInt(1, ((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
                            procedimento.execute();
                            
                            listaEmpresa.remove(tblEmpresas.getSelectionModel().getSelectedIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    limpiarControles();
                    deseleccionarElemento();
                }else{
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEmpresas.getSelectionModel().getSelectedItem() != null){
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
                if(validarCampos(txtNombreEmpresa, txtDireccionEmpresa, txtTelefonoEmpresa)){
                    actualizar();
                    desactivarControles();
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarEmpresa(?,?,?,?)}");
            Empresa registro = (Empresa)tblEmpresas.getSelectionModel().getSelectedItem();
            
            registro.setNombreEmpresa(txtNombreEmpresa.getText());
            registro.setDireccion(txtDireccionEmpresa.getText());
            registro.setTelefono(txtTelefonoEmpresa.getText());
            
            procedimiento.setInt(1, registro.getCodigoEmpresa());
            procedimiento.setString(2, registro.getNombreEmpresa());
            procedimiento.setString(3, registro.getDireccion());
            procedimiento.setString(4, registro.getTelefono());
            procedimiento.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                deseleccionarElemento();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoEmpresa", null);
        GenerarReporte.mostrarReporte("ReporteEmpresas.jasper", "Reporte de Empresas", parametros);
    }
    
    // ---------------- Utilidades ------------------
    public void seleccionarElemento(){
        if(tblEmpresas.getSelectionModel().getSelectedItem() != null){
            txtCodigoEmpresa.setText(String.valueOf(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
            txtNombreEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getNombreEmpresa());
            txtDireccionEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getDireccion());
            txtTelefonoEmpresa.setText(((Empresa)tblEmpresas.getSelectionModel().getSelectedItem()).getTelefono());
        }
    }
    private void deseleccionarElemento(){
        int respuesta = tblEmpresas.getItems().indexOf(tblEmpresas.getSelectionModel().getSelectedItem());
        if(respuesta >= 0){
            tblEmpresas.getSelectionModel().select(null);
        }
    }
    
    public void desactivarControles(){
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(false);
        txtDireccionEmpresa.setEditable(false);
        txtTelefonoEmpresa.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoEmpresa.setEditable(false);
        txtNombreEmpresa.setEditable(true);
        txtDireccionEmpresa.setEditable(true);
        txtTelefonoEmpresa.setEditable(true);
    }
    
    public void limpiarControles(){
        txtCodigoEmpresa.setText("");
        txtNombreEmpresa.setText("");
        txtDireccionEmpresa.setText("");
        txtTelefonoEmpresa.setText("");
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaPresupuesto(){
        this.escenarioPrincipal.ventanaPresupuesto();
    }
    
    public void ventanaServicio(){
        this.escenarioPrincipal.ventanaServicio();
    }
    
    // ----------------- Getters and Setters -----------------
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    // ---------------- Validaciones ------------------
    
    private boolean validarCampos(TextField nombre, TextField direccion, TextField telefono){
        
        boolean bandera = false;
        
        if(!nombre.getText().equals("") && !direccion.getText().equals("") && !telefono.getText().equals("")){
            if(validarTelefono(telefono)){
                bandera = true;
            }else{
                JOptionPane.showMessageDialog(null, "POR FAVOR, INGRESE EL TELEFONO DE LA SIGUIENTE FORMA:"
                        + "\n> \t NNNN-NNNN", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "POR FAVOR, LLENO TODOS LOS CAMPOS", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return bandera;
    }
    
    private boolean validarTelefono(TextField telefono){
        expresionRegularTelefono = Pattern.compile("^\\d{4}-\\d{4}$");
        mat = expresionRegularTelefono.matcher(telefono.getText());
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarEliminarEmpresa(int respuesta){
        boolean bandera = false;
        int decision = 0;
        
        if(respuesta == JOptionPane.YES_OPTION){
            decision = JOptionPane.showConfirmDialog(null, "AL ELIMINAR ESTE REGISTRO, POSIBLEMENTE OTROS SE VARÁN AFECTADOS "
                    + "\n>¿Está seguro de querer elimnar este registro?", "ADVERTENCIA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(decision == JOptionPane.YES_OPTION){
                bandera = true;
            }
        }
        return bandera;
    }

}
