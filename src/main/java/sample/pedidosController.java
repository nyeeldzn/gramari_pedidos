package sample;

import com.jfoenix.controls.*;
import helpers.DefaultComponents;
import helpers.db_connect;
import helpers.intentData;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;
import jxl.write.Label;
import models.OrdemPedido;

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

public class pedidosController implements Initializable {

    @FXML
    private JFXComboBox cbStatus;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXDatePicker pickerDataInicial;

    @FXML
    private JFXDatePicker pickerDataFinal;

    @FXML
    private JFXTextField edtSearch;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnExportar;

    @FXML
    private JFXCheckBox checkBoxDatas;

    @FXML
    private TableView<OrdemPedido> tableView;

    @FXML
    private TableColumn<OrdemPedido, Integer> idCol;

    @FXML
    private TableColumn<OrdemPedido, String> nomeCol;

    @FXML
    private TableColumn<OrdemPedido, String> pagamentoCol;

    @FXML
    private TableColumn<OrdemPedido, String> envioCol;

    @FXML
    private TableColumn<OrdemPedido, String> entradaCol;

    @FXML
    private TableColumn<OrdemPedido, String> triagemCol;

    @FXML
    private TableColumn<OrdemPedido, String> finalizadoCol;

    int cb_selectedIndex = 0;
    boolean useDatas = false;
    String dataInicial;
    String dataFinal;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    ObservableList<OrdemPedido> listaPedidosTodos = FXCollections.observableArrayList();
    ObservableList<OrdemPedido> listaPedidosEntrada = FXCollections.observableArrayList();
    ObservableList<OrdemPedido> listaPedidosTriagem = FXCollections.observableArrayList();
    ObservableList<OrdemPedido> listaPedidosFinalizado = FXCollections.observableArrayList();
    ObservableList<String> listaComboBox = FXCollections.observableArrayList(
            "Pedidos Pendentes","Pedidos em Triagem","Pedidos Finalizados", "Todos"
    );
    FilteredList<OrdemPedido> filteredData;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComponentes();
        prepararTableView();
    }



    //metodos iniciais
    private void setupComponentes() {
        pickerDataInicial.setValue(
                nowOnDate()
        );
        pickerDataInicial.setOnAction((e) -> {
            dataInicial = onDate(pickerDataInicial);
        });
        pickerDataInicial.setConverter(new StringConverter<LocalDate>() {
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

        pickerDataFinal.setValue(
                nowOnDate()
        );
        pickerDataFinal.setOnAction((e) -> {
            dataInicial = onDate(pickerDataInicial);
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

        cbStatus.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                verificarComboBox();
            }
        });
        cbStatus.getItems().addAll(listaComboBox);
        cbStatus.getSelectionModel().select(0);
        edtSearch.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtSearch.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                pesquisarPedido();
            }
        });
        btnSearch.setOnAction((e) -> {
            if (edtSearch.getText().equals("")) {
                try {
                    switchQuery();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });

        checkBoxDatas.setOnAction((e) -> {
            System.out.println(checkBoxDatas.isSelected());
        });

        btnExportar.setOnAction((e) -> {
            File file = DefaultComponents.fileChooserSave(stackPane, "DOCUMENTO DO EXCEL (*.xls)", "*.xls");
            gerarDocumentoXLS(file);
        });

    }
    private void prepararTableView() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("cliente_nome"));
        pagamentoCol.setCellValueFactory(new PropertyValueFactory<>("forma_envio"));
        envioCol.setCellValueFactory(new PropertyValueFactory<>("forma_pagamento"));
        entradaCol.setCellValueFactory(new PropertyValueFactory<>("data_entrada"));
        triagemCol.setCellValueFactory(new PropertyValueFactory<>("data_triagem"));
        finalizadoCol.setCellValueFactory(new PropertyValueFactory<>("data_finalizado"));
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
    }
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
    private void verificarComboBox() {
        cb_selectedIndex = cbStatus.getSelectionModel().getSelectedIndex();
        System.out.println("Index selecionado: " + cb_selectedIndex);
    }
    //metodos iniciais

    //metodos de negocios
    private LocalDate nowOnDate(){
        LocalDate localDate = LocalDate.now();
        localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
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
    private ObservableList<OrdemPedido> recuperarPedidosPorData(String query, String table) throws SQLException {
        ObservableList<OrdemPedido> lista = FXCollections.observableArrayList();
        lista.clear();
        System.out.println("Data Inicial de busca: " + dataInicial + "Data final de busca: " + dataFinal);
        connection = db_connect.getConnect();
        preparedStatement = connection.prepareStatement(query);
        if(checkBoxDatas.isSelected() == true && cb_selectedIndex != 3){
            preparedStatement.setString(1, table);
            preparedStatement.setString(2, dataInicial);
            preparedStatement.setString(3, dataFinal);
        }else if (checkBoxDatas.isSelected() == true && cb_selectedIndex == 3){
            preparedStatement.setString(1, dataInicial);
            preparedStatement.setString(2, dataFinal);
        }
        if(checkBoxDatas.isSelected() == false && cb_selectedIndex != 3){
            preparedStatement.setString(1, table);
        }
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            lista.add(new OrdemPedido(
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
                    resultSet.getDouble("troco"),
                    resultSet.getString("caixa_responsavel"),
                    resultSet.getInt("status_id")
            ));
            System.out.println(resultSet.getString("cliente_nome"));
        }
        return lista;
    }
    private void switchQuery() throws SQLException {
        String query;
        if(checkBoxDatas.isSelected() == true) {
            switch (cb_selectedIndex) {
                case 0:
                    System.out.println("Buscando nos Pedidos de Entrada");
                    //query = "SELECT * FROM `Ordem_De_Pedido` WHERE `data_entrada` >= ? AND `data_entrada` <= ?";
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =? AND `data_entrada` BETWEEN ?  AND ? ";
                    listaPedidosEntrada = recuperarPedidosPorData(query, "1");
                    tableView.setItems(listaPedidosEntrada);
                    break;
                case 1:
                    System.out.println("Buscando nos Pedidos em Triagem");
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =? AND `data_entrada` >= ? AND `data_entrada` <= ?";
                    listaPedidosTriagem = recuperarPedidosPorData(query, "2");
                    tableView.setItems(listaPedidosTriagem);
                    break;
                case 2:
                    System.out.println("Buscando nos Pedidos Finalizados");
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =? AND `data_entrada` >= ? AND `data_entrada` <= ?";
                    listaPedidosFinalizado = recuperarPedidosPorData(query, "3");
                    tableView.setItems(listaPedidosFinalizado);
                    break;
                case 3:
                    listaPedidosTodos.clear();
                    System.out.println("Buscando em todos os status");
                    int size = listaComboBox.size();
                    query = "SELECT * FROM `Pedidos` WHERE `data_entrada` >= ? AND `data_entrada` <= ?";
                    listaPedidosTodos = recuperarPedidosPorData(query, "");
                    tableView.setItems(listaPedidosTodos);

            }
        }else if(checkBoxDatas.isSelected() == false){
            switch (cb_selectedIndex) {
                case 0:
                    System.out.println("Buscando nos Pedidos de Entrada");
                    //query = "SELECT * FROM `Ordem_De_Pedido` WHERE `data_entrada` >= ? AND `data_entrada` <= ?";
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =? ";
                    listaPedidosEntrada = recuperarPedidosPorData(query, "1");
                    tableView.setItems(listaPedidosEntrada);
                    break;
                case 1:
                    System.out.println("Buscando nos Pedidos em Triagem");
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =?";
                    listaPedidosTriagem = recuperarPedidosPorData(query, "2");
                    tableView.setItems(listaPedidosTriagem);
                    break;
                case 2:
                    System.out.println("Buscando nos Pedidos Finalizados");
                    query = "SELECT * FROM `Pedidos` WHERE `status_id` =?";
                    listaPedidosFinalizado = recuperarPedidosPorData(query, "3");
                    tableView.setItems(listaPedidosFinalizado);
                    break;
                case 3:
                    listaPedidosTodos.clear();
                    System.out.println("Buscando em todos os status");
                    query = "SELECT * FROM `Pedidos`";
                    listaPedidosTodos = recuperarPedidosPorData(query, "");
                                tableView.setItems(listaPedidosTodos);
                                        break;
                    }
            }
        }

    private void pesquisarPedido(){
        switch (cb_selectedIndex){
            case 0:
                System.out.println("Busca com index: " + cb_selectedIndex);
                filteredSearch(listaPedidosEntrada);
                break;
            case 1:
                System.out.println("Busca com index: " + cb_selectedIndex);
                filteredSearch(listaPedidosTriagem);
                break;
            case 2:
                System.out.println("Busca com index: " + cb_selectedIndex);
                filteredSearch(listaPedidosFinalizado);
                break;
            case 3:
                System.out.println("Busca com index: " + cb_selectedIndex);
                filteredSearch(listaPedidosTodos);
                break;
        }
    }
    public void gerarDocumentoXLS(File file){
        System.out.println("Iniciando exportação");
        //Busca todos os itens da tabela
        ObservableList<OrdemPedido> listaTabela = FXCollections.observableArrayList();
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


    private void filteredSearch(ObservableList list){
        filteredData = new FilteredList<>(list, b -> true);
        edtSearch.textProperty().addListener((observable, oldValue, newValue) ->{
            filteredData.setPredicate(pedido -> {
                if(newValue == null && newValue.isEmpty()){
                    return true;
                }

                String upperCaseFilter = newValue.toUpperCase().trim();
                if(pedido.getCliente_nome().toUpperCase().indexOf(upperCaseFilter) != -1){
                    return true;
                }else if(pedido.getEnd_cliente().toUpperCase().indexOf(upperCaseFilter) != -1)
                    return true;
                else if(pedido.getNum_cliente().toUpperCase().indexOf(upperCaseFilter) != -1)
                    return true;
                else if(pedido.getForma_pagamento().toUpperCase().indexOf(upperCaseFilter) != -1){
                    return true;
                }else if(pedido.getForma_envio().toUpperCase().indexOf(upperCaseFilter) != -1)
                    return true;
                else if(pedido.getForma_subst().toUpperCase().indexOf(upperCaseFilter) != -1)
                return true;
                else
                    return false;
            });

            SortedList<OrdemPedido> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
        });
    }
    //metodos de controle

    //metodos de controle

    //metodos de controle

}
