package mmabdlrgp.Projet_Moviez;

import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
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
		dataFrameReader.csv(LINK_PATH).createOrReplaceTempView("links");
		dataFrameReader.csv(TAG_PATH).createOrReplaceTempView("tags");
		dataFrameReader.csv(GENOME_SCORE_PATH).createOrReplaceTempView("genomeScores");
		dataFrameReader.csv(GENOME_TAG_PATH).createOrReplaceTempView("genomeTags");
		
		
		//Exemple de manipulation
		spark.sql("SELECT count(*) FROM ratings").show();
		spark.sql("SELECT count(*) FROM movies").show();
		spark.sql("SELECT count(*) FROM links").show();
		spark.sql("SELECT count(*) FROM tags").show();
		spark.sql("SELECT count(*) FROM genomeScores").show();
		spark.sql("SELECT count(*) FROM genomeTags").show();

    }
}
