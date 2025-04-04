package ca.mcmaster.se2aa4.mazerunner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;



public class MainTest {
    @Test
    void validCanonicalPathTest() {
        //input
        String[] args = {"-i", "./examples/direct.maz.txt"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Canonical Path"));
        assertTrue(outputString.contains("FRFFLFFFRFLFRFLFF"));


    }

    @Test
    void validFactorialPathTest() {
        //input
        String[] args = {"-i", "./examples/small.maz.txt"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Factorized Path"));
        assertTrue(outputString.contains("F R F 2L 2F R 2F R 2F 2L 4F R 2F R 4F 2L 2F R 4F R 2F R 2F 2L 2F L 2F L 4F R 2F R 2F 2L 4F R 2F R 2F 2L 2F R 2F R 4F R 2F L 2F R 2F L F"));
    }

    @Test
    void invalidPathInputTest() { //take it really close to the exit but mess up at the end
        //input
        String[] args = {"-i", "./examples/small.maz.txt", "-p", "FLFR2FL6FR4FR2FL2FR23F"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();
        assertTrue(outputString.contains("Invalid path!"));

    }

    @Test //Check if program mistakenly considers entry as exit point
    void reachingEntryInsteadOfExitTest() {
        //input
        String[] args = {"-i", "./examples/small.maz.txt", "-p", "FFLLFF"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();
        assertTrue(outputString.contains("Invalid path!"));
    }

    @Test
    void isEmptyPathTest() {
        //input
        String[] args = {"-i", "./examples/small.maz.txt", "-p"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);
        //Capture output
        String outputString = output.toString();

        // Validate output contains the usage message
        String expectedOutput = "usage: MazeRunner";
        assertTrue(outputString.contains(expectedOutput));
    }

    @Test
    void biggestMazeTest() {
        //input
        String[] args = {"-i", "./examples/giant.maz.txt"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Canonical Path"));
        assertTrue(outputString.contains("Factorized Path"));
    }

    @Test
    void smallestMazeTest() {
        //input
        String[] args = {"-i", "./examples/tiny.maz.txt"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Canonical Path"));
        assertTrue(outputString.contains("Factorized Path"));
    }

    @Test
    void validPathWithRedundantStepsTest() { //has a lot of unnessessary 180 and 360 degree turns but still reaches exit
        //input
        String[] args = {"-i", "./examples/tiny.maz.txt", "-p", "FFFLLLLLFFRLLRFFLFFRRFFFFF"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Valid path!"));
    }

    @Test //path is valid but it goes past the length of the maze after exiting
    void invalidPathAfterExitTest() {
        //input
        String[] args = {"-i", "./examples/tiny.maz.txt", "-p", "FFFLLLLLFFRLLRFFLFFRRFFFFFFFFFFF"};

        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        //Run program
        Main.main(args);

        //Capture output
        String outputString = output.toString();

        //Validate output contains expected text
        assertTrue(outputString.contains("Invalid path!")); //Returns invalid because because it would break the premise that the maze solver should only exit once the end is reached
    }

    @Test
    void invalidLoopingPathTest() {
        //input with a looping path (e.g., moves back to a previously visited point)
        String[] args = {"-i", "./examples/small.maz.txt", "-p", "FFFLFRFFLFRFFFFLFRFFLFRF"};
    
        //Capture output
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    
        //Run program
        Main.main(args);
    
        //Capture output
        String outputString = output.toString();
    
        //Validate output contains expected text
        assertTrue(outputString.contains("Invalid path!"));
    }

}
