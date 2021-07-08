package sample;

import com.jfoenix.controls.*;
import helpers.AlertDialogModel;
import helpers.AuthenticationSystem;
import helpers.DefaultComponents;
import helpers.Database.db_connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Usuario;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginLogoutController implements Initializable {


    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXButton btnClose;

    @FXML
    private JFXButton btnConfig;

    @FXML
    private JFXTextField edtUsername;

    @FXML
    private JFXPasswordField edtPassword;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        verificarConnection();
        btnConfig.setOnAction((e) -> {
            configHostDialog();
        });
        
        setupStackPane();
        edtUsername.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));
        edtUsername.requestFocus();
        edtUsername.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    edtPassword.requestFocus();
                    break;
            }
        });
        edtPassword.setOnKeyPressed((e) -> {
            switch (e.getCode()){
                case ENTER:
                    authMethod();
                    break;
            }
        });

        btnLogin.setOnAction((e) -> {
            authMethod();
        });

        btnClose.setOnAction((e) -> {
            Stage stage = (Stage) stackPane.getScene().getWindow();
            // do what you have to do
            stage.close();
        });

        stackPane.setOnMousePressed(pressEvent -> {
            stackPane.setOnMouseDragged(dragEvent -> {
                System.out.println("Movendo a Janela");
                Stage stage = (Stage) stackPane.getScene().getWindow();
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });


    }

    private void configHostDialog() {
        //recuperar raiz da aplicação
        String caminho = "";
        try {
            caminho = LoginLogoutController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            //caminho = caminho.substring(1, caminho.lastIndexOf('/') + 1);
            System.out.println(caminho);

            //AuthenticationSystem.WConfig(caminho);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonCancelar = new JFXButton("Cancelar");
        JFXButton buttonSalvar = new JFXButton("Salvar");

        JFXDialog dialogErro = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);

        VBox vBox = DefaultComponents.defaultVBox();

        JFXTextField field = DefaultComponents.textFieldPadrao(100);
        Text texto = new Text();
        texto.setText("Informe o HOST: ");
        texto.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 20));
        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialogErro.close();
        });
        String finalCaminho = caminho;
        buttonSalvar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            db_connect.setHOST(field.getText().trim());
            boolean state = verificarConnection();
            if(state){

                /*        implementacao de salvar arquivo de config
                try {
                    System.out.println("Tentando salvar aquivo de config");
                    AuthenticationSystem.setConfig(finalCaminho);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                         //implementacao de salvar arquivo de config
                 */

                dialogErro.close();
            }
        });
        vBox.getChildren().addAll(texto, field);
        dialogLayout.setBody(vBox);
        dialogLayout.setActions(buttonSalvar, buttonCancelar);

        dialogErro.show();




    }


    private void setupStackPane() {
        // new Image(url)
        Image image = new Image(getClass().getResource("/loginbg.jpeg").toExternalForm());
// new BackgroundSize(width, height, widthAsPercentage, heightAsPercentage, contain, cover)
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
// new BackgroundImage(image, repeatX, repeatY, position, size)
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
// new Background(images...)
        Background background = new Background(backgroundImage);
        borderPane.setBackground(background);
    }


    private void authMethod() {
        if (edtUsername.getText().isEmpty()) {
        JFXDialog dialog = AlertDialogModel.alertDialogErro("Preencha o campo de usuario", stackPane);
        dialog.show();
        }else{
            if(edtPassword.getText().isEmpty()){
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Preencha o campo de senha", stackPane);
                dialog.show();
            }else{
                boolean state = AuthenticationSystem.loginWithUsernameAndPassword(edtUsername.getText().toUpperCase().trim(), edtPassword.getText().trim());
                if(state == true){
                    iniciarHome();
                }else {
                    JFXDialog dialog = AlertDialogModel.alertDialogErro("Usuario ou senha incorreta, tente novamente", stackPane);
                    dialog.show();
                }
            }
        }
    }

    private boolean verificarConnection() {
        boolean state = false;
        String host = db_connect.getHost();
        if(host.equals("")){
            JFXDialog dialog = AlertDialogModel.alertDialogErro("Favor realizar conexao ao DB", stackPane);
            state = false;
            dialog.show();
        }else{
            ObservableList<Usuario> users  = FXCollections.observableArrayList();
            String query = "SELECT * FROM Usuarios";
            try {
                Connection conn = db_connect.getConnect();
                PreparedStatement p = conn.prepareStatement(query);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    users.add(new Usuario(
                            r.getInt("id"),
                            r.getString("nome"),
                            "",
                            r.getInt("privilegio")
                    ));
                }
            }catch (SQLException ex){
                ex.printStackTrace();
            }
            if(users.size() <= 0){
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Houve um problema ao tentar se conectar", stackPane);
                dialog.show();
                state = false;
            }else{
                JFXDialog dialog = AlertDialogModel.alertDialogErro("Conectado com sucesso", stackPane);
                dialog.show();
                state = true;
            }
        }

        return state;
    }

    private void iniciarHome() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Scene cenaAtual = btnLogin.getScene();
        Stage stageAtual = (Stage) cenaAtual.getWindow();


        cenaAtual = new Scene(parent);

        //stageAtual.setTitle("Gerenciador de Pedidos - Supermercados Gramari Eireli");
        stageAtual.setScene(cenaAtual);

    }

}
