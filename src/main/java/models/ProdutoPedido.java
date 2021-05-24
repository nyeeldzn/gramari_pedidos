package models;

public class ProdutoPedido {
    int index;
    String nome;
    int qtd;

    public ProdutoPedido(int index, String nome, int qtd) {
        this.index = index;
        this.nome = nome;
        this.qtd = qtd;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
