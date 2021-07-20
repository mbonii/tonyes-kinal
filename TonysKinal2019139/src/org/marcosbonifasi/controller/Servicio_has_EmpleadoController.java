
package org.marcosbonifasi.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import org.marcosbonifasi.bean.Empleado;
import org.marcosbonifasi.bean.Servicio;
import org.marcosbonifasi.bean.Servicio_has_Empleado;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class Servicio_has_EmpleadoController implements Initializable{
    private Principal menuPrincipal;
        private enum operaciones {NINGUNO, GUARDAR, ELIMINAR, EDITAR, CANCELAR, REPORTE, ACTUALIZAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Servicio_has_Empleado> listaServicioHasEmpleado;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Empleado> listaEmpleado;
    private DatePicker fechaEvento;
    //Objetos para expresiones regulares
    private Pattern expresionRegularFecha;
    private Pattern expresionRegularHora;
    private Matcher mat;
    
    @FXML private TextField txtCodigoServicioHasEmpleado;
    @FXML private TextField txtHoraEvento;
    @FXML private TextField txtLugarEvento;
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private ComboBox cmbCodigoEmpleado;
    @FXML private GridPane grpServiciosHasEmpleados;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private TableView tblServiciosHasEmpleados;
    @FXML private TableColumn colCodigoServicioHasEmpleado; 
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colCodigoEmpleado;
    @FXML private TableColumn colFechaEvento;
    @FXML private TableColumn colHoraEvento;
    @FXML private TableColumn colLugarEvento;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fechaEvento = new DatePicker(Locale.ENGLISH);
        fechaEvento.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaEvento.getCalendarView().todayButtonTextProperty().set("Today");
        fechaEvento.getCalendarView().setShowWeeks(false);
        fechaEvento.getStylesheets().add("/org/marcosbonifasi/resource/DatePicker.css");
        grpServiciosHasEmpleados.add(fechaEvento, 3, 0);
        cmbCodigoServicio.setItems(getServicio());
        cmbCodigoEmpleado.setItems(getEmpleado());
    }
    
    public void cargarDatos(){
        tblServiciosHasEmpleados.setItems(getServicioHasEmpleado());
        colCodigoServicioHasEmpleado.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, Integer>("codigoServicio_has_Empleado"));
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, Integer>("codigoServicio"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, Integer>("codigoEmpleado"));
        colFechaEvento.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, Date>("fechaEvento"));
        colHoraEvento.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, String>("horaEvento"));
        colLugarEvento.setCellValueFactory(new PropertyValueFactory<Servicio_has_Empleado, String>("lugarEvento"));
    }
    
    public ObservableList<Servicio_has_Empleado> getServicioHasEmpleado(){
        ArrayList<Servicio_has_Empleado> lista = new ArrayList<Servicio_has_Empleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios_has_Empleados()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Servicio_has_Empleado(resultado.getInt("codigoServicios_has_Empleados"),
                                                    resultado.getInt("codigoServicio"),
                                                    resultado.getInt("codigoEmpleado"),
                                                    resultado.getDate("fechaEvento"),
                                                    resultado.getString("horaEvento"),
                                                    resultado.getString("lugarEvento")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServicioHasEmpleado = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Servicio> getServicio(){
        ArrayList<Servicio> lista = new ArrayList<Servicio>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
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
    
    public ObservableList<Empleado> getEmpleado(){
        ArrayList<Empleado> lista = new ArrayList<Empleado>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEmpleados()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Empleado(resultado.getInt("codigoEmpleado"),
                                        resultado.getInt("numeroEmpleado"),
                                        resultado.getString("apellidosEmpleado"),
                                        resultado.getString("nombresEmpleado"),
                                        resultado.getString("direccionEmpleado"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getString("gradoCocinero"),
                                        resultado.getInt("codigoTipoEmpleado")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaEmpleado = FXCollections.observableArrayList(lista);
    }
    
    public Servicio buscarServicio(int codigoServicio){
        Servicio registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarServicio(?)}");
            procedimiento.setInt(1, codigoServicio);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                registro = new Servicio(resultado.getInt("codigoServicio"),
                                        resultado.getDate("fechaServicio"),
                                        resultado.getString("horaServicio"),                
                                        resultado.getString("tipoServicio"),
                                        resultado.getString("lugarServicio"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getInt("codigoEmpresa")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    public Empleado buscarEmpleado(int codigoEmpleado){
        Empleado registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEmpleado(?)}");
            procedimiento.setInt(1, codigoEmpleado);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                registro = new Empleado(resultado.getInt("codigoEmpleado"),
                                        resultado.getInt("numeroEmpleado"),
                                        resultado.getString("apellidosEmpleado"),
                                        resultado.getString("nombresEmpleado"),
                                        resultado.getString("direccionEmpleado"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getString("gradoCocinero"),
                                        resultado.getInt("codigoTipoEmpleado")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    // ------------------- CRUD --------------------
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                limpiarControles();
                deseleccionarElemento();
                activarControles();
                btnNuevo.setText("GUARDAR");
                btnEliminar.setText("CANCELAR");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblServiciosHasEmpleados.setDisable(true);
                
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(validarCampos(fechaEvento, txtHoraEvento, txtLugarEvento)){
                    guardar();
                    limpiarControles();
                    desactivarControles();
                    btnNuevo.setText("NUEVO");
                    btnEliminar.setText("ELIMINAR");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tblServiciosHasEmpleados.setDisable(false);

                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }else {
                    JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE TODOS LOS CAMPOS");
                }
                break;
        }
    }
    
    public void guardar(){
        Servicio_has_Empleado registro = new Servicio_has_Empleado();
        registro.setCodigoServicio(((Servicio)cmbCodigoServicio.getSelectionModel().getSelectedItem()).getCodigoServicio());
        registro.setCodigoEmpleado(((Empleado)cmbCodigoEmpleado.getSelectionModel().getSelectedItem()).getCodigoEmpleado());
        registro.setFechaEvento(fechaEvento.getSelectedDate());
        registro.setHoraEvento(txtHoraEvento.getText());
        registro.setLugarEvento(txtLugarEvento.getText());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarServicio_has_Empleado(?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoServicio());
            procedimiento.setInt(2, registro.getCodigoEmpleado());
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaEvento().getTime()));
            procedimiento.setString(4, registro.getHoraEvento());
            procedimiento.setString(5, registro.getLugarEvento());
            
            procedimiento.execute();
            listaServicioHasEmpleado.add(registro);
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
                tblServiciosHasEmpleados.setDisable(false);
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar este registro?", "ADVERTENCIA", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarServicio_has_Empleado(?)}");
                            procedimiento.setInt(1, ((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio_has_Empleado());
                            procedimiento.execute();

                            listaServicioHasEmpleado.remove(tblServiciosHasEmpleados.getSelectionModel().getSelectedIndex());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    limpiarControles();
                    deseleccionarElemento();
                }else {
                    JOptionPane.showMessageDialog(null , "Debe seleccionar un elemento");
                }
                break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
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
                if(validarCampos(fechaEvento, txtHoraEvento, txtLugarEvento)){
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
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ActualizarServicio_has_Empleado(?,?,?,?)}");
            Servicio_has_Empleado registro = ((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem());
            
            registro.setFechaEvento(fechaEvento.getSelectedDate());
            registro.setHoraEvento(txtHoraEvento.getText());
            registro.setLugarEvento(txtLugarEvento.getText());
            
            procedimiento.setInt(1, registro.getCodigoServicio_has_Empleado());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaEvento().getTime()));
            procedimiento.setString(3, txtHoraEvento.getText());
            procedimiento.setString(4, registro.getLugarEvento());
            
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
                
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    // ------------------- Utilidades --------------------
    
    public void limpiarControles(){
        txtCodigoServicioHasEmpleado.setText("");
        cmbCodigoServicio.getSelectionModel().select(null);
        cmbCodigoEmpleado.getSelectionModel().select(null);
        fechaEvento.selectedDateProperty().set(null);
        txtHoraEvento.setText("");
        txtLugarEvento.setText("");
    }
    
    public void desactivarControles(){
        txtCodigoServicioHasEmpleado.setEditable(false);
        cmbCodigoServicio.setDisable(false);
        cmbCodigoEmpleado.setDisable(false);
        grpServiciosHasEmpleados.setDisable(false);
        txtHoraEvento.setEditable(false);
        txtLugarEvento.setEditable(false);
    }
    
    public void activarControles(){
        txtCodigoServicioHasEmpleado.setEditable(false);
        cmbCodigoServicio.setEditable(false);
        cmbCodigoEmpleado.setEditable(false);
        grpServiciosHasEmpleados.setDisable(false);
        txtHoraEvento.setEditable(true);
        txtLugarEvento.setEditable(true);
    }
    
    public void seleccionarElemento(){
        if(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem() != null){
            txtCodigoServicioHasEmpleado.setText(String.valueOf(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio_has_Empleado()));
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            cmbCodigoEmpleado.getSelectionModel().select(buscarEmpleado(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getCodigoEmpleado()));
            fechaEvento.selectedDateProperty().set(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getFechaEvento());
            txtHoraEvento.setText(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getHoraEvento());
            txtLugarEvento.setText(((Servicio_has_Empleado)tblServiciosHasEmpleados.getSelectionModel().getSelectedItem()).getLugarEvento());
        }
    }
    
    public void deseleccionarElemento(){
        int index = tblServiciosHasEmpleados.getItems().indexOf(tblServiciosHasEmpleados.getSelectionModel().getSelectedItem());
        if(index >= 0){
            tblServiciosHasEmpleados.getSelectionModel().select(null);
        }
    }
    
    // ------------------- Setters and Getters ------------------
    
    public void setMenuPrincipal(Principal menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
    }
    
    public void menuPrincipal(){
        this.menuPrincipal.menuPrincipal();
    }
    // ------------------- Validaciones ---------------------
    
    private boolean validarCampos(DatePicker fecha, TextField hora, TextField lugarEvento){
        boolean bandera = false;
        
        if(fecha.getSelectedDate() != null && !hora.getText().equals("") && !lugarEvento.getText().equals("")){
            if(validarFecha(fecha) && validarHora(hora)){
                bandera = true;
            }else {
                JOptionPane.showMessageDialog(null, "POR FAVOR, LLENE LOS SIGUIENTES CAMPOS DE LA SIGUIENTE FORMA:"
                        + "\n > Fecha Evento: yyyy-MM-dd"
                        + "\n > Hora Evento:  hh:mm:ss");
            }
        }
        
        return bandera;
    }
    
    private boolean validarFecha(DatePicker fechaE){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = String.valueOf(dateFormat.format(fechaE.getSelectedDate().getTime()));
        expresionRegularFecha = Pattern.compile("^\\d{4}-[0-9]{2}-[0-9]{2}$");
        mat = expresionRegularFecha.matcher(fecha);
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
    private boolean validarHora(TextField hora){
        expresionRegularHora = Pattern.compile("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$");
        mat = expresionRegularHora.matcher(hora.getText());
        boolean bandera = false;
        
        if(mat.matches()){
            bandera = true;
        }
        
        return bandera;
    }
    
}
