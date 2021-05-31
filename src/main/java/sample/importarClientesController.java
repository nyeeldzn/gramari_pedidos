package sample;

import com.jfoenix.controls.JFXButton;
import helpers.DefaultComponents;
import helpers.db_connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import models.Cliente;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class importarClientesController implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton btnImport;

    @FXML
    private JFXButton btnQuit;

    @FXML
    private JFXButton btnSelect;

    @FXML
    private Text textNome;

    ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    Connection connection = null;
    Thread t1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread t = Thread.currentThread();
        System.out.println( "Thread Inicial: " + t.getName());
        setupComponents();
    }

    private void setupComponents() {
        btnQuit.setOnAction((e) -> {
            quitScreen();
        });
        btnImport.setOnAction((e) -> {
                connection = db_connect.getConnect();
                int i;
                for (i = 0; i < listaClientes.size(); i++) {
                    boolean state = metodoInsertListaClientes(listaClientes.get(i));
                    if(state){
                        System.out.println("Cliente: " + listaClientes.get(i).getNome() + " Adicionado com sucesso!");
                        int finalI = i;
                        Thread t1 = new Thread( () -> {
                            textNome.setText("Adicionando cliente: " + listaClientes.get(finalI).getNome() + "  " + "(" + finalI + "/" + listaClientes.size() + ")");
                        });
                        t1.start();
                    }else {
                        System.out.println("Houve um problema ao adicionar o cliente: " + listaClientes.get(i).getNome());
                    }
                }
        });
        btnSelect.setOnAction((e) -> {
            metodoSelecao();
        });
    }
    //metodos de negocios
    private void metodoSelecao() {
        File file = DefaultComponents.fileChooserSelect(stackPane, "DOCUMENTO DO EXCEL (*.xls)", "*.xls");
        textNome.setText(file.getName());

        try {
            listaClientes = metodoImport(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        if(listaClientes.size() > 0){
            textNome.setText("Sucesso, clique em importar para enviar para o Banco de Dados");
        }
    }

    private ObservableList<Cliente> metodoImport(File file) throws IOException, BiffException {
        ObservableList<Cliente> listaTemporaria = FXCollections.observableArrayList();
        Workbook workbook = Workbook.getWorkbook(file);
        Sheet sheet = workbook.getSheet(0);
        int linhas = sheet.getRows();

        System.out.println("Iniando leitura da planilha: " + file.getName());
        System.out.println("Planilha com: " + linhas + " linhas");

        for(int i = 0; i<linhas; i++){
            Cell cellNome = sheet.getCell(0, i);
            Cell cellEndereco = sheet.getCell(4, i);
            Cell cellNumero = sheet.getCell(5, i);
            Cell cellBairro = sheet.getCell(6, i);
            String enderecoCompleto = cellEndereco.getContents() + "," + cellNumero.getContents() + "," + cellBairro.getContents() ;
            listaTemporaria.add(new Cliente(0, cellNome.getContents(), enderecoCompleto, "Sem Numero Cadastrado", "Usuario Importado", 0));
            System.out.println("Cliente: " + listaTemporaria.get(i).getNome() + " " + listaTemporaria.get(i).getTelefone() + " " + listaTemporaria.get(i).getEndereco() + " " + listaTemporaria.get(i).getData_cadastro());
        }
        workbook.close();
        return listaTemporaria;
    }

    private boolean metodoInsertListaClientes(Cliente cliente) {
        boolean state = false;
        String query = null;
        PreparedStatement preparableStatement = null;
        try {
            query = "INSERT INTO `Clientes`(`id`, `cliente_nome`, `cliente_endereco`, `cliente_telefone`, `data_cadastro`) VALUES (?,?,?,?,?)";
            preparableStatement = connection.prepareStatement(query);
            preparableStatement.setInt(1, 0);
            preparableStatement.setString(2, cliente.getNome());
            preparableStatement.setString(3, cliente.getEndereco());
            preparableStatement.setString(4, cliente.getTelefone());
            preparableStatement.setString(5, cliente.getData_cadastro());
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

    //metodos de negocios

    //metodos de controle
    private void quitScreen() {
        Stage stage;
        stage = (Stage) stackPane.getScene().getWindow();
        stage.close();
    }
    //metodos de controle
}
