package mmabdlrgp.Projet_Moviez.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;

import mmabdlrgp.Projet_Moviez.model.distance.DistanceManager;
import scala.Tuple2;

/**
 * Hello world!
 *
 */
public class RecommandationModel
{

	private final static String RATING_PATH = "./ratings2.csv";
	private final static String MOVIE_PATH = "./movies.csv";
	private final static String USER_PATH = "./user.csv";
	
	private static SparkSession spark;
	private static SQLContext sqlContext;
	private static JavaSparkContext jspark;
	
	
	private static Map<Integer, Double> currentUserNotation = new HashMap<Integer,Double>(); // Vecteur des notes de l'utilisateur
	private static  List<User> userList = new ArrayList<User>(); // List of all the users
	private static List<Integer> movieList = new ArrayList<Integer>();
	private static JavaRDD<Rating> ratingRDD;
	
//	private static int NB_CLOSEST_USER = 5;
	private static int NB_RECOMMANDATION_RESULT = 10;
	//NEW
	private static int NB_RANK = 10;
	private static int NB_ITERATION = 10;
	
	
//	public int getNbUser() {
//		return NB_CLOSEST_USER;
//	}
	
	public int getNbRecommandation() {
		return NB_RECOMMANDATION_RESULT;
	}
	
	public int getRank() {
		return NB_RANK;
	}
	
	public int getIteration() {
		return NB_ITERATION;
	}
	
	public JavaRDD<Rating> convertFromMapToRDDRating(Map<Integer,Double> map){
		List<Tuple2<Integer,Double>> tempList = new ArrayList<Tuple2<Integer,Double>>();
		for(Integer key : map.keySet()) {
			tempList.add(new Tuple2<Integer,Double>(key, map.get(key)));
		}
		
		return jspark.parallelizePairs(tempList).map(elem -> new Rating(-1,elem._1,elem._2));
	}
	
	public Map<Integer,Double> launchRecommandation() {
		
		JavaRDD<Rating> allElems = jspark.union(convertFromMapToRDDRating(currentUserNotation),ratingRDD);
		
		System.out.println("Starting ALS..."); 
        ALS als = new ALS();
        MatrixFactorizationModel model = als.setRank(NB_RANK).setIterations(NB_ITERATION).run(allElems);
        System.out.println("ALS initialized.");
        
        List<Tuple2<Integer,Integer>> currEmptyPlace = new ArrayList<Tuple2<Integer,Integer>>();
        for(Integer movieId : movieList) {
        	if(!currentUserNotation.containsKey(movieId)) {	
        		currEmptyPlace.add(new Tuple2<Integer,Integer>(-1,movieId));
        	}        	
        }
        
        
		Map<Integer, Double> alsResults = model.predict(jspark.parallelize(currEmptyPlace).mapToPair(f -> f)).mapToPair(rating -> new Tuple2<Integer,Double>(rating.product(),rating.rating())).collectAsMap();
        
        
    	
    	/** 
    	 * Get the X first closest movies
    	 */
    	MapExtractor extractor = MapExtractor.INSTANCE;
    	extractor.setMap(alsResults);
    	extractor.setNbResult(NB_RECOMMANDATION_RESULT);
    	List<Tuple2<Integer,Double>> recommandateMovie = extractor.getXFirstResults();
    	Map<Integer,Double> result = new HashMap<Integer,Double>();
    	for(Tuple2<Integer,Double> tuple : recommandateMovie) {
    		result.put(tuple._1, tuple._2);
    		//System.out.println("Movie "+tuple._1+" "+tuple._2);
    	}
    	return result;
	}
	
	public void initialize() {
		System.out.println("Starting initalization");
		
		/**
		 * Create SQL context
		 */
		spark = new Builder()
			     .appName("Reommendation Engine")
			     .master("local")
			     .getOrCreate();	
		jspark = new JavaSparkContext(spark.sparkContext());
		sqlContext = spark.sqlContext();
		
		final DataFrameReader dataFrameReader = spark.read();
		dataFrameReader.option("header", "true");
		
		
		/**
		 * Load datas
		 */
		System.out.println("Start data loading...");
		movieList = dataFrameReader.csv(MOVIE_PATH).javaRDD()
				.map(row -> Integer.parseInt(row.getAs(0))).collect();
		
		ratingRDD = dataFrameReader.csv(RATING_PATH).javaRDD()
				.map(row -> new Rating(Integer.parseInt(row.getAs(0)),Integer.parseInt(row.getAs(1)),Double.parseDouble(row.getAs(2))));

		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser = ratingRDD.groupBy(rating ->rating.user());
		
		// Warning : Need the ratings group by or you will have one user by ratings, and not by idUser
		userList = ratingsGroupByUser.keys().map(id -> new User(id)).collect();

		System.out.println("End data loading.");
         
         /**
          * Load the current user
          */
         
        currentUserNotation = dataFrameReader.csv(USER_PATH).javaRDD()
     			.mapToPair(row -> new Tuple2<Integer,Double>(Integer.parseInt(row.getAs(0)),Double.parseDouble(row.getAs(1)))).collectAsMap();
         
        System.out.println("End of initialisation");
	}    
    
