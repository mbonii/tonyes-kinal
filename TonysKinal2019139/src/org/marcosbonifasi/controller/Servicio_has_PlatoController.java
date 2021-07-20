
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.marcosbonifasi.bean.Plato;

import org.marcosbonifasi.bean.Servicio;
import org.marcosbonifasi.bean.Servicio_has_Plato;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class Servicio_has_PlatoController implements Initializable{
    private Principal escenarioPrincipal;
    private ObservableList<Servicio_has_Plato> listaServicioHasPlato;
    private ObservableList<Servicio> listaServicio;
    private ObservableList<Plato> listaPlato;
    
    @FXML private ComboBox cmbCodigoServicio;
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private TableView tblServiciosHasPlatos;
    @FXML private TableColumn colCodigoServicio;
    @FXML private TableColumn colCodigoPlato;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoServicio.setItems(getServicio());
        cmbCodigoPlato.setItems(getPlato());
    }
    
    public void cargarDatos(){
        tblServiciosHasPlatos.setItems(getServicio_has_Plato());
        colCodigoServicio.setCellValueFactory(new PropertyValueFactory<Servicio_has_Plato, Integer>("codigoServicio"));
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Servicio_has_Plato, Integer>("codigoPlato"));   
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
    
    public ObservableList<Servicio_has_Plato> getServicio_has_Plato(){
        ArrayList<Servicio_has_Plato> lista = new ArrayList<Servicio_has_Plato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarServicios_has_Platos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                lista.add(new Servicio_has_Plato(resultado.getInt("codigoServicio"), 
                                                    resultado.getInt("codigoPlato")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaServicioHasPlato = FXCollections.observableArrayList(lista);
    }
    
    public Servicio buscarServicio(int codigoServicio){
        Servicio registro =  null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarServicio(?)}");
            procedimiento.setInt(1, codigoServicio);
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                registro = new Servicio(resultado.getInt("codigoServicio"),
                                        resultado.getDate("fechaServicio"),
                                        resultado.getString("tipoServicio"),
                                        resultado.getString("horaServicio"),
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
    
    public Plato buscarPlato(int codigoPlato){
        Plato registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarPlato(?)}");
            procedimiento.setInt(1, codigoPlato);
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                registro = new Plato(resultado.getInt("codigoPlato"),
                                        resultado.getInt("cantidad"),
                                        resultado.getString("nombrePlato"),
                                        resultado.getString("descripcionPlato"),
                                        resultado.getDouble("precioPlato"),
                                        resultado.getInt("codigoTipoPlato")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registro;
    }
    
    // ----------------- Utilidades -------------------
    public void seleccionarElemento(){
        if(tblServiciosHasPlatos.getSelectionModel().getSelectedItem() != null){
            cmbCodigoServicio.getSelectionModel().select(buscarServicio(((Servicio_has_Plato)tblServiciosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoServicio()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((Servicio_has_Plato)tblServiciosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
        }
    }
    
    //---------------- Setters and Getters ----------------
    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
}
