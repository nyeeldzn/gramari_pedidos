package helpers.Database.PedidoProduto;

import helpers.Database.db_connect;
import sample.novoPedidoController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class pedido_crud {

    public static boolean addproduto_pedido(int produto_id, int pedido_id,String nome, int qtd) {
        boolean state = false;
        System.out.println("Produto de ID: " + produto_id + "Nome: " + nome + " sendo adicionado ao pedido...");
        String query = "INSERT INTO `Pedido_Produto`(`id`, `pedido_index`, `produto_index`,`produto_nome`,  `quantidade`) VALUES (?,?,?,?,?)";
        try {
            Connection conn = db_connect.getConnect();
            PreparedStatement p = conn.prepareStatement(query);
            p.setInt(1, 0);
            p.setInt(2, pedido_id); // pedido index
            p.setInt(3, produto_id); // produto index
            p.setString(4, nome); // produto nome
            p.setInt(5, qtd); // quantidade
            int count = p.executeUpdate();
            if(count > 0){
                System.out.println("Produto adicionado");
                state = true;
            }else
            {
                state = false;
                System.out.println("Houve um problema");
            }
        }catch (SQLException ex){
            Logger.getLogger(novoPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }


}
