package ca.mcmaster.se2aa4.mazerunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

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
        options.addOption("p", "path", true, "Given path that needs to be validated");
        //Create CL Parser, formatter objects to parse arguments
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd; //initialize variable to store parsed argument 
        char flag;
        String pathSequence = "";
        boolean checkPathWin = false;


        try {

            //Parse command line argument and option
            cmd = parser.parse(options, args);

            //If there is no input file given with the -i flag, throw error
            if (!cmd.hasOption("i")) {
                logger.error("/!\\ Missing required option: -i <input file> /!\\");
                formatter.printHelp("MazeRunner", options);
                return;
            } else { //Else if there is a -p flag, set flag variable to p
                if (cmd.hasOption("p")) {
                    pathSequence = cmd.getOptionValue("p");
                    flag = 'p';    
                }
                else {
                    flag = 'i';
                }

            }

            //Get file path from -i flag input argument
            String inputFilePath = cmd.getOptionValue("i");

            // Create Maze object and load the maze
            Maze maze = new Maze(inputFilePath);
            char[][] mazeArray = maze.getMazeArray();

            // Display the maze
            logger.info("Maze Loaded:");
            for (char[] row : mazeArray) {
                logger.info(new String(row));
            }

            // Get entry and exit points
            int[] entry = maze.getEntry();
            int[] exit = maze.getExit();
            logger.info("Entry Point: (" + entry[0] + ", " + entry[1] + ")");
            logger.info("Exit Point: (" + exit[0] + ", " + exit[1] + ")");

            
            // Initialize runner at entry point
            char initialFace = maze.getInitialFace();
            maze.initializeFace(); // Set the runner at the entry
            // Display the maze
            logger.info("Maze Loaded:");
            for (char[] row : mazeArray) {
                logger.info(new String(row));
            }
            //Initialize person and path
            Person runner = new Person(mazeArray, initialFace, entry, entry, exit);
            //Path class commented out as it is not used in this assignment
            //Path path = new Path(runner);


            // Initialize the appropriate path solver based on the flag
            AbstractPathSolver pathSolver;


            //If there is a path validation flag,  check for valid path
            if (flag == 'p') {
                pathSolver = new ValidatePath(runner);
                
                checkPathWin = ((ValidatePath) pathSolver).checkPath(pathSequence);
                if (checkPathWin == true) {
                    System.out.println("Valid path!");
                } else {
                    System.out.println("Invalid path!");
                }

            }
            else {
                // Simulate maze traversal
                logger.info("\n--- Simulating Maze Traversal ---");
                pathSolver = new GeneratedPath(runner);
                pathSolver.solvePath();
                
            }



        } catch (ParseException e) {
            //Handle parsing errors
            logger.error("/!\\ Error parsing command line arguments: " + e.getMessage() + " /!\\");
            formatter.printHelp("MazeRunner", options);
        } catch(Exception e) {
            //Handle all other errors
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);

        }

    }
}


//Class to initialize maze and get exit, entry points
class Maze {
    private static final Logger logger = LogManager.getLogger();
    //Define maze attributes 
    private int rows;
    private int cols;
    private final String inputFilePath;
    private char[][] mazeArray;
    private final char initialFace = 'E'; //When maze is initialized, the current facing is always East
    private final char runner = 'X';
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
                    // Replace null character with space
                    if (mazeArray[r][idx] == '\0') {
                        mazeArray[r][idx] = '\32';
                    }

                    
                }
                logger.info(System.lineSeparator());
                r+=1;
            }

            newReader.close();

            return mazeArray;
            
        } catch (Exception e) {
            logger.info("Unexpected error occured: " + e);
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
                exit[1] = cols-1;
                break;
            }
            
        }
        return exit;
    }

    /* Public method: initializeFace()
    Description: puts initial face on the maze entrance
    Returns: void */
    public void initializeFace() {
        int[] entrance = getEntry();
        mazeArray[entrance[0]][entrance[1]] = runner;
    }

    //Methods for behaviours of person 
    /* Public method: getCurrentFace()
    Description: gets char for current direction facing 
    Returns: char */
    public char getInitialFace() {
        return initialFace;           
    }
    
}

