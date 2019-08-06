package jcloudcrypt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

public class Arguments {
    private char selection;
    private String filePath;
    private Options options;
    private CommandLine arguments;
    private boolean parserError;

    public Arguments(String[] args) {
        options = new Options();
        buildOptions();
        parserError = parseArgs(args);
        if (!parserError) {
            if (arguments.hasOption("encrypt")) {
                selection = 'e';
                filePath = arguments.getOptionValue("encrypt");
            } else if (arguments.hasOption("encrypt")) {
                selection = 'd';
                filePath = arguments.getOptionValue("decrypt");
            }
        }
    }

    /**
     * Prints the help text.
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JCloudCryptCLI", options);
    }

    /**
     * Checks if an argument is present when running the program.
     * 
     * @param opt String argument
     * @return true if specified argument is present
     */
    public boolean hasOption(String opt) {
        return arguments.hasOption(opt);
    }

    /**
     * Checks if there is an error present in parsing arguments.
     * 
     * @return true if an error is present in arguments
     */
    public boolean hasArgumentErrors() {
        return parserError;
    }

    /**
     * Tells if user has selected to encrypt or decrypt a file.
     * 
     * @return char selection - 'e' for encrypt and 'd' for decrypt
     */
    public char getSelection() {
        return selection;
    }

    /**
     * Gets the path to the file to be encrypted/decrypted.
     * 
     * @return String file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Checks if user included both encrypt and decrypt arguments or neither one.
     * 
     * @return true if user included both or neither one.
     */
    public boolean checkForConflicts() {
        boolean hasEncrypt = arguments.hasOption("encrypt");
        boolean hasDecrypt = arguments.hasOption("decrypt");
        // Checks for the XOR of both to see if one and only one is present.
        if (hasEncrypt ^ hasDecrypt)
            return false;
        return true;
    }

    /**
     * Checks user's memory cost argument, if it's present, for incorrect input or
     * input outside of a given bounds. Boundaries are 1MB - 1GB.
     * 
     * @return true if an error is found
     */
    public boolean checkOutOfMemBounds() {
        if (arguments.hasOption("memCost")) {
            String memCost = arguments.getOptionValue("memCost");
            if (StringUtils.isNumeric(memCost)) {
                int memCostVal = Integer.parseInt(arguments.getOptionValue("memCost"));
                if (memCostVal < 1 || memCostVal > 1024)
                    return true;
            } else
                return true;
        }
        return false;
    }

    /**
     * Checks user's parallelism argument, if it's present, for incorrect input or
     * input outside of a given bounds. Boundaries are 1 - 32.
     * 
     * @return true if an error is found.
     */
    public boolean checkOutOfParallelismBounds() {
        if (arguments.hasOption("parallelism")) {
            String parallelism = arguments.getOptionValue("parallelism");
            if (StringUtils.isNumeric(parallelism)) {
                int parallelismVal = Integer.parseInt(arguments.getOptionValue("parallelism"));
                if (parallelismVal < 1 || parallelismVal > 32)
                    return true;
            } else
                return true;
        }
        return false;
    }

    /**
     * Checks user's time cost argument, if it's present, for incorrect input or
     * input outside of a given bounds. Boundaries are 1 - 100.
     * 
     * @return true if an error is found.
     */
    public boolean checkOutOfTimeCostBounds() {
        if (arguments.hasOption("timeCost")) {
            String timeCost = arguments.getOptionValue("timeCost");
            if (StringUtils.isNumeric(timeCost)) {
                int timeCostVal = Integer.parseInt(arguments.getOptionValue("timeCost"));
                if (timeCostVal < 1 || timeCostVal > 100)
                    return true;
            } else
                return true;
        }
        return false;
    }

    /**
     * Sets the hashing variables requested by the user.
     */
    public void fixVariables() {
        if (arguments.hasOption("memCost"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("memCost")) * 1024;
        if (arguments.hasOption("parallelism"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("parallelism"));
        if (arguments.hasOption("timeCost"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("timeCost"));
    }

    /**
     * Parses the arguments set by the user.
     * 
     * @param args String array containing the program arguments
     * @return true if there is an error in the arguements
     */
    private boolean parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            arguments = parser.parse(options, args);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    /**
     * Builds the arguments avaiable.
     */
    private void buildOptions() {
        Option decrypt = Option.builder("d").longOpt("decrypt").argName("file").hasArg().desc("file to be decrypted")
                .build();
        options.addOption(decrypt);
        Option encrypt = Option.builder("e").longOpt("encrypt").argName("file").hasArg().desc("file to be encrypted")
                .build();
        options.addOption(encrypt);
        Option memCost = Option.builder("m").longOpt("memCost").argName("SIZE").hasArg()
                .desc("password hashing memory cost in MB (default = 64)").build();
        options.addOption(memCost);
        Option parallelism = Option.builder("p").longOpt("parallelism").argName("NUMBER").hasArg()
                .desc("number of lanes and threads to be used for password hashing (default = 4)").build();
        options.addOption(parallelism);
        Option timeCost = Option.builder("t").longOpt("timeCost").argName("NUMBER").hasArg()
                .desc("number of passes through memory (default = 10)").build();
        options.addOption(timeCost);
        Option help = new Option("h", "help", false, "prints this message");
        options.addOption(help);
    }
}