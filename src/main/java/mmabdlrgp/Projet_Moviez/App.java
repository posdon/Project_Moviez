package mmabdlrgp.Projet_Moviez;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.User;

/**
 * Hello world!
 *
 */
public class App 
{

	private final static String RATING_PATH = "./ratings.csv";
	private final static String MOVIE_PATH = "./movies.csv";
	private final static String LINK_PATH = "./links.csv";
	private final static String TAG_PATH = "./tags.csv";
	private final static String GENOME_SCORE_PATH = "./genome-scores.csv";
	private final static String GENOME_TAG_PATH = "./genome-tags.csv";
	
	private static SparkSession spark;
	private static SQLContext sqlContext;
	
	/**
	 * Retourne l'entier de la ligne row à l'indice index
	 * @param row
	 * @param index
	 * @return
	 */
	public static int getInt(Row row, int index) {
		return Integer.parseInt(row.getString(index));
	}
	
    public static void main( String[] args )
    {
    	
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
		JavaRDD<Movie> movieRDD = dataFrameReader.csv(MOVIE_PATH).javaRDD()
				.map(row -> new Movie(Integer.parseInt(row.getAs(0)),row.getAs(1),row.getAs(2)));
		
		JavaRDD<Rating> ratingRDD = dataFrameReader.csv(RATING_PATH).javaRDD()
				.map(row -> new Rating(Integer.parseInt(row.getAs(0)),Integer.parseInt(row.getAs(1)),Double.parseDouble(row.getAs(2))));

		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByProduct = ratingRDD.groupBy(rating -> rating.product());
		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser = ratingRDD.groupBy(rating ->rating.user());
		
		// Warning : Need the ratings group by or you will have one user by ratings, and not by idUser
		JavaRDD<User> userRDD = ratingsGroupByUser.keys().map(id -> new User(id));

		//printExampleLoadedData(movieRDD,ratingRDD,userRDD,ratingsGroupByProduct,ratingsGroupByUser);

		
		/**
		 * Users DF
		 */
		
		Dataset<Row> usersDF = sqlContext.createDataFrame(userRDD, User.class);
		usersDF.createOrReplaceTempView("users");
		
		usersDF.printSchema();
		
		//printExamplePostDF(usersDF);
		
		
		/**
		 * Ratings DF
		 * 
		 */
		StructType structType = new StructType(new StructField[]{DataTypes.createStructField("user", DataTypes.IntegerType, true),
				DataTypes.createStructField("product", DataTypes.IntegerType, true),
				DataTypes.createStructField("rating", DataTypes.DoubleType, true)});
		
 
		JavaRDD<Row> ratingRowRdd = ratingRDD.map(rating -> RowFactory.create(rating.user() , rating.product() , rating.rating()));
		
		
		Dataset<Row> schemaPeople = sqlContext.createDataFrame(ratingRowRdd, structType);
		schemaPeople.createOrReplaceTempView("ratings");
		
		schemaPeople.printSchema();
 
		/* Example
		System.out.println("Number of rows : (user = 1 and product = 110 ) : " + teenagers.count());
		
		List<Row> filteredDF = teenagers.collectAsList();
		
		for(Row row : filteredDF){
			System.out.print("UserId : " + row.getAs("user"));
			System.out.print("	MovieId : " + row.getAs("product"));
			System.out.println("	Rating : " + row.getAs("rating"));
		}*/
			
		
		/**
		 * Movie DF
		 */
		
		Dataset<Row> moviesDF = sqlContext.createDataFrame(movieRDD, Movie.class);
		moviesDF.createOrReplaceTempView("movies");
		
		moviesDF.printSchema();
		
		/* Examples
		System.out.println("Total Number of movies df : " + usersDF.count());
		
		Dataset<Row> filteredMoviesDF = sqlContext.sql("select * from movies where movies.movieId in (19,4000)");
		
		List<Row> filteredMovies  = filteredMoviesDF.collectAsList();
		
		for(Row row : filteredMovies){
			System.out.print("MovieId : " + row.getAs("movieId"));
			System.out.print("	Title : " + row.getAs("title"));
			System.out.println("	Genres : " + row.getAs("genres"));
		}*/
		
		
		/* Split in training and testing set */

        JavaRDD<Rating>[] ratingSplits = ratingRDD.randomSplit(new double[] { 0.8, 0.2 });

        JavaRDD<Rating> trainingRatingRDD = ratingSplits[0].cache();
        JavaRDD<Rating> testRatingRDD = ratingSplits[1].cache();

        long numOfTrainingRating = trainingRatingRDD.count();
        long numOfTestingRating = testRatingRDD.count();

        System.out.println("Number of training Rating : " + numOfTrainingRating);
        System.out.println("Number of training Testing : " + numOfTestingRating);

        /* Learning the prediction model using ALS (Alternating Least Squares) */
        ALS als = new ALS();
        MatrixFactorizationModel model = als.setRank(20).setIterations(10).run(trainingRatingRDD);
        
        /* Example for 5 best recommendation for user 1 */
        Rating[] recommendedsFor1 = model.recommendProducts(1, 5);
        System.out.println("Recommendations for 1");
        for (Rating ratings : recommendedsFor1) {
            System.out.println("Product id : " + ratings.product() + "-- Rating : " + ratings.rating());
        }
    }
    
    /**
     * Print some results for analyse Loaded data.
     * @param movieRDD
     * @param ratingRDD
     * @param userRDD
     * @param ratingsGroupByProduct
     * @param ratingsGroupByUser
     */
    public static void printExampleLoadedData(JavaRDD<Movie> movieRDD, JavaRDD<Rating> ratingRDD, JavaRDD<User> userRDD, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByProduct, JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser) {
    	System.out.println("Total number of movies : "+movieRDD.count()); // Value = 45843
		System.out.println("Total number of ratings  : " + ratingRDD.count()); // Value = 26024289
		System.out.println("Total number of user  : " + userRDD.count()); // Value = 270896
		System.out.println("Total number of movies rated   : " + ratingsGroupByProduct.count()); // Value = 45115
		System.out.println("Total number of users who rated movies   : " + ratingsGroupByUser.count()); // Value = 270896
    }

    public static void printExamplePostDF(Dataset<Row> usersDF) {
    	System.out.println("Total Number of users df : " + usersDF.count()); // Value = 270896
		Dataset<Row> filteredUsersDF = sqlContext.sql("select * from users where users.userId in (11,12)");
		
		List<Row> filteredUsers  = filteredUsersDF.collectAsList();
		
		for(Row row : filteredUsers){
			System.out.println("UserId : " + row.getAs("userId"));
		}
    }
}
