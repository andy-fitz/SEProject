package client.java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
    Box which appears to ask for user input.
 */

public class ConfirmBox{

    private static boolean answer;

    public static boolean display(String title, String message, Stage mainStage){
        // Setting up pop up Window
        Stage window = new Stage();
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(250);
        window.setX(mainStage.getScene().getWidth()/2 - window.getMinWidth()/2);
        window.setY(mainStage.getScene().getHeight()/2);

        // Creating Nodes
        Label label = new Label();
        label.setText(message);
        label.setTextFill(Color.rgb(232, 142, 39));
        Button yesButton = new Button("YES");
        Button noButton = new Button("NO");

        // Action Listeners
        yesButton.setOnAction(e-> {
            answer = true;
            window.close();
        });
        noButton.setOnAction(e ->{
            answer = false;
            window.close();
        });

        // Applying Layout
        VBox yLayout = new VBox(20);
        yLayout.setPadding(new Insets(20, 20, 20, 20));
        HBox xLayout = new HBox(20);
        yLayout.setAlignment(Pos.CENTER);
        xLayout.setAlignment(Pos.CENTER);
        xLayout.getChildren().addAll(yesButton,noButton);
        yLayout.getChildren().addAll(label, xLayout);

        // Showing Window
        Scene scene = new Scene(yLayout);
        scene.getStylesheets().add("/client/resources/css/alertBox.css");
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

}
