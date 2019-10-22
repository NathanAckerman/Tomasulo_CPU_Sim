import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class Config {

    public static String NF;
    public static String NW;
    public static String NI;
    public static String NR;
    public static String NB;


    // No one can instantiate this class
    private Config() {
    }

    public static void readFile() {
        try{
            String filename = "TomasuloSimulator.config";
            Scanner reader = new Scanner(new File(filename));

            while(reader.hasNext()){
                String line = reader.nextLine();
                String key = line.split("=")[0];
                String value = line.split("=")[1];

                switch(key.toUpperCase()) {
                    case "NF": 
                        NF = value;
                        break;
                    case "NI":
                        NI = value;
                        break;
                    case "NW":
                        NW = value;
                        break;
                    case "NR":
                        NR = value;
                        break;
                    case "NB":
                        NB = value;
                        break;
                    default:
                        System.out.println("Invalid Key: " + key);
                        System.exit(1);
                }
            }
            reader.close();
        }catch(FileNotFoundException e){
            System.out.println("Please ensure that you have file TomasuloSimulator.config in the correct directory");
            System.exit(1);
        }catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }
}
