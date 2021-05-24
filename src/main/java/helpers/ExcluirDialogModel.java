package helpers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ExcluirDialogModel {

    public static JFXDialog alertDialogErro(String erro, StackPane stackPane, JFXButton btnConfirmar, JFXButton btnCancelar) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXDialog dialogErro = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        Text texto = new Text();
        texto.setText(erro);
        texto.setFont(Font.font("verdana", FontWeight.LIGHT, FontPosture.REGULAR, 20));
        dialogLayout.setBody(texto);
        dialogLayout.setActions(btnConfirmar ,btnCancelar);
        //dialogErro.show();
        return dialogErro;
    }

}
