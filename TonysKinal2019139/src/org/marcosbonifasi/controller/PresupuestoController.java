
package org.marcosbonifasi.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.marcosbonifasi.bean.Empresa;
import org.marcosbonifasi.bean.Presupuesto;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.report.GenerarReporte;
import org.marcosbonifasi.system.Principal;

public class PresupuestoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Empresa> listaEmpresa;
    private ObservableList<Presupuesto> listaPresupuesto;
    private DatePicker fechaSoli;
    //Objetos para validaciones
    private Pattern expresionRegularFecha;
    private Pattern expresionRegularCantidad;
    private Matcher mat;
    
    @FXML private TextField txtCodigoPresupuesto;
    @FXML private TextField txtCantidadPresupuesto;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private TableView tblPresupuesto;
    @FXML private TableColumn colCodigoPresupuesto;
    @FXML private TableColumn colFechaSolicitud;
    @FXML private TableColumn colCantidadPresupuesto;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private GridPane grpFechaSolicitud;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fechaSoli = new DatePicker(Locale.ENGLISH);
        fechaSoli.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaSoli.getCalendarView().todayButtonTextProperty().set("Today");
        fechaSoli.getCalendarView().setShowWeeks(false);
        fechaSoli.getStylesheets().add("/org/marcosbonifasi/resource/DatePicker.css");
        grpFechaSolicitud.add(fechaSoli, 0, 0);
        cmbCodigoEmpresa.setItems(getEmpresa());
    }
    
    public void cargarDatos(){
        tblPresupuesto.setItems(getPresupuesto());
        colCodigoPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoPresupuesto"));
        colFechaSolicitud.setCellValueFactory(new PropertyValueFactory<Presupuesto, Date>("fechaSolicitud"));
        colCantidadPresupuesto.setCellValueFactory(new PropertyValueFactory<Presupuesto, Double>("cantidadPresupuesto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Presupuesto, Integer>("codigoEmpresa"));
    }
    
    public ObservableList<Presupuesto> getPresupuesto(){
        ArrayList<Presupuesto> lista = new ArrayList<Presupuesto>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPresupuesto()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Presupuesto(resultado.getInt("codigoPresupuesto"),
                                            resultado.getDate("fechaSolicitud"),
                                            resultado.getDouble("cantidadPresupuesto"),
                                            resultado.getInt("codigoEmpresa")
                ));
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPresupuesto = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Empresa> getEmpresa(){
        ArrayList<Empresa> lista = new ArrayList<Empresa>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpresas()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Empresa(resultado.getInt("codigoEmpresa"),
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
    
    public Empresa buscarEmpresa(int codigoEmpresa){
        Empresa resultado = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpresa(?)}");
            procedimiento.setInt(1, codigoEmpresa);
            
            ResultSet registro = procedimiento.executeQuery();
            while (registro.next()) {                
                resultado = new Empresa(registro.getInt("codigoEmpresa"),
                                        registro.getString("nombreEmpresa"),
                                        registro.getString("direccion"),
                                        registro.getString("telefono")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }
        
    // -------------------- CRUD ---------------------
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                deseleccionarElemento();
                limpiarControles();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblPresupuesto.setDisable(true);
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCamposPresupuesto(fechaSoli, txtCantidadPresupuesto, cmbCodigoEmpresa)){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblPresupuesto.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void guardar(){
        Presupuesto registro = new Presupuesto();
        registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
        registro.setFechaSolicitud(fechaSoli.getSelectedDate());
        registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPresupuesto(?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaSolicitud().getTime()));
            procedimiento.setDouble(2, registro.getCantidadPresupuesto());
            procedimiento.setInt(3, registro.getCodigoEmpresa());
            
            procedimiento.execute();
            listaPresupuesto.add(registro);
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
                btnEliminar.setText("ELIMINAR");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblPresupuesto.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro que querer eliminar este registro?", "ELIMINAR PRESUPUESTO", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarPresupuesto(?)}");
                            procedimiento.setInt(1, ((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoPresupuesto());
                            procedimiento.execute();
                            
                            listaPresupuesto.remove(tblPresupuesto.getSelectionModel().getSelectedIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    limpiarControles();
                    deseleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un registro");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    //cmbCodigoEmpresa.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                if(validarCamposPresupuesto(fechaSoli, txtCantidadPresupuesto, cmbCodigoEmpresa)){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    deseleccionarElemento();
                    btnEditar.setText("EDITAR");
                    btnReporte.setText("REPORTE");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    //cmbCodigoEmpresa.setDisable(false);
                    
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void actualizar(){
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarPresupuesto(?,?,?)}");
            Presupuesto registro = (Presupuesto) tblPresupuesto.getSelectionModel().getSelectedItem();
            
            registro.setFechaSolicitud(fechaSoli.getSelectedDate());
            registro.setCantidadPresupuesto(Double.parseDouble(txtCantidadPresupuesto.getText()));
            
            procedimiento.setInt(1, registro.getCodigoPresupuesto());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaSolicitud().getTime()));
            procedimiento.setDouble(3, registro.getCantidadPresupuesto());
            procedimiento.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                deseleccionarElemento();
                btnEditar.setText("EDITAR");
                btnReporte.setText("REPORTE");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cmbCodigoEmpresa.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            case NINGUNO:
                if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
                    imprimirReporte();
                }else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        int codEmpresa = Integer.valueOf(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        parametros.put("codEmpresa", codEmpresa);
        GenerarReporte.mostrarReporte("ReportePresupuesto.jasper", "Reporte de Presupuesto", parametros);
    }
    
    // -------------------- Utilidades --------------------
    public void seleccionarElemento(){
        if(tblPresupuesto.getSelectionModel().getSelectedItem() != null){
            txtCodigoPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoPresupuesto()));
            fechaSoli.selectedDateProperty().set(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getFechaSolicitud());
            txtCantidadPresupuesto.setText(String.valueOf(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCantidadPresupuesto()));
            cmbCodigoEmpresa.getSelectionModel().select((buscarEmpresa(((Presupuesto)tblPresupuesto.getSelectionModel().getSelectedItem()).getCodigoEmpresa())));
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblPresupuesto.getItems().indexOf(tblPresupuesto.getSelectionModel().getSelectedItem());
        if(index >= 0)
            tblPresupuesto.getSelectionModel().select(null); 
        
    }
    
    public void desactivarControles(){
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(false);
        grpFechaSolicitud.setDisable(false);
        cmbCodigoEmpresa.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoPresupuesto.setEditable(false);
        txtCantidadPresupuesto.setEditable(true);
        grpFechaSolicitud.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoPresupuesto.setText("");
        txtCantidadPresupuesto.setText("");
        fechaSoli.selectedDateProperty().set(null);
        cmbCodigoEmpresa.getSelectionModel().select(null);
    }
    

    // ----------------- Getters and Setters ---------------------
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaEmpresa(){
        this.escenarioPrincipal.ventanaEmpresa();
    }
    
    
    // --------------- Validaciones -----------------
    private boolean validarCamposPresupuesto(DatePicker fechaSolicitud, TextField cantidadPresupuesto, ComboBox codigoEmpresa){
        boolean bandera = false;
        
        if(fechaSolicitud.getSelectedDate() != null && !cantidadPresupuesto.getText().equals("") && codigoEmpresa.getSelectionModel().getSelectedItem() != null){
            if(validarFecha(fechaSolicitud) && validarCantidadPresupuesto(cantidadPresupuesto)){
                    bandera = true;
            }else{
                JOptionPane.showMessageDialog(null,"POR FAVOR, LLENE EL CAMPO DE FECHA DE ESTA FORMA:"
                    + "\n > Fecha Servicio: yyyy-MM-dd"
                    + "\nTAMBIÉN LLENE LA CANTIDAD DE PRESUPUESTO, ADECUADAMENTE.", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
            }
        }else{ 
            JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE TODOS LOS CAMPOS", "ADVERTENCIA" ,JOptionPane.INFORMATION_MESSAGE);
        }
        return bandera;
    }
    
    private boolean validarCantidadPresupuesto(TextField cantidadPresupuesto){
        expresionRegularCantidad = Pattern.compile("^\\d{1,7}|\\d{1,7}\\.\\d{1,2}$");
        mat = expresionRegularCantidad.matcher(cantidadPresupuesto.getText());
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarFecha(DatePicker fechaSolicitud){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = String.valueOf(dateFormat.format(fechaSolicitud.getSelectedDate().getTime()));
        expresionRegularFecha = Pattern.compile("^\\d{4}-[0-9]{2}-[0-9]{2}$");
        mat = expresionRegularFecha.matcher(fecha);
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
}
