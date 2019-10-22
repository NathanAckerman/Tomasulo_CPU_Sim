import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class Config {

    public static int NF;
    public static int NW;
    public static int NI;
    public static int NR;
    public static int NB;


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
                        NF = Integer.valueOf(value);
                        break;
                    case "NI":
                        NI = Integer.valueOf(value);
                        break;
                    case "NW":
                        NW = Integer.valueOf(value);
                        break;
                    case "NR":
                        NR = Integer.valueOf(value);
                        break;
                    case "NB":
                        NB = Integer.valueOf(value);
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
