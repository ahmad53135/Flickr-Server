import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Global {

    public static List<String> list = Collections.synchronizedList(new ArrayList(10000000));
    public static int imageCounter = 0;
    public static int imageWidth = 480;
    public static int imageheight = 480;
    public static int bitsPerBlock = 23;

    public Global(){
        if (list.isEmpty()) {
            load2List();
        }
    }

    public  void load2List(){

        synchronized (list) {
            Path inputFile = Paths.get("C:\\Users\\Ahmad\\IdeaProjects\\DCTcompute\\ValidValues-9Block\\TOTALBITS.txt");
            try (InputStream in = Files.newInputStream(inputFile);
                 BufferedReader reader =
                         new BufferedReader(new InputStreamReader(in))) {
                String line = null;

                System.out.println("Loading List");
                while ((line = reader.readLine()) != null) {
                    //
                    // System.out.println(line);

                    list.add(line);
                }
            } catch (IOException x) {
                System.out.println("load2list error");
                System.err.println(x);
            }
        }
    }

}
