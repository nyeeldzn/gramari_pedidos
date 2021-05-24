package sample;

import com.jfoenix.controls.JFXButton;
import com.mysql.cj.xdevapi.PreparableStatement;
import helpers.DefaultComponents;
import helpers.db_connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class importarProdutoController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private JFXButton btnQuit;
    @FXML
    private JFXButton btnImport;
    @FXML
    private JFXButton btnSelect;
    @FXML
    private Text textNome;

    ObservableList<String> listaProdutos = FXCollections.observableArrayList();
    Connection connection = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setupComponents();

    }
    //metodos iniciais
    private void setupComponents() {
        btnSelect.setOnAction((e) -> {
            metodoSelecao();
        });
        btnImport.setOnAction((e) -> {
            connection = db_connect.getConnect();
            for (int i = 0; i < listaProdutos.size(); i++) {
                textNome.setText("Adicionando produto: " + listaProdutos.get(i) + "  " + "(" + i + "/" + listaProdutos.size() + ")");
                boolean state = metodoInsertListaProdutos(listaProdutos.get(i));
                if(state){
                    System.out.println("Produto: " + listaProdutos.get(i) + " Adicionado com sucesso!");
                }else {
                    System.out.println("Houve um problema ao adicionar o produto: " + listaProdutos.get(i));
                }
            }
        });
    }

    private boolean metodoInsertListaProdutos(String value) {
        boolean state = false;
        String query = null;
        PreparedStatement preparableStatement = null;
        try {
            query = "INSERT INTO `Produto`(`id`, `nome_produto`) VALUES (?,?)";
            preparableStatement = connection.prepareStatement(query);
            preparableStatement.setInt(1, 0);
            preparableStatement.setString(2, value);
            int count = preparableStatement.executeUpdate();
            if(count > 0){
                state = true;
            }else {
                state = false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return state;
    }

    //metodos iniciais
    //metodos de negocio
    private void metodoSelecao() {
        File file = DefaultComponents.fileChooserSelect(stackPane, "DOCUMENTO DO EXCEL (*.xls)", "*.xls");
        textNome.setText(file.getName());

        try {
            listaProdutos = metodoImport(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        if(listaProdutos.size() > 0){
            textNome.setText("Sucesso, clique em importar para enviar para o Banco de Dados");
        }

    }

    private ObservableList<String> metodoImport(File file) throws IOException, BiffException {
        ObservableList<String> listaProdutosImportados = FXCollections.observableArrayList();
        Workbook workbook = Workbook.getWorkbook(file);
        Sheet sheet = workbook.getSheet(0);
        int linhas = sheet.getRows();

        System.out.println("Iniando leitura da planilha: " + file.getName());
        System.out.println("Planilha com: " + linhas + " linhas");

        for(int i = 0; i<linhas; i++){
            Cell cell = sheet.getCell(3, i);
            listaProdutosImportados.add(cell.getContents());
            System.out.println("Produto: " + listaProdutosImportados.get(i));
        }
        workbook.close();
        return listaProdutosImportados;
    }
    //metodos de negocio
}
