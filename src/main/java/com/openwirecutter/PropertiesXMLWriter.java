package com.openwirecutter;

import javafx.scene.control.Alert;

import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesXMLWriter {

    public static void storePropertiesToXML(Properties properties, String filename){
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            properties.storeToXML(fos, "");
            fos.close();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Cannot save file " + filename + ".");
            alert.showAndWait();
        }
    }
}
