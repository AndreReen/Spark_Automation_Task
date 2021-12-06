import com.epam.pop.AppConfig;
import com.epam.pop.PoetryAnalyzer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.spark.sql.AnalysisException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class PoetryAnalyzerIT {

  @Autowired
  private PoetryAnalyzer analyzer;

  private final int WORDS = 10;

  /**
   * Task: 1) start PoetryAnalyzer service (list of available popstars are: 2pac, gaga, perry,
   * tool). You can use your own set of lyrics, if you are curious; 2) make sure it's working as
   * expected (see corresponding Javadoc) - write some positive tests; 3) try to break the service.
   * Is there a way to avoid such breaks? How you would validate it? let's say we need to do a data
   * migration. Create a test, that 100% determines, if any text file is missing after the
   * migration.
   */


  @Test
  public void testRunAllStars() {
    String[] stars = getAllTextDirs();
    for (String star : stars) {
      List<String> actualResult = analyzer.mostPopularWords(star, WORDS);
      System.out.println("Top " + WORDS + " words in " + star + " songs: " + actualResult);
    }
  }

  @Test
  public void negativeWordNum() {
    Assertions.assertThrows(AnalysisException.class, () -> analyzer.mostPopularWords("gaga", -1));
  }

  @Test
  public void compareFunctionTest() {
    int sharedWordsNumber = analyzer.compare("gaga", "2pac", 100);
    System.out.println(sharedWordsNumber);
  }

  @Test
  public void compareFunctionTestNegative() {
    int sharedWordsNumber = 0;
    try {
      sharedWordsNumber = analyzer.compare("hello", "world", 100);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println(sharedWordsNumber);
  }

  @Test
  public void textsDirExists() throws Exception {
    List<String> stars = getAllTextDirs2("texts");
    String star1 = "gaga";
    String star2 = "shafutinskiy";

    if (stars.contains(star1)) {
      if (stars.contains(star2)) {
        int sharedWordsNumber = analyzer.compare(star1, star2, 100);
        System.out.println(sharedWordsNumber);
      } else {
        System.out.println(star2 + " does not exist");
      }
    } else {
      System.out.println(star1 + " does not exist");
    }
  }

  @Test
  public void fileMigrationTest() throws Exception {
    List<String> stars = getAllTextDirs2("texts");
    for (String star: stars
    ) {
      migrationFileValidator("texts/"+star, "textsMigrated/"+star);
    }
  }


  public String[] getAllTextDirs() {
    File file = new File("texts");
    String[] directories = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return new File(current, name).isDirectory();
      }
    });
    System.out.println(Arrays.toString(directories));
    return directories;
  }

  public List<String> getAllTextDirs2(String path) {
    File file = new File(path);
    String[] directories = file.list(new FilenameFilter() {
      @Override
      public boolean accept(File current, String name) {
        return new File(current, name).isDirectory();
      }
    });
    System.out.println(Arrays.toString(directories));
    return Arrays.asList(directories);
  }

  public void migrationFileValidator (String original, String migrated) throws IOException {

    try{
      File folderA = new File(original);
      File[] listOfFilesInA = folderA.listFiles();
      File folderB = new File(migrated);
      File[] listOfFilesInB = folderB.listFiles();

      for (File fileA : listOfFilesInA) {
        if (fileA.isFile()) {
          for (File fileB : listOfFilesInB) {
            if (fileB.isFile()) {
              if (fileA.getName().equals(fileB.getName())) {
                InputStream inputStream1 = new FileInputStream(fileA);
                InputStream inputStream2 = new FileInputStream(fileB);

                if (IOUtils.contentEquals(inputStream1, inputStream2)) {
                  System.out.println(fileB.getName() + " is identical");
                } else {
                  System.out.println(fileB.getName() + " is CORRUPTED");
                }
              }
            }
          }
        }
      }
    }
    catch (NullPointerException e) {
      System.out.println(migrated + " does not exist");
    }
  }

}