//Class for runner to traverse through the maze
class Person {
    private static final Logger logger = LogManager.getLogger();
    //Attributes for person
    private char currentFace;
    @SuppressWarnings("FieldMayBeFinal")
    private int[] currentPosition;
    @SuppressWarnings("FieldMayBeFinal")
    private char[] surroundings = new char[4];
    @SuppressWarnings("FieldMayBeFinal")
    private char[][] maze;
    private char east;
    private char west;
    private char north;
    private char south;
    private final int[] start;
    private final int[] end;

    //Constructor
    public Person(char[][] maze, char currentFace, int[] currentPosition, int[] start, int[] end) {
        this.maze = maze;
        this.currentFace = currentFace;
        this.currentPosition = currentPosition;
        this.start = start;
        this.end = end;

        
    }

    //Methods for behaviours of person 
    /* Public method: getCurrentFace()
    Description: gets char for current direction facing 
    Returns: char */
    public char getCurrentFace() {
        return currentFace;           
    }

    /* Public method: getCurrentPosition()
    Description: gets index values for current position
    Returns: int[] */
    public int[] getCurrentPosition() {
        return currentPosition;           
    }

    /* Public method: getMaze()
    Description: gets the maze's current placements
    Returns: char[][] */
    public char[][] getMaze() {
        return maze;           
    }

    /* Public method: getStart()
    Description: gets index values for start position
    Returns: int[] */
    public int[] getStart() {
        return start;           
    }

        /* Public method: getEnd()
    Description: gets index values for end position
    Returns: int[] */
    public int[] getEnd() {
        return end;           
    }

    /* Public method: checkSurroundings()
    Description: Updates the north, south, east, west variables based on what's surrounding the current position
    Returns: char[] */
    public void checkSurroundings() {

        //Check for column forward
        if (currentPosition[1] + 1 < maze[0].length) {
            east = maze[currentPosition[0]][currentPosition[1]+1];
        }
        else {
            east = '#';
        }
        //Check for column behind
        if (currentPosition[1] - 1 >= 0) { 
            west = maze[currentPosition[0]][currentPosition[1]-1];
        }
        else {
            west = '#';
        }
        //Check for row above
        if (currentPosition[0] - 1 >= 0) {
            north = maze[currentPosition[0]-1][currentPosition[1]];
        }
        else {
            north = '#';
        }
        //Check for row below
        if (currentPosition[0] + 1 < maze.length) {
            south = maze[currentPosition[0]+1][currentPosition[1]];
        }
        else {
            south = '#';
        }
        //Assign all to a standard surroundings array
        surroundings[0] = east;
        surroundings[1] = west;
        surroundings[2] = north;
        surroundings[3] = south;
    }

    /* Public method: getSurroundings()
    Description: gets the surroundigns of the current runner
    Returns: int[] */
    public char[] getSurroundings() {
        return surroundings;           
    }


    /* Public method: moveForward()
    Description: Checks surroundings, moves the runner 1 step forward respective to the direction they are facing
    and checks surroundings again
    Returns: void */
    public void moveForward() {
        switch(currentFace) {
            //Facing east means move a column forward that row
            case 'E':
                currentPosition[1]+=1;
                break;
            //Facing west means move a column back that row
            case 'W':
                currentPosition[1]-=1;
                break;
            //Facing north means move a row up (back) that column
            case 'N':
                currentPosition[0]-=1;
                break;
            //Facing south means move a row down (forward) that column
            case 'S':
                currentPosition[0]+=1;
                break;
        }

    }

    /* Public method: turnRight()
    Description: Turns the runner right respective to the direction they are facing
    and checks surroundings
    Returns: void */
    public void turnRight() {
        switch(currentFace) {
            case 'E':
                currentFace = 'S';
                break;
            case 'W':
                currentFace = 'N';
                break;
            case 'N':
                currentFace = 'E';
                break;
            case 'S':   
                currentFace = 'W';
                break;
        }

        

    }

