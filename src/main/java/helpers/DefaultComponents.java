package helpers;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import models.Produto;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class DefaultComponents {

    private static JFXDialog dialog;
    public static JFXButton buttonIcon(String texto, String glyph, double larguraPadrao){
        JFXButton button = new JFXButton(texto, FontIcon(glyph));
        button.setPrefSize(larguraPadrao, 50);
        button.setStyle("-fx-background-color: white; -fx-background-radius: 15;  -fx-border-color: black; -fx-border-radius: 15");
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }
    public static FontAwesomeIconView FontIcon(String glyphName){
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setGlyphName(glyphName);
        icon.setSize("25.0");
        return icon;
    }
    public static Text defaultText(String texto) {
        Text text = new Text(texto);
        text.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 20));
        text.setStyle("-fx-fill: black");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(1200/6);
        return text;
    }
    public static JFXButton defaultButton(String texto){
        JFXButton button = new JFXButton();
        button.setStyle("-fx-background-color: white");
        button.setText(texto);
        button.setPrefHeight(40);
        return button;
    }
    public static JFXPasswordField passwordfieldPadrao(double size){
        JFXPasswordField textField = new JFXPasswordField();
        textField.setStyle("-fx-background-color: lightgrey");
        textField.setPrefWidth(size);
        return textField;
    }
    public static JFXTextField textFieldPadrao(double size){
        JFXTextField textField = new JFXTextField();
        textField.setStyle("-fx-background-color: white");
        textField.setPrefWidth(size);
        textField.setPrefHeight(40);
        return textField;
    }
    public static VBox defaultVBox(){
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }
    public static HBox defaultHBox(){
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    public static VBox card(){
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10,10,10,10));
        card.setSpacing(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0.0 , 0 );");
        card.setPrefWidth(1200/6);
        card.setPrefHeight(150);
        return card;
    }

    public static void alertDialog3Itens(JFXButton action, JFXTextField edt1, StackPane stackPane) throws IOException {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialogLayout.setBody(
                formulario3itens(action,edt1 )
        );
        dialog.show();
    }
    public static AnchorPane formulario3itens(JFXButton action, JFXTextField edt1){
        AnchorPane pane = new AnchorPane();
        VBox vboxPrincipal = defaultVBox();
        JFXButton btnAction = action;
        JFXButton btnCancelar = defaultButton("CANCELAR");
        edt1 = textFieldPadrao(400);
        edt1.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        JFXComboBox<String> cbPermissoes = new JFXComboBox();
        ObservableList<String> listaPermissoes = FXCollections.observableArrayList(
                "Visitante","Entregador","Operador","Admin"
        );
        cbPermissoes.setItems(listaPermissoes);
        cbPermissoes.getSelectionModel().select(0);
        JFXPasswordField edtSenha = passwordfieldPadrao(550);

        HBox row1 = defaultHBox();
        HBox row2 = defaultHBox();
        HBox row3 = defaultHBox();

        VBox R1C1 = defaultVBox();
        VBox R1C2 = defaultVBox();
        VBox R2C1 = defaultVBox();

        R1C1.getChildren().addAll(
                defaultText("NOME"),
                edt1
        );
        R1C2.getChildren().addAll(
                defaultText("PERMISSÕES"),
                cbPermissoes
        );
        R2C1.getChildren().addAll(
                defaultText("SENHA"),
                edtSenha
        );

        row1.getChildren().addAll(
                R1C1,
                R1C2);

        row2.getChildren().addAll(
                R2C1
        );

        row3.getChildren().addAll(
                btnAction,
                btnCancelar
        );

        row3.setAlignment(Pos.CENTER_RIGHT);

        vboxPrincipal.getChildren().addAll(row1, row2, row3);

        btnCancelar.setOnAction((event -> {
            dialog.close();
        }));
        pane.getChildren().add(vboxPrincipal);
        return pane;
    }

    public static void alertDialogBuscaProdutosListaRuptura(JFXButton action, TableView<Produto> tableView, JFXTextField edt1, StackPane stackPane) throws IOException {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialogLayout.setBody(
                formularioProdutosLista(action, edt1, tableView)
        );
        dialog.show();
    }
    public static AnchorPane formularioProdutosLista(JFXButton action, JFXTextField edt, TableView table2){
        AnchorPane pane = new AnchorPane();
        VBox vboxPrincipal = defaultVBox();
        JFXButton btnCancelar = defaultButton("SAIR");
        TableView<Produto> table = table2;
        TableColumn<Produto, Integer> idCol = new TableColumn<>();
        TableColumn<Produto, String> nomeCol = new TableColumn<>();
        table.getColumns().addAll(idCol, nomeCol);

        JFXTextField edt1 = edt;
        edt1 = textFieldPadrao(500);
        edt1.setTextFormatter(new TextFormatter<Object>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));


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
                edt1, action
                );

        row3.getChildren().addAll(
                btnCancelar
        );

        row3.setAlignment(Pos.CENTER_RIGHT);

        vboxPrincipal.getChildren().addAll(vBox, table, row3);

        btnCancelar.setOnAction((event -> {
            dialog.close();
        }));
        pane.getChildren().add(vboxPrincipal);
        return pane;
    }
    public static Integer countOfChar (String string){
        int count = 0;

        //Counts each character except space
        for(int i = 0; i < string.length(); i++) {
            if(string.charAt(i) != ' ')
                count++;
        }

        //Displays the total number of characters present in the given string
        System.out.println("Total number of characters in a string: " + count);
        return count;
    }
    public static File fileChooserSave(StackPane stackPane, String descFile, String extFile) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(descFile, extFile);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stackPane.getScene().getWindow());

        return file;
    }
    public static File fileChooserSelect(StackPane stackPane, String descFile, String extFile) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(descFile, extFile);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(stackPane.getScene().getWindow());

        return file;
    }


}

