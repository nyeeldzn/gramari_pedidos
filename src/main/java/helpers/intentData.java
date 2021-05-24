package helpers;

public final class intentData {
    private String dadosteste;
    private int tableIndex;
    private final static intentData INSTANCE = new intentData();

    public int getTableIndex() {
        return tableIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public intentData() {
    }

    public static intentData getINSTANCE() {
        return INSTANCE;
    }

    public String getDadosteste() {
        return dadosteste;
    }

    public void setDadosteste(String dadosteste) {
        this.dadosteste = dadosteste;
    }

}

