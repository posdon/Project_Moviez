package mmabdlrgp.Projet_Moviez;

import java.util.HashMap;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


import scala.Tuple2;
import scala.Tuple3;

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
    	System.out.println("Start reading");
		
		System.out.println("End reading");

    }
}
