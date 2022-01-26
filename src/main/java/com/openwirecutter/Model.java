package com.openwirecutter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Properties;

public class Model {

    private final ObservableList<CutOrder> cutList = FXCollections.observableArrayList();

    private final StringProperty feederAxisName = new SimpleStringProperty("X");
    private final StringProperty feederAxisFeedrate = new SimpleStringProperty("1000");
    private final StringProperty cutGCode = new SimpleStringProperty();
    private final StringProperty startingGCode = new SimpleStringProperty();
    private final StringProperty endingGCode = new SimpleStringProperty();
    private final StringProperty gCode = new SimpleStringProperty();

    public Model(Properties programSettings) {

        feederAxisName.set(programSettings.getProperty("feederAxisName","X"));
        feederAxisFeedrate.set(programSettings.getProperty("feederAxisFeedrate","1000"));
        cutGCode.set(programSettings.getProperty("cutGCode","Y1 F25"));
        startingGCode.set(programSettings.getProperty("startingGCode",""));
        endingGCode.set(programSettings.getProperty("endingGCode",""));

        cutList.addListener((ListChangeListener<CutOrder>) change -> {
            generateGCode();
        });
        feederAxisName.addListener((obs, oldValue, newValue) -> {
            generateGCode();
        });
        feederAxisFeedrate.addListener((obs, oldValue, newValue) -> {
            generateGCode();
        });
        cutGCode.addListener((obs, oldValue, newValue) -> {
            generateGCode();
        });
        startingGCode.addListener((obs, oldValue, newValue) -> {
            generateGCode();
        });
        endingGCode.addListener((obs, oldValue, newValue) -> {
            generateGCode();
        });
    }

    public ObservableList<CutOrder> getCutList() {
        return cutList;
    }

    public void addOrder(Double length, Double quantity) {
        cutList.add(new CutOrder(length,quantity.intValue()));
    }

    public void modifyOrder(int index, Double length, Double quantity) {
        CutOrder modifiedOrder = new CutOrder(length, quantity.intValue());

        cutList.set(index,modifiedOrder);
    }

    public void deleteOrder(int index){
        cutList.remove(index);
    }

    private void generateGCode(){
        gCode.set("");
        StringBuilder gCodeBuilder = new StringBuilder();

        if(startingGCode.get() != "") {
            gCodeBuilder.append(startingGCode.get());
            gCodeBuilder.append(System.lineSeparator());
        }

        for(int i=0; i < cutList.size(); i++){
            CutOrder currentOrder = cutList.get(i);

            int numberOfRepetitions = currentOrder.getQuantity();

            for(int j=0; j < numberOfRepetitions; j++){
                gCodeBuilder.append(feederAxisName.get());
                gCodeBuilder.append(currentOrder.getLength());
                gCodeBuilder.append(" F");
                gCodeBuilder.append(feederAxisFeedrate.get());
                gCodeBuilder.append(System.lineSeparator());
                gCodeBuilder.append(cutGCode.get());
                gCodeBuilder.append(System.lineSeparator());
            }
        }

        gCodeBuilder.append(endingGCode.get());

        gCode.set(gCodeBuilder.toString());
    }

    public void setFeederAxisName(String feederAxisName) {
        this.feederAxisName.set(feederAxisName);
    }

    public void setCutGCode(String cutGCode) {
        this.cutGCode.set(cutGCode);
    }

    public void setFeederAxisFeedrate(String feederAxisFeedrate) {
        this.feederAxisFeedrate.set(feederAxisFeedrate);
    }

    public void setStartingGCode(String startingGCode) {
        this.startingGCode.set(startingGCode);
    }

    public void setEndingGCode(String endingGCode) {
        this.endingGCode.set(endingGCode);
    }

    public String getgCode() {
        return gCode.get();
    }

    public StringProperty gCodeProperty() {
        return gCode;
    }

    public String getFeederAxisName() {
        return feederAxisName.get();
    }

    public StringProperty feederAxisNameProperty() {
        return feederAxisName;
    }

    public String getFeederAxisFeedrate() {
        return feederAxisFeedrate.get();
    }

    public StringProperty feederAxisFeedrateProperty() {
        return feederAxisFeedrate;
    }

    public String getCutGCode() {
        return cutGCode.get();
    }

    public StringProperty cutGCodeProperty() {
        return cutGCode;
    }

    public String getStartingGCode() {
        return startingGCode.get();
    }

    public StringProperty startingGCodeProperty() {
        return startingGCode;
    }

    public String getEndingGCode() {
        return endingGCode.get();
    }

    public StringProperty endingGCodeProperty() {
        return endingGCode;
    }

    public Properties getProgramSettings() {

        Properties properties = new Properties();

        properties.setProperty("feederAxisName",feederAxisName.get());
        properties.setProperty("feederAxisFeedrate",feederAxisFeedrate.get());
        properties.setProperty("cutGCode",cutGCode.get());
        properties.setProperty("startingGCode",startingGCode.get());
        properties.setProperty("endingGCode",endingGCode.get());

        return properties;
    }


}
