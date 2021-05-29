package sample;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.*;
import helpers.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.OrdemPedido;
import models.Produto;
import models.ProdutoPedido;
import models.Usuario;

import java.awt.print.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static helpers.DataManagerAnalytcs.createEstatistica;

public class  detalhesPedidoController implements Initializable {

    @FXML
    private JFXButton btnSair;

    @FXML
    private JFXButton btnImprimir;

    @FXML
    private JFXButton btnAlterarPolimorf;

    @FXML
    private JFXButton btnAtualizarLista;

    @FXML
    private TableView<ProdutoPedido> tableProdutos;

    @FXML
    private TableColumn<ProdutoPedido, String> nomeCol;

    @FXML
    private TableColumn<ProdutoPedido, Integer> qtdCol;

    @FXML
    private TextField edtNomeProduto;

    @FXML
    private TextField edtQTD;

    @FXML
    private Text textNome;

    @FXML
    private Text textEndereco;

    @FXML
    private Text textTelefone;

    @FXML
    private Text textData_Entrada;

    @FXML
    private Text textPagamento;

    @FXML
    private Text textEnvio;

    @FXML
    private JFXButton btnADD;

    private  JFXDialog dialog;

    private BorderPane border;

    private JFXTextField edtNome;

    @FXML
    private StackPane stackPane;

    String pedido_id;
    int table_index;
    String prod_id, prod_nome;
    Produto produto;
    OrdemPedido pedido;
    int selectedProduto;
    boolean isSelected = false;
    String horario_atual;
    double bHeight;
    Usuario user;

    ArrayList<Produto> produtos = new ArrayList();
    ObservableList<ProdutoPedido> produtosPedido = FXCollections.observableArrayList();


    String query = null;
    String pedido_Produto_query = null;
    Connection connection = null;
    PreparedStatement preparedStatementProduto = null;
    PreparedStatement preparedStatementOrdem = null;

    PreparedStatement preparedStatementPedidoProduto = null;

