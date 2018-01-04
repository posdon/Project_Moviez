package mmabdlrgp.Projet_Moviez;

import java.util.function.Function;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;

import mmabdlrgp.Projet_Moviez.model.Movie;
import scala.util.Try;

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
		
		
		/**
		 * Load Movie data
		 */
		final DataFrameReader dataFrameReader = spark.read();
		dataFrameReader.option("header", "true");
		JavaRDD<Movie> movieRDD = dataFrameReader.csv(MOVIE_PATH).javaRDD()
				.map(row -> new Movie(Integer.parseInt(row.getAs(0)),row.getAs(1),row.getAs(2)));
				
			 
		 
		System.out.println(movieRDD.first()); 

    }
}
