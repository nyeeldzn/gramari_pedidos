package helpers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.OrdemPedido;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class DataManagerAnalytcs {
    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static ArrayList<Integer> listaTotalData = new ArrayList<>();
    private static ArrayList<Integer> listaTotalHoras = new ArrayList<>();
    private static ArrayList<Integer> listaHorasTriagem = new ArrayList<>();



    public static boolean isFinished = false;
    public static ArrayList<Integer> getListaTotalData() {
        return listaTotalData;
    }
    public static ArrayList<Integer> getListaTotalHorasTriagem() {
        return listaHorasTriagem;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public static PedidoEstatistica createEstatistica(int pedido_id, String hTriagem, String hCheckout, String hFinalizado, String horaPedido) {

        int horario_inicial = toMinutes(hTriagem);

        int horario_checkout = toMinutes(hCheckout);
        int horario_final = toMinutes(hFinalizado);
        double mt = horario_checkout - horario_inicial;
        double me = horario_final - horario_checkout;
        int mp = toHour(hFinalizado);

        PedidoEstatistica estatistica = new PedidoEstatistica(pedido_id, mt, me, mp);
        System.out.println("Tempo de Listagem: " + mt + " Tempo de Entrega: " + me + " Horario Pedido: " + horaPedido);
        return estatistica;
    }

    public static ArrayList<String> getDatasArray(int dias, ArrayList<String> array, String dataInicial) {
        try {
            array = filtrarDadosPorData(dataInicial, dias);
            System.out.println("Datas de busca: " + array);
            boolean next = recuperarQtdPedidos(array, dias);
            if(next == true){
                System.out.println(listaTotalData);
                isFinished = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return array;
    }
    public static ArrayList<String> getDatasArrayMT(int dias, ArrayList<String> array, String dataInicial) {
        try {
            array = filtrarDadosPorData(dataInicial, dias);
            System.out.println("Datas de busca: " + array);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return array;
    }
    public static ArrayList<String> getHorariosArray(int dias, ArrayList<String> array, ObservableList listaPedidoFiltrados, String dataInicial) {

        array = new ArrayList<String>();
        System.out.println("Horas informadas: " + dias);
        for(int i = 0; i < dias; i++){
            array.add(String.valueOf(6+i));
        }
        System.out.println("Horas de busca: " + array);
        boolean next = recuperarQtdPedidosPhora(array, dias);
        if(next == true){
            System.out.println(listaTotalData);
            isFinished = true;
        }

        return array;
    }
    public static ArrayList<String> getMediaTempoTriagem(int dias, ArrayList<String> array, ObservableList listaPedidoFiltrados, String dataInicial) {

        dias = 3;
        array = new ArrayList<String>();
        System.out.println("Horas informadas: " + dias);

        //array.add(String.valueOf());
        array.add("1");
        array.add("2");
        array.add("3");

        System.out.println("Horas de busca: " + array);
        boolean next = recuperarHorarioTriagem(array, dias);
        if(next == true){
            System.out.println(listaHorasTriagem);
            isFinished = true;
        }

        return array;
    }
    public static ArrayList<String> getMTporDia(int dias, ArrayList<String> array) {

        System.out.println("Horas de busca: " + array);
        boolean next = recuperarHorarioTriagem(array, dias);
        if(next == true){
            System.out.println(listaHorasTriagem);
            isFinished = true;
        }

        return array;
    }
    private static boolean recuperarQtdPedidos(ArrayList<String> arrayDatas, int qtdDias) {
        String query;
        query = "SELECT * FROM `Pedidos` WHERE `data_entrada` =?";
        boolean isFinalizado = false;
        for (int i = 0; i < qtdDias; i++) {
            try {
                System.out.println("Busca na Data: " + arrayDatas.get(i));
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, arrayDatas.get(i));
                resultSet = preparedStatement.executeQuery();
                ObservableList<OrdemPedido> listaTemp = FXCollections.observableArrayList();
                listaTemp.clear();
                while (resultSet.next()) {
                    listaTemp.add(new OrdemPedido(
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
                    //System.out.println(resultSet.getString("cliente_nome"));
                }
                //System.out.println("Quantidade de pedidos na data: " + arrayDatas.get(i) + " É: " + listaTemp.size());
                listaTotalData.add(listaTemp.size());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (i == qtdDias - 1) {
                isFinalizado = true;
            }
        }
        return isFinalizado;
    }
    private static boolean recuperarQtdPedidosPhora(ArrayList<String> arrayDatas, int qtdHoras) {
        String query;
        query = "SELECT * FROM `Pedido_Estatisticas` WHERE `h.p` =?";
        boolean isFinalizado = false;
        for (int i = 0; i < qtdHoras; i++) {
            try {
                System.out.println("Busca no Horario: " + arrayDatas.get(i));
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, arrayDatas.get(i));
                resultSet = preparedStatement.executeQuery();
                ObservableList<Integer> listaTemp = FXCollections.observableArrayList();
                listaTemp.clear();
                while (resultSet.next()) {
                    listaTemp.add(resultSet.getInt("h.p"));
                    //System.out.println(resultSet.getString("cliente_nome"));
                }
                //System.out.println("Quantidade de pedidos na data: " + arrayDatas.get(i) + " É: " + listaTemp.size());
                listaTotalData.add(listaTemp.size());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (i == qtdHoras - 1) {
                isFinalizado = true;
            }
        }
        return isFinalizado;
    }
    private static boolean recuperarHorarioTriagem(ArrayList<String> arrayDatas, int qtdHoras) {
        String query;
        query = "SELECT * FROM `Pedido_Estatisticas` WHERE `m.t` =?";
        boolean isFinalizado = false;
        for (int i = 0; i < qtdHoras; i++) {
            try {
                System.out.println("Busca no tempo de Triagem: " + arrayDatas.get(i));
                connection = db_connect.getConnect();
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, arrayDatas.get(i));
                resultSet = preparedStatement.executeQuery();
                ObservableList<Integer> listaTemp = FXCollections.observableArrayList();
                listaTemp.clear();
                while (resultSet.next()) {
                    listaTemp.add(resultSet.getInt("m.t"));
                    System.out.println(resultSet.getInt("m.t"));
                }
                System.out.println("Quantidade de pedidos no tempo de triagem: " + arrayDatas.get(i) + " É: " + listaTemp.size());
                listaHorasTriagem.add(listaTemp.size());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (i == qtdHoras - 1) {
                isFinalizado = true;
            }
        }
        return isFinalizado;
    }
    private static ArrayList<String> filtrarDadosPorData(String data, int qtdDias) throws ParseException {
        System.out.println("Iniciando busca por data");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date data_comparacao = format.parse(data);
        ArrayList<String> listaDatas = new ArrayList<>();
        listaDatas.add(data);
        LocalDateTime localDateTime = data_comparacao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        for (int i = 0; i < qtdDias; i++) {
            //System.out.println("Data original: "+ localDateTime);

            LocalDateTime localDateTime1 = localDateTime.minusDays(1);

            //System.out.println("Menos um dia: " + localDateTime1);

            Date date = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());

            //System.out.println(localDateTime1);
            //System.out.println(date);
            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
            //Date data_formatada = dt1.format(date);
            //System.out.println(dt1.format(date));
            listaDatas.add(dt1.format(date));
            //listaDatas.add(localDateTime1.toString());

            localDateTime = localDateTime1;
        }


        return listaDatas;
    }
    private static int mediaHorario (String h1, String h2){
        int media = 0;


        return media;
    }

    public static int getMTAVGtotal (){
        int value = 0;

        String query = "SELECT  AVG (`m.t`) FROM `Pedido_Estatisticas`";
        try {
            Connection connection = db_connect.getConnect();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                value = (int) resultSet.getFloat(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return value;
    }
    public static int getMEAVGtotal (){
        int value = 0;

        String query = "SELECT  AVG (`m.e`) FROM `Pedido_Estatisticas`";
        try {
            Connection connection = db_connect.getConnect();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                value = (int) resultSet.getFloat(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return value;
    }
    public static int getPedidostotal (){
        int value = 0;
        try {
            Connection connection = db_connect.getConnect();

            Statement s = connection.createStatement();
            ResultSet r = s.executeQuery("SELECT COUNT(*) AS rowcount FROM Pedidos");
            r.next();
            value= r.getInt("rowcount");
            r.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return value;
    }
    public static int getMPC (){
        int value = 0;

        String query = "SELECT  AVG (`qtdPedidos`) FROM `Pedidos`";
        try {
            Connection connection = db_connect.getConnect();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT AVG (qtdPedidos) FROM Clientes WHERE qtdPedidos > 0");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                value = (int) resultSet.getFloat(1);
                System.out.println(resultSet.getFloat(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return value;
    }




    public static int toMinutes (String sDur){
        String[] hoursMin = sDur.split(":");

        int iHour = Integer.parseInt(hoursMin[0]);
        int iMin = Integer.parseInt(hoursMin[1]);

        int hoursInMinutes = iHour * 60;
        int horarioFinal = hoursInMinutes + iMin;

        System.out.println("HORARIO: " + sDur + "CONVERTIDO PARA MINUTOS: " + horarioFinal);
        return horarioFinal;
    }
    public static int toHour (String sDur){
        String[] hoursMin = sDur.split(":");

        int iHour = Integer.parseInt(hoursMin[0]);
        //int iMin = Integer.parseInt(hoursMin[1]);



        System.out.println("HORA RECEBIDO: " + sDur + "CONVERTIDO PARA HORAS: " + iHour);
        return iHour;
    }



}
