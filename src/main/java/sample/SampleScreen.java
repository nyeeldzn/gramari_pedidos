package sample;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;
import helpers.AutoCompleteTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Cliente;

import java.net.URL;
import java.util.*;

public class SampleScreen implements Initializable {

    @FXML
    private AnchorPane AnchorPane;
    private JFXTextField textField = new JFXTextField();
    private ObservableList<Cliente> array = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        array.add(new Cliente(0,"Daniel","","","",4));
        array.add(new Cliente(1,"Floriza","","","",5));
        array.add(new Cliente(2,"Fernando","","","",0));
        array.add(new Cliente(3,"Rafael","","","",3));
        array.add(new Cliente(4,"Joao","","","", 3));

        ArrayList<String> nomes = new ArrayList<>();
        for(int i = 0; i<array.size(); i++){
            nomes.add(array.get(i).getNome());
        }

        /*
        SortedSet<Cliente> entries = new TreeSet<>(Comparator.comparing(Object::toString));

        entries.add(new Cliente(50, "Main Street", "Oakville", "Ontario", "T6P4K9"));
        entries.add(new Cliente(3, "Fuller Road", "Toronto", "Ontario", "B6S4T9"));

        //anchorPane.getChildren().add();

        AutoCompleteTextField<Cliente> text = new AutoCompleteTextField(entries);
        text.getEntryMenu().setOnAction((e) -> {
            ((MenuItem) e.getTarget()).addEventHandler(Event.ANY, event ->
            {
                if (text.getLastSelectedObject() != null)
                {
                    text.setText(text.getLastSelectedObject().getNome());
                    System.out.println(text.getLastSelectedObject().getNome());
                }
            });
        });

         */

        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(nomes);

        autoCompletePopup.setSelectionHandler(event -> {
            textField.setText(event.getObject());

            // you can do other actions here when text completed
        });

        // filtering options
        textField.textProperty().addListener(observable -> {
            autoCompletePopup.filter(string -> string.toLowerCase().contains(textField.getText().toLowerCase()));
            if (autoCompletePopup.getFilteredSuggestions().isEmpty() || textField.getText().isEmpty()) {
                autoCompletePopup.hide();
                // if you remove textField.getText.isEmpty() when text field is empty it suggests all options
                // so you can choose
            } else {
                autoCompletePopup.show(textField);
            }
        });


        StackPane stackPane = new StackPane();
        BorderPane borderPane = new BorderPane();
        stackPane.getChildren().add(borderPane);
        borderPane.setCenter(textField);
        AnchorPane.getChildren().add(stackPane);
    }
}
