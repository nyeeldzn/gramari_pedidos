package helpers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Cliente;
import models.OrdemPedido;
import models.Usuario;
import sample.clientesController;
import sample.detalhesPedidoController;
import sample.novoPedidoController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class db_crud {

    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static String query;
    private static ResultSet resultSet = null;

    public static boolean metodoExlusao(String query, int index){
        boolean state = false;
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, index);
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException e){
            Logger.getLogger(clientesController.class.getName()).log(Level.SEVERE, null, e);
        }
        return state;
    }

    public static boolean metodoClienteAddPedido(int cliente_id, int cliente_qtd){
        boolean state = false;

        try {
            Connection connection = db_connect.getConnect();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `Clientes` SET `qtdPedidos`= ? WHERE `id` =?");
            preparedStatement.setInt(1, cliente_qtd + 1);
            preparedStatement.setInt(2, cliente_id);
            int count  = preparedStatement.executeUpdate();
            if (count > 0) {
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return state;
    }
    public static Usuario metodoRecuperarUsuario (String id, String nome){
       boolean state = false;
        Usuario usuario = null;
        if(id.equals("")){
            query = "SELECT * FROM `Usuarios` WHERE `nome` =?";
            connection = db_connect.getConnect();
            try {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nome);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    usuario = new Usuario(
                            resultSet.getInt("id"),
                            resultSet.getString("nome"),
                            resultSet.getString("senha"),
                            resultSet.getInt("privilegio"));
                }

            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }else if(nome.equals("")){
            query = "SELECT * FROM `Usuarios` WHERE `id` =?";
            connection = db_connect.getConnect();
            try {
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, Integer.parseInt(id));
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    usuario = new Usuario(
                            resultSet.getInt("id"),
                            resultSet.getString("nome"),
                            resultSet.getString("senha"),
                            resultSet.getInt("privilegio"));
                }

            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return usuario;
    }

    public static boolean metodoEditarProduto(int produto_id, String produto_nome){
        boolean state = false;

        query = "UPDATE `Produto` SET `nome_produto`= ? WHERE `id` = ? ";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, produto_nome);
            preparedStatement.setInt(2, produto_id);
            int count = preparedStatement.executeUpdate();
            if (count > 0) {
                System.out.println("Update OK");
                state = true;
            }else{
                System.out.println("Problema no Update");
                state = false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return state;
    }
    public static boolean metodoInsertPedido(OrdemPedido pedido, String query) {
        boolean insert = false;
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(   1,  pedido.getId());
            preparedStatement.setInt(   2,  pedido.getCliente_id());
            preparedStatement.setString(3,  pedido.getCliente_nome());
            preparedStatement.setString(4,  pedido.getEnd_cliente());
            preparedStatement.setString(5,  pedido.getNum_cliente());
            preparedStatement.setString(6,  pedido.getForma_envio());
            preparedStatement.setString(7,  pedido.getForma_pagamento());
            preparedStatement.setString(8,  pedido.getForma_subst());
            preparedStatement.setString(9,  pedido.getData_entrada());
            preparedStatement.setString(10, pedido.getHorario_entrada());
            preparedStatement.setString(11, pedido.getHorario_triagem());
            preparedStatement.setString(12, pedido.getHorario_checkout());
            preparedStatement.setString(13, pedido.getHorario_finalizado());
            preparedStatement.setInt(   14, pedido.getOperador_id());
            preparedStatement.setDouble(15, pedido.getEntregador_id());
            preparedStatement.setString(16, pedido.getFonte_pedido());
            preparedStatement.setString(17, pedido.getStatus());
            preparedStatement.setDouble(18, pedido.getTroco());
            preparedStatement.setString(19, pedido.getCaixa_responsavel());
            preparedStatement.setInt(20, pedido.getStatus_id());
            System.out.println(pedido.getStatus_id());
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                System.out.println("Pedido Criado com sucesso!");
                insert = true;
            }else{
                System.out.println("Houve um problema");
                insert = false;
            }
        }catch (SQLException ex){
            Logger.getLogger(novoPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insert;
    }
    public static boolean metodoInsertEstatistica(PedidoEstatistica estatistica,String data, String query){
        boolean state = false;
        connection = db_connect.getConnect();
        try{
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, estatistica.getId());
            preparedStatement.setDouble(3, estatistica.getMt());
            preparedStatement.setDouble(4, estatistica.getMe());
            preparedStatement.setInt(5, estatistica.getHp());
            preparedStatement.setString(6, data);
            int count = preparedStatement.executeUpdate();
            if (count > 0){
                state = true;
            }else {
                state = false;
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        return state;
    }
    public static boolean metodoAtualizarPedido(int id, String valueCampo, String query){
        boolean state = false;
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, valueCampo);
            preparedStatement.setInt(2, id);
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return state;
    }
    public static boolean metodoRecuperarCampoString(int id, String query){
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            //preparedStatement.setString(1, campo);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
    public static boolean metodoInsertUsuario(String username, String password, int permissions){
        boolean state = false;
        System.out.println(password);
        query = "INSERT INTO `Usuarios`(`id`, `nome`, `senha`, `privilegio`) VALUES (?,?,?,?)";
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, permissions);
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return state;
    }
    public static boolean metodoUpdateUsuario(String username, String password, int permissions, int id){
        boolean state = false;
        query = "UPDATE `Usuarios` SET `nome`=?,`senha`=?,`privilegio`=? WHERE `id` =?";
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, permissions);
            preparedStatement.setInt(4, id);
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return state;
    }
    /*
    public static boolean metodoInsertProdutoLista(int lista_id, int produto_id){
        boolean state = false;
        query = "INSERT INTO `Usuarios`(`id`, `nome`, `senha`, `privilegio`) VALUES (?,?,?,?)";
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setInt(4, permissions);
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return state;
    }

     */
    public static boolean insertProduto(int id, String prod_nome) {
        boolean state = false;
        query = "INSERT INTO `Produto`(`id`, `nome_produto`) VALUES (?,?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, prod_nome);
            int count = preparedStatement.executeUpdate();
            if (count > 0) {
                state = true;
                System.out.println("Produto Criado");
            }else{
                state = false;
                System.out.println("Problema ao criar produto");
            }

        }catch (SQLException ex){
            Logger.getLogger(detalhesPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return state;
    }


    public static ObservableList<OrdemPedido> recuperarPedidosViaStatus (String query,int value){
        ObservableList<OrdemPedido> listaTemporaria = FXCollections.observableArrayList();
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, value);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                listaTemporaria.add(new OrdemPedido(
                        resultSet.getInt("id"),
                        resultSet.getInt("cliente_id"),
                        resultSet.getString("cliente_nome"),
                        resultSet.getString("cliente_endereco"),
                        resultSet.getString("cliente_telefone"),
                        resultSet.getString("forma_envio"),
                        resultSet.getString("forma_pagamento"),
                        resultSet.getString("forma_subst"),
                        resultSet.getString("data_entrada"),
                        resultSet.getString("horario_entrada"),
                        resultSet.getString("horario_triagem"),
                        resultSet.getString("horario_checkout"),
                        resultSet.getString("horario_finalizado"),
                        resultSet.getInt("operador_id"),
                        resultSet.getInt("entregador_id"),
                        resultSet.getString("fonte_pedido"),
                        resultSet.getString("status"),
                        resultSet.getDouble("troco"),
                        resultSet.getString("caixa_responsavel"),
                        resultSet.getInt("status_id")
                ));
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }

        return listaTemporaria;
    }

    public static Usuario metodoRecupUsuario(String username){
        boolean state = false;
        Usuario usuario = null;
        query = "SELECT * FROM `Usuarios` WHERE `nome` =?";
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                usuario = new Usuario(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("senha"),
                        resultSet.getInt("privilegio"));
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return usuario;
    }
    public static Cliente metodoRecupCliente(String username){
        boolean state = false;
        Cliente cliente = null;
        query = "SELECT * FROM `Clientes` WHERE `cliente_nome` =?";
        connection = db_connect.getConnect();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                cliente = new Cliente(
                        resultSet.getInt("id"),
                        resultSet.getString("cliente_nome"),
                        resultSet.getString("cliente_endereco"),
                        resultSet.getString("cliente_telefone"),
                        resultSet.getString("data_cadastro"),
                        resultSet.getInt("qtdPedidos"));
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return cliente;
    }

    public static boolean metodoUpdateCliente(String value, String query, int id){
        boolean state = false;
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,value);
            preparedStatement.setInt(2, id);
            int count = preparedStatement.executeUpdate();
            if(count>0){
                state = true;
            }else{
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return state;
    }

}
