package mmabdlrgp.Projet_Moviez;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.controller.MovieMoreInfoController;
import mmabdlrgp.Projet_Moviez.controller.MovieOverviewController;
import mmabdlrgp.Projet_Moviez.controller.MovieRateController;
import mmabdlrgp.Projet_Moviez.controller.WaitingController;
import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class Main extends Application {
	
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Movie> movieData = FXCollections.observableArrayList();
	private Map<Integer,Double> currentUserVector;
	
	private RecommandationModel model = new RecommandationModel();
	
	public Main() throws NumberFormatException, IOException {
		String path = "./movies.csv";
		BufferedReader file = new BufferedReader(new FileReader(path));
		String chaine;
		int i = 1;

		while((chaine = file.readLine())!= null)
		{
			if(i > 1)
			{
				String[] tabChaine = chaine.split(",");
				//System.out.println(tabChaine[0]);
				movieData.add(new Movie(Integer.parseInt(tabChaine[0]),tabChaine[1], tabChaine[2]));
			}
			i++;
		}
		file.close(); 
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		/*try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MovieApp");
		initRootLayout();
		showInitializeOverview();
	}
	public ObservableList<Movie> getMovieData() {
		return movieData;
	}
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("./view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showInitializeOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("./view/Waiting.fxml"));
			AnchorPane waitingOverview = (AnchorPane) loader.load();
			rootLayout.setCenter(waitingOverview);
			WaitingController controller = loader.getController();
			controller.setMainApp(this,model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showMovieOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("./view/MovieOverview.fxml"));
			AnchorPane movieOverview = (AnchorPane) loader.load();
			rootLayout.setCenter(movieOverview);
			MovieOverviewController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public RecommandationModel getModel() {
		return model;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}


	public void showMoreInfoMovie(Movie movie) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("./view/MovieMoreInfo.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("More Movie Details");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			MovieMoreInfoController controller = loader.getController();
			controller.setDialogStage(dialogStage, model);
			controller.setMovie(movie);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean showMovieRate(Movie movie) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("./view/MovieRate.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Rate Movie");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			MovieRateController controller = loader.getController();
			controller.setDialogStage(this, dialogStage, model);
			controller.setMovie(movie);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setCurrentVector(Map<Integer,Double> vector) {
		this.currentUserVector = new HashMap<Integer, Double>(vector);
	}
	
	public void putCurrentVector(Integer id, Double rate) {
		currentUserVector.put(id, rate);
		model.setCurrentUserVector(currentUserVector);
	}
	
	public Map<Integer,Double> getCurrentVector(){
		return currentUserVector;
	}

	public static void main(String[] args) {
		
		
		/*RecommandationModel model = new RecommandationModel();
		model.initialize();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Waiting your order chief !");
        while(scanner.hasNextLine()) {
        	String currLine = scanner.nextLine();
        	if(currLine.startsWith("work")) {
        		Map<Integer,Double> recommandation = model.launchRecommandation();
	        	for(Integer movieId : recommandation.keySet()) {
	        		System.out.println(movieId+" "+recommandation.get(movieId));
	        	}
	        }else if(currLine.startsWith("set user")) {
	        	//model.setNbClosestUser(Integer.parseInt(scanner.nextLine()));
        	}else if(currLine.startsWith("set movie")) {
        		model.setNbRecommandation(Integer.parseInt(scanner.nextLine()));
        	}
        }*/
		
		
		launch(args);
	}
}

