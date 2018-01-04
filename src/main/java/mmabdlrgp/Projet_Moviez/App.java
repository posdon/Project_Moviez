package mmabdlrgp.Projet_Moviez;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;

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
		SparkSession spark = new Builder()
			     .appName("Reommendation Engine")
			     .master("local")
			     .getOrCreate();		
		
		SQLContext sqlContext = spark.sqlContext();
		
		final DataFrameReader dataFrameReader = spark.read();
		dataFrameReader.option("header", "true");
		
		/**
		 * Load Movie data
		 */
		JavaRDD<Movie> movieRDD = dataFrameReader.csv(MOVIE_PATH).javaRDD()
				.map(row -> new Movie(Integer.parseInt(row.getAs(0)),row.getAs(1),row.getAs(2)));
						 
		 
				 
		/**
		 * Load Rating data
		 */
		JavaRDD<Rating> ratingRDD = dataFrameReader.csv(RATING_PATH).javaRDD().map(row -> new Rating(Integer.parseInt(row.getAs(0)),Integer.parseInt(row.getAs(1)),Double.parseDouble(row.getAs(2))));
		
		
		/**
		 * Group ratings
		 */
		
		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByProduct = ratingRDD.groupBy(rating -> rating.product());
		JavaPairRDD<Integer, Iterable<Rating>> ratingsGroupByUser = ratingRDD.groupBy(rating ->rating.user());
		 
		/**
		 * Load User data
		 * Warning : Need the ratings group by or you will have one user by ratings, and not by idUser
		 */
		JavaRDD<User> userRDD = ratingsGroupByUser.keys().map(id -> new User(id));

		
		
		
		 
		//System.out.println("Total number of movies : "+movieRDD.count()); // Value = 45843
		//System.out.println("Total number of ratings  : " + ratingRDD.count()); // Value = 26024289
		//System.out.println("Total number of user  : " + userRDD.count()); // Value = 270896
		//System.out.println("Total number of movies rated   : " + ratingsGroupByProduct.count()); // Value = 45115
		//System.out.println("Total number of users who rated movies   : " + ratingsGroupByUser.count()); // Value = 270896

		
		/**
		 * Users DF
		 */
		
		Dataset<Row> usersDF = sqlContext.createDataFrame(userRDD, User.class);
		usersDF.createOrReplaceTempView("users");
		
		usersDF.printSchema();
		
		System.out.println("Total Number of users df : " + usersDF.count());
		
		Dataset<Row> filteredUsersDF = sqlContext.sql("select * from users where users.userId in (11,12)");
		
		List<Row> filteredUsers  = filteredUsersDF.collectAsList();
		
		for(Row row : filteredUsers){
			System.out.print("UserId : " + row.getAs("userId"));
		}
    }
}
