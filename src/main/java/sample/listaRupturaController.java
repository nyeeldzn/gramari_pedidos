package sample;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import helpers.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import models.ListaRuptura;
import models.Produto;
import models.Usuario;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static helpers.DefaultComponents.*;

public class listaRupturaController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private TableView<ListaRuptura> tableLista;
    @FXML
    private TableColumn<ListaRuptura, Integer> idCol;
    @FXML
    private TableColumn<ListaRuptura, String> dataeCol;
    @FXML
    private TableColumn<ListaRuptura, String> responsavelCol;
    @FXML
    private JFXButton btnNovo;
    @FXML
    private JFXButton btnEditar;
    @FXML
    private JFXButton btnExcluir;
    @FXML
    private JFXDatePicker pickerDataInicial;
    @FXML
    private JFXDatePicker pickerDataFinal;
    @FXML
    private JFXCheckBox checkBoxDatas;
    @FXML
    private JFXButton btnSearch;

    JFXTextField edt1;
    JFXDialog dialog;
    TableView<Produto> tableProdutosLista;

    String prod_nome;
    String dataInicial = null;
    String dataFinal = null;
    String dataAtual = null;
    Usuario user;
    ListaRuptura modelLista;
    boolean isSelectedLista;
    Produto produto;

    //db
    String query = null;
    PreparedStatement preparedStatement = null;
    Connection connection = null;
    ResultSet resultSet = null;
    //db
    ArrayList<Produto> produtos = new ArrayList();
    ObservableList<ListaRuptura> listaRupturas = FXCollections.observableArrayList();
    ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComponents();
        recuperarusuario();
        verificarListaNoDia();
        try {
            recuperarProdutos();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }




    //metodo iniciais
    private void setupComponents() {
        pickerDataInicial.setValue(
                nowOnDate()
        );
        pickerDataInicial.setOnAction((e) -> {
            dataInicial = onDate(pickerDataInicial);
        });
        pickerDataInicial.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        pickerDataFinal.setValue(
                nowOnDate()
        );
        pickerDataFinal.setOnAction((e) -> {
            dataFinal = onDate(pickerDataFinal);
        });
        pickerDataFinal.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dataeCol.setCellValueFactory(new PropertyValueFactory<>("data"));
        responsavelCol.setCellValueFactory(new PropertyValueFactory<>("responsavel_id"));

        btnNovo.setOnAction((e) -> {
          /*  try {
                DefaultComponents.alertDialog3Itens(btnConfirmar, stackPane);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
           */
            boolean state = verificarListaNoDia();
            if(state == true){
                int larguraPadrao = 85;
                JFXButton btnConfirmar = button("SIM", "CHECK", larguraPadrao);
                JFXButton btnCancelar = button("NÃO", "TIMES", larguraPadrao);
                JFXDialog dialog = ExcluirDialogModel.alertDialogErro("Já existe uma lista criada no dia, deseja criar um nova?", stackPane, btnConfirmar, btnCancelar);
                btnConfirmar.setOnAction((actionEvent) -> {
                    dialog.close();
                    criarLista();
                });
                btnCancelar.setOnAction((actionEvent) -> {
                    dialog.close();
                });
                dialog.show();
            }else {
                criarLista();
            }
        });
        btnEditar.setOnAction((e) -> {
            try {
                alertDialogBuscaProdutosListaRuptura();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });


        btnExcluir.setOnAction((e) -> {
            if(isSelectedLista == true){
                excluirLista();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Selecione uma lista", stackPane);
                dialog.show();
            }
        });
        tableLista.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ListaRuptura>() {
            @Override
            public void changed(ObservableValue<? extends ListaRuptura> observable, ListaRuptura oldValue, ListaRuptura newValue) {
                if(tableLista.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = tableLista.getSelectionModel();
                    modelLista = (ListaRuptura) tableLista.getSelectionModel().getSelectedItem();
                    isSelectedLista = true;
                    System.out.println(modelLista.getId());
                }
            }
        });
        btnSearch.setOnAction((e) -> {
            if(checkBoxDatas.isSelected()){
                //buscar por datas
                listaRupturas.clear();
                recuperarListasPorData();
            }else{
                refreshTable();
            }
        });

    }
    private void recuperarusuario() {
        user = AuthenticationSystem.getUser();
    }
    private boolean verificarListaNoDia() {
        System.out.println("Verificando Lista do dia");
        boolean state = false;
        query = "SELECT * FROM `ListaRuptura` WHERE `data` =?";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dataAtual);
            resultSet = preparedStatement.executeQuery();
            ObservableList<ListaRuptura> listaRupturasLocal = FXCollections.observableArrayList();
            while (resultSet.next()) {
                ListaRuptura lista = new ListaRuptura(
                  resultSet.getInt("id"),
                  resultSet.getString("data"),
                  resultSet.getInt("responsavel_id")
                );
                System.out.println("Lista Recuperada" + lista.getId());
                listaRupturasLocal.add(lista);
            }
            System.out.println("Tamanho da lista: " + listaRupturasLocal.size());
            if (listaRupturasLocal.size() < 1) {
                state = false;
                JFXButton btnOK = defaultButton("Criar");

                JFXButton btnNO = defaultButton("Não");

                JFXDialog dialog = ExcluirDialogModel.alertDialogErro("Não foi encontrada lista no dia, deseja criar uma nova?", stackPane,btnOK, btnNO );
                dialog.show();
                btnOK.setOnAction((e) ->{
                    criarLista();
                    dialog.close();
                });
                btnNO.setOnAction((e) -> {
                    dialog.close();
                });
            }else{
                //buscar todos
                state = true;
                recuperarTodosAsListas();
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return state;
        }
    private void recuperarTodosAsListas() {
        listaRupturas.clear();
        query = "SELECT * FROM `ListaRuptura`";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ListaRuptura lista = new ListaRuptura(
                        resultSet.getInt("id"),
                        resultSet.getString("data"),
                        resultSet.getInt("responsavel_id")
                );
                listaRupturas.add(lista);
            }
            tableLista.setItems(listaRupturas);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    private void recuperarListasPorData() {
        query = "SELECT * FROM `ListaRuptura` WHERE `data` BETWEEN ? AND ?";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dataInicial);
            preparedStatement.setString(2, dataFinal);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ListaRuptura lista = new ListaRuptura(
                        resultSet.getInt("id"),
                        resultSet.getString("data"),
                        resultSet.getInt("responsavel_id")
                );
                listaRupturas.add(lista);
            }
            tableLista.setItems(listaRupturas);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }




    //metodos iniciais

    //metodos de negocios
    private void salvarProduto(){
        connection = db_connect.getConnect();
        prod_nome = edt1.getText().toUpperCase().trim();
        if(prod_nome.isEmpty()){
            JFXDialog dialog = AlertDialogModel.alertDialogErro("Preencha todos os campos", stackPane);
            dialog.show();
        }else{
            for(int i = 1; i<produtos.size(); i++){
                int id = produtos.get(i).getId();
                String nomerecup = produtos.get(i).getNome().toUpperCase().trim();

                System.out.println(nomerecup);
                System.out.println(prod_nome);
                System.out.println(i);

                System.out.println(produtos.size());

                if(nomerecup.equals(prod_nome)){
                    System.out.println("Finalizando for, adcionando produto.");
                    i = produtos.size();
                    System.out.println("ArraySize " + produtos.size() + "For size " + i);
                    addproduto_pedido(id, nomerecup.toUpperCase().trim());
                }else if(!(nomerecup.equals(prod_nome))){
                    if(i == produtos.size() - 1 && !(prod_nome.equals(nomerecup))){
                        System.out.println("Criando novo produto, finalizando for");
                        i = i + 1;
                        boolean state = db_crud.insertProduto(id, prod_nome);
                        if(state == true){
                            try {
                                System.out.println("Produto Criado: " + prod_nome);
                                recuperarProdutoCriado(prod_nome);
                                restartAdd();
                            }catch (SQLException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
    }

    private void restartAdd() {
        listaProdutos.clear();
        try {
            recuperarProdutosLista(modelLista.getId());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        edt1.clear();
        edt1.requestFocus();
    }

    private void recuperarProdutoCriado(String nome) throws SQLException {
        query = "SELECT * FROM `Produto` WHERE nome_produto = ?";
        connection = db_connect.getConnect();
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, nome);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String nome_produto = resultSet.getString("nome_produto");
            int id = resultSet.getInt("id");
            produto = new Produto(id, nome_produto);
            System.out.println("Produto criado com ID: " + produto.getId() + "com Nome: " + produto.getNome());
        }
        addproduto_pedido(produto.getId(), produto.getNome());
    }

    private void addproduto_pedido(int id, String nome) {
        System.out.println("Produto de ID: " + id + "Nome: " + nome + " sendo adicionado ao pedido...");
        query = "INSERT INTO `Lista_Produto`(`id`,`lista_id`, `produto_id`) VALUES (?,?,?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, modelLista.getId());
            preparedStatement.setInt(3, id); // pedido index
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                System.out.println("Produto adicionado");
                restartAdd();
            }else
            {
                System.out.println("Houve um problema");
            }
        }catch (SQLException ex){
            Logger.getLogger(novoPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void excluirLista(){
        JFXButton btnExcluir = defaultButton("SIM");
        JFXButton btnCancelar = defaultButton("NAO");

        JFXDialog dialog = ExcluirDialogModel.alertDialogErro("Deseja realmente excluir lista?", stackPane ,btnExcluir,  btnCancelar);
        dialog.show();
        btnCancelar.setOnAction((e) -> {
            dialog.close();
        });
        btnExcluir.setOnAction((e) -> {
            query = "DELETE FROM `ListaRuptura` WHERE `id` =?" ;
            boolean state = db_crud.metodoExlusao(query, modelLista.getId());
            if(state == true){
                refreshTable();
                dialog.close();
            }else{
                JFXDialog dialog2 = AlertDialogModel.alertDialogErro("Houve um problema na exclusao", stackPane);
                dialog2.show();
            }
        });

    }
    private void criarLista() {
        System.out.println("Criando nova Lista de Rupturas");
        query = "INSERT INTO `ListaRuptura`(`id`, `data`, `responsavel_id`) VALUES (?,?,?)";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, dataAtual);
            preparedStatement.setInt(3, user.getId());
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                System.out.println("Sucesso");
                refreshTable();
            }else{
                System.out.println("Houve um problema");
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    private LocalDate nowOnDate(){
        LocalDate localDate = LocalDate.now();
        localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dataFinal = formatData(localDate.toString());
        dataInicial = formatData(localDate.toString());
        dataAtual = formatData(localDate.toString());
        System.out.println("Data Atual: " + dataInicial);
        System.out.println("Data Atual: " + dataFinal);
        return localDate;

    }
    private String onDate (JFXDatePicker picker){
        LocalDate localDate = picker.getValue();
        String dataFormatada = formatData(localDate.toString());
        System.out.println(dataFormatada);
        //dataInicial = formatData(localDate.toString());
        //datePicker.setPromptText(dataInicial);
        return dataFormatada;
    }
    public String formatData (String data){
        SimpleDateFormat sdf = null;
        Date d = null;
        try{
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            d = sdf.parse(data);
            sdf.applyPattern("yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(d);
    }
    private void autoCompleteTextField(String search){
        String complete = "";
        int start = search.length();
        int last = search.length();


        for(int i = 0; i<produtos.size(); i++){
            if(produtos.get(i).getNome().toUpperCase().startsWith(search.toUpperCase())){
                complete = produtos.get(i).getNome().toUpperCase();
                last = complete.length();
                break;
            }
        }
        if(last>start){
            edt1.setText(complete);
            edt1.positionCaret(last);
            edt1.selectPositionCaret(start);
        }



    }
    private void recuperarProdutos() throws SQLException {
        produtos.clear();
        query = "SELECT * FROM `Produto`";
        connection = db_connect.getConnect();
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            produtos.add(new Produto(
                            resultSet.getInt("id"),
                            resultSet.getString("nome_produto")
                    )
            );
            System.out.println("Produto recuperado: " + resultSet.getString("nome_produto"));
        }
    }
    private void recuperarProdutosLista(int id) throws SQLException {
        query = "SELECT * FROM `Lista_Produto` WHERE `lista_id` =?";
        connection = db_connect.getConnect();
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        resultSet = preparedStatement.executeQuery();
        ArrayList<Integer> listaProdutosRecup = new ArrayList<>();
        while (resultSet.next()) {
            listaProdutosRecup.add(resultSet.getInt("produto_id"));
            //System.out.println("Produto recuperado: " + resultSet.getInt("produto_id"));
        }
        listaProdutos.clear();
        for(int i = 0; i<listaProdutosRecup.size(); i++) {
            String query_inner = "SELECT * FROM `Produto` WHERE `id` =?";
            Connection connection_inner = null;
            connection_inner = db_connect.getConnect();
            PreparedStatement preparedStatement_inner = null;
            preparedStatement_inner = connection_inner.prepareStatement(query_inner);
            preparedStatement_inner.setInt(1, listaProdutosRecup.get(i));
            ResultSet resultSet_inner = null;
            resultSet_inner = preparedStatement_inner.executeQuery();
            while (resultSet_inner.next()){
                listaProdutos.add(new Produto(
                        resultSet_inner.getInt("id"),
                        resultSet_inner.getString("nome_produto")
                ));
            }
            System.out.println("Produto Recuperado:" + listaProdutos.get(i).getNome());
            }
        tableProdutosLista.setItems(listaProdutos);
    }


    //metodos de negocios

    //objetos
    public void alertDialogBuscaProdutosListaRuptura() throws IOException {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialogLayout.setBody(
                formularioProdutosLista()
        );
        dialog.show();
        try {
            recuperarProdutosLista(modelLista.getId());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
    public AnchorPane formularioProdutosLista(){
        AnchorPane pane = new AnchorPane();
        VBox vboxPrincipal = defaultVBox();
        JFXButton btnCancelar = defaultButton("SAIR");
        JFXButton btnADD = DefaultComponents.buttonIcon("", "PLUS", 50);
        btnADD.setOnAction((e) -> {
            salvarProduto();
        });
        tableProdutosLista = new TableView<>();
        TableColumn<Produto, Integer> idCol = new TableColumn<>();
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setText("");
        TableColumn<Produto, String> nomeCol = new TableColumn<>();
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        nomeCol.setText("Nome do Produto");
        nomeCol.setPrefWidth(400);
        tableProdutosLista.getColumns().addAll(idCol, nomeCol);

        edt1 = textFieldPadrao(500);
        edt1.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edt1.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case BACK_SPACE:
                    break;
                case ENTER:
                    edt1.setText(edt1.getText().toUpperCase());
                    btnADD.requestFocus();
                    break;
                case SHIFT:
                    break;
                default:
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String txt = edt1.getText().toUpperCase();
                            autoCompleteTextField(txt);
                        }
                    });
            }
        });
        btnADD.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    salvarProduto();
                    break;
            }
        });

        HBox row1 = defaultHBox();
        HBox row3 = defaultHBox();



        VBox vBox = defaultVBox();
        vBox.setSpacing(5);
        Text texto = defaultText("ADIÇÃO PRODUTO");
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setStyle("-fx-background-color: navy; -fx-background-radius: 10");
        //vBox.setStyle("-fx-background-radius: 15");


        vBox.getChildren().addAll(texto, row1);
        row1.getChildren().addAll(
                edt1, btnADD
        );

        row3.getChildren().addAll(
                btnCancelar
        );

        row3.setAlignment(Pos.CENTER_RIGHT);

        vboxPrincipal.getChildren().addAll(vBox, tableProdutosLista, row3);

        btnCancelar.setOnAction((event -> {
            dialog.close();
        }));
        pane.getChildren().add(vboxPrincipal);
        return pane;
    }

    public JFXButton button(String texto, String glyph, double larguraPadrao){
        JFXButton button = new JFXButton(texto, FontIcon(glyph));
        button.setPrefSize(larguraPadrao, 50);
        button.setStyle("-fx-background-color: #d3d3d3");
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }
    public FontAwesomeIconView FontIcon(String glyphName){
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName(glyphName);
        icon.setSize("35.0");
        return icon;
    }
    //objetos

    //metodos de controle
    private void refreshTable(){
        listaRupturas.clear();
        recuperarTodosAsListas();
    }
    //metodos de controle
}