    public Map<Integer, Double> getCurrentUserVector(){
    	return currentUserNotation;
    }
    
    public void setCurrentUserVector(Map<Integer,Double> vector) {
    	currentUserNotation = vector;
    }
    
    public Double suppressUserNotation(Integer movieId) {
    	return currentUserNotation.remove(movieId);
    }
    
//    public void setDistance(String distanceName) {
//    	System.out.println("Set distance as : "+distanceName);
//    	DistanceManager.setDistance(distanceName);
//    }
    
    public void setNbRecommandation(int i) {
    	if(i > 0 && i<movieList.size()) {
    		System.out.println("Set number of recommandation to "+i);
    		NB_RECOMMANDATION_RESULT = i;   	
    	}
    }
    
//    public void setNbClosestUser(int i) {
//    	if(i > 0 && i<userList.size()) {
//    		System.out.println("Set number of user to "+i);
//    		NB_CLOSEST_USER = i;
//    	}
//    }
    
    
    /*
     * 
     * Print functions below
     * 
     */
    
//    /**
//     * Print the nb first ratings
//     * @param javaRDD
//     * @param nb
//     */
//    public static void printRatingJavaRDDFirstContent(JavaRDD<Rating> javaRDD, Integer nb) {
//    	javaRDD.take(nb).forEach(k -> System.out.println("UserID : " + k.user() + "||ProductId: " + k.product() + "|| Test Rating : " + k.rating()));
//    }
//    
//    /**
//     * Print some results for analyse Loaded data.
//     * @param movieRDD
//     * @param ratingRDD
//     * @param userRDD
//     * @param ratingsGroupByMovie
//     * @param ratingsGroupByUser
//     */
//	public static void printExampleLoadedData(JavaRDD<Movie> movieRDD, JavaRDD<Rating> ratingRDD, JavaRDD<User> userRDD, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByMovie, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser) {
//    	System.out.println("Total number of movies : "+movieRDD.count()); // Value = 45843
//		System.out.println("Total number of ratings  : " + ratingRDD.count()); // Value = 26024289
//		System.out.println("Total number of user  : " + userRDD.count()); // Value = 270896
//		System.out.println("Total number of movies rated   : " + ratingsGroupByMovie.count()); // Value = 45115
//		System.out.println("Total number of users who rated movies   : " + ratingsGroupByUser.count()); // Value = 270896
//    }
//
//    public static void printExamplePostUserDF(Dataset<Row> usersDF) {
//    	System.out.println("Total Number of users df : " + usersDF.count()); // Value = 270896
//		Dataset<Row> filteredUsersDF = sqlContext.sql("select * from users where users.userId in (11,12)");
//		
//		List<Row> filteredUsers  = filteredUsersDF.collectAsList();
//		
//		for(Row row : filteredUsers){
//			System.out.println("UserId : " + row.getAs("userId"));
//		}
//    }
//    
//    public static void printExamplePostRatingDF(Dataset<Row> ratingDF) {
//    	
//		System.out.println("Number of rows : (user = 1 and movie = 110 ) : " + ratingDF.count());
//		
//		List<Row> filteredDF = ratingDF.collectAsList();
//		
//		for(Row row : filteredDF){
//			System.out.print("UserId : " + row.getAs("user"));
//			System.out.print("	MovieId : " + row.getAs("product"));
//			System.out.println("	Rating : " + row.getAs("rating"));
//		}
//    }
//    
//    public static void printExamplePostMovieDF(Dataset<Row> movieDF) {
//    	System.out.println("Total Number of movies df : " + movieDF.count());
//		
//		Dataset<Row> filteredMoviesDF = sqlContext.sql("select * from movies where movies.movieId in (19,4000)");
//		
//		List<Row> filteredMovies  = filteredMoviesDF.collectAsList();
//		
//		for(Row row : filteredMovies){
//			System.out.print("MovieId : " + row.getAs("movieId"));
//			System.out.print("	Title : " + row.getAs("title"));
//			System.out.println("	Genres : " + row.getAs("genres"));
//		}
//    }
//
//    public static void printBestRecommandationForUser(MatrixFactorizationModel model, int userId, int nbRecommandation) {
//    	Rating[] recommendedsFor = model.recommendProducts(userId, nbRecommandation);
//        System.out.println("Recommendations for "+userId);
//        for (Rating ratings : recommendedsFor) {
//            System.out.println("MovieId : " + ratings.product() + "-- Rating : " + ratings.rating());
//        }
//    }
//
//    public static void printExamplePostSplittingSet(JavaRDD<Rating> trainingRatingRDD, JavaRDD<Rating> testRatingRDD) {
//    	long numOfTrainingRating = trainingRatingRDD.count();
//        long numOfTestingRating = testRatingRDD.count();
//
//        System.out.println("Number of training Rating : " + numOfTrainingRating);
//        System.out.println("Number of training Testing : " + numOfTestingRating);
//    }
}