    /* Public method: turnLeft()
    Description: Turns the runner left respective to the direction they are facing
    and checks surroundings
    Returns: void */
    public void turnLeft() {
        switch(currentFace) {
            case 'E':
                currentFace = 'N';
                break;
            case 'W':
                currentFace = 'S';
                break;
            case 'N':
                currentFace = 'W';
                break;
            case 'S':   
                currentFace = 'E';
                break;
        }


    }

    


}

/*Command interface for design pattern
Method: execute(), takes Person as a parameter*/
interface Command {
    void execute(Person person);
}

//Subclass move forward for Command
class MoveForwardCommand implements Command {

    @Override
    public void execute(Person person) {
        person.moveForward(); //calls person to execute
    }
    
    
}

//Subclass turn left for Command
class TurnLeftCommand implements Command {

    @Override
    public void execute(Person person) {
        person.turnLeft(); //calls person to execute
    }
    
    
}

//Subclass turn right for Command
class TurnRightCommand implements Command {

    @Override
    public void execute(Person person) {
        person.turnRight(); //calls person to execute
    }
    
    
}

//Abstract class for template design pattern
abstract class AbstractPathSolver {
    protected Person person;

    public AbstractPathSolver (Person person) {
        this.person = person;
    }

    //Template method
    public final void solvePath() {
        while (!checkWin()) {
            doStep();

        }
        postProcess();
    }

    //methods overrided in subclasses
    protected abstract void doStep(); 
    protected abstract void postProcess();

    //concrete method
    protected boolean checkWin() {
        return person.getCurrentPosition()[0] == person.getEnd()[0]
            && person.getCurrentPosition()[1] == person.getEnd()[1];
    }
    
}

//Subclass of abstract path solver class
class GeneratedPath extends AbstractPathSolver {
    private static final Logger logger = LogManager.getLogger();

    private StringBuilder path = new StringBuilder();
    private StringBuilder factorizedPath =  new StringBuilder();
    private Command moveForwardCommand = new MoveForwardCommand();
    private Command turnLeftCommand = new TurnLeftCommand();
    private Command turnRightCommand = new TurnRightCommand();

    private char right;
    private char left;
    private char front;
    private char back;

    public GeneratedPath(Person person) {
        super(person);

        
    }


    @Override
    /* Protected method: doStep()
    Description: Handles make decision Logic
    Returns: void */
    protected void doStep() {
        //Check for element on front 
        //Check for the element on the right
        updateRelativeDirections();
        if (right == '#') { //If it's a wall
            //Turn left if there is also a wall in the front
            if (front == '#') {
                turnLeftCommand.execute(person);
                //Add to path
                path.append("L");
                logger.info("Turning Left");
            }
            else { //Move forward if there is no wall in the front
                moveForwardCommand.execute(person);
                //Add to path
                path.append("F");
                logger.info("Moving forward");
            }
        } else { //If element on right is not a wall 
            
            turnRightCommand.execute(person); //Turn right
            logger.info("Turning Right");
            //Add to path
            path.append("R");
            logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
            logger.info("Current Face: " + person.getCurrentFace());
            updateRelativeDirections();

            if (front != '#') {
                //Move forward
                moveForwardCommand.execute(person);
                //Add to path
                path.append("F");
                logger.info("Moving forward");
                logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
                logger.info("Current Face: " + person.getCurrentFace());
            }
            
        }


    }

    @Override
    /* Protected method: postProcess()
    Description: Prints canonical and factorized path
    Returns: void */
    protected void postProcess() {
        System.out.println("Canonical Path: " + showPath());
        System.out.println("Factorized Path: " + factorizedPath(showPath()));
    }

