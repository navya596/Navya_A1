package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("** Starting Maze Runner");

        //Add option to read -i flag as argument 
        Options options = new Options();
        options.addOption("i", "input", true, "Input file that contains the maze");

        //Create CL Parser, formatter objects to parse arguments
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd; //initialize variable to store parsed argument 


        try {

            //Parse command line argument and option
            cmd = parser.parse(options, args);

            //If there is no input file given with the -i flag, throw error
            if (!cmd.hasOption("i")) {
                logger.error("/!\\ Missing required option: -i <input file> /!\\");
                formatter.printHelp("MazeRunner", options);
                return;

            }

            //Get file path from -i flag input argument
            String inputFilePath = cmd.getOptionValue("i");

            // Create Maze object and load the maze
            Maze maze = new Maze(inputFilePath);
            char[][] mazeArray = maze.getMazeArray();

            // Display the maze
            System.out.println("Maze Loaded:");
            for (char[] row : mazeArray) {
                System.out.println(new String(row));
            }

            // Get and display the entry and exit points
            int[] entry = maze.getEntry();
            System.out.println("Entry Point: (" + entry[0] + ", " + entry[1] + ")");

            int[] exit = maze.getExit();
            System.out.println("Exit Point: (" + exit[0] + ", " + exit[1] + ")");
            
            //Read file
            /*logger.info("**** Reading the maze from file " + inputFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line;
            while ((line = reader.readLine()) != null) {
                for (int idx = 0; idx < line.length(); idx++) {
                    if (line.charAt(idx) == '#') {
                        System.out.print("WALL ");
                    } else if (line.charAt(idx) == ' ') {
                        System.out.print("PASS ");
                    }
                }
                System.out.print(System.lineSeparator());
            }

            reader.close();*/

        } catch (ParseException e) {
            //Handle parsing errors
            logger.error("/!\\ Error parsing command line arguments: " + e.getMessage() + " /!\\");
            formatter.printHelp("MazeRunner", options);
        } catch(Exception e) {
            //Handle all other errors
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);

        }
        logger.info("**** Computing path");
        logger.error("PATH NOT COMPUTED"); 
        logger.info("** End of MazeRunner");
    }
}


class Maze {
    //Define maze attributes 
    private int rows;
    private int cols;
    private final String inputFilePath;
    private char[][] mazeArray;

    //Define maze constructor
    public Maze(String inputFilePath) {
        this.inputFilePath = inputFilePath;

    }

    //Maze Methds
    /* Public method: getMazeArray()
    Description: creates 2D array for maze
    Returns: char[][] */
    public char[][] getMazeArray() {
        try {
            //Read maze file to store number of rows and columns
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line = reader.readLine();
            cols = line.length();
            rows = 1;
            while ((line = reader.readLine()) != null) {
                rows+=1;
            }
            //Close buffered reader
            reader.close();

            //Create string array for maze
            mazeArray = new char[rows][cols];

            //Open new buffered reader to create array for maze
            BufferedReader newReader = new BufferedReader(new FileReader(inputFilePath));
            String newLine;
            int r = 0;
            while ((newLine = newReader.readLine()) != null) {
                for (int idx = 0; idx < newLine.length(); idx++) {
                    mazeArray[r][idx] = newLine.charAt(idx);
                }
                System.out.print(System.lineSeparator());
                r+=1;
            }

            newReader.close();

            return mazeArray;
            
        } catch (Exception e) {
            System.out.println("Unexpected error occured: " + e);
        }

        return null;
    }

    /* Public method: getEntry()
    Description: gets index of entry point and stores it in an array of size 2
    Returns: int[] */
    public int[] getEntry() {
        int[] entry = new int[2];
        for (int i = 0; i < rows; i++) {
            if (mazeArray[i][0] == ' ') {
                entry[0] = i;
                entry[1] = 0;
                break;
            }
        }
        return entry;
    }

    /* Public method: getExit()
    Description: gets index of exit point and stores it in an array of size 2
    Returns: int[] */
    public int[] getExit() {
        int[] exit = new int[2];
        for (int i = 0; i < rows; i++) {
            if (mazeArray[i][cols-1] == ' ') {
                exit[0] = i;
                exit[1] = 0;
                break;
            }
        }
        return exit;
    }



    
}

