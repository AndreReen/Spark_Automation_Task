import com.epam.pop.AppConfig;
import com.epam.pop.PopService;
import com.epam.pop.UserConfig;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class PopServiceImplTest {

  @Autowired
  private JavaSparkContext cxt;

  @Autowired
  private PopService ps;

  @Autowired
  private UserConfig userConfig;

  /**
   * Task: Make sure {@link PopService#topWords} works as expected. Create tests for each test case
   * (as many as you think is enough). Check corresponding javadoc to get acceptance criteria.
   * <p>
   * All required beans (class instances) are already injected into this class.
   * <p>
   * Suppose, you should get familiar with SparkContext API. Find all the required information in
   * javadocs
   *
   * @see <a href="https://spark.apache.org/docs/2.1.0/api/java/index.html?org/apache/spark/api/java/JavaSparkContext.html">
   * JavaSparkContext</a>
   * @see PopService#topWords
   */

  @Test
  public void oneItemList() {
    List<String> expectedResult = List.of("5", "4", "3", "2", "1");
    List<String> data = List.of("1 2 2 3 3 3 4 4 4 4 5 5 5 5 5");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 5;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void multipleItemsList() {
    List<String> expectedResult = List.of("5", "4", "3", "2", "1");
    List<String> data = List.of("1", "2 2", "3 3 3", "4 4 4 4", "5 5 5 5 5");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 5;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void textUpperCase() {
    List<String> expectedResult = List.of("four", "three", "two");
    List<String> data = List.of("one", "two TWO", "three THREE THREE", "FouR four FoUr FOUR");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 3;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void unicodeTest() {
    List<String> expectedResult = List.of("\u2764", "\u210B", "ò");
    List<String> data = List.of("\u00D2", "\u2764", "\u210B \u210B \u210B",
        "\u2764 \u2764 \u2764 \u2764");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 300;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void filterTest() {
    List<String> expectedResult = List.of();
    List<String> data = List.of("but", "as", "as as as", "all all all all");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 1;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void specialSymbolTest() {
    List<String> expectedResult = List.of("\t", "\r", "\n", "\\");
    List<String> data = List.of("\\", "\t \t \t \t", "\r \r \r", "\n \n");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 5;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void emptyZeroElement() {
    List<String> expectedResult = List.of();
    List<String> data = List.of();
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 1;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void emptyStringList() {
    List<String> expectedResult = List.of("");
    List<String> data = List.of("");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = 1;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void wordNumTest() {
    List<String> expectedResult = List.of();
    List<String> data = List.of("1", "2 2", "3 3 3", "4 4 4 4", "5 5 5 5 5");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = -1;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  public void mandarinCharTest() {
    List<String> expectedResult = List.of();
    List<String> data = List.of("\u9FFF", "\u2000", "\u2A6D");
    JavaRDD<String> distData = cxt.parallelize(data);
    int wordNum = -1;
    List<String> actualResult = ps.topWords(distData, wordNum);
    System.out.println(actualResult);
    Assertions.assertEquals(expectedResult, actualResult);
  }
}
