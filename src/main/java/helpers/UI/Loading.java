package helpers.UI;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;

public class Loading {

    public static VBox newLoadingCircular(StackPane stackPane){
        double sHeight = stackPane.getHeight();
        double sWidth = stackPane.getWidth();
        String res = "src/main/resources/loading.gif";

        VBox vbox = new VBox();
        vbox.setPrefHeight(sHeight);
        vbox.setPrefWidth(sWidth);
        vbox.setStyle("-fx-background-color: rgba(204, 204, 204, 0.5)");
        vbox.setAlignment(Pos.CENTER);

        File file = new File(res);
        Image loading = new Image(file.toURI().toString());
        ImageView imageLoading = new ImageView(loading);
        imageLoading.setFitHeight(50);
        imageLoading.setFitWidth(50);

        vbox.getChildren().add(imageLoading);
        return vbox;
    }


}
