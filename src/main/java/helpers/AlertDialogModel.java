package helpers;

import com.itextpdf.text.Document;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AlertDialogModel {

    public static JFXDialog alertDialogErro(String erro, StackPane stackPane) {
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
        //dialogErro.show();
        return dialogErro;
    }

    public static JFXDialog dialogPreviewPDF(Document doc, StackPane stackPane) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton buttonCancelar = new JFXButton("OK");
        JFXDialog dialogErro = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialogErro.setPrefWidth(800);
        dialogErro.setPrefHeight(500);
        dialogLayout.setPrefSize(800,500);

        VBox vBoxPrincipal = DefaultComponents.defaultVBox();
        HBox hboxTopBar = DefaultComponents.defaultHBox();
        JFXButton btnSalvarPDF = DefaultComponents.buttonIcon("Salvar PDF", "ARCHIVE", 100);;
        JFXButton btnImprimirPDF = DefaultComponents.buttonIcon("Imprimir", "PRINT", 100);
        hboxTopBar.getChildren().addAll(btnSalvarPDF, btnImprimirPDF);
        hboxTopBar.setSpacing(15);
        hboxTopBar.setPadding(new Insets(10,10,10,10));
        vBoxPrincipal.getChildren().addAll(hboxTopBar);





        buttonCancelar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) ->{
            dialogErro.close();
        });
        dialogLayout.setBody(vBoxPrincipal);
        dialogLayout.setActions(buttonCancelar);
        dialogErro.show();
        return dialogErro;
    }

}
