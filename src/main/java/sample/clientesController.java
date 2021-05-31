package sample;

import com.jfoenix.controls.*;
import helpers.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;
import jxl.write.Label;
import models.Cliente;
import models.OrdemPedido;
import models.Usuario;

import java.io.File;
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
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class clientesController implements Initializable {

        @FXML
        private StackPane stackPane;

        @FXML
        private TableView<Cliente> tableView;

        @FXML
        private TableColumn<Cliente, Integer> idCol;

        @FXML
        private TableColumn<Cliente, String> nomeCol;

        @FXML
        private TableColumn<Cliente, String> endCol;

        @FXML
        private TableColumn<Cliente, String> telCol;


        @FXML
        private JFXButton btnEditar;

        @FXML
        private JFXButton btnExcluir;

        @FXML
        private JFXButton btnDetalhes;

        @FXML
        private JFXButton btnPrintClientes;

        @FXML
        private JFXTextField edtSearch;

        @FXML
        private JFXButton btnSearch;

        @FXML
        private JFXButton btnImport;

        private JFXDatePicker datePickerInicial;
        private JFXDatePicker datePickerFinal;
        private JFXComboBox comboBoxFiltroStatus;
        TableView<OrdemPedido> tableViewPedidos;
        FilteredList<Cliente> filteredData;


        ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
        ObservableList<String> listaComboBox = FXCollections.observableArrayList(
          "Pedidos Pendentes","Pedidos em Triagem","Pedidos Finalizados", "Todos"
        );
        ObservableList<String> listaQuerys = FXCollections.observableArrayList(
                "Ordem_De_Pedido","Ordem_De_Pedido_Triagem","Ordem_De_Pedido_Finalizado"
        );
        String dataInicial = "";
        String dataFinal = "";
        ObservableList<OrdemPedido> listaPedidos = FXCollections.observableArrayList();
        ObservableList<OrdemPedido> listaPedidosTriagem = FXCollections.observableArrayList();
        ObservableList<OrdemPedido> listaPedidosFinalizados = FXCollections.observableArrayList();
        ObservableList<OrdemPedido> listaPedidosTodos = FXCollections.observableArrayList();

        String query = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Cliente cliente = null;
        boolean selectedCliente = false;
        int cb_selectedIndex = 0;
        Usuario user;

        JFXDialog dialog;
        @Override
        public void initialize(URL location, ResourceBundle resources) {
                recuperarUsuario();
                setupComponentes();
                prepararTableView();
        }



    //metodos iniciais
            private void recuperarUsuario() {
                    user = AuthenticationSystem.getUser();
            }
            private void setupComponentes(){
                  btnImport.setOnAction((e) -> {
                      intentImport();
                  });
                  btnExcluir.setOnAction((e) -> {
                      if(UserPrivilegiesVerify.permissaoVerBotao(user, 3) == true){
                          alertDialogExclusao();
                      }else{
                          JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                          dialog.show();
                      }
                  });
                  btnEditar.setOnAction((e) ->{
                      if(selectedCliente == true){
                          try {
                              alertDialogClientes();
                          } catch (IOException exception) {
                              exception.printStackTrace();
                          }
                      }else if(selectedCliente == false){
                          System.out.println("Selecione um cliente");
                          JFXDialog dialogErro = AlertDialogModel.alertDialogErro("Selecione um Cliente",stackPane);
                          dialogErro.show();
                      }
                  });
                  btnDetalhes.setOnAction((e) -> {
                  if(selectedCliente == true){
                      try {
                          alertDialogDetalhes();
                          comboBoxFiltroStatus.getSelectionModel().select(0);
                          comboBoxFiltroStatus.valueProperty().addListener(new ChangeListener() {
                              @Override
                              public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                  verificarComboBox();
                              }
                          });
                      } catch (IOException exception) {
                          exception.printStackTrace();
                      }
                  }else if(selectedCliente == false){
                      System.out.println("Selecione um cliente");
                      JFXDialog dialogErro = AlertDialogModel.alertDialogErro("Selecione um Cliente",stackPane);
                      dialogErro.show();
                  }


              });
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

                  btnPrintClientes.setOnAction((e) ->{
                      if(UserPrivilegiesVerify.permissaoVerBotao(user, 3) == true){
                          File file = DefaultComponents.fileChooserSave(stackPane, "DOCUMENTO EXCEL (*.XLS)", "*.xls");
                          gerarDocumentoXLS(file);
                      }
                  });

          }
            private void prepararTableView(){
                        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
                        endCol.setCellValueFactory(new PropertyValueFactory<>("endereco"));
                        telCol.setCellValueFactory(new PropertyValueFactory<>("telefone"));
                        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cliente>() {
                            @Override
                            public void changed(ObservableValue<? extends Cliente> observable, Cliente oldValue, Cliente newValue) {
                                if(tableView.getSelectionModel().getSelectedItem() != null){
                                    TableView.TableViewSelectionModel selectionModel = tableView.getSelectionModel();
                                    cliente = (Cliente) tableView.getSelectionModel().getSelectedItem();
                                    selectedCliente = true;
                                    System.out.println(cliente.getNome());
                                }
                            }
                        });
                        try {
                                recuperarClientes();
                        } catch (SQLException exception) {
                                exception.printStackTrace();
                        }
                }
        //metodos iniciais


        //metodos de negocios
            private void iniciarDetalhesScreen(OrdemPedido pedido) {
            System.out.println("Iniciando If Statment");
            intentData intent = intentData.getINSTANCE();
            int selectedTable = 0;
            System.out.println("Pedido de status: " + pedido.getStatus()
                    + " Horario de triagem: "
                    + pedido.getHorario_entrada()
                    + " Horario checkout: "
                    + pedido.getHorario_triagem()
                    + " Horario de finalizaçao: "
                    + pedido.getHorario_finalizado());

            if(pedido.getStatus().equals("Pendente") && pedido.getHorario_triagem().equals("")){
                //table 1
                selectedTable = 1;
                System.out.println("Pedido na tabela 1");
            }else if(pedido.getStatus().equals("Pendente") && !(pedido.getHorario_triagem().equals(""))){
                //table 2
                selectedTable = 2;
                System.out.println("Pedido na tabela 2");
            }else if(pedido.getStatus().equals("Saiu para entrega")){
                //table 2
                selectedTable = 2;
                System.out.println("Pedido na tabela 2");
            }else if(pedido.getStatus().equals("Finalizado")){
                //table 3
                selectedTable = 3;
                System.out.println("Pedido na tabela 3");
            }

            intent.setTableIndex(selectedTable);
            intent.setDadosteste(String.valueOf(pedido.getId()));
            if(selectedTable != 0 && pedido.getId() != 0){
                try {
                    Parent parent = FXMLLoader.load(getClass().getResource("/detalhesPedido.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initStyle(StageStyle.UTILITY);
                    stage.show();
                } catch (IOException exception){
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
                }
            }



        }
            private void alertDialogExclusao() {
            JFXButton btnExcluir = defaultButton("CONFIRMAR");
            JFXButton btnCancelar = defaultButton("CANCELAR");
            JFXDialog dialogExclusao = ExcluirDialogModel.alertDialogErro("TEM CERTEZA QUE DESEJA EXCLUIR?", stackPane, btnExcluir, btnCancelar);
            dialogExclusao.show();
            btnCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                dialogExclusao.close();
            });
            btnExcluir.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                if(excluirCliente() == true){
                    dialogExclusao.close();
                    refreshTable();
                }else{
                    dialogExclusao.close();
                    JFXDialog dialogErro = AlertDialogModel.alertDialogErro("Houve um problema na exclusão", stackPane);
                    dialogErro.show();
                }
            });
        }
            private boolean excluirCliente() {
        boolean state = false;
        Cliente backupCliente = cliente;
        try {
            query = "DELETE FROM `Clientes` WHERE `id` =?";
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, cliente.getId());
            int count = preparedStatement.executeUpdate();
            if(count > 0){
                int contagem = excluirPedidosCliente();
                if(contagem == 4){
                    state = true;
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Pedidos Excluidos",stackPane);
                    dialog.show();
                }
                if(contagem > 0){
                    state = true;
                }else if(contagem == 0) {
                    state = false;
                    retornarBackupCliente(backupCliente);
                }
            }else{
                state = false;
            }
        }catch (SQLException e){
            Logger.getLogger(clientesController.class.getName()).log(Level.SEVERE, null, e);
        }
        return state;
    }
            private int excluirPedidosCliente(){
        int countTables = 3;
        int count = 0;
        for(int i = 0; i<countTables; i++){
            switch (i){
                case 0:
                    query = "DELETE FROM `Ordem_De_Pedido` WHERE `cliente_id` =?";
                    if(db_crud.metodoExlusao(query,cliente.getId()) == true){
                        count++;
                    }else{
                        count = 0;
                    }
                    break;
                case 1:
                    query = "DELETE FROM `Ordem_De_Pedido_Triagem` WHERE `cliente_id` =?";
                    if(db_crud.metodoExlusao(query,cliente.getId()) == true){
                        count++;
                    }else{
                        count = 0;
                    }
                    break;
                case 2:
                    query = "DELETE FROM `Ordem_De_Pedido_Finalizado` WHERE `cliente_id` =?";
                    if(db_crud.metodoExlusao(query,cliente.getId()) == true){
                        count++;
                    }else {
                        count = 4;
                    }
                    break;
            }
        }
        return count;
    }
            public void gerarDocumentoXLS(File file){
            System.out.println("Iniciando exportação");
            //Busca todos os itens da tabela
            ObservableList<Cliente> listaTabela = FXCollections.observableArrayList();
            int tableSize = tableView.getItems().size();
            for(int a = 0; a<tableSize; a++){
                System.out.println("Busca: " + a);
                System.out.println(tableSize);
                listaTabela.add(tableView.getItems().get(a));
            }
            //
            try{
                WritableWorkbook planilha = Workbook.createWorkbook(file);
                // Adcionando o nome da aba
                WritableSheet aba = planilha.createSheet("Lista de Produtos", 0);
                //Cabeçalhos
                String cabecalho[] = new String[9];
                cabecalho[0] = "ID:";
                cabecalho[1] = "Nome do Cliente:";
                cabecalho[2] = "Endereco do Cliente:";
                cabecalho[3] = "Telefone do Cliente:";
                cabecalho[4] = "Data de Cadastro:";


                // Cor de fundo das celular
                Colour bckColor = Colour.DARK_BLUE2;
                WritableCellFormat cellFormat = new WritableCellFormat();
                cellFormat.setBackground(bckColor);
                // Cor e tipo de Fonte
                WritableFont fonte = new WritableFont(WritableFont.ARIAL);
                fonte.setColour(Colour.GOLD);
                cellFormat.setFont(fonte);

                // escrever o Header para o xls
                for(int i =0; i< cabecalho.length; i++){
                    Label label = new Label(i, 0, cabecalho[i]);
                    aba.addCell(label);
                    WritableCell cell = aba.getWritableCell(i, 0);
                    cell.setCellFormat(cellFormat);
                }

                for (int linha = 1; linha <= listaTabela.size(); linha++) {
                    Label label = new Label(0, linha, String.valueOf(listaTabela.get(linha - 1).getId()));
                    aba.addCell(label);
                    label = new Label(1, linha, listaTabela.get(linha- 1).getNome());
                    //int count = DefaultComponents.countOfChar(listaTabela.get(linha).getCliente_nome());
                    aba.setColumnView(1, 55);
                    aba.addCell(label);
                    label = new Label(2, linha, listaTabela.get(linha- 1).getEndereco());
                    aba.setColumnView(2, 55);
                    aba.addCell(label);
                    label = new Label(3, linha, listaTabela.get(linha- 1).getTelefone());
                    aba.setColumnView(3, 10);
                    aba.addCell(label);
                    label = new Label(4, linha, listaTabela.get(linha- 1).getData_cadastro());
                    aba.setColumnView(4, 10);
                    aba.addCell(label);
                }

                planilha.write();
                //fecha o arquivo
                planilha.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("Fim");
        }
            public void gerarDocumentoPedidosXLS(File file){
        System.out.println("Iniciando exportação");
        //Busca todos os itens da tabela
        ObservableList<OrdemPedido> listaTabela = FXCollections.observableArrayList();
        int tableSize = tableViewPedidos.getItems().size();
        for(int a = 0; a<tableSize; a++){
            System.out.println("Busca: " + a);
            System.out.println(tableSize);
            listaTabela.add(tableViewPedidos.getItems().get(a));
        }
        //
        try{
            WritableWorkbook planilha = Workbook.createWorkbook(file);
            // Adcionando o nome da aba
            WritableSheet aba = planilha.createSheet("Lista de Produtos", 0);
            //Cabeçalhos
            String cabecalho[] = new String[9];
            cabecalho[0] = "ID:";
            cabecalho[1] = "Nome do Cliente:";
            cabecalho[2] = "Endereco do Cliente:";
            cabecalho[3] = "Telefone do Cliente:";
            cabecalho[4] = "Data de Entrada:";
            cabecalho[5] = "H. Entrada";
            cabecalho[6] = "H. Triagem";
            cabecalho[7] = "H. Checkout";
            cabecalho[8] = "H. Finaliz.";


            // Cor de fundo das celular
            Colour bckColor = Colour.DARK_BLUE2;
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setBackground(bckColor);
            // Cor e tipo de Fonte
            WritableFont fonte = new WritableFont(WritableFont.ARIAL);
            fonte.setColour(Colour.GOLD);
            cellFormat.setFont(fonte);

            // escrever o Header para o xls
            for(int i =0; i< cabecalho.length; i++){
                Label label = new Label(i, 0, cabecalho[i]);
                aba.addCell(label);
                WritableCell cell = aba.getWritableCell(i, 0);
                cell.setCellFormat(cellFormat);
            }

            for (int linha = 1; linha <= listaTabela.size(); linha++) {
                Label label = new Label(0, linha, String.valueOf(listaTabela.get(linha - 1).getId()));
                aba.addCell(label);
                label = new Label(1, linha, listaTabela.get(linha- 1).getCliente_nome());
                //int count = DefaultComponents.countOfChar(listaTabela.get(linha).getCliente_nome());
                aba.setColumnView(1, 35);
                aba.addCell(label);
                label = new Label(2, linha, listaTabela.get(linha- 1).getEnd_cliente());
                aba.setColumnView(2, 35);
                aba.addCell(label);
                label = new Label(3, linha, listaTabela.get(linha- 1).getNum_cliente());
                aba.setColumnView(3, 14);
                aba.addCell(label);
                label = new Label(4, linha, listaTabela.get(linha- 1).getData_entrada());
                aba.setColumnView(4, 10);
                aba.addCell(label);
                label = new Label(5, linha, listaTabela.get(linha- 1).getHorario_entrada());
                aba.addCell(label);
                label = new Label(6, linha, listaTabela.get(linha- 1).getHorario_triagem());
                aba.addCell(label);
                label = new Label(7, linha, listaTabela.get(linha- 1).getHorario_checkout());
                aba.addCell(label);
                label = new Label(8, linha, listaTabela.get(linha- 1).getHorario_finalizado());
                aba.addCell(label);
            }

            planilha.write();
            //fecha o arquivo
            planilha.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Fim");
    }
            private void pesquisarCliente(){
            filteredData = new FilteredList<>(listaClientes, b -> true);
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
            private void switchQuery() throws SQLException {
                switch (cb_selectedIndex){
                    case 0:
                        System.out.println("Buscando nos Pedidos de Entrada");
                        query = "SELECT * FROM `Pedidos` WHERE `data_entrada` >= ? AND `data_entrada` <= ? AND `cliente_id` =? AND `status_id` =?";
                        listaPedidos = recuperarPedidosPorData(query, "1");
                        tableViewPedidos.setItems(listaPedidos);
                        break;
                    case 1:
                        System.out.println("Buscando nos Pedidos em Triagem");
                        query = "SELECT * FROM `Pedidos` WHERE `data_entrada` >= ? AND `data_entrada` <= ? AND `cliente_id` =? AND `status_id` =?";
                        listaPedidosTriagem = recuperarPedidosPorData(query, "2");
                        tableViewPedidos.setItems(listaPedidosTriagem);
                        break;
                    case 2:
                        System.out.println("Buscando nos Pedidos Finalizados");
                        query = "SELECT * FROM `Pedidos` WHERE `data_entrada` >= ? AND `data_entrada` <= ? AND `cliente_id` =? AND `status_id` =?";
                        listaPedidosFinalizados = recuperarPedidosPorData(query, "3");
                        tableViewPedidos.setItems(listaPedidosFinalizados);
                        break;
                    case 3:
                        listaPedidosTodos.clear();
                        System.out.println("Buscando em todos os status");
                        query = "SELECT * FROM `Pedidos` WHERE `data_entrada` >= ? AND `data_entrada` <= ? AND `cliente_id` =?";
                        listaPedidosTodos = recuperarPedidosPorData(query, "");
                                    tableViewPedidos.setItems(listaPedidosTodos);
                                    break;
                }
            }
            private ObservableList<OrdemPedido> recuperarPedidosPorData(String query, String table) throws SQLException{
                ObservableList<OrdemPedido> lista = FXCollections.observableArrayList();
                lista.clear();
                System.out.println("Cliente: " + cliente.getNome() + " " + "ID: " + cliente.getId());
                System.out.println("Data Inicial de busca: " + dataInicial + "Data final de busca: " + dataFinal);
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                if(!(table.equals(""))) {
                    preparedStatement.setString(1, dataInicial);
                    preparedStatement.setString(2, dataFinal);
                    preparedStatement.setInt(3, cliente.getId());
                    preparedStatement.setString(4, table);
                }else if(table.equals("")){
                    preparedStatement.setString(1, dataInicial);
                    preparedStatement.setString(2, dataFinal);
                    preparedStatement.setInt(3, cliente.getId());
                }
                // preparedStatement.setString(2, dataFinal);
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    lista.add(new OrdemPedido(resultSet.getInt("id"),
                            resultSet.getInt("id"),
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
                            resultSet.getDouble("troco"),
                            resultSet.getString("caixa_responsavel"),
                            resultSet.getInt("status_id")
                    ));
                    System.out.println(resultSet.getString("cliente_nome"));
                }
                return lista;
            }
            private void recuperarClientes() throws SQLException {
                query = "SELECT * FROM `Clientes`";
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                        listaClientes.add(new Cliente(
                                resultSet.getInt("id"),
                                resultSet.getString("cliente_nome"),
                                resultSet.getString("cliente_endereco"),
                                resultSet.getString("cliente_telefone"),
                                resultSet.getString("data_cadastro"),
                                resultSet.getInt("qtdPedidos")
                                )
                        );
                        System.out.println("Clinte de ID: " + listaClientes.get(listaClientes.size() - 1).getId() + "|| Nome: " + listaClientes.get(listaClientes.size() - 1).getNome());
                }
                tableView.setItems(listaClientes);
            }
            private void alertDialogClientes() throws IOException {
                JFXDialogLayout dialogLayout = new JFXDialogLayout();
                dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
                dialogLayout.setBody(
                        formularioCliente()
                );
                dialog.show();
            }
            private void alertDialogDetalhes() throws IOException {
                JFXDialogLayout dialogLayout = new JFXDialogLayout();
                dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
                dialogLayout.setBody(
                        tablePedidosCliente()
                );
                dialog.show();
            }
            private boolean insertCliente(String query, String value, int id) throws SQLException {
            boolean updateState = false;
            if(query != null && value != null){
                System.out.println("Iniciando Update");
                System.out.println("Inserindo " + value + " no cliente de id: " + id + " com a query: " + query);
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, value);
                preparedStatement.setInt(2,id);
                int count = preparedStatement.executeUpdate();
                if(count > 0){
                    System.out.println("Atualização feita com sucesso");
                    refreshTable();
                    dialog.close();
                    updateState = true;
                }else{
                    JFXDialog dialogErro = AlertDialogModel.alertDialogErro("Houve um problema na atualização",stackPane);
                    dialogErro.show();
                    System.out.println("Houve um problema na atualização");
                    updateState = false;
                    }
                }
            return updateState;
            }
            private void insertClienteTodosCampos(String query, String nome, String telefone, String enderco, int id) throws SQLException {
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nome);
                preparedStatement.setString(2, enderco);
                preparedStatement.setString(3, telefone);
                preparedStatement.setInt(4, id);
                int count = preparedStatement.executeUpdate();
                if(count > 0){
                    System.out.println("Update feita com sucesso");
                    refreshTable();
                    dialog.close();
                }else{
                    System.out.println("Houve um problema na Update");
                    JFXDialog dialogerro = AlertDialogModel.alertDialogErro("Houve um Problema na atualizacao", stackPane);
                    dialogerro.show();
                }

            }
            private void verificarComboBox() {
            cb_selectedIndex = comboBoxFiltroStatus.getSelectionModel().getSelectedIndex();
            System.out.println("Index de busca: " + cb_selectedIndex);
        }
            private void retornarBackupCliente(Cliente bkpCliente) {
        try {
            query = "INSERT INTO `Clientes`(`id`, `cliente_nome`, `cliente_endereco`, `cliente_telefone`) VALUES (?,?,?,?)";
            connection = db_connect.getConnect();
            preparedStatement.setInt(1, bkpCliente.getId());
            preparedStatement.setString(2, bkpCliente.getNome());
            preparedStatement.setString(3, bkpCliente.getEndereco());
            preparedStatement.setString(4, bkpCliente.getTelefone());
        }catch (SQLException e){
            Logger.getLogger(clientesController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    //metodos de negocios

        //objetos
            public AnchorPane formularioCliente(){
                AnchorPane pane = new AnchorPane();
                VBox vboxPrincipal = defaultVBox();
                JFXButton btnSalvar = defaultButton("SALVAR ALTERAÇÕES");
                JFXButton btnCancelar = defaultButton("CANCELAR");
                JFXTextField edtNome = textFieldPadrao(400);

                JFXTextField edtTelefone = textFieldPadrao(150);
                JFXTextField edtEndereco = textFieldPadrao(550);

                HBox row1 = defaultHBox();
                HBox row2 = defaultHBox();
                HBox row3 = defaultHBox();

                VBox R1C1 = defaultVBox();
                VBox R1C2 = defaultVBox();
                VBox R2C1 = defaultVBox();

                R1C1.getChildren().addAll(
                        defaultText("NOME"),
                        edtNome
                );
                R1C2.getChildren().addAll(
                        defaultText("TELEFONE"),
                        edtTelefone
                );
                R2C1.getChildren().addAll(
                        defaultText("ENDEREÇO"),
                        edtEndereco
                );

                row1.getChildren().addAll(
                        R1C1,
                        R1C2);

                row2.getChildren().addAll(
                        R2C1
                );

                row3.getChildren().addAll(
                        btnSalvar,
                        btnCancelar
                );

                row3.setAlignment(Pos.CENTER_RIGHT);

                vboxPrincipal.getChildren().addAll(row1, row2, row3);

                edtNome.setText(cliente.getNome());
                edtTelefone.setText(cliente.getTelefone());
                edtEndereco.setText(cliente.getEndereco());
                btnSalvar.setOnAction((e) -> {
                    System.out.println("Iniciando verificação");
                    int id = cliente.getId();
                    String nome = edtNome.getText().toUpperCase().trim();
                    String telefone = edtTelefone.getText().toUpperCase().trim();
                    String endereco = edtEndereco.getText().toUpperCase().trim();
                    String query = null;
                    if(!(nome.equals(cliente.getNome().toUpperCase().trim()))
                            && telefone.equals(cliente.getTelefone().trim())
                            && endereco.equals(cliente.getEndereco().toUpperCase().trim())){
                        System.out.println("Setando query");
                        query = "UPDATE `Clientes` SET `cliente_nome` =? WHERE `id`=?";
                        try {
                            insertCliente(query, nome, id);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }else if(!(telefone.equals(cliente.getTelefone().trim()))
                            && nome.equals(cliente.getNome().toUpperCase().trim())
                            && endereco.equals(cliente.getEndereco().toUpperCase().trim())){
                        query = "UPDATE `Clientes` SET `cliente_telefone` = ? WHERE `id` = ?";
                        System.out.println("Novo: " + telefone + " Anterior:" + cliente.getTelefone().trim());
                        System.out.println("String nova: " + telefone.length() + " String anterior: " + cliente.getTelefone().trim().length());
                        try {
                            insertCliente(query, telefone, id);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }else if(!(endereco.equals(cliente.getEndereco().toUpperCase().trim()))
                            && nome.equals(cliente.getNome().toUpperCase().trim())
                            && telefone.equals(cliente.getTelefone().trim())){
                      query = "UPDATE `Clientes` SET `cliente_endereco` = ? WHERE `id` = ?";
                        try {
                            insertCliente(query, endereco, id);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }else if(!(nome.equals(cliente.getNome().toUpperCase().trim()))
                            && !(telefone.equals(cliente.getTelefone().toUpperCase().trim()))
                            && !(endereco.equals(cliente.getEndereco().toUpperCase().trim()))){
                        String queryModel = "UPDATE `Clientes` SET `cliente_nome` =?, `cliente_endereco` =?, `cliente_telefone` = ? WHERE `id`=?";
                        try {
                            insertClienteTodosCampos(queryModel,nome,telefone,endereco, id);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }


                });
                btnCancelar.setOnAction((event -> {
                    dialog.close();
                }));
                pane.getChildren().add(vboxPrincipal);
                return pane;
            }
            public AnchorPane tablePedidosCliente(){
                AnchorPane pane = new AnchorPane();
                VBox vboxPrincipal = defaultVBox();
                JFXButton btnSair = defaultButton("SAIR");
                btnSair.setStyle("-fx-background-color: white");
                JFXButton btnPesquisar = defaultButton("BUSCAR");
                btnPesquisar.setPrefHeight(40);
                btnPesquisar.setStyle("-fx-background-color: white");

                HBox row2 = defaultHBox();
                row2.setAlignment(Pos.CENTER_LEFT);
                HBox row3 = defaultHBox();
                row3.getChildren().addAll(
                        btnSair
                );

                row3.setAlignment(Pos.CENTER_RIGHT);

                tableViewPedidos = tableViewPedidos();

                JFXButton btnPrint = DefaultComponents.buttonIcon(" ", "SAVE", 40);
                btnPrint.setPrefHeight(40);
                comboBoxFiltroStatus = new JFXComboBox();
                comboBoxFiltroStatus.setPrefHeight(40);
                datePickerInicial = new JFXDatePicker();
                datePickerInicial.setPrefHeight(40);
                datePickerInicial.setStyle("-fx-background-color: white");
                datePickerInicial.setValue(
                        nowOnDate()
                );
                datePickerInicial.setOnAction((e) -> {
                    dataInicial = onDate(datePickerInicial);
                });

                datePickerInicial.setConverter(new StringConverter<LocalDate>() {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

                datePickerFinal = new JFXDatePicker();
                datePickerFinal.setPrefHeight(40);
                datePickerFinal.setStyle("-fx-background-color: white");
                datePickerFinal.setPromptText("Data Final");

                datePickerFinal.setValue(
                        nowOnDate()
                );
                datePickerFinal.setOnAction((e) ->{
                    dataFinal = onDate(datePickerFinal);
                });

                datePickerFinal.setConverter(new StringConverter<LocalDate>() {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

                row2.getChildren().addAll(comboBoxFiltroStatus, datePickerInicial, datePickerFinal, btnPrint, btnPesquisar);
                row2.setStyle("-fx-background-color: navy; -fx-background-radius: 15");
                row2.setPadding(new Insets(10,10,10,10));
                comboBoxFiltroStatus.setPrefWidth(150);
                comboBoxFiltroStatus.getItems().addAll(listaComboBox);
                comboBoxFiltroStatus.setStyle("-fx-background-color: white");


                vboxPrincipal.getChildren().addAll(row2, tableViewPedidos, row3);

                btnSair.setOnAction((event -> {
                    dialog.close();
                }));

                btnPesquisar.setOnAction((e) -> {
                    try {
                        switchQuery();
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                });

                btnPrint.setOnAction((e) -> {
                    File file = DefaultComponents.fileChooserSave(stackPane, "DOCUMENTO EXCEL (*.xls)", "*.xls");
                    gerarDocumentoPedidosXLS(file);
                });

                pane.getChildren().add(vboxPrincipal);
                return pane;
            }
            private TableView tableViewPedidos(){
                TableView<OrdemPedido> tableView = new TableView();
                double prefWidth = 650;
                tableView.setPrefWidth(prefWidth);
                TableColumn<OrdemPedido, Integer> idCol = new TableColumn<>();
                idCol.setText("ID");
                idCol.setPrefWidth(prefWidth/10);
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<OrdemPedido, String> Data1Col = new TableColumn<>();
                Data1Col.setText("DATA ENTRADA");
                Data1Col.setPrefWidth(prefWidth/3.5);
                Data1Col.setCellValueFactory(new PropertyValueFactory<>("data_entrada"));

                TableColumn<OrdemPedido, String> Data2Col = new TableColumn<>();
                Data2Col.setText("H. TRIAGEM");
                Data2Col.setPrefWidth(prefWidth/3.5);
                Data2Col.setCellValueFactory(new PropertyValueFactory<>("horario_triagem"));

                TableColumn<OrdemPedido, String> Data3Col = new TableColumn<>();
                Data3Col.setText("H. FINALIZADO");
                Data3Col.setPrefWidth(prefWidth/3.5);
                Data3Col.setCellValueFactory(new PropertyValueFactory<>("horario_finalizado"));
                tableView.getColumns().addAll(idCol, Data1Col, Data2Col, Data3Col);
                ContextMenu cm = new ContextMenu();
                MenuItem mi1 = new MenuItem("ABRIR DETALHES DO PEDIDO");
                cm.getItems().add(mi1);
                tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent t) {
                        if(t.getButton() == MouseButton.SECONDARY) {
                            cm.show(tableView, t.getScreenX(), t.getScreenY());
                            mi1.setOnAction((e) -> {
                                iniciarDetalhesScreen(tableView.getSelectionModel().getSelectedItem());
                                System.out.println("ID do pedido selecionado: " + tableView.getSelectionModel().getSelectedItem().getId());
                            });
                        }
                    }
                });
                return tableView;
            }
            public JFXButton defaultButton(String texto){
                JFXButton button = new JFXButton();
                button.setStyle("-fx-background-color: lightgrey");
                button.setText(texto);
                return button;
            }
            public JFXTextField textFieldPadrao(double size){
                JFXTextField textField = new JFXTextField();
                textField.setStyle("-fx-background-color: lightgrey");
                textField.setPrefWidth(size);
                return textField;
            }
            public Text defaultText(String texto){
                Text text = new Text();
                text.setText(texto);
                text.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 20));
                return text;
            }
            public VBox defaultVBox(){
                VBox vBox = new VBox();
                vBox.setSpacing(5);
                vBox.setAlignment(Pos.CENTER);
                return vBox;
            }
            public HBox defaultHBox(){
                HBox hBox = new HBox();
                hBox.setSpacing(5);
                hBox.setAlignment(Pos.CENTER);
                return hBox;
            }
            /*
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

             */
        //objetos

        //metodos de controle
             private void intentImport() {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("/importarClientesScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.show();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        refreshTable();
                        System.out.println("Janela fechada");
                    }
                });
            } catch (IOException exception){
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
            }
        }

             private LocalDate nowOnDate(){
                LocalDate localDate = LocalDate.now();
                localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dataFinal = formatData(localDate.toString());
                dataInicial = formatData(localDate.toString());
                System.out.println("Data Atual: " + dataInicial);
                System.out.println("Data Atual: " + dataFinal);
                return localDate;

            }
            private String onDate (JFXDatePicker picker){
                LocalDate localDate = picker.getValue();
                String dataFormatada = formatData(localDate.toString());
                //dataInicial = formatData(localDate.toString());
                //datePicker.setPromptText(dataInicial);
                System.out.println(dataFormatada);
                return dataFormatada;
            }
            public String formatData (String data){
                SimpleDateFormat sdf = null;
                Date d = null;
                try{
                    sdf = new SimpleDateFormat("yy-MM-dd");
                    d = sdf.parse(data);
                    sdf.applyPattern("yyyy-MM-dd");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return sdf.format(d);
            }
            private void refreshTable() {
                listaClientes.clear();
                try {
                    recuperarClientes();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

            }
        //metodos de controle
}
