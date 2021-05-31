package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mysql.cj.xdevapi.Client;
import helpers.db_connect;
import helpers.intentData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Cliente;
import models.OrdemPedido;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class edtClienteAlert implements Initializable {

    @FXML
    private JFXTextField edtNome;

    @FXML
    private JFXTextField edtTelefone;

    @FXML
    private JFXTextField edtEndereco;

    @FXML
    private TableView<OrdemPedido> tableViewClientePedidos;

    @FXML
    private TableColumn<OrdemPedido, Integer> idPedidoCol;

    @FXML
    private TableColumn<OrdemPedido, String> atentendentePedidoCol;

    @FXML
    private TableColumn<OrdemPedido, String> entregadorPedidoCol;

    @FXML
    private TableColumn<OrdemPedido, String> dataPCol;

    @FXML
    private TableColumn<OrdemPedido, String> dataTCol;

    @FXML
    private TableColumn<OrdemPedido, String> dataFCol;

    @FXML
    private JFXButton btnSalvar;

    @FXML
    private JFXButton btnCancelar;

    Cliente cliente = null;
    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String cliente_id;
    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }


    //Metodos iniciais



    private void recuperarDadosCliente() throws SQLException {
        query = "SELECT * from `Clientes` where `id` = ?";
        connection = db_connect.getConnect();
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(cliente_id));
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            cliente = new Cliente(
               resultSet.getInt("id"),
                    resultSet.getString("cliente_nome"),
                    resultSet.getString("cliente_endereco") ,
                    resultSet.getString("cliente_telefone"),
                    resultSet.getString("data_cadastro"),
                    resultSet.getInt("qtdPedidos")
            );
            System.out.println(resultSet.getString("cliente_nome"));
        }
        }
    }
    //Metodos iniciais




    //Metodos de Negocios

    //Metodos de Negocios



    //Metodos de Controle

    //Metodos de Controle