    /* Public method: updateRelativeDirections()
    Description: Updates relative right, left, front, back directions based on current facing 
    Returns: void */
    public void updateRelativeDirections() {
        person.checkSurroundings();
        char[] surroundings = person.getSurroundings();
        char[][] maze = person.getMaze();

        //use switch case to identify what direction is right, left, front, back based on the respective facing side
        switch(person.getCurrentFace()) {
            case 'E':
                right = surroundings[3]; //element on its south side
                left = surroundings[2]; //element on its north side
                front = surroundings[0]; //element on its east side
                back = surroundings[1]; //element on its west side
                break;
            case 'W':
                right = surroundings[2];
                left = surroundings[3];
                front = surroundings[1];
                back = surroundings[0];
                break;
            case 'N':
                right = surroundings[0];
                left = surroundings[1];
                front = surroundings[2];
                back = surroundings[3];
                break;
            case 'S':
                right = surroundings[1];
                left = surroundings[0];
                front = surroundings[3];
                back = surroundings[2];
                break;
        }
    }

    /* Public method: recordPath()
    Description:Loops decisions to record path for given maze until end point is reached
    Returns: void */
    public void recordPath() {
        while(!checkWin()) {
            doStep();
            logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
            logger.info("Current Face: " + person.getCurrentFace());
        }
        logger.info("Done recording path!");
    }

    /* Public method: showPath()
    Description: generates canonical path of string
    Returns: String */
    public String showPath() {
        return path.toString();
    }

    /* Public method: factorizedPath()
    Parameters: StringBuilder (path given (cound be from -i or -p flag))
    Description: generates factorized expresson for path
    Returns: void */
    public String factorizedPath(String givenPath) {
        char currentLetter = givenPath.charAt(0); //first index of path string
        int count = 1;
        //Account for only 1 letter in path
        if (givenPath.length() == 1) {
            factorizedPath.append(currentLetter).append(" ");
            return factorizedPath.toString();
        }
        for (int i = 1; i < givenPath.length(); i++) {
            if (givenPath.charAt(i) == currentLetter) { //increase count if a consecutive index matches currentLetter
                count++;
            }
            else { //If there is no match and the index is not the last one
                if (count != 1) {
                    factorizedPath.append(count); //append currentLetter and count to factorizedPath string
                }
                
                factorizedPath.append(currentLetter).append(" ");
            
                currentLetter = givenPath.charAt(i); //set currentLetter to current index character
                count = 1; //reset count to 1
            }
        }
        //Account for last letter
        if (count != 1) {
            factorizedPath.append(count); //append currentLetter and count to factorizedPath string
        }
        factorizedPath.append(currentLetter).append(" ");

        return factorizedPath.toString();

    }

}

class ValidatePath extends AbstractPathSolver {
    private static final Logger logger = LogManager.getLogger();
    
    private Command moveForwardCommand = new MoveForwardCommand();
    private Command turnLeftCommand = new TurnLeftCommand();
    private Command turnRightCommand = new TurnRightCommand();

    public ValidatePath(Person person) {
        super(person);


    }

    @Override
    public void doStep() {}

    @Override
    public void postProcess() {}


    /* Public method: checkPath()
    Description: checks given path to see if it is valid
    Returns: Boolean */
    public boolean checkPath(String givenPath) {
        //Traverse the path string
        for (int i = 0; i < givenPath.length(); i++) {
            char currentChar = givenPath.charAt(i);

            //Detect consecutive digits
            if (Character.isDigit(currentChar)) {
                StringBuilder numberBuilder = new StringBuilder();
                while (i < givenPath.length() && Character.isDigit(givenPath.charAt(i))) {
                    numberBuilder.append(givenPath.charAt(i));
                    i++;
                }

                int moveCount = Integer.parseInt(numberBuilder.toString());

                //Apply move multiple times if number precedes a letter
                if (i < givenPath.length()) {
                    char moveChar = givenPath.charAt(i);
                    for (int j = 0; j < moveCount; j++) {
                        executeMove(moveChar);
                    }
                }

                // Skip any spaces after the letter
                while (i < givenPath.length() && givenPath.charAt(i) == ' ') {
                    i++;
                }
            } else if (currentChar == 'F' || currentChar == 'R' || currentChar == 'L') { ////Process single letters normally if there is no number
                logger.info("moving: " + currentChar);
                executeMove(currentChar);
            } 
            
            
        }

        return checkWin();
    }
    /* Private method: executeMove(char move)
    Description: executes move for checkPath() method
    Returns: void */
    private void executeMove(char move) {
        char[] surroundings = person.getSurroundings();
    
        switch (move) {
            case 'R':
                turnRightCommand.execute(person);;
                break;
            case 'L':
                turnLeftCommand.execute(person);
                break;
            case 'F':
                if (surroundings[0] == '#') {  // Wall in front
                    logger.error("Error: wall found in path");
                    System.exit(0);
                } else {
                    moveForwardCommand.execute(person);
                }
                break;
            default:
                logger.error("Invalid move: " + move);
                System.exit(0);
        }
    }

}

