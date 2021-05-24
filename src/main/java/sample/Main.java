package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
           /*
        try{
        Parent parent = FXMLLoader.load(getClass().getResource("/tableView.fxml"));
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.show();
        } catch (IOException ex){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

            */


        Parent root = FXMLLoader.load(getClass().getResource("/LoginLogoutScreen.fxml"));
        primaryStage.setTitle("LOGIN - GRAMARI");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();


        /*
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Gramari - Gerenciador de Pedidos");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.show();


         */
    }


    public static void main(String[] args) {
        launch(args);
    }
}
