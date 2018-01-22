package mmabdlrgp.Projet_Moviez;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;

import mmabdlrgp.Projet_Moviez.model.MapExtractor;
import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.User;
import mmabdlrgp.Projet_Moviez.model.distance.DistanceManager;
import scala.Tuple2;

/**
 * Hello world!
 *
 */
public class App 
{

	private final static String RATING_PATH = "./ratings.csv";
	private final static String MOVIE_PATH = "./movies.csv";
	private final static String LINK_PATH = "./links.csv";
	private final static String USER_PATH = "./user.csv";
	private final static String TAG_PATH = "./tags.csv";
	private final static String GENOME_SCORE_PATH = "./genome-scores.csv";
	private final static String GENOME_TAG_PATH = "./genome-tags.csv";
	
	private static SparkSession spark;
	private static SQLContext sqlContext;
	
	
	
	private static Map<Integer, Double> currentUserNotation; // Vecteur des notes de l'utilisateur
	private static List<User> userList; // List of all the users
	private static JavaPairRDD<Integer,Map<Integer,Double>> alsPairResults; // Matrice résultat d'ALS
	
	private static int NB_CLOSEST_USER = 5;
	
	/**
	 * Retourne l'entier de la ligne row à l'indice index
	 * @param row
	 * @param index
	 * @return
	 */
	public static int getInt(Row row, int index) {
		return Integer.parseInt(row.getString(index));
	}
	
	public static void launchRecommandation() {
		/**
    	 * Affect weight for each user
    	 */
    	Map<Integer,Double> userWeight = new HashMap<Integer,Double>();
    	for(User u : userList) {
    		userWeight.put(u.getUserId(), 1.0);
    	}
    	
    	/**
    	 * Calculate betweenness from each user and the current user
    	 */
    	JavaPairRDD<Integer,Map<Integer,Double>> filterAlsPairResults = alsPairResults.filter( tuple -> {
    		for(Integer movieId : currentUserNotation.keySet()) {
    			if(!tuple._2.containsKey(movieId)) {
    				return false;
    			}
    		}
    		return true;
    	});
    	
    	System.out.println("Start distance calcul");
    	Map<Integer, Double> betweenessVector = DistanceManager.distance(currentUserNotation, filterAlsPairResults.collectAsMap(), userWeight);
    	System.out.println("End turn");
    	
    	/**
    	 * Get the X first closest user
    	 */
    	MapExtractor extractor = MapExtractor.INSTANCE;
    	extractor.setMap(betweenessVector);
    	extractor.setNbResult(NB_CLOSEST_USER);
    	
    	/**
    	 * 
    	 */
    	
	}
	
	public static void initialize() {
		System.out.println("Starting initalization");
		/**
		 * Create SQL contex
		 */
		spark = new Builder()
			     .appName("Reommendation Engine")
			     .master("local")
			     .getOrCreate();		
		
		sqlContext = spark.sqlContext();
		
		final DataFrameReader dataFrameReader = spark.read();
		dataFrameReader.option("header", "true");
		
		
		/**
		 * Load datas
		 */
		System.out.println("Start data loading...");
		//JavaRDD<Movie> movieRDD = dataFrameReader.csv(MOVIE_PATH).javaRDD()
		//		.map(row -> new Movie(Integer.parseInt(row.getAs(0)),row.getAs(1),row.getAs(2))).cache();
		
		JavaRDD<Rating> ratingRDD = dataFrameReader.csv(RATING_PATH).javaRDD()
				.map(row -> new Rating(Integer.parseInt(row.getAs(0)),Integer.parseInt(row.getAs(1)),Double.parseDouble(row.getAs(2))));

		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser = ratingRDD.groupBy(rating ->rating.user());
		
		// Warning : Need the ratings group by or you will have one user by ratings, and not by idUser
		userList = ratingsGroupByUser.keys().map(id -> new User(id)).collect();

		System.out.println("End data loading.");
		
		
		
		/**
		 * Split into training and testing sets
		 */
		System.out.println("Start set splitting...");
        JavaRDD<Rating>[] ratingSplits = ratingRDD.randomSplit(new double[] { 0.8, 0.2 });
        JavaRDD<Rating> trainingRatingRDD = ratingSplits[0];
        JavaRDD<Rating> testRatingRDD = ratingSplits[1];
        System.out.println("End set splitting.");
        //printExamplePostSplittingSet(trainingRatingRDD,testRatingRDD);

        /**
         *  Learning the prediction model using ALS (Alternating Least Squares)
         */
        System.out.println("Starting ALS..."); // 13:43:03
        ALS als = new ALS();
        MatrixFactorizationModel model = als.setRank(20).setIterations(10).run(trainingRatingRDD);
        System.out.println("ALS initialized.");
        
        JavaPairRDD<Integer, Integer> testUserMovieRDD = testRatingRDD.mapToPair(rating -> new Tuple2<Integer, Integer>(rating.user(), rating.product()));
        JavaRDD<Rating> alsResults = model.predict(testUserMovieRDD).cache();
        
         alsPairResults = alsResults.mapToPair(rating -> {
        	Tuple2<Integer,Map<Integer,Double>> result = new Tuple2<Integer,Map<Integer,Double>>(rating.user(),new HashMap<Integer,Double>());
        	result._2.put(rating.product(),rating.rating());
        	return result;
        }).reduceByKey((map1,map2) -> {
        	map1.putAll(map2);
        	return map1;
        });
         
         /**
          * Load the current user
          */
         
        currentUserNotation = dataFrameReader.csv(USER_PATH).javaRDD()
     			.mapToPair(row -> new Tuple2<Integer,Double>(Integer.parseInt(row.getAs(0)),Double.parseDouble(row.getAs(1)))).collectAsMap();
         
        System.out.println("End of initialisation");
	}
	
	
    public static void main( String[] args )
    {
    	initialize();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Waiting your order chief !");
        while(scanner.hasNextLine()) {
        	scanner.nextLine();
        	launchRecommandation();
        }
    }
    
    
    
