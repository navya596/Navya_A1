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
            //Read file
            logger.info("**** Reading the maze from file " + inputFilePath);
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

            reader.close();

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