    ResultSet resultSet = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recuperarUsuario();
        try {
            recuperarIntentProdUid();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try {
            recuperarProdutosPedido();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        setupComponentes();

    }

    private void recuperarUsuario() {
        user = AuthenticationSystem.getUser();
    }

    private void setupEdt() {
        ArrayList<String> nomes = new ArrayList<>();
        for(int i = 0; i<produtos.size(); i++){
            nomes.add(produtos.get(i).getNome());
        }

        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setPrefWidth(500);
        autoCompletePopup.getSuggestions().addAll(nomes);

        autoCompletePopup.setSelectionHandler(event -> {
            edtNomeProduto.setText(event.getObject());
            edtQTD.requestFocus();
            // you can do other actions here when text completed
        });

        // filtering options
        edtNomeProduto.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string -> string.toLowerCase().contains(edtNomeProduto.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() || edtNomeProduto.getText().isEmpty()) {
                autoCompletePopup.hide();
                // if you remove textField.getText.isEmpty() when text field is empty it suggests all options
                // so you can choose
            } else {
                autoCompletePopup.show(edtNomeProduto);
            }
        });
    }

    //Metodos Iniciais
    private void setupComponentes() {
        setupEdt();
        edtQTD.setText("1");
       /*
        edtNomeProduto.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case BACK_SPACE:
                    break;
                case ENTER:
                    edtNomeProduto.setText(edtNomeProduto.getText().toUpperCase());
                    edtQTD.requestFocus();
                    break;
                case SHIFT:
                    break;
                default:
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            String txt = edtNomeProduto.getText().toUpperCase();
                            autoCompleteTextField(txt);
                        }
                    });
            }
        });



        */
        edtNomeProduto.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        edtQTD.setOnKeyPressed((e) ->{
            switch (e.getCode()){
                case ENTER:
                    btnADD.requestFocus();
                    break;
            }
        });
        btnADD.setOnKeyPressed((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                switch (e.getCode()){
                    case ENTER:
                        salvarProduto();
                        break;
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }

        });
        switch (table_index){
            case 1:
                btnAlterarPolimorf.setVisible(true);
                break;
            case 2:
                btnAlterarPolimorf.setVisible(true);
                break;
            case 3:
                btnAlterarPolimorf.setVisible(false);
                break;

        }
        btnAlterarPolimorf.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                if(table_index != 3){
                    alterarStatusPedido();
                }else{
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Não é permitadas, alterações no pedido finalizado", stackPane);
                    dialog.show();
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });
        btnADD.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                if(table_index != 3){
                    salvarProduto();
                }else{
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Não é permitidas alterações, no pedido finalizado", stackPane);
                    dialog.show();
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });
        btnSair.setOnAction((e) -> {
            fecharJanela();
        });
        btnAtualizarLista.setOnAction((e) -> {
            try {
                recuperarProdutosPedido();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
        btnImprimir.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                File file = DefaultComponents.fileChooserSave(stackPane, "PDF files (*.pdf)", "*.pdf");
                if(file != null){
                    criarPDF(file);
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });
        tableProdutos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProdutoPedido>() {
            @Override
            public void changed(ObservableValue<? extends ProdutoPedido> observable, ProdutoPedido oldValue, ProdutoPedido newValue) {
                if(tableProdutos.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = tableProdutos.getSelectionModel();
                    ProdutoPedido produto = tableProdutos.getSelectionModel().getSelectedItem();
                    selectedProduto = produto.getIndex();
                    isSelected = true;
                }
            }
        });
        tableProdutos.setOnKeyPressed((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                switch (e.getCode()) {
                    case DELETE:
                        if(isSelected == true){
                            if(table_index != 3){
                                removerProduto();
                            }else{
                                JFXDialog dialog = AlertDialogModel.alertDialogErro("Não é permitida alterações no pedido finalizado", stackPane);
                                dialog.show();
                            }
                        }else{

                        }
                        break;
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });

    }

    private void removerProduto() {
        query = "DELETE FROM `Pedido_Produto` WHERE `produto_index` =? AND `pedido_index` =?";
        boolean state = false;
            try {
                connection = db_connect.getConnect();
                preparedStatementProduto = connection.prepareStatement(query);
                preparedStatementProduto.setInt(1, selectedProduto);
                preparedStatementProduto.setInt(2, Integer.parseInt(pedido_id));

                int count = preparedStatementProduto.executeUpdate();
                if(count > 0){
                    state = true;
                }else{
                    state = false;
                }
            }catch (SQLException e){
                Logger.getLogger(clientesController.class.getName()).log(Level.SEVERE, null, e);
            }
        if(state == true){
            try {
                restartAdd();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }else{

        }
    }

    private void criarPDF(File file) {
        Document document = new Document();
        int size = produtosPedido.size();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
            document.open();

            // adicionando um parágrafo no documento
            document.add(new Paragraph("=============================="));
            document.add(new Paragraph("                              "));
            document.add(new Paragraph("        Gerenciador de Pedidos    "));
            document.add(new Paragraph("              SunOnRails          "));
            document.add(new Paragraph("                              "));
            document.add(new Paragraph("=========Dados do Cliente========"));
            document.add(new Paragraph("-Nome:        " + pedido.getCliente_nome()));
            document.add(new Paragraph("-Endereço:    " + pedido.getEnd_cliente()));
            document.add(new Paragraph("-Telefone:    " + pedido.getNum_cliente()));
            document.add(new Paragraph("-F. de Pag.:  " + pedido.getForma_pagamento()));
            document.add(new Paragraph("-F. de Envio: " + pedido.getForma_envio()));
            document.add(new Paragraph("-D. Entrada:    " + pedido.getData_entrada()));
            document.add(new Paragraph("-H. Triagem:    " + pedido.getHorario_triagem()));
            document.add(new Paragraph("=============================="));
            document.add(new Paragraph("  Qtd.                   Item no.   "));
            document.add(new Paragraph("=============================="));
            for(int i = 0; i < size; i++){
            document.add(new Paragraph(" " + produtosPedido.get(i).getQtd() +" || " + produtosPedido.get(i).getNome()));
            }
            document.add(new Paragraph("=============================="));
            document.add(new Paragraph("-Operador: " + pedido.getOperador_id()));
            document.add(new Paragraph("-Entregador: " + pedido.getEntregador_id()));
            document.add(new Paragraph("-F. Pedido: " + pedido.getFonte_pedido()));
            document.add(new Paragraph("=============================="));

        }catch (DocumentException | FileNotFoundException de) {
            de.printStackTrace();
        }
        document.close();
    }

    private void recuperarIntentProdUid() throws SQLException {
        String id;
        int table;
        intentData intent = intentData.getINSTANCE();
        id = intent.getDadosteste();
        table = intent.getTableIndex();
        pedido_id = id;
        table_index = table;
        System.out.println("Dado recebido: " + pedido_id);

        recuperarProdutos();
        recuperarDadosPedido();
    }
    private void recuperarProdutos() throws SQLException {
        produtos.clear();
        query = "SELECT * FROM `Produto`";
        connection = db_connect.getConnect();
        preparedStatementProduto = connection.prepareStatement(query);
        resultSet = preparedStatementProduto.executeQuery();
        while (resultSet.next()) {
            produtos.add(new Produto(
                            resultSet.getInt("id"),
                            resultSet.getString("nome_produto")
                    )
            );
            System.out.println("Produto recuperado: " + resultSet.getString("nome_produto"));
        }
    }
    private void recuperarProdutosPedido() throws SQLException {
        produtosPedido.clear();
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        qtdCol.setCellValueFactory(new PropertyValueFactory<>("qtd"));
        //
        String query2 = "SELECT * FROM `Produto` WHERE id = ?";
        Connection connection2 = db_connect.getConnect();
        PreparedStatement preparedStatement2 = connection2.prepareStatement(query2);
        //
        query = "SELECT * FROM `Pedido_Produto` WHERE pedido_index = ?";
        connection = db_connect.getConnect();
        preparedStatementProduto = connection.prepareStatement(query);
        preparedStatementProduto.setInt(1,Integer.parseInt(pedido_id));
        resultSet = preparedStatementProduto.executeQuery();
        while (resultSet.next()) {
            preparedStatement2.setInt(1, resultSet.getInt("produto_index"));
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            while (resultSet2.next()){
                produtosPedido.add(new ProdutoPedido(
                                resultSet2.getInt("id"),
                                resultSet2.getString("nome_produto"),
                                resultSet.getInt("quantidade")
                        )
                );
            }
            System.out.println("Produto do Pedido: " + resultSet.getString("produto_nome") + " Quantidade: " + resultSet.getInt("quantidade"));
        }
        tableProdutos.setItems(produtosPedido);
    }
    private void recuperarProdutoCriado(String nome) throws SQLException {
        query = "SELECT * FROM `Produto` WHERE nome_produto = ?";
        connection = db_connect.getConnect();
        preparedStatementProduto = connection.prepareStatement(query);
        preparedStatementProduto.setString(1, nome);
        resultSet = preparedStatementProduto.executeQuery();
        while (resultSet.next()) {
            String nome_produto = resultSet.getString("nome_produto");
            int id = resultSet.getInt("id");
            produto = new Produto(id, nome_produto);
            System.out.println("Produto criado com ID: " + produto.getId() + "com Nome: " + produto.getNome());
        }
        addproduto_pedido(produto.getId(), produto.getNome());
    }
    private void recuperarDadosPedido() throws SQLException {

        query = "SELECT * FROM `Pedidos` WHERE `id` =? AND `status_id` =?";

        connection = db_connect.getConnect();
            preparedStatementOrdem = connection.prepareStatement(query);
            preparedStatementOrdem.setInt(1, Integer.parseInt(pedido_id));
            preparedStatementOrdem.setInt(2, table_index);

        resultSet = preparedStatementOrdem.executeQuery();
            while (resultSet.next()){
                pedido = new OrdemPedido(
                        resultSet.getInt("id"),
                        resultSet.getInt("cliente_id"),
                        resultSet.getString("cliente_nome"),
                        resultSet.getString("cliente_endereco"),
                        resultSet.getString("cliente_telefone"),
                        resultSet.getString("forma_envio"),
                        resultSet.getString("forma_pagamento"),
                        resultSet.getString("forma_subst"),
                        resultSet.getString("data_entrada"),
                        resultSet.getString("horario_entrada"),
                        resultSet.getString("horario_triagem"),
                        resultSet.getString("horario_checkout"),
                        resultSet.getString("horario_finalizado"),
                        resultSet.getInt("operador_id"),
                        resultSet.getInt("entregador_id"),
                        resultSet.getString("fonte_pedido"),
                        resultSet.getString("status"),
                        resultSet.getDouble("troco" ),
                        resultSet.getString("caixa_responsavel"),
                        resultSet.getInt("status_id")
                );

                System.out.println(pedido.getCliente_nome());

        }
        setarDados();
    }
    //Metodos Iniciais

    //Metodos de Negocios
    private void addproduto_pedido(int id, String nome) {
        int quantidade = Integer.parseInt(edtQTD.getText());
        System.out.println("Produto de ID: " + id + "Nome: " + nome + " sendo adicionado ao pedido...");
        pedido_Produto_query = "INSERT INTO `Pedido_Produto`(`id`, `pedido_index`, `produto_index`,`produto_nome`,  `quantidade`) VALUES (?,?,?,?,?)";
        try {
            preparedStatementPedidoProduto = connection.prepareStatement(pedido_Produto_query);
            preparedStatementPedidoProduto.setInt(1, 0);
            preparedStatementPedidoProduto.setInt(2, Integer.parseInt(pedido_id)); // pedido index
            preparedStatementPedidoProduto.setInt(3, id); // produto index
            preparedStatementPedidoProduto.setString(4, nome); // produto nome
            preparedStatementPedidoProduto.setInt(5, quantidade); // quantidade
            int count = preparedStatementPedidoProduto.executeUpdate();
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
    private void salvarProduto(){
        connection = db_connect.getConnect();

        prod_nome = edtNomeProduto.getText().toUpperCase().trim();

        if(prod_nome.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os Dados!");
            alert.showAndWait();
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
                        insertProduto();
                    }
                }
            }

        }
    }
    private void insertProduto() {
        boolean state = db_crud.insertProduto(Integer.parseInt("0"), prod_nome);
        if(state == true){
            try {
                recuperarProdutoCriado(prod_nome);
                restartAdd();
            }catch (SQLException ex){
                ex.printStackTrace();
            }

        }

    }
    private void alertDialogProdutos(int id, String query) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        JFXButton buttonConfirmar = new JFXButton("Salvar");
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialog.close();
        });
        buttonConfirmar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            String nome_caixa = edtNome.getText().toUpperCase().trim();
            if(!(nome_caixa.equals(""))){
                boolean state = db_crud.metodoAtualizarPedido(id, nome_caixa,query);
                if(state == true){
                    fecharJanela();
                }
            }else{
                //alertDialogAlert();
            }
        });
        dialogLayout.setPrefSize(250, 150);
        dialogLayout.setBody(
                borderPaneNovoProduto()
        );
        dialogLayout.setActions(buttonConfirmar,buttonCancelar);
        dialog.show();
    }

    public BorderPane borderPaneNovoProduto(){
        border = new BorderPane();
        edtNome = new JFXTextField();
        edtNome.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtNome.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-border-color: black");
        Text text = new Text("Favor informar o nome do Caixa Responsavel");
        text.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 15));

        VBox vbox = new VBox();
        vbox.getChildren().addAll(text ,edtNome);
        border.setCenter(vbox);
        return border;
    }

    private void alterarStatusPedido() {
        int count;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        horario_atual = dateFormat.format(date);

        OrdemPedido pedidoAtualizado = new OrdemPedido(
                pedido.getId(), pedido.getCliente_id(), pedido.getCliente_nome(), pedido.getEnd_cliente(), pedido.getNum_cliente(),
                pedido.getForma_envio(), pedido.getForma_pagamento(), pedido.getForma_subst(), pedido.getData_entrada(),
                pedido.getHorario_entrada(), horario_atual, pedido.getHorario_checkout(),
                pedido.getHorario_finalizado(), pedido.getOperador_id(), pedido.getEntregador_id(), pedido.getFonte_pedido(),
                pedido.getStatus(), pedido.getTroco(), pedido.getCaixa_responsavel(), pedido.getStatus_id()
        );

        if (table_index == 1) {
            System.out.println("Iniciando INSERT Pedido Triagem");
            query = "UPDATE `Pedidos` SET `status_id` = ? WHERE `id` =? ";
            boolean state ;
            state = db_crud.metodoAtualizarPedido(Integer.parseInt(pedido_id), "2", query);
            if (state == true) {
                query = "UPDATE `Pedidos` SET `horario_triagem` = ? WHERE `id` =? ";
                boolean state2 ;
                state2 = db_crud.metodoAtualizarPedido(Integer.parseInt(pedido_id), horario_atual, query);
                if (state2 == true) {
                    fecharJanela();
                    //fechar dialog
                }
            } else {
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Houve um problema modificar o pedido", stackPane);
                dialog.show();
            }

        } else if (table_index == 2) {
            System.out.println("Iniciando UPDATE Pedido horario checkout");
            if (pedido.getHorario_checkout().equals("")) {
                query = "UPDATE `Pedidos` SET `horario_checkout`=? WHERE `id` =?";
                boolean state = db_crud.metodoAtualizarPedido(pedido.getId(), horario_atual, query);
                if (state == true) {
                    query = "UPDATE `Pedidos` SET `status` =? WHERE `id` =?";
                    boolean state2 = db_crud.metodoAtualizarPedido(pedido.getId(), "Saiu para Entrega", query);
                    if (state2 == true) {
                        query = "UPDATE `Pedidos` SET `caixa_responsavel` =? WHERE `id` =?";
                        alertDialogProdutos(pedido.getId(), query);
                    }

                } else {
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Houve um problema ao tentar atualizar o pedido", stackPane);
                    dialog.show();
                }
            } else {
                if (pedido.getHorario_checkout().equals("") == false) {
                    System.out.println("Iniciando INSERT nos finalizados");
                    query = "UPDATE `Pedidos` SET `status_id` = ? WHERE `id` =? ";
                    boolean state = db_crud.metodoAtualizarPedido(Integer.parseInt(pedido_id), "3", query);
                    if (state == true) {
                        query = "UPDATE `Pedidos` SET `status` = ? WHERE `id` =? ";
                        boolean state2 = db_crud.metodoAtualizarPedido(Integer.parseInt(pedido_id), "Finalizado", query);
                        if (state2 == true) {
                            query = "UPDATE `Pedidos` SET `horario_finalizado` = ? WHERE `id` =? ";
                            boolean state3 ;
                            state3 = db_crud.metodoAtualizarPedido(Integer.parseInt(pedido_id), horario_atual, query);
                            if (state3 == true) {
                                fecharJanela();
                                //fechar dialog
                            }
                        }
                    } else {
                        JFXDialog dialog = AlertDialogModel.alertDialogErro("Houve um problema ao tentar atualizar o pedido", stackPane);
                        dialog.show();
                    }
                    query = "INSERT INTO `Pedido_Estatisticas`" +
                            "(`id_static`,`id`, `m.t`, `m.e`, `h.p`, `data`) " +
                            "VALUES (?,?,?,?,?,?)";
                    System.out.println("Criando model estatistica com os dados:" + "ID: " + pedido.getId() + "Horario Triagem: " + pedido.getHorario_triagem() + "Horario Checkout: " + pedido.getHorario_checkout() + "Horario Finalizado: " + horario_atual + "Horario Entrada: " +  pedido.getHorario_entrada());
                    PedidoEstatistica estatistica = createEstatistica(pedido.getId(), pedido.getHorario_triagem(), pedido.getHorario_checkout(), horario_atual, pedido.getHorario_entrada());
                    boolean statistics = db_crud.metodoInsertEstatistica(estatistica, pedido.getData_entrada(), query);
                    if(statistics == true){
                        System.out.println("Sucesso ao gerar estatistica do pedido");
                    }else{
                        System.out.println("Houve um problema ao gerar estatistica do pedido");
                    }
                }
            }

        }
    }
    private void iniciarExclusaoPedidoEntrada(int id, int table) throws SQLException {
        if(table == 1){
            query = "DELETE FROM `Ordem_De_Pedido` WHERE id=?";
        }else if(table == 2){
            query = "DELETE FROM `Ordem_De_Pedido_Triagem` WHERE id=?";
        }
        connection = db_connect.getConnect();
        preparedStatementOrdem = connection.prepareStatement(query);
        preparedStatementOrdem.setInt(1, id);

        int rowsDeleted = preparedStatementOrdem.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Deletado com sucesso!");
            fecharJanela();
        }else{
            System.out.println("Problema ao Deletar");
        }
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
            edtNomeProduto.setText(complete);
            edtNomeProduto.positionCaret(last);
            edtNomeProduto.selectPositionCaret(start);
        }



    }
    //Metodos de Negocios

    //Metodos de Controle
    private void restartAdd() throws SQLException {
        System.out.println("Reiniciando Componentes");
        edtNomeProduto.clear();
        edtQTD.clear();
        edtQTD.setText("1");
        edtNomeProduto.requestFocus();
        recuperarProdutos();
        recuperarProdutosPedido();
    }
    private void setarDados() {
        textNome.setText(pedido.getCliente_nome());
        textEndereco.setText(pedido.getEnd_cliente());
        textEnvio.setText(pedido.getForma_envio());
        textPagamento.setText(pedido.getForma_pagamento());
        textTelefone.setText(pedido.getNum_cliente());
        textData_Entrada.setText(pedido.getData_entrada());

    }
    private void fecharJanela(){
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage.close();
    }

    //Metodos de Controle



}
