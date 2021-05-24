package helpers;

import models.OrdemPedido;

public class PedidoDAO {

    public void salvar(OrdemPedido pedido){
        String sql = "INSERT INTO `Ordem_De_Pedido`(`id`, `cliente_nome`, `end_cliente`, `num_cliente`, `forma_envio`, `forma_pagamento`, `forma_subst`, `troco`) VALUES ([value-1],[value-2],[value-3],[value-4],[value-5],[value-6],[value-7],[value-8])";
    }
}
