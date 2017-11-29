package mmabdlrgp.Projet_Moviez;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

/**
 * Hello world!
 *
 */
public class App 
{
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
        
		SQLContext sqlContext = new SQLContext(spark);
		String path = "./movies.csv";
		Dataset<Row> products = sqlContext.load(path,"csv");
		System.out.println(products.first());

    }
}
