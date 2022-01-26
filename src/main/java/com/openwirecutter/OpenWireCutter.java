package com.openwirecutter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.openwirecutter.PropertiesXMLWriter.storePropertiesToXML;

public class OpenWireCutter extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //loading settings
        Properties programSettings = new Properties();
        try {
            FileInputStream fio = new FileInputStream("settings.xml");
            programSettings.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {
            storePropertiesToXML(programSettings,"settings.xml");
        }

        Model model = new Model(programSettings);


        FXMLLoader fxmlLoader = new FXMLLoader(OpenWireCutter.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainWindowController mainWindowController = fxmlLoader.getController();

        mainWindowController.init(model,stage);

        stage.setTitle("Open Wire Cutter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}