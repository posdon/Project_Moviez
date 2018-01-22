package mmabdlrgp.Projet_Moviez;

import java.util.Map;
import java.util.Scanner;

import javafx.scene.layout.BorderPane;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		
		/*
		RecommandationModel.initialize();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Waiting your order chief !");
        while(scanner.hasNextLine()) {
        	String currLine = scanner.nextLine();
        	if(currLine.startsWith("work")) {
        		Map<Integer,Double> recommandation = RecommandationModel.launchRecommandation();
	        	for(Integer movieId : recommandation.keySet()) {
	        		System.out.println(movieId+" "+recommandation.get(movieId));
	        	}
	        }else if(currLine.startsWith("set user")) {
	        	RecommandationModel.setNbClosestUser(Integer.parseInt(scanner.nextLine()));
        	}else if(currLine.startsWith("set movie")) {
        		RecommandationModel.setNbRecommandation(Integer.parseInt(scanner.nextLine()));
        	}
        }*/
		
		
		launch(args);
	}
}

