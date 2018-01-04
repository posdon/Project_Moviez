package mmabdlrgp.Projet_Moviez;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

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
    	// Creation de session spark.
    	// Example : https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/sql/JavaSparkSQLExample.java
    	SparkSession spark = SparkSession
    		      .builder()
    		      .appName("Java Spark SQL basic example")
    		      .config("spark.some.config.option", "some-value")
    		      .master("local")
    		      .getOrCreate();
		
		// Define headers
		final DataFrameReader dataFrameReader = spark.read();
		dataFrameReader.option("header", "true");

		// Loading database
		dataFrameReader.csv(RATING_PATH).createOrReplaceTempView("ratings");
		dataFrameReader.csv(MOVIE_PATH).createOrReplaceTempView("movies");
		dataFrameReader.csv(TAG_PATH).createOrReplaceTempView("tags");
		dataFrameReader.csv(GENOME_SCORE_PATH).createOrReplaceTempView("genomeScores");
		dataFrameReader.csv(GENOME_TAG_PATH).createOrReplaceTempView("genomeTags");
		
		System.out.println("Starting reading");
		
		
		/*List<Row> users = spark.sql("SELECT userId, count(userId) FROM ratings GROUP BY userId").collectAsList();
		Map<Integer,List<Integer>> usersModel = new HashMap<Integer, List<Integer>>();
		for(Row row : users) {
			List<Integer> userVector = new ArrayList<Integer>();
			userVector.add(getInt(row,1));
			usersModel.put(getInt(row,0), userVector);
		}*/
		
		//Number of rating for every movie
		/*List<Row> mostRatedMovies = spark.sql("SELECT title,count(R.movieId) FROM ratings as R, movies as M WHERE R.movieId=M.movieId GROUP BY title").collectAsList();
		Map<String,List<Integer>> mostRatedMoviesModel = new HashMap<String, List<Integer>>();
		System.out.println("End sql");
		for(Row row : mostRatedMovies) {
			System.out.println(row.get(0)+" =======> "+row.get(1));
		}*/
		
		//mean
		
		// Exemple de tétajointure
		//spark.sql("SELECT * FROM ratings as R, movies as M WHERE R.movieId=M.movieId");
		
		//Exemple de manipulation
		/*System.out.println(spark.sql("SELECT * FROM ratings").filter(new FilterFunction() {

			public boolean call(Object arg0) throws Exception {
				Row row = (Row) arg0;
				return Double.parseDouble(row.getString(2)) < 6.0;
			}
		}).count());*/
		
		System.out.println("End reading");

    }
}
