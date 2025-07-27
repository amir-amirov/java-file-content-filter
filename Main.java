import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Statistics {
    private int integerCount;
    private long minInt;
    private long maxInt;
    private double sumInt;
    private double avgInt;
    private long medianInt;
    private int floatCount;
    private double minFloat;
    private double maxFloat;
    private double sumFloat;
    private double avgFloat;
    private double medianFloat;
    private int stringCount;
    private int shortestString;
    private int longestString;

    public void computeIntegerStats(ArrayList<Long> integers) {
        if (integers.isEmpty())
            return;
        integerCount = integers.size();
        minInt = Collections.min(integers);
        maxInt = Collections.max(integers);
        sumInt = integers.stream().mapToLong(Long::longValue).sum();
        avgInt = sumInt / integerCount;
        ArrayList<Long> sorted = new ArrayList<>(integers);
        Collections.sort(sorted);
        medianInt = sorted.get(sorted.size() / 2);
    }

    public void computeFloatStats(ArrayList<Double> floats) {
        if (floats.isEmpty())
            return;
        floatCount = floats.size();
        minFloat = Collections.min(floats);
        maxFloat = Collections.max(floats);
        sumFloat = floats.stream().mapToDouble(Double::doubleValue).sum();
        avgFloat = sumFloat / floatCount;
        ArrayList<Double> sorted = new ArrayList<>(floats);
        Collections.sort(sorted);
        medianFloat = sorted.get(sorted.size() / 2);
    }

    public void computeStringStats(ArrayList<Integer> stringLengths) {
        if (stringLengths.isEmpty())
            return;
        stringCount = stringLengths.size();
        shortestString = Collections.min(stringLengths);
        longestString = Collections.max(stringLengths);
    }

    public void printShortStats() {
        System.out.println("\nStatistics:");
        System.out.println("Integers: " + integerCount);
        System.out.println("Floats: " + floatCount);
        System.out.println("Strings: " + stringCount);
    }

    public void printFullStats() {
        System.out.println("\nStatistics:");
        System.out
                .println("Integers: "
                        + integerCount + (integerCount > 0
                                ? " (min: " + minInt + ", max: " + maxInt + ", sum: " + sumInt +
                                        ", avg: " + avgInt + ", median: " + medianInt + ")"
                                : ""));
        System.out
                .println("Floats: "
                        + floatCount + (floatCount > 0
                                ? " (min: " + minFloat + ", max: " + maxFloat + ", sum: " + sumFloat +
                                        ", avg: " + avgFloat + ", median: " + medianFloat + ")"
                                : ""));
        System.out.println("Strings: " + stringCount
                + (stringCount > 0 ? " (shortest: " + shortestString + ", longest: " + longestString + ")" : ""));
    }
}

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
    private ArrayList<Integer> stringLengths = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();
    private Statistics stats = new Statistics();

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

    public void processFiles() {
        for (String file : inputFiles) {
            File inputFile = new File(file);
            if (!inputFile.exists()) {
                errors.add("Error: File " + file + " does not exist.");
                System.out.println(errors.get(errors.size() - 1));
                continue;
            }
            processFile(file);
        }
    }

    private void processFile(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int lineNumber = 1;
            while (line != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }
                if (line.matches("[+-]?\\d+")) {
                    try {
                        Long num = Long.parseLong(line);
                        integers.add(num);
                        System.out.println("Line " + lineNumber + ": " + line + " (integer)");
                    } catch (NumberFormatException e) {
                        errors.add(file + ": " + line + " (invalid integer)");
                        System.out.println("Line " + lineNumber + ": " + line + " (invalid integer)");
                    }
                } else if (line.matches("[+-]?\\d*\\.\\d+([Ee][+-]?\\d+)?")) {
                    try {
                        Double num = Double.parseDouble(line);
                        floats.add(num);
                        System.out.println("Line " + lineNumber + ": " + line + " (float)");
                    } catch (NumberFormatException e) {
                        errors.add(file + ": " + line + " (invalid float)");
                        System.out.println("Line " + lineNumber + ": " + line + " (invalid float)");
                    }
                } else {
                    strings.add(line);
                    stringLengths.add(line.length());
                    System.out.println("Line " + lineNumber + ": " + line + " (string)");
                }
                line = reader.readLine();
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            errors.add("Error: File " + file + " not found.");
            System.out.println(errors.get(errors.size() - 1));
        } catch (SecurityException e) {
            errors.add("Error: Permission denied reading " + file);
            System.out.println(errors.get(errors.size() - 1));
        } catch (IOException e) {
            errors.add("Error: Could not read file " + file + ": " + e.getMessage());
            System.out.println(errors.get(errors.size() - 1));
        }
    }

    public void writeOutputFiles() {
        writeOutputFile(outputPath + prefix + "integers.txt", integers, true);
        writeOutputFile(outputPath + prefix + "floats.txt", floats, true);
        writeOutputFile(outputPath + prefix + "strings.txt", strings, false);
        if (!errors.isEmpty()) {
            writeOutputFile(outputPath + prefix + "errors.txt", errors, false);
        }
    }

    private <T> void writeOutputFile(String fileName, ArrayList<T> data, boolean convertToString) {
        if (data.isEmpty())
            return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, appendMode))) {
            for (T item : data) {
                writer.write(convertToString ? String.valueOf(item) : item.toString());
                writer.newLine();
            }
        } catch (SecurityException e) {
            System.out.println("Error: Permission denied writing to " + fileName);
        } catch (IOException e) {
            System.out.println("Error: Could not write to " + fileName + ": " + e.getMessage());
        }
    }

    public void computeAndPrintStatistics() {
        stats.computeIntegerStats(integers);
        stats.computeFloatStats(floats);
        stats.computeStringStats(stringLengths);
        if (fullStats) {
            stats.printFullStats();
        } else if (shortStats) {
            stats.printShortStats();
        }
    }

    public void reset() {
        integers.clear();
        floats.clear();
        strings.clear();
        stringLengths.clear();
        errors.clear();
        inputFiles.clear();
        appendMode = false;
        shortStats = false;
        fullStats = false;
        prefix = "";
        outputPath = "";
    }
}

public class Main {
    public static void main(String[] args) {
        FileProcessor processor = new FileProcessor();
        if (processor.parseArguments(args)) {
            processor.processFiles();
            processor.writeOutputFiles();
            processor.computeAndPrintStatistics();
        }
    }
}