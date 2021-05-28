package sample;

import com.jfoenix.controls.*;
import helpers.AlertDialogModel;
import helpers.AuthenticationSystem;
import helpers.db_connect;
import helpers.db_crud;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Cliente;
import models.OrdemPedido;
import models.Usuario;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class novoPedidoController implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField edtNome;

    @FXML
    private JFXTextField edtEndereco;

    @FXML
    private JFXTextField edtTel;

    @FXML
    private JFXTextField edtFormaPagamento;

    @FXML
    private JFXTextField edtFormaEnvio;

    @FXML
    private JFXTextField edtCasoFalta;

    @FXML
    private JFXTextField edtTroco;

    @FXML
    private JFXTextField edtFonte;

    @FXML
    private JFXButton btnSalvar;

    @FXML
    private CheckBox checkBoxManual;

    @FXML
    private HBox hboxManual;

    @FXML
    private JFXButton btnClientePesquisa;

    JFXTextField edtSearch;
    JFXDialog dialog;
    BorderPane border;
    TableView tableView;

    @FXML
    private JFXButton btnCancelar;

    int selectedIndex;
    String query = null;
    int cliente_id = 0;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    Usuario user;

    ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    FilteredList<Cliente> filteredData;

    Cliente selected_Cliente;

    String clienteNome, clienteEndereco, clienteTelefone, formaEntrega, formaPagamento, casoFalta, data_entrada, fonte;
    int clienteid;
    double troco;
    int id;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        user = AuthenticationSystem.getUser();

        try {
            recuperarClientes();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        setupComponentes();
    }

    //Metodos Iniciais
    private void recuperarClientes() throws SQLException {
        clientes.clear();
        connection = db_connect.getConnect();
        query = "SELECT * FROM `Clientes`";
        preparedStatement = connection.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            clientes.add(new Cliente(
                            resultSet.getInt("id"),
                            resultSet.getString("cliente_nome"),
                            resultSet.getString("cliente_endereco"),
                            resultSet.getString("cliente_telefone"),
                            resultSet.getString("data_cadastro")
                    )
            );
            System.out.println("Usuario recuperado: " + resultSet.getString("cliente_nome"));
        }
    }
    private void setupComponentes() {
        /*
        edtNome.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case BACK_SPACE:
                    break;
                case ENTER:
                    edtNome.setText(edtNome.getText().toUpperCase());
                    edtEndereco.requestFocus();
                    edtEndereco.setText(edtEndereco.getText().toUpperCase());
                    edtTel.setText(edtTel.getText().toUpperCase());
                    break;
                case SHIFT:
                    break;
                default:
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String txt = edtNome.getText().toUpperCase();
                            autoCompleteTextField(txt);
                        }
                    });
            }
        });
        edtNome.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
         */
        setupEdt();
        checkBoxManual.setOnMouseClicked((e) -> {
            if(checkBoxManual.isSelected()){
                hboxManual.setVisible(true);
            }else{
                hboxManual.setVisible(false);
            }
        });


        edtEndereco.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtTel.requestFocus();
                    break;
            }
        });
        edtEndereco.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtTel.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtFormaPagamento.requestFocus();
                    verificarClienteExistente();
                    break;
                case TAB:
                    verificarClienteExistente();
                    break;
            }
        });
        edtTel.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtFormaPagamento.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtFormaEnvio.requestFocus();
                    break;
            }
        });
        edtFormaPagamento.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtFormaEnvio.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtCasoFalta.requestFocus();
                    break;
            }
        });
        edtFormaEnvio.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtCasoFalta.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtTroco.requestFocus();
                    break;
            }
        });
        edtCasoFalta.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtTroco.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtFonte.requestFocus();
                    break;
            }
        });
        edtTroco.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtFormaPagamento.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verificarClienteExistente();
            }
        });
        edtCasoFalta.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verificarClienteExistente();
            }
        });
        edtFormaEnvio.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verificarClienteExistente();
            }
        });
        edtTroco.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verificarClienteExistente();
            }
        });
        edtFonte.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtFonte.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verificarClienteExistente();
            }
        });
        edtFonte.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    btnSalvar.requestFocus();
                    break;
            }
        });

        btnCancelar.setOnAction((e) -> {
            fecharJanela();
        });

        btnSalvar.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    salvarPedido();
                    break;
            }
        });
        btnSalvar.setOnAction((e) -> {
            salvarPedido();
        });
        btnClientePesquisa.setOnAction((e) ->{
            alertDialogClientes();
        });

    }

    private void setupEdt() {
        ArrayList<String> nomes = new ArrayList<>();
        for(int i = 0; i<clientes.size(); i++){
            nomes.add(clientes.get(i).getNome());
        }

        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setPrefWidth(500);
        autoCompletePopup.getSuggestions().addAll(nomes);

        autoCompletePopup.setSelectionHandler(event -> {
            edtNome.setText(event.getObject());
            recuperarClienteSelecionado(event.getObject());
            // you can do other actions here when text completed
        });

        // filtering options
        edtNome.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string -> string.toLowerCase().contains(edtNome.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() || edtNome.getText().isEmpty()) {
                autoCompletePopup.hide();
                // if you remove textField.getText.isEmpty() when text field is empty it suggests all options
                // so you can choose
            } else {
                autoCompletePopup.show(edtNome);
            }
        });
    }

    private void recuperarClienteSelecionado(String nomeSelecionado) {
        Cliente clienteSelecionado = db_crud.metodoRecupCliente(nomeSelecionado);
        edtTel.setText(clienteSelecionado.getTelefone());
        edtEndereco.setText(clienteSelecionado.getEndereco());
        edtEndereco.requestFocus();
    }
    //Metodos Iniciais

    //Metodos de Negocios
    private void pesquisarCliente(){
        filteredData = new FilteredList<>(clientes, b -> true);
        edtSearch.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredData.setPredicate(cliente -> {
                if(newValue == null && newValue.isEmpty()){
                    return true;
                }

                String upperCaseFilter = newValue.toUpperCase().trim();
                if(cliente.getNome().toUpperCase().indexOf(upperCaseFilter) != -1){
                    return true;
                }else if(cliente.getEndereco().toUpperCase().indexOf(upperCaseFilter) != -1){
                    return true;
                }else if(cliente.getTelefone().toUpperCase().indexOf(upperCaseFilter) != -1)
                    return true;
                         else
                            return false;
            });

            SortedList<Cliente> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
        });
    }
    private void salvarPedido(){
        connection = db_connect.getConnect();

        clienteNome = edtNome.getText();
        clienteEndereco = edtEndereco.getText();
        clienteTelefone = edtTel.getText();
        formaEntrega = edtFormaEnvio.getText();
        formaPagamento = edtFormaPagamento.getText();
        casoFalta = edtCasoFalta.getText();
        fonte = edtFonte.getText();
        if(edtTroco.getText() != null && !(edtTroco.getText().isEmpty())){
            troco = Double.parseDouble(edtTroco.getText());
        }

        if(clienteNome.isEmpty() || clienteEndereco.isEmpty() || clienteTelefone.isEmpty() ||
                formaEntrega.isEmpty() || formaPagamento.isEmpty() || casoFalta.isEmpty() || fonte.isEmpty()){
            alertDialogErro("Preencha todos os Campos!");
        }else if(clienteNome.length() > 155 || clienteEndereco.length() > 500 || clienteTelefone.length() > 55 ){
            alertDialogErro("Os campos excedem o tamanho de maximo de caracteres.");
        }else{
            insertPedido();
        }
    }


    private void verificarCheckboxManual (){

    }

    private void insertPedido() {
        query =  "INSERT INTO `Pedidos`(`id`, `cliente_id`, `cliente_nome`, " +
                "`cliente_endereco`, `cliente_telefone`, `forma_envio`, `forma_pagamento`, " +
                "`forma_subst`, `data_entrada`, `horario_entrada`, `horario_triagem`, " +
                "`horario_checkout`, `horario_finalizado`, `operador_id`, `entregador_id`, " +
                "`fonte_pedido`, `status`, `troco`, `caixa_responsavel`, `status_id`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date horario = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String horario_entrada = format.format(horario);
        System.out.println(dateFormat.format(date));
        System.out.println(horario_entrada);
        data_entrada = dateFormat.format(date);

        OrdemPedido pedido = new OrdemPedido(
                0,
                cliente_id,
                clienteNome,
                clienteEndereco,
                clienteTelefone,
                formaEntrega,
                formaPagamento,
                casoFalta,
                data_entrada,
                horario_entrada,
                "",
                "",
                "",
                user.getId(),
                3,
                fonte,
                "Pendente",
                troco,
                "",
                1);

        System.out.println(pedido.getStatus_id());
        boolean state = db_crud.metodoInsertPedido(pedido, query);
        if(state == true){
            fecharJanela();
        }else{
            JFXDialog dialog = AlertDialogModel.alertDialogErro("Houve um problema ao tentar incluir pedido", stackPane);
            dialog.show();
        }
    }
    public void autoCompleteTextField(String search){
        String complete = "";
        String endereco = "";
        String numtel = "";

        int start = search.length();
        int last = search.length();

        for(int i = 0; i<clientes.size(); i++){
            if(clientes.get(i).getNome().toUpperCase().startsWith(search.toUpperCase())){
                complete = clientes.get(i).getNome().toUpperCase();
                endereco = clientes.get(i).getEndereco().toUpperCase();
                numtel = clientes.get(i).getTelefone().toUpperCase();
                last = complete.length();
                break;
            }
        }
        if(last>start){
            edtNome.setText(complete);
            edtNome.positionCaret(last);
            edtNome.selectPositionCaret(start);
            edtEndereco.setText(endereco);
            edtTel.setText(numtel);
        }
    }
    private void verificarClienteExistente() {
        for(int i = 0; i < clientes.size(); i++){
            int id_cliente = clientes.get(i).getId();
            String nomerecup = clientes.get(i).getNome().toUpperCase().trim();
            String nomeEditext = edtNome.getText().toUpperCase().trim();
            String telefoneEdit = edtTel.getText().toUpperCase().trim();
            String telefoneRecup = clientes.get(i).getTelefone().toUpperCase().trim();
            String enderecoEdit = edtEndereco.getText().toUpperCase().trim();
            String enderecoRecup = clientes.get(i).getEndereco().toUpperCase().trim();
            if(nomerecup.equals(nomeEditext)){
                cliente_id = clientes.get(i).getId();
                i = clientes.size() - 1;
                if(!(telefoneRecup.equals(telefoneEdit))){
                    alertDialogTelefone(id_cliente);
                }
                if(!(enderecoRecup.equals(enderecoEdit))){
                    alertDialogEndereco(id_cliente);
                }
            }else if(!(nomerecup.equals(nomeEditext))){
                if(i == clientes.size() - 1 && !(nomeEditext.equals(nomerecup))){
                    if(!(nomeEditext.equals(""))){
                        alertDialog();
                    }
                }
            }
        }
    }
    private void criarCliente() throws SQLException {
        String cliente_nome = edtNome.getText().toUpperCase().toUpperCase().trim();
        String cliente_endereco = edtEndereco.getText().toUpperCase().trim();
        String cliente_telefone = edtTel.getText().toUpperCase().trim();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
        Date data = new Date();
        String data_cadastro = format.format(data);
        if(cliente_nome.length() < 150 && cliente_endereco.length() < 150 && cliente_telefone.length() < 150){
            if(!(cliente_nome.equals("") && !(cliente_endereco.equals("")) && !(cliente_telefone.equals("")))){
                query = "INSERT INTO `Clientes`(`id`, `cliente_nome`, `cliente_endereco`, `cliente_telefone`, `data_cadastro`) VALUES (?,?,?,?,?)";
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, 0);
                preparedStatement.setString(2, cliente_nome);
                preparedStatement.setString(3, cliente_endereco);
                preparedStatement.setString(4, cliente_telefone);
                preparedStatement.setString(5, data_cadastro);
                int count = preparedStatement.executeUpdate();
                if(count > 0){
                    System.out.println("Cliente Criado com Sucesso");
                    recuperarClientes();
                    dialog.close();
                }else{
                    System.out.println("Houve um erro");
                    alertDialogErro("Houve um problema ao criar novo Cliente");
                }
            }else{
                System.out.println("Preencha todos os Campos");
            }
        }else{
            System.out.println("Os campos devem ter menos de 55 caracteres");
        }

    }
    //Metodos de Negocios

    //Objetos
    private void alertDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonSim = new JFXButton("Sim");
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        buttonSim.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            try {
                criarCliente();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialog.close();
        });
        dialogLayout.setBody(new Text("Deseja criar um novo Cliente?"));
        dialogLayout.setActions(buttonSim, buttonCancelar);
        dialog.show();
    }
    private void alertDialogTelefone(int id) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonSim = new JFXButton("Sim");
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        JFXDialog dialoginner = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        buttonSim.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            //
            String query = "UPDATE `Clientes` SET `cliente_telefone` =? WHERE `id` =?";
            boolean state = db_crud.metodoUpdateCliente(edtTel.getText().toUpperCase().trim(), query, id);
            if(state){
                dialoginner.close();
                try {
                    recuperarClientes();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }else{
                JFXDialog alert = AlertDialogModel.alertDialogErro("Houve um problema ao atualizar o cliente", stackPane);
                alert.show();
            }
            System.out.println("Atualizando telefone");
        });
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialoginner.close();
        });
        dialogLayout.setBody(new Text("Deseja alterar o numero de telefone?"));
        dialogLayout.setActions(buttonSim, buttonCancelar);
        dialoginner.show();
    }
    private void alertDialogEndereco(int cliente_id) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonSim = new JFXButton("Sim");
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        JFXDialog dialoginner = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        buttonSim.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            String query = "UPDATE `Clientes` SET `cliente_endereco` =? WHERE `id` =?";
            boolean state = db_crud.metodoUpdateCliente(edtEndereco.getText().toUpperCase().trim(), query, cliente_id);
            if(state){
                try {
                    recuperarClientes();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                dialoginner.close();
            }else{
                JFXDialog alert = AlertDialogModel.alertDialogErro("Houve um problema ao atualizar o cliente", stackPane);
                alert.show();
            }
            System.out.println("Atualizando endereco");
        });
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialoginner.close();
        });
        dialogLayout.setBody(new Text("Deseja alterar o endereço do cliente?"));
        dialogLayout.setActions(buttonSim, buttonCancelar);
        dialoginner.show();
    }


    private void alertDialogClientes() {
        selectedIndex = 0;
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        JFXButton buttonConfirmar = new JFXButton("Confirmar");
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialog.close();
        });
        buttonConfirmar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if(selectedIndex > 0){
                setClienteEDT(selected_Cliente);
                dialog.close();
            }else{
                System.out.println("Selecione um cliente");
            }
        });
        dialogLayout.setBody(
                borderPane()
        );
        dialogLayout.setActions(buttonConfirmar,buttonCancelar);
        dialog.show();
        tableView.setItems(clientes);
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cliente>() {
            @Override
            public void changed(ObservableValue<? extends Cliente> observable, Cliente oldValue, Cliente newValue) {
                if(tableView.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = tableView.getSelectionModel();
                    Cliente cliente = (Cliente) tableView.getSelectionModel().getSelectedItem();
                    selectedIndex = cliente.getId();
                    selected_Cliente = cliente;
                    System.out.println(selectedIndex);
                }
            }
        });
    }
    public BorderPane borderPane(){
        border = new BorderPane();
        edtSearch = new JFXTextField();
        edtSearch.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                pesquisarCliente();
            }
        });
        edtSearch.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        edtSearch.setStyle("-fx-background-color: #d3d3d3");
        border.setTop(edtSearch);
        border.setCenter(tableView());
        return border;
    }
    public TableView tableView(){
        tableView = new TableView<Cliente>();
        TableColumn nomeCol = new TableColumn<Cliente, String>();
        nomeCol.setSortType(TableColumn.SortType.ASCENDING);
        nomeCol.setText("NOME");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn telCol = new TableColumn<Cliente, String>();
        telCol.setText("TELEFONE");
        telCol.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        TableColumn endCol = new TableColumn<Cliente, String>();
        endCol.setText("ENDEREÇO");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        tableView.getColumns().addAll(nomeCol, endCol, telCol);
        tableView.getSortOrder().add(nomeCol);
        return tableView;
    }
    private void alertDialogErro(String erro) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonCancelar = new JFXButton("OK");
        JFXDialog dialogErro = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        Text texto = new Text();
        texto.setText(erro);
        texto.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 20));
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialogErro.close();
        });
        dialogLayout.setBody(texto);
        dialogLayout.setActions(buttonCancelar);
        dialogErro.show();
    }
    //Objetos

    //Metodos de Controle
    private void setClienteEDT(Cliente cliente){
        edtNome.setText(cliente.getNome());
        edtEndereco.setText(cliente.getEndereco());
        edtTel.setText(cliente.getTelefone());
        cliente_id = cliente.getId();
    }
    private void fecharJanela() {
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage.close();
    }
    //Metodos de Controle
}

