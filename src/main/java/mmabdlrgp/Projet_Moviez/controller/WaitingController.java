package mmabdlrgp.Projet_Moviez.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import mmabdlrgp.Projet_Moviez.Main;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class WaitingController {
	
	@FXML
	private Button initializeButton;

	@FXML
	private Label label;
	
    private Main mainApp;
    private RecommandationModel model;

    
    private boolean isClicked = false;
    
    public WaitingController() {
    	
    }
    
    @FXML
    private void initialize() {
    }

    public void setMainApp(Main mainApp, RecommandationModel model) {
        this.mainApp = mainApp;
        this.model = model;
        label.setTextAlignment(TextAlignment.CENTER);;
        label.setText("We need to initalize our ALS matrix. It will take some times (~5min).\nSo click on the button below and go take a coffee !");
    }

    @FXML
    public void handleInitialize() {
    	if(!isClicked) {
    		isClicked = true;
    		initializeButton.setText("Wait a moment...");
        	model.initialize();
        	mainApp.showMovieOverview();
    	}
    }
}
