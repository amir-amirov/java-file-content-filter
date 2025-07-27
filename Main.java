import java.io.File;
import java.util.ArrayList;

class FileProcessor {

    private boolean appendMode = false;
    private boolean shortStats = false;
    private boolean fullStats = false;

    private String prefix = "";
    private String outputPath = "";

    private ArrayList<String> inputFiles = new ArrayList<>();

    private ArrayList<Long> integers = new ArrayList<>();
    private ArrayList<Double> floats = new ArrayList<>();
    private ArrayList<String> strings = new ArrayList<>();

    private ArrayList<Integer> stringsLength = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();

    public boolean parseArguments(String[] args) {

        if (args.length == 0) {
            System.out.println("Error: No input file specified.");
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-s")) {
                shortStats = true;
            } else if (args[i].equals("-f")) {
                fullStats = true;
            } else if (args[i].equals("-p")) {
                if (i + 1 < args.length) {
                    prefix = args[++i];
                } else {
                    System.out.println("Error: -p requires a prefix.");
                    return false;
                }
            } else if (args[i].equals("-a")) {
                appendMode = true;
            } else if (args[i].equals("-o")) {
                if (i + 1 < args.length) {
                    outputPath = args[++i];
                } else {
                    System.out.println("Error: -o requires a path.");
                    return false;
                }
            } else {
                inputFiles.add(args[i]);
            }
        }

        if (inputFiles.isEmpty()) {
            System.out.println("Error: No input files provided.");
            return false;
        }
        File outputDir = new File(outputPath);
        if (!outputPath.isEmpty() && (!outputDir.exists() || !outputDir.isDirectory() || !outputDir.canWrite())) {
            System.out.println("Error: Output path " + outputPath + " is not writable.");
            return false;
        }
        return true;
    }
}

public class Main {
    public static void main(String[] args) {

    }
}
