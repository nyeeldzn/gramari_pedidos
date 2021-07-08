package helpers.Database.Clientes;

import helpers.Database.db_connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;

public class client_crud {

    public static ObservableList<Cliente> recuperarClientes() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        Connection conn = db_connect.getConnect();
        String query = "SELECT * FROM `Clientes`";

        try {
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                clientes.add(new Cliente(
                                r.getInt("id"),
                                r.getString("cliente_nome"),
                                r.getString("cliente_endereco"),
                                r.getString("bairro"),
                                r.getString("cliente_telefone"),
                                r.getString("data_cadastro"),
                                r.getInt("qtdPedidos")
                        )
                );
                System.out.println("Usuario recuperado: " + r.getString("cliente_nome"));
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return clientes;
    }

}
