
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
import org.marcosbonifasi.bean.Servicio;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.report.GenerarReporte;
import org.marcosbonifasi.system.Principal;

public class ServicioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empresa> listaEmpresa;
    private DatePicker fechaSoli;
    //Objetos para validaciones
    private Pattern expresionRegularFecha;
    private Pattern expresionRegularHora;
    private Pattern expresionRegularTelefono;
    private Matcher mat;
    
    @FXML private TextField txtCodigoServicio;
    @FXML private TextField txtTipoServicio;
    @FXML private TextField txtLugarServicio;
    @FXML private TextField txtTelefonoContacto;
    @FXML private TextField txtHoraServicio;
    @FXML private GridPane grpServicio;
    @FXML private ComboBox cmbCodigoEmpresa;
    @FXML private TableView tblServicios;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colFechaServicio;
    @FXML private TableColumn colTipoServicio;
    @FXML private TableColumn colHoraServicio;
    @FXML private TableColumn colLugarServicio;
    @FXML private TableColumn colTelefonoContacto;
    @FXML private TableColumn colCodigoEmpresa;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fechaSoli = new DatePicker(Locale.ENGLISH);
        fechaSoli.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fechaSoli.getCalendarView().todayButtonTextProperty().set("Today");
        fechaSoli.getCalendarView().setShowWeeks(false);
        fechaSoli.getStylesheets().add("/org/marcosbonifasi/resource/DatePicker.css");
        grpServicio.getStylesheets().add("/org/marcosbonifasi/resource/DatePicker.css");
        grpServicio.add(fechaSoli, 1, 1);
        cmbCodigoEmpresa.setItems(getEmpresa());
    }
    
    public void cargarDatos(){
        tblServicios.setItems(getServicio());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoServicio"));
        colFechaServicio.setCellValueFactory(new PropertyValueFactory<Servicio, Date>("fechaServicio"));
        colTipoServicio.setCellValueFactory(new PropertyValueFactory<Servicio, String>("tipoServicio"));
        colHoraServicio.setCellValueFactory(new PropertyValueFactory<Servicio, String>("horaServicio"));
        colLugarServicio.setCellValueFactory(new PropertyValueFactory<Servicio, String>("lugarServicio"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Servicio, String>("telefonoContacto"));
        colCodigoEmpresa.setCellValueFactory(new PropertyValueFactory<Servicio, Integer>("codigoEmpresa"));
    }
    
    public ObservableList<Servicio> getServicio(){
        ArrayList<Servicio> lista = new ArrayList<Servicio>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Servicio(resultado.getInt("codigoServicio"),
                                        resultado.getDate("fechaServicio"),
                                        resultado.getString("tipoServicio"),
                                        resultado.getString("horaServicio"),
                                        resultado.getString("lugarServicio"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getInt("codigoEmpresa")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServicio = FXCollections.observableArrayList(lista);
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
        Empresa registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpresa(?)}");
            procedimiento.setInt(1, codigoEmpresa);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                registro = new Empresa(resultado.getInt("codigoEmpresa"), 
                                        resultado.getString("nombreEmpresa"), 
                                        resultado.getString("direccion"), 
                                        resultado.getString("telefono"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    // ------------------ CRUD ------------------
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                deseleccionarElemento();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblServicios.setDisable(true);
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(fechaSoli, txtTipoServicio, txtHoraServicio, txtLugarServicio, txtTelefonoContacto, cmbCodigoEmpresa)){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblServicios.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void guardar(){
        Servicio registro = new Servicio();
        registro.setFechaServicio(fechaSoli.getSelectedDate());
        registro.setTipoServicio(txtTipoServicio.getText());
        registro.setHoraServicio(txtHoraServicio.getText());
        registro.setLugarServicio(txtLugarServicio.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarServicio(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaServicio().getTime()));
            procedimiento.setString(2, registro.getTipoServicio());
            procedimiento.setString(3,  registro.getHoraServicio());
            procedimiento.setString(4, registro.getLugarServicio());
            procedimiento.setString(5, registro.getTelefonoContacto());
            procedimiento.setInt(6, registro.getCodigoEmpresa());
            procedimiento.execute();
            
            listaServicio.add(registro);
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
                    tblServicios.setDisable(false);
                    
                    tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar este registro?", "ADVERTENCIA", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(validarEliminarServicio(respuesta)){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarServicio(?)}");
                            procedimiento.setInt(1, ((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio());
                            procedimiento.execute();
                            
                            listaServicio.remove(tblServicios.getSelectionModel().getSelectedIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    limpiarControles();
                    deseleccionarElemento();
                }else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnEditar.setText("ACTUALIZAR");
                    btnReporte.setText("CANCELAR");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoEmpresa.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                break;
            case ACTUALIZAR:
                    if(validarCampos(fechaSoli, txtTipoServicio, txtHoraServicio, txtLugarServicio, txtTelefonoContacto, cmbCodigoEmpresa)){
                        actualizar();
                        desactivarControles();
                        limpiarControles();
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarServicio(?,?,?,?,?,?)}");
            Servicio registro = (Servicio) tblServicios.getSelectionModel().getSelectedItem();
            
            registro.setFechaServicio(fechaSoli.getSelectedDate());
            registro.setTipoServicio(txtTipoServicio.getText());
            registro.setHoraServicio(txtHoraServicio.getText());
            registro.setLugarServicio(txtLugarServicio.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            registro.setCodigoEmpresa(((Empresa)cmbCodigoEmpresa.getSelectionModel().getSelectedItem()).getCodigoEmpresa());
            
            procedimiento.setInt(1, registro.getCodigoServicio());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaServicio().getTime()));
            procedimiento.setString(3, registro.getTipoServicio());
            procedimiento.setString((4), registro.getHoraServicio());
            procedimiento.setString(5, registro.getLugarServicio());
            procedimiento.setString(6, registro.getTelefonoContacto());
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
                if(tblServicios.getSelectionModel().getSelectedItem() != null){
                    imprimirReporte();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
                
                break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        int codServicio = Integer.valueOf(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio());
        parametros.put("codServicio", codServicio);
        GenerarReporte.mostrarReporte("ReporteServicios.jasper", "Reporte de Servicios", parametros);
    }
    
    // ----------------- Utilidades -------------------
    public void activarControles(){
        txtCodigoServicio.setEditable(false);
        txtTipoServicio.setEditable(true);
        txtLugarServicio.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        txtHoraServicio.setEditable(true);
        grpServicio.setDisable(false);
        cmbCodigoEmpresa.setDisable(false);
    }
    
    public void desactivarControles(){
        txtCodigoServicio.setEditable(false);
        txtTipoServicio.setEditable(false);
        txtLugarServicio.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        txtHoraServicio.setEditable(false);
        grpServicio.setDisable(false);
        cmbCodigoEmpresa.setEditable(false);
    }
    
    public void limpiarControles(){
        txtCodigoServicio.setText("");
        txtTipoServicio.setText("");
        txtLugarServicio.setText("");
        txtTelefonoContacto.setText("");
        txtHoraServicio.setText("");
        fechaSoli.selectedDateProperty().set(null);
        cmbCodigoEmpresa.getSelectionModel().select(null);
    }
    
    public void seleccionarElemento(){
        if(tblServicios.getSelectionModel().getSelectedItem() != null){
            txtCodigoServicio.setText(String.valueOf(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            txtTipoServicio.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getTipoServicio());
            txtLugarServicio.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getLugarServicio());
            txtTelefonoContacto.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            txtHoraServicio.setText(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getHoraServicio());
            fechaSoli.selectedDateProperty().set(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getFechaServicio());
            cmbCodigoEmpresa.getSelectionModel().select(buscarEmpresa(((Servicio)tblServicios.getSelectionModel().getSelectedItem()).getCodigoEmpresa()));
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblServicios.getItems().indexOf(tblServicios.getSelectionModel().getSelectedItem());
        if(index >= 0)
            tblServicios.getSelectionModel().select(null);
        
    }
    
    // ---------------- Setters and Getters ----------------
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaEmpresa(){
        this.escenarioPrincipal.ventanaEmpresa();
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    // -------------- Validaciones -----------------
    
    private boolean validarCampos(DatePicker fechaSolicitud, TextField tipoServicio, TextField horaServicio, TextField lugarServicio, TextField telefonoContacto, ComboBox codigoEmpresa){
        boolean bandera = false;
        
        if(fechaSolicitud.getSelectedDate() != null && !tipoServicio.getText().equals("") && !horaServicio.getText().equals("") && !telefonoContacto.getText().equals("") && cmbCodigoEmpresa.getSelectionModel().getSelectedItem() != null){
            if(validarFecha(fechaSolicitud) && validarTelefono(telefonoContacto) && validarHora(horaServicio)){
                bandera = true;
            }else{
                JOptionPane.showMessageDialog(null,"POR FAVOR, LLENE LOS SIGUIENTES CAMPOS DE ESTA FORMA:"
                        + "\n > Fecha Servicio: yyyy-MM-dd"
                        + "\n > Hora Servicio:  hh:mm:ss"
                        + "\n > Telefono Contacto: NNNN-NNNN", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE TODOS LOS CAMPOS", "ADVERTENCIA", JOptionPane.INFORMATION_MESSAGE);
        }
        return bandera;
    }
    
    private boolean validarFecha(DatePicker fechaServicio){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = String.valueOf(dateFormat.format(fechaServicio.getSelectedDate().getTime()));
        expresionRegularFecha = Pattern.compile("^\\d{4}-[0-9]{2}-[0-9]{2}$");
        mat = expresionRegularFecha.matcher(fecha);
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarHora(TextField horaServicio){
        expresionRegularHora = Pattern.compile("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$");
        mat = expresionRegularHora.matcher(horaServicio.getText());
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarTelefono(TextField telefonoContacto){
        expresionRegularTelefono = Pattern.compile("^\\d{4}-\\d{4}$");
        mat = expresionRegularTelefono.matcher(telefonoContacto.getText());
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarEliminarServicio(int respuesta){
        boolean bandera = false;
        if(respuesta == JOptionPane.YES_OPTION){
            int decision = JOptionPane.showConfirmDialog(null, "AL ELIMINAR ESTE REGISTRO, PROSIBLEMENTE OTROS SE VERAN AFECTADOS"
                    + "\n>¿Está seguro de querer eliminar este registro?", "ADVERTENCIA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(decision == JOptionPane.YES_OPTION){
                bandera = true;
            }
        }
        return bandera;
    }
}
