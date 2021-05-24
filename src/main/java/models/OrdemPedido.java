package models;

public class OrdemPedido {
    int id;
    int cliente_id;
    String cliente_nome, end_cliente, num_cliente, forma_envio,
            forma_pagamento, forma_subst, data_entrada, horario_entrada,
            horario_triagem, horario_checkout, horario_finalizado;
    int operador_id;
    int entregador_id;
    String fonte_pedido;
    String status;
    double troco;
    String caixa_responsavel;
    int status_id;


    public OrdemPedido(int id, int cliente_id, String cliente_nome, String end_cliente,
                       String num_cliente, String forma_envio, String forma_pagamento,
                       String forma_subst, String data_entrada, String horario_entrada,
                       String horario_triagem, String horario_checkout, String horario_finalizado,
                       int operador_id, int entregador_id, String fonte_pedido, String status, double troco,
                        String caixa_responsavel, int status_id) {
        this.id = id;
        this.cliente_id = cliente_id;
        this.cliente_nome = cliente_nome;
        this.end_cliente = end_cliente;
        this.num_cliente = num_cliente;
        this.forma_envio = forma_envio;
        this.forma_pagamento = forma_pagamento;
        this.forma_subst = forma_subst;
        this.data_entrada = data_entrada;
        this.horario_entrada = horario_entrada;
        this.horario_triagem = horario_triagem;
        this.horario_checkout = horario_checkout;
        this.horario_finalizado = horario_finalizado;
        this.operador_id = operador_id;
        this.entregador_id = entregador_id;
        this.fonte_pedido = fonte_pedido;
        this.status = status;
        this.troco = troco;
        this.caixa_responsavel = caixa_responsavel;
        this.status_id = status_id;
    }

    public String getCaixa_responsavel() {
        return caixa_responsavel;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public void setCaixa_responsavel(String caixa_responsavel) {
        this.caixa_responsavel = caixa_responsavel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(int cliente_id) {
        this.cliente_id = cliente_id;
    }

    public String getCliente_nome() {
        return cliente_nome;
    }

    public void setCliente_nome(String cliente_nome) {
        this.cliente_nome = cliente_nome;
    }

    public String getEnd_cliente() {
        return end_cliente;
    }

    public void setEnd_cliente(String end_cliente) {
        this.end_cliente = end_cliente;
    }

    public String getNum_cliente() {
        return num_cliente;
    }

    public void setNum_cliente(String num_cliente) {
        this.num_cliente = num_cliente;
    }

    public String getForma_envio() {
        return forma_envio;
    }

    public void setForma_envio(String forma_envio) {
        this.forma_envio = forma_envio;
    }

    public String getForma_pagamento() {
        return forma_pagamento;
    }

    public void setForma_pagamento(String forma_pagamento) {
        this.forma_pagamento = forma_pagamento;
    }

    public String getForma_subst() {
        return forma_subst;
    }

    public void setForma_subst(String forma_subst) {
        this.forma_subst = forma_subst;
    }

    public String getData_entrada() {
        return data_entrada;
    }

    public void setData_entrada(String data_entrada) {
        this.data_entrada = data_entrada;
    }

    public String getHorario_entrada() {
        return horario_entrada;
    }

    public void setHorario_entrada(String horario_entrada) {
        this.horario_entrada = horario_entrada;
    }

    public String getHorario_triagem() {
        return horario_triagem;
    }

    public void setHorario_triagem(String horario_triagem) {
        this.horario_triagem = horario_triagem;
    }

    public String getHorario_checkout() {
        return horario_checkout;
    }

    public void setHorario_checkout(String horario_checkout) {
        this.horario_checkout = horario_checkout;
    }

    public String getHorario_finalizado() {
        return horario_finalizado;
    }

    public void setHorario_finalizado(String horario_finalizado) {
        this.horario_finalizado = horario_finalizado;
    }

    public int getOperador_id() {
        return operador_id;
    }

    public void setOperador_id(int operador_id) {
        this.operador_id = operador_id;
    }

    public int getEntregador_id() {
        return entregador_id;
    }

    public void setEntregador_id(int entregador_id) {
        this.entregador_id = entregador_id;
    }

    public String getFonte_pedido() {
        return fonte_pedido;
    }

    public void setFonte_pedido(String fonte_pedido) {
        this.fonte_pedido = fonte_pedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTroco() {
        return troco;
    }

    public void setTroco(double troco) {
        this.troco = troco;
    }
}
