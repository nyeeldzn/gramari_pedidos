package models;

public class ListaRuptura {

    int id;
    String data;
    int responsavel_id;

    public ListaRuptura(int id, String data, int responsavel_id) {
        this.id = id;
        this.data = data;
        this.responsavel_id = responsavel_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResponsavel_id() {
        return responsavel_id;
    }

    public void setResponsavel_id(int responsavel_id) {
        this.responsavel_id = responsavel_id;
    }
}
