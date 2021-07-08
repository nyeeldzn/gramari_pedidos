package sample;

import com.aspose.pdf.Document;
import com.aspose.pdf.Image;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.Page;
import com.jfoenix.controls.*;
import helpers.*;
import helpers.Database.db_connect;
import helpers.Database.db_crud;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import models.OrdemPedido;
import models.Usuario;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static helpers.DefaultComponents.*;
import static helpers.LineChartModel.lineChart;

public class MainController implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox mainCenter;

    @FXML
    private BorderPane dashBoard;

    @FXML
    private JFXButton btnNovoPedido;

    @FXML
    private JFXButton btnExit;

    @FXML
    private JFXButton btnDetalhesPedido;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private ImageView btnPesquisar;

    @FXML
    private JFXButton btnPerfil;

    @FXML
    private JFXHamburger btnConfig;


    //Table Entrada
    @FXML
    private TableView<OrdemPedido> tablePedido;

    @FXML
    private TableColumn<OrdemPedido, Integer> idCol;

    @FXML
    private TableColumn<OrdemPedido, String> nomeCol;

    @FXML
    private TableColumn<OrdemPedido, String> telCol;

    @FXML
    private TableColumn<OrdemPedido, Integer> statusCol;
    //Table Entrada

    //Table Triagem
    @FXML
    private TableView<OrdemPedido> tablePedidoTriagem;

    @FXML
    private TableColumn<OrdemPedido, Integer> idColTriagem;

    @FXML
    private TableColumn<OrdemPedido, String> nomeColTriagem;

    @FXML
    private TableColumn<OrdemPedido, String> dataColTriagem;

    @FXML
    private TableColumn<OrdemPedido, String> statusColTriagem;

    //Table Triagem

    //Table Finalizado
    @FXML
    private TableView<OrdemPedido> tablePedidoFinalizado;

    @FXML
    private TableColumn<OrdemPedido, Integer> idColFinalizado;

    @FXML
    private TableColumn<OrdemPedido, String> nomeColFinalizado;

    @FXML
    private TableColumn<OrdemPedido, String> dataColFinalizado;

    @FXML
    private TableColumn<OrdemPedido, String> statusColFinalizado;




    //Table Finalizado


    private String selectedIndex;
    private int selectedTable;
    private boolean leftPane = false;
    private int selectedPeriodo;

    String dataInicial;
    String dataFinal;
    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    FilteredList<OrdemPedido> filteredData;
    Usuario user = null;

    ObservableList<OrdemPedido> listaPedidos = FXCollections.observableArrayList();
    ObservableList<OrdemPedido> listaPedidosTriagem = FXCollections.observableArrayList();
    ObservableList<OrdemPedido> listaPedidosFinalizado = FXCollections.observableArrayList();

    ObservableList<OrdemPedido> listaPedidoFiltrados = FXCollections.observableArrayList();
    ArrayList<Integer> listaTotalData = new ArrayList<>();
    ArrayList<Integer> listaTotalHorario = new ArrayList<>();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recuperarPedidos();
        setupComponentes();
        recuperarUsuario();
    }



    //Metodos Iniciais
    private void recuperarUsuario() {
       user = AuthenticationSystem.getUser();
       System.out.println("usuario logado: " + user.getUsername());
    }
    private void recuperarPedidos() {
        connection = db_connect.getConnect();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("cliente_nome"));
        telCol.setCellValueFactory(new PropertyValueFactory<>("num_cliente"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        idColTriagem.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColTriagem.setCellValueFactory(new PropertyValueFactory<>("cliente_nome"));
        dataColTriagem.setCellValueFactory(new PropertyValueFactory<>("horario_triagem"));
        statusColTriagem.setCellValueFactory(new PropertyValueFactory<>("status"));

        idColFinalizado.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeColFinalizado.setCellValueFactory(new PropertyValueFactory<>("cliente_nome"));
        dataColFinalizado.setCellValueFactory(new PropertyValueFactory<>("horario_finalizado"));
        statusColFinalizado.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshTable();
    }
    private void setupComponentes(){

        stackPane.setOnMousePressed(pressEvent -> {
            stackPane.setOnMouseDragged(dragEvent -> {
                System.out.println("Movendo a Janela");
                Stage stage = (Stage) stackPane.getScene().getWindow();
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
        btnExit.setOnAction((e) -> {
            Stage stage = (Stage) stackPane.getScene().getWindow();
            stage.close();
        });
        btnRefresh.setOnAction((e) -> {
            refreshTable();
        });
        btnNovoPedido.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                intentnovoPedido();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });
        btnDetalhesPedido.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                if(selectedIndex != null && !(selectedIndex.isEmpty())){
                    intentDados(selectedIndex, selectedTable);
                }else{
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Selecione um pedido", stackPane);
                    dialog.show();
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }

        });
        btnConfig.setOnMouseClicked((e) ->{
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                if(leftPane == false){
                    borderPane.setRight(anchorPane());
                    leftPane = true;
                }else if(leftPane == true){
                    borderPane.setRight(null);
                    leftPane = false;
                }
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });
        mainCenter.setVisible(true);
        tablePedidoTriagem.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrdemPedido>() {
            @Override
            public void changed(ObservableValue<? extends OrdemPedido> observable, OrdemPedido oldValue, OrdemPedido newValue) {
                if(tablePedidoTriagem.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = tablePedidoTriagem.getSelectionModel();
                    OrdemPedido ordem = tablePedidoTriagem.getSelectionModel().getSelectedItem();
                    selectedIndex = String.valueOf(ordem.getId());
                    selectedTable = 2;
                    System.out.println(selectedIndex);
                    System.out.println(selectedTable);
                }
            }
        });
        tablePedidoFinalizado.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrdemPedido>() {
            @Override
            public void changed(ObservableValue<? extends OrdemPedido> observable, OrdemPedido oldValue, OrdemPedido newValue) {
                if(tablePedidoFinalizado.getSelectionModel().getSelectedItem() != null){
                    TableView.TableViewSelectionModel selectionModel = tablePedidoFinalizado.getSelectionModel();
                    OrdemPedido ordem = tablePedidoFinalizado.getSelectionModel().getSelectedItem();
                    selectedTable = 3;
                    selectedIndex = String.valueOf(ordem.getId());
                }
            }
        });
        tablePedido.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label
                if(tablePedido.getSelectionModel().getSelectedItem() != null)
                {
                    TableView.TableViewSelectionModel selectionModel = tablePedido.getSelectionModel();
                    OrdemPedido ordem = tablePedido.getSelectionModel().getSelectedItem();
                    selectedIndex = String.valueOf(ordem.getId());
                    selectedTable = 1;
                    System.out.println(ordem.getId());
                    System.out.println(selectedTable);
                }
            }
        });
    }
    //Metodos Iniciais

    public AnchorPane anchorPane (){
        double larguraPadrao = 200;
        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: white");
        pane.setMinSize(larguraPadrao,500);

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5,5,5,5));

        JFXButton btnInicio = buttonIcon("INICIO", "HOME", larguraPadrao);
        btnInicio.setOnAction((e) -> {
            fecharAbrirMenu();
            mainCenter.setVisible(true);
            dashBoard.setVisible(false);
        });

        JFXButton btnProdutos = buttonIcon("PRODUTOS", "GIFT", larguraPadrao);
        btnProdutos.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                fecharAbrirMenu();
                openProdutosController();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }

        });

        JFXButton btnPedidos = buttonIcon("PEDIDOS", "CART_PLUS", larguraPadrao);
        btnPedidos.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                fecharAbrirMenu();
                openPedidosController();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });

        JFXButton btnClientes = buttonIcon("CLIENTES", "USER", larguraPadrao);
        btnClientes.setOnAction((e) ->{
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 1) == true){
                fecharAbrirMenu();
                openClientesController();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });

        JFXButton btnDashboard = buttonIcon("DASHBOARD", "CLIPBOARD", larguraPadrao);
        btnDashboard.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 3) == true){
                fecharAbrirMenu();
                mainCenter.setVisible(false);
                configDashBoard(25);
                dashBoard.setVisible(true);
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }

        });


        JFXButton btnFuncionarios = buttonIcon("FUNCIONARIO", "USER", larguraPadrao);
        btnFuncionarios.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 3) == true){
                intentFuncionarios();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });

        JFXButton btnRupturas = buttonIcon("LISTA DE RUPTURA", "LIST", larguraPadrao);
        btnRupturas.setOnAction((e) -> {
            if(UserPrivilegiesVerify.permissaoVerBotao(user, 2) == true){
                intentListaRupturas();
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Você não tem permissão para isso.",stackPane);
                dialog.show();
            }
        });


        vBox.getChildren().addAll(btnInicio, btnProdutos, btnPedidos, btnClientes, btnRupturas, btnDashboard, btnFuncionarios);
        pane.getChildren().addAll(vBox);
        return pane;
    }

    private void configDashBoard(int dias) {
        ArrayList<String> array = new ArrayList<>();
        ArrayList<String> array2 = new ArrayList<>();

        int tempoDeBusca = 14;

        nowOnDate();

        //Componentes
        ScrollPane scrollPane = new ScrollPane();

        JFXButton btnPrint = defaultButton("Imprimir Dashboard");


        Text textPedidoPeriodo = defaultText("Pedidos Por Dia");
        Text textHorarioPico = defaultText("Horarios de Pico");
        Text textMediaTempoTriagem = defaultText("Media de Listagem por Dia");
        Text textMTE = defaultText("Media de Entrega por dia");
        Text totalText = defaultText("Total:");
        Text numTotalText = defaultText("");

        Text textcard1 = defaultText("Total de Pedidos");
        Text textcard2 = defaultText("Média de Pedidos/Cliente");
        Text textcard3 = defaultText("Média de Listagem");
        Text textcard4 = defaultText("Média de Entrega");

        Text textPie1 = defaultText("Fonte do Pedido");
        Text textPie2 = defaultText("Formas de Pagamento");
        Text textPie3 = defaultText("Relação Caixa/Checkout");
        Text textPie4 = defaultText("Clientes/Bairro");




        Text valuecard1 = defaultText("--,--");
        Text bottomCard1 = defaultText("PEDIDOS");
        Text valuecard2 = defaultText("--,--");
        Text bottomCard2 = defaultText("PEDIDOS");
        Text valuecard3 = defaultText("--,--");
        Text bottomCard3 = defaultText("MINUTOS");
        Text valuecard4 = defaultText("--,--");
        Text bottomCard4 = defaultText("MINUTOS");

        //LineChart
        LineChart<String, Number> lineChartPedidosDiarios = lineChart();
        XYChart.Series seriesPedidosDiarios = new XYChart.Series();
        lineChartPedidosDiarios.getData().add(seriesPedidosDiarios);

        LineChart<String, Number> lineChartHorariosPico = lineChart();
        XYChart.Series seriesHorarioPico = new XYChart.Series();
        lineChartHorariosPico.getData().add(seriesHorarioPico);

        LineChart<String, Number> lineChartMediaTempoTriagem = lineChart();
        XYChart.Series seriesMediaTempoTriagem = new XYChart.Series();
        lineChartMediaTempoTriagem.getData().add(seriesMediaTempoTriagem);

        LineChart<String, Number> lineChartMTE = lineChart();
        XYChart.Series seriesMTE = new XYChart.Series();
        lineChartMTE.getData().add(seriesMTE);

        //test
        LineChart<String, Number> lcClienteBairro = lineChart();
        lcClienteBairro.setPrefHeight(700);
        XYChart.Series sClienteBairro = new XYChart.Series();
        lcClienteBairro.getData().add(sClienteBairro);
        //test

        //LineChart

        //PieChart
        PieChart pieChartOrigem = new PieChart();
        PieChart pieChartPagamento = new PieChart();
        PieChart pieChartCaixaCheckout = new PieChart();
        PieChart pieChartClienteBairro = new PieChart();
        PieChart pieChartEntrega = new PieChart();
        PieChart pieChartSubst = new PieChart();

        //PieChart

        HBox rowTop = new HBox();
        rowTop.getChildren().addAll(btnPrint);

        HBox row1 = new HBox();
        row1.setAlignment(Pos.CENTER);
        row1.setSpacing(40);
        HBox row2 = new HBox();
        row2.setAlignment(Pos.CENTER);
        HBox row3 = new HBox();
        row3.setAlignment(Pos.CENTER);
        HBox row4 = new HBox();
        row4.setSpacing(40);
        row4.setAlignment(Pos.CENTER);

        HBox row5 = new HBox();
        row5.setSpacing(40);
        row5.setAlignment(Pos.CENTER);

        HBox row6 = new HBox();
        row6.setSpacing(40);
        row6.setAlignment(Pos.CENTER);

        VBox card1 = card();
        card1.getChildren().addAll(textcard1, FontIcon("FILE"), valuecard1, bottomCard1);
        VBox card2 = card();
        card2.getChildren().addAll(textcard2, FontIcon("USER_PLUS"), valuecard2, bottomCard2);
        VBox card3 = card();
        card3.getChildren().addAll(textcard3, FontIcon("SHOPPING_CART"), valuecard3, bottomCard3);
        VBox card4 = card();
        card4.getChildren().addAll(textcard4, FontIcon("MOTORCYCLE"), valuecard4, bottomCard4);

        VBox cardChart1 = card();
        cardChart1.setPrefWidth(500);
        cardChart1.setPrefHeight(400);
        cardChart1.getChildren().addAll(textPedidoPeriodo, lineChartPedidosDiarios);

        VBox cardChart2 = card();
        cardChart2.setPrefWidth(500);
        cardChart2.setPrefHeight(400);
        cardChart2.getChildren().addAll(textHorarioPico, lineChartHorariosPico);

        VBox cardChart3 = card();
        cardChart3.setPrefWidth(500);
        cardChart3.setPrefHeight(400);
        cardChart3.getChildren().addAll(textMediaTempoTriagem, lineChartMediaTempoTriagem);

        VBox cardChart4 = card();
        cardChart4.setPrefWidth(500);
        cardChart4.setPrefHeight(400);
        cardChart4.getChildren().addAll(textMTE, lineChartMTE);


        VBox cardPie1 = card();
        cardPie1.setPrefWidth(500);
        cardPie1.setPrefHeight(400);
        cardPie1.getChildren().addAll(textPie1, pieChartOrigem);

        VBox cardPie2 = card();
        cardPie2.setPrefWidth(500);
        cardPie2.setPrefHeight(400);
        cardPie2.getChildren().addAll(textPie2, pieChartPagamento);

        VBox cardPie3 = card();
        cardPie3.setPrefWidth(500);
        cardPie3.setPrefHeight(400);
        cardPie3.getChildren().addAll(textPie3, pieChartCaixaCheckout);

        /*
        VBox cardPie4 = card();
        cardPie4.setPrefWidth(500);
        cardPie4.setPrefHeight(400);
        cardPie4.getChildren().addAll(textPie4, pieChartClienteBairro);


         */

        //test
        VBox cardPie4 = card();
        cardPie4.setPrefWidth(1000);
        cardPie4.setPrefHeight(800);
        cardPie4.getChildren().addAll(textPie4, lcClienteBairro);
        //test


        //Componentes


        row1.getChildren().addAll(card1, card2, card3, card4);
        row2.getChildren().addAll(cardChart1, cardChart2);
        row2.setSpacing(20);
        row3.getChildren().addAll(cardChart3, cardChart4);
        row3.setSpacing(20);
        row4.getChildren().addAll(cardPie1, cardPie2);
        row5.getChildren().addAll(cardPie3);

        row6.getChildren().addAll(cardPie4);


        VBox vboxFinal = new VBox();
        vboxFinal.getChildren().addAll(rowTop,row1, row2, row3, row4, row5, row6);
        vboxFinal.setPadding(new Insets(10,0,10,0));
        vboxFinal.setSpacing(40);

        setupChartPedidosDiarios(tempoDeBusca, array, seriesPedidosDiarios);
        setupChartHorariosPico(array, seriesHorarioPico);

        //setup chartline MT
        ArrayList<String> listaDatasMT = getArrayDatas(tempoDeBusca);
        ArrayList<Float> MTChartArray = recuperarMediaTempoListagem(listaDatasMT);
        seriesMediaTempoTriagem.getData().clear();
        for(int i = 0; i<MTChartArray.size(); i++){
            seriesMediaTempoTriagem.getData().add(new XYChart.Data<String, Number>(listaDatasMT.get(i), MTChartArray.get(i)));
        }
        //

        //setup chatline ME
        ArrayList<String> listaDatasME = getArrayDatas(tempoDeBusca);
        ArrayList<Float> MTEChartArray = recuperarMEdata(listaDatasME);
        //System.out.println("MTECHARTARRAY SIZE: " + MTEChartArray);
        //System.out.println("MTESERIES SIZE: " + seriesMTE.hashCode());
        //System.out.println("DATASME: " + listaDatasME);
        seriesMTE.getData().clear();
        for(int i = 0; i<MTEChartArray.size(); i++){
            seriesMTE.getData().add(new XYChart.Data<String, Number>(listaDatasME.get(i), MTEChartArray.get(i)));
           // System.out.println("MTESERIES SIZE: " + seriesMTE.hashCode());

        }



        //test
        //setup lc ClienteBairro

        ArrayList<String> nomeBairros = db_crud.getBairros();
        ArrayList<Integer> qtdClientesBairros = new ArrayList<>();

        Connection connection = db_connect.getConnect();

        for(int b = 0; b< nomeBairros.size(); b++){

            try{
                ArrayList<Integer> listatemp = new ArrayList<>();

                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Clientes WHERE `bairro` = ?");
                ps.setString(1, nomeBairros.get(b));
                ResultSet r = ps.executeQuery();
                while (r.next()){
                    listatemp.add(r.getInt("id"));
                }

                qtdClientesBairros.add(listatemp.size());
            }catch (SQLException e){
                e.printStackTrace();
            }


        }

        sClienteBairro.getData().clear();
        for(int i = 0; i<qtdClientesBairros.size(); i++){
            sClienteBairro.getData().add(new XYChart.Data<String, Number>(nomeBairros.get(i), qtdClientesBairros.get(i)));
        }

        //

        //setup cardpie
        System.out.println("Chart: Pie Origem - Configurando");
            ObservableList<PieChart.Data> listOrigem = DataManagerAnalytcs.getOrigemData();
                pieChartOrigem.setData(listOrigem);

        System.out.println("Chart: Pie Pagamento - Configurando");
        ObservableList<PieChart.Data> listPagamento = DataManagerAnalytcs.getPagamentoData();
                pieChartPagamento.setData(listPagamento);

        System.out.println("Chart: Pie Caixa/Checkout - Configurando");
        ObservableList<PieChart.Data> listaCaixaCheckout = DataManagerAnalytcs.getCaixaCheckout();
                pieChartCaixaCheckout.setData(listaCaixaCheckout);

                /*
        System.out.println("Chart: Line Cliente/Bairro - Configurando");
        ObservableList<PieChart.Data> listaClienteBairro = DataManagerAnalytcs.getClienteBairro();
        pieChartClienteBairro.setData(listaClienteBairro);


                 */
        //



        valuecard1.setText(String.valueOf(DataManagerAnalytcs.getPedidostotal()));
        valuecard2.setText(String.valueOf(DataManagerAnalytcs.getMPC()));
        valuecard3.setText(String.valueOf(DataManagerAnalytcs.getMTAVGtotal()));
        valuecard4.setText(String.valueOf(DataManagerAnalytcs.getMEAVGtotal()));

        numTotalText.setText(String.valueOf(listaTotalData.size()));

        scrollPane.setContent(vboxFinal);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(20,20,20,20));
        dashBoard.setCenter(scrollPane);


        btnPrint.setOnAction((e) -> {
            ArrayList<Node> listaComponents = new ArrayList<>();
            listaComponents.add(cardChart1);
            listaComponents.add(cardChart2);
            listaComponents.add(cardChart3);
            listaComponents.add(cardChart4);
            listaComponents.add(cardPie1);
            listaComponents.add(cardPie2);
            listaComponents.add(card1);
            listaComponents.add(card2);
            listaComponents.add(card3);
            listaComponents.add(card4);
            listaComponents.add(cardPie3);
            //add componentes

            try {
                //VBox newVbox = vboxFinal;
                nodesToImage(listaComponents);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
        //dashBoard.setPadding(new Insets(20,20,20,20));
    }

    private void metodoImprimir(ByteArrayInputStream bais) throws IOException {


        Document doc = new Document();

        Image image = new Image();

        Page page = doc.getPages().add();
        MarginInfo marginInfo = new MarginInfo();
        marginInfo.setRight(25);
        marginInfo.setLeft(25);
        marginInfo.setTop(100);
        marginInfo.setBottom(100);
        page.getPageInfo().setMargin(marginInfo);
        page.getParagraphs().add(image);
        image.setImageStream(bais);
        //210 mm by 297
        File file = fileChooserSave(stackPane, "PDF", "*.pdf");
        doc.save(file.getAbsolutePath());


        configDashBoard(14);

    }



    private void nodesToImage (ArrayList<Node> Componentes) throws IOException {

        VBox vBoxPrincipal = defaultVBox();
        vBoxPrincipal.setSpacing(10);



        HBox row1 = defaultHBox();
        HBox row2 = defaultHBox();
        HBox row3 = defaultHBox();
        HBox row4 = defaultHBox();

        row1.getChildren().addAll(Componentes.get(6), Componentes.get(7), Componentes.get(8), Componentes.get(9));
        row2.getChildren().addAll(Componentes.get(0), Componentes.get(1), Componentes.get(2));
        row3.getChildren().addAll(Componentes.get(3), Componentes.get(4), Componentes.get(5));
        row4.getChildren().addAll(Componentes.get(10));

        vBoxPrincipal.getChildren().addAll(row1, row2, row3, row4);


        Scene scene = new Scene(new Group());


        ((Group) scene.getRoot()).getChildren().add(vBoxPrincipal);

        WritableImage image = scene.snapshot(null);
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteOutput);
        //byteOutput.flush();
        ByteArrayInputStream bais = new ByteArrayInputStream(byteOutput.toByteArray());



        metodoImprimir(bais);

        System.out.println("Image Saved");
    }

    private ArrayList<String> getArrayDatas(int dias){
        ArrayList<String> arrayDatas = new ArrayList<>();
        //Recupera datas para busca
        arrayDatas = DataManagerAnalytcs.getDatasArrayMT(dias, arrayDatas, dataInicial);
        System.out.println("Debug Test, datas de busca: " + arrayDatas);
        //
        return arrayDatas;
    }

    private ArrayList<Float> recuperarMediaTempoListagem(ArrayList<String> arrayDatas) {
        int dias = arrayDatas.size();
        //recupera os dados por data
        ArrayList<Float> listaQTDPedidosPorData = new ArrayList<>();
        for(int i = 0; i<dias; i++) {
            //ArrayList<PedidoEstatistica> innerEstatisticas = new ArrayList<>();
            //listaMediaTriagem
            //
            try {
                //query = "SELECT * FROM `Pedido_Estatisticas` WHERE `data` =?";
                preparedStatement = connection.prepareStatement("SELECT AVG(`m.t`) FROM `Pedido_Estatisticas` WHERE `data` = ?");
                //preparedStatement.setString(1, arrayDatas.get(i));
                //resultSet = preparedStatement.executeQuery();
                preparedStatement.setString(1, arrayDatas.get(i));
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next())
                    //System.out.println("avg media triagem é: " + rs.getFloat(1));
                    //innerEstatisticas.add(rs.getFloat(1));
                    listaQTDPedidosPorData.add(rs.getFloat(1));
                System.out.println("M.T LOG: Data:  " + arrayDatas.get(i) + " M.T PEDIDO = " + listaQTDPedidosPorData.get(i));
                //listaQTDPedidosPorData.add(innerEstatisticas.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //
        return listaQTDPedidosPorData;
    }
    private ArrayList<Float> recuperarMEdata(ArrayList<String> arrayDatas) {
        int dias = arrayDatas.size();
        //recupera os dados por data
        ArrayList<Float> listaQTDPedidosPorData = new ArrayList<>();
        for(int i = 0; i<dias; i++) {
            //ArrayList<PedidoEstatistica> innerEstatisticas = new ArrayList<>();
            //listaMediaTriagem
            try {
                //query = "SELECT * FROM `Pedido_Estatisticas` WHERE `data` =?";
                preparedStatement = connection.prepareStatement("SELECT AVG(`m.e`) FROM `Pedido_Estatisticas` WHERE `data` = ?");
                //preparedStatement.setString(1, arrayDatas.get(i));
                //resultSet = preparedStatement.executeQuery();
                preparedStatement.setString(1, arrayDatas.get(i));
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next())
                    //System.out.println("avg media triagem é: " + rs.getFloat(1));
                    //innerEstatisticas.add(rs.getFloat(1));
                    listaQTDPedidosPorData.add(rs.getFloat(1));
                System.out.println("M.E LOG: Data:  " + arrayDatas.get(i) + " M.E PEDIDO = " + listaQTDPedidosPorData.get(i));
                //listaQTDPedidosPorData.add(innerEstatisticas.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //
        return listaQTDPedidosPorData;
    }


    private void setupChartPedidosDiarios(int dias, ArrayList<String> array, XYChart.Series series) {
        listaTotalData.clear();
        listaPedidoFiltrados.clear();
        //dias = getDias(dias, cbPeriodo);
        array = DataManagerAnalytcs.getDatasArray(dias, array, dataInicial);
        if (DataManagerAnalytcs.isFinished == true) {
            listaTotalData = DataManagerAnalytcs.getListaTotalData();
            setChartLine(array, series);
        }
    }

    private void setupChartHorariosPico(ArrayList<String> array, XYChart.Series series) {
        listaTotalData.clear();
        listaPedidoFiltrados.clear();
        int horasDoDia = 15;
        array = DataManagerAnalytcs.getHorariosArray(horasDoDia, array, listaPedidoFiltrados, dataInicial);
        if (DataManagerAnalytcs.isFinished == true) {
            listaTotalData = DataManagerAnalytcs.getListaTotalData();
            setChartLine(array, series);
        }
    }



    private void setChartLine(ArrayList<String> array, XYChart.Series series) {
        series.getData().clear();
        for(int i = 0; i<listaTotalData.size(); i++){
            series.getData().add(new XYChart.Data<String, Number>(array.get(i), listaTotalData.get(i)));
        }
    }

    private JFXComboBox<String> setupComboBox() {
        JFXComboBox<String> cbPeriodo = new JFXComboBox<>();
        ObservableList<String> cbNome = FXCollections.observableArrayList();
        cbNome.add("7 dias");
        cbNome.add("14 dias");
        cbNome.add("30 dias");
        cbPeriodo.setItems(cbNome);
        cbPeriodo.getSelectionModel().select(0);
        return cbPeriodo;
    }
    private int getDias(int dias, JFXComboBox<String> cbPeriodo) {
        selectedPeriodo = cbPeriodo.getSelectionModel().getSelectedIndex();
        switch (selectedPeriodo) {
            case 0:
                dias = 7;
                break;
            case 1:
                dias = 14;
                break;
            case 2:
                dias = 30;
                break;
        }
        return dias;
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
    private void recuperarDadosPedidos(String data) {
        query = "SELECT * FROM `Ordem_De_Pedido_Triagem` WHERE `data_entrada` <= ?";
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, data);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                listaPedidoFiltrados.add(new OrdemPedido(
                        resultSet.getInt("id"),
                        resultSet.getInt("cliente_id"),
                        resultSet.getString("cliente_nome"),
                        resultSet.getString("cliente_endereco"),
                        resultSet.getString("bairro"),
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

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

    }
    private ObservableList recuperarPorData(ObservableList list, String query, String data) {
        try {
            connection = db_connect.getConnect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, data);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(new OrdemPedido(resultSet.getInt("id"),
                        resultSet.getInt("id"),
                        resultSet.getString("cliente_nome"),
                        resultSet.getString("cliente_endereco"),
                        resultSet.getString("bairro"),
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

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return list;
    }


    private void openProdutosController() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/produtosScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException exception){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
        }
    }
    private void openPedidosController() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/pedidosScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException exception){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
        }
    }
    private void openClientesController() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/clientesScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException exception){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
        }
    }



    private void fecharAbrirMenu() {
        if(leftPane == false){
            borderPane.setRight(anchorPane());
            leftPane = true;
        }else if(leftPane == true){
            borderPane.setRight(null);
            leftPane = false;

        }
    }



    //Metodos de Controle
    @FXML
    private void refreshTable(){
        //
        //Recuperção entrada
        //
            listaPedidos.clear();
            String query = "SELECT * FROM `Pedidos` WHERE `status_id` = ?";
            listaPedidos = db_crud.recuperarPedidosViaStatus(query,1);
                tablePedido.setItems(listaPedidos);
                tablePedido.getSortOrder().add(idCol);
        //
        //Recuperção triagem
        //
            listaPedidosTriagem.clear();
            query = "SELECT * FROM `Pedidos` WHERE `status_id` =?";
            listaPedidosTriagem = db_crud.recuperarPedidosViaStatus(query, 2);
                tablePedidoTriagem.setItems(listaPedidosTriagem);
                tablePedidoTriagem.getSortOrder().add(idColTriagem);
        //Recuperção Finalizado
        //
            listaPedidosFinalizado.clear();
            query = "SELECT * FROM `Pedidos` WHERE `status_id` =?";
            listaPedidosFinalizado = db_crud.recuperarPedidosViaStatus(query,3);
                tablePedidoFinalizado.setItems(listaPedidosFinalizado);
                tablePedidoFinalizado.getSortOrder().add(idColFinalizado);

    }
    private void intentnovoPedido() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/novoPedido.fxml"));
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
    private void intentFuncionarios() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/funcionariosScreen.fxml"));
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
    private void intentListaRupturas() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/listaRupturaScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException exception){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
        }
    }


    private void intentDados(String dadoString, int table_index) {
        intentData intent = intentData.getINSTANCE();
        intent.setDadosteste(dadoString);
        intent.setTableIndex(table_index);
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/detalhesPedido.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    refreshTable();
                }
            });
        } catch (IOException exception){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, exception);
        }
    }
    //Metodos de Controle
}
