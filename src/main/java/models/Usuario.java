package models;

public class Usuario {

    int id;
    String username;
    String pass;
    int priv;

    public Usuario(int id, String username, String pass, int priv) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.priv = priv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getPriv() {
        return priv;
    }

    public void setPriv(int priv) {
        this.priv = priv;
    }
}