    public static Map<Integer, Double> getCurrentUserVector(){
    	return currentUserNotation;
    }
    
    public static void addOrModifyUserNotation(Integer movieId, Double note) {
    	if(note >= 0.0 || note <= 5.0) {
    		currentUserNotation.put(movieId,note);
    	}else {
    		System.out.println("Error :: Note isn't in the good format");
    	}
    }
    
    public static Double suppressUserNotation(Integer movieId) {
    	return currentUserNotation.remove(movieId);
    }
    
    public static void setDistance(String distanceName) {
    	DistanceManager.setDistance(distanceName);
    }
    
    
    /*
     * 
     * Print functions below
     * 
     */
    
    /**
     * Print the nb first ratings
     * @param javaRDD
     * @param nb
     */
    public static void printRatingJavaRDDFirstContent(JavaRDD<Rating> javaRDD, Integer nb) {
    	javaRDD.take(nb).forEach(k -> System.out.println("UserID : " + k.user() + "||ProductId: " + k.product() + "|| Test Rating : " + k.rating()));
    }
    
    /**
     * Print some results for analyse Loaded data.
     * @param movieRDD
     * @param ratingRDD
     * @param userRDD
     * @param ratingsGroupByMovie
     * @param ratingsGroupByUser
     */
	public static void printExampleLoadedData(JavaRDD<Movie> movieRDD, JavaRDD<Rating> ratingRDD, JavaRDD<User> userRDD, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByMovie, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser) {
    	System.out.println("Total number of movies : "+movieRDD.count()); // Value = 45843
		System.out.println("Total number of ratings  : " + ratingRDD.count()); // Value = 26024289
		System.out.println("Total number of user  : " + userRDD.count()); // Value = 270896
		System.out.println("Total number of movies rated   : " + ratingsGroupByMovie.count()); // Value = 45115
		System.out.println("Total number of users who rated movies   : " + ratingsGroupByUser.count()); // Value = 270896
    }

    public static void printExamplePostUserDF(Dataset<Row> usersDF) {
    	System.out.println("Total Number of users df : " + usersDF.count()); // Value = 270896
		Dataset<Row> filteredUsersDF = sqlContext.sql("select * from users where users.userId in (11,12)");
		
		List<Row> filteredUsers  = filteredUsersDF.collectAsList();
		
		for(Row row : filteredUsers){
			System.out.println("UserId : " + row.getAs("userId"));
		}
    }
    
    public static void printExamplePostRatingDF(Dataset<Row> ratingDF) {
    	
		System.out.println("Number of rows : (user = 1 and movie = 110 ) : " + ratingDF.count());
		
		List<Row> filteredDF = ratingDF.collectAsList();
		
		for(Row row : filteredDF){
			System.out.print("UserId : " + row.getAs("user"));
			System.out.print("	MovieId : " + row.getAs("product"));
			System.out.println("	Rating : " + row.getAs("rating"));
		}
    }
    
    public static void printExamplePostMovieDF(Dataset<Row> movieDF) {
    	System.out.println("Total Number of movies df : " + movieDF.count());
		
		Dataset<Row> filteredMoviesDF = sqlContext.sql("select * from movies where movies.movieId in (19,4000)");
		
		List<Row> filteredMovies  = filteredMoviesDF.collectAsList();
		
		for(Row row : filteredMovies){
			System.out.print("MovieId : " + row.getAs("movieId"));
			System.out.print("	Title : " + row.getAs("title"));
			System.out.println("	Genres : " + row.getAs("genres"));
		}
    }

    public static void printBestRecommandationForUser(MatrixFactorizationModel model, int userId, int nbRecommandation) {
    	Rating[] recommendedsFor = model.recommendProducts(userId, nbRecommandation);
        System.out.println("Recommendations for "+userId);
        for (Rating ratings : recommendedsFor) {
            System.out.println("MovieId : " + ratings.product() + "-- Rating : " + ratings.rating());
        }
    }

    public static void printExamplePostSplittingSet(JavaRDD<Rating> trainingRatingRDD, JavaRDD<Rating> testRatingRDD) {
    	long numOfTrainingRating = trainingRatingRDD.count();
        long numOfTestingRating = testRatingRDD.count();

        System.out.println("Number of training Rating : " + numOfTrainingRating);
        System.out.println("Number of training Testing : " + numOfTestingRating);
    }
}
