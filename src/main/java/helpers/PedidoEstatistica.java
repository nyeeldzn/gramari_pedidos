package helpers;

public class PedidoEstatistica {

    int id;
    double mt;
    double me;
    int hp;


    public PedidoEstatistica(int id, double mt, double me, int hp) {
        this.id = id;
        this.mt = mt;
        this.me = me;
        this.hp = hp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMt() {
        return mt;
    }

    public void setMt(double mt) {
        this.mt = mt;
    }

    public double getMe() {
        return me;
    }

    public void setMe(double me) {
        this.me = me;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}