//PATH CLASS WAS ORIGINALLY USED IN A1 CODE
//THIS CLASS IS COMMENTED OUT AS IT IS NOT USED FOR THIS ASSIGNMENT
/* 
class Path {
    private static final Logger logger = LogManager.getLogger();
    private Person person;
    private StringBuilder factorizedPath =  new StringBuilder();
    private StringBuilder path = new StringBuilder();
    private char right;
    private char left;
    private char front;
    private char back;
    private Command moveForwardCommand;
    private Command turnLeftCommand;
    private Command turnRightCommand;


    public Path(Person person) {
        this.person = person;
        this.moveForwardCommand = new MoveForwardCommand();
        this.turnLeftCommand = new TurnLeftCommand();
        this.turnRightCommand = new TurnRightCommand();
        
    }

    /* Public method: updateRelativeDirections()
    Description: Updates relative right, left, front, back directions based on current facing 
    Returns: void */ /* 
    public void updateRelativeDirections() {
        person.checkSurroundings();
        char[] surroundings = person.getSurroundings();
        char[][] maze = person.getMaze();

        //use switch case to identify what direction is right, left, front, back based on the respective facing side
        switch(person.getCurrentFace()) {
            case 'E':
                right = surroundings[3]; //element on its south side
                left = surroundings[2]; //element on its north side
                front = surroundings[0]; //element on its east side
                back = surroundings[1]; //element on its west side
                break;
            case 'W':
                right = surroundings[2];
                left = surroundings[3];
                front = surroundings[1];
                back = surroundings[0];
                break;
            case 'N':
                right = surroundings[0];
                left = surroundings[1];
                front = surroundings[2];
                back = surroundings[3];
                break;
            case 'S':
                right = surroundings[1];
                left = surroundings[0];
                front = surroundings[3];
                back = surroundings[2];
                break;
        }
    }

    /* Public method: makeDecision()
    Description: Makes a movement decision for the runner based on its surroundings
    Returns: void */ /* 
    public void makeDecision() {

        //Check for element on front 
        //Check for the element on the right
        updateRelativeDirections();
        if (right == '#') { //If it's a wall
            //Turn left if there is also a wall in the front
            if (front == '#') {
                turnLeftCommand.execute(person);
                //Add to path
                path.append("L");
                logger.info("Turning Left");
            }
            else { //Move forward if there is no wall in the front
                moveForwardCommand.execute(person);
                //Add to path
                path.append("F");
                logger.info("Moving forward");
            }
        } else { //If element on right is not a wall 
            
            turnRightCommand.execute(person); //Turn right
            logger.info("Turning Right");
            //Add to path
            path.append("R");
            logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
            logger.info("Current Face: " + person.getCurrentFace());
            updateRelativeDirections();

            if (front != '#') {
                //Move forward
                moveForwardCommand.execute(person);
                //Add to path
                path.append("F");
                logger.info("Moving forward");
                logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
                logger.info("Current Face: " + person.getCurrentFace());
            }
            
        }



    }

    /* Public method: checkWin()
    Description:Checks for win after each decision
    Returns: void */ /* 
    public boolean checkWin() {
        if (person.getCurrentPosition()[0] == person.getEnd()[0] && person.getCurrentPosition()[1] == person.getEnd()[1]) {
            logger.info("YAY you won!!");
            return true;
        }
        else {
            return false;
        }
    }

    /* Public method: recordPath()
    Description:Loops decisions to record path for given maze until end point is reached
    Returns: void */ /* 
    public void recordPath() {
        while(!checkWin()) {
            makeDecision();
            logger.info("Current Position: " + Arrays.toString(person.getCurrentPosition()));
            logger.info("Current Face: " + person.getCurrentFace());
        }
        logger.info("Done recording path!");
    }

    /* Public method: showPath()
    Description: generates canonical path of string
    Returns: String */ /* 
    public String showPath() {
        return path.toString();
    }


    /* Public method: checkPath()
    Description: checks given path to see if it is valid
    Returns: Boolean */ /* 
    public boolean checkPath(String givenPath) {
        //Traverse the path string
        for (int i = 0; i < givenPath.length(); i++) {
            char currentChar = givenPath.charAt(i);

            //Detect consecutive digits
            if (Character.isDigit(currentChar)) {
                StringBuilder numberBuilder = new StringBuilder();
                while (i < givenPath.length() && Character.isDigit(givenPath.charAt(i))) {
                    numberBuilder.append(givenPath.charAt(i));
                    i++;
                }

                int moveCount = Integer.parseInt(numberBuilder.toString());

                //Apply move multiple times if number precedes a letter
                if (i < givenPath.length()) {
                    char moveChar = givenPath.charAt(i);
                    for (int j = 0; j < moveCount; j++) {
                        executeMove(moveChar);
                    }
                }

                // Skip any spaces after the letter
                while (i < givenPath.length() && givenPath.charAt(i) == ' ') {
                    i++;
                }
            } else if (currentChar == 'F' || currentChar == 'R' || currentChar == 'L') { ////Process single letters normally if there is no number
                logger.info("moving: " + currentChar);
                executeMove(currentChar);
            } 
            
            
        }

        return checkWin();
    }
    /* Private method: executeMove(char move)
    Description: executes move for checkPath() method
    Returns: void */ /* 
    private void executeMove(char move) {
        char[] surroundings = person.getSurroundings();
    
        switch (move) {
            case 'R':
                turnRightCommand.execute(person);
                break;
            case 'L':
                turnLeftCommand.execute(person);
                break;
            case 'F':
                if (surroundings[0] == '#') {  // Wall in front
                    logger.error("Error: wall found in path");
                    System.exit(0);
                } else {
                    moveForwardCommand.execute(person);
                }
                break;
            default:
                logger.error("Invalid move: " + move);
                System.exit(0);
        }
    }
    

    /* Public method: factorizedPath()
    Parameters: StringBuilder (path given (cound be from -i or -p flag))
    Description: generates factorized expresson for path
    Returns: void */ /* 
    public String factorizedPath(String givenPath) {
        char currentLetter = givenPath.charAt(0); //first index of path string
        int count = 1;
        //Account for only 1 letter in path
        if (givenPath.length() == 1) {
            factorizedPath.append(currentLetter).append(" ");
            return factorizedPath.toString();
        }
        for (int i = 1; i < givenPath.length(); i++) {
            if (givenPath.charAt(i) == currentLetter) { //increase count if a consecutive index matches currentLetter
                count++;
            }
            else { //If there is no match and the index is not the last one
                if (count != 1) {
                    factorizedPath.append(count); //append currentLetter and count to factorizedPath string
                }
                
                factorizedPath.append(currentLetter).append(" ");
            
                currentLetter = givenPath.charAt(i); //set currentLetter to current index character
                count = 1; //reset count to 1
            }
        }
        //Account for last letter
        if (count != 1) {
            factorizedPath.append(count); //append currentLetter and count to factorizedPath string
        }
        factorizedPath.append(currentLetter).append(" ");

        return factorizedPath.toString();

    }


} */
