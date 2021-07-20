
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
import org.marcosbonifasi.bean.Producto;
import org.marcosbonifasi.bean.Producto_has_Plato;
import org.marcosbonifasi.db.Conexion;
import org.marcosbonifasi.system.Principal;

public class Producto_has_PlatoController implements Initializable{
    private Principal escenarioPrincipal;
    private ObservableList<Producto_has_Plato> listaProducto_has_Plato;
    private ObservableList<Producto> listaProducto;
    private ObservableList<Plato> listaPlato;
    
    @FXML private ComboBox cmbCodigoProducto;
    @FXML private ComboBox cmbCodigoPlato;
    @FXML private TableView tblProductosHasPlatos;
    @FXML private TableColumn colCodigoProducto;
    @FXML private TableColumn colCodigoPlato;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoProducto.setItems(getProducto());
        cmbCodigoPlato.setItems(getPlato());
    }
    
    public void cargarDatos(){
        tblProductosHasPlatos.setItems(getProducto_has_Plato());
        colCodigoProducto.setCellValueFactory(new PropertyValueFactory<Producto_has_Plato, Integer>("codigoProducto"));
        colCodigoPlato.setCellValueFactory(new PropertyValueFactory<Producto_has_Plato, Integer>("codigoPlato"));
    }
    
    public ObservableList<Producto_has_Plato> getProducto_has_Plato(){
        ArrayList<Producto_has_Plato> lista = new ArrayList<Producto_has_Plato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProductos_has_Platos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                lista.add(new Producto_has_Plato(resultado.getInt("codigoProducto"), 
                                                    resultado.getInt("codigoPlato")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaProducto_has_Plato = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Producto> getProducto(){
        ArrayList<Producto> lista = new ArrayList<Producto>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarProductos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                lista.add(new Producto(resultado.getInt("codigoProducto"),
                                        resultado.getString("nombreProducto"),
                                        resultado.getInt("cantidad")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaProducto = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Plato> getPlato(){
        ArrayList<Plato> lista = new ArrayList<Plato>();
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPlatos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
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
    
    public Producto buscarProducto(int codigoProducto){
        Producto registro = null;
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarProducto(?)}");
            procedimiento.setInt(1, codigoProducto);
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {                
                registro = new Producto(resultado.getInt("codigoProducto"),
                                        resultado.getString("nombreProducto"),
                                        resultado.getInt("cantidad")
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
    
    // ---------------------- Utilidades-------------------
    public void seleccionarElemento(){
        if(tblProductosHasPlatos.getSelectionModel().getSelectedItem() != null){
            cmbCodigoProducto.getSelectionModel().select(buscarProducto(((Producto_has_Plato)tblProductosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoProducto()));
            cmbCodigoPlato.getSelectionModel().select(buscarPlato(((Producto_has_Plato)tblProductosHasPlatos.getSelectionModel().getSelectedItem()).getCodigoPlato()));
        }
    }
    
    // ----------------------- Setters and Getters ---------------------

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        this.escenarioPrincipal.menuPrincipal();
    }
    
    
}
