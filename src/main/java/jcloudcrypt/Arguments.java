package jcloudcrypt;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

public class Arguments {
    /** Encryption / Decryption flag. 'e' = encryption and 'd' = decryption */
    private char selection;
    /** Path to file. */
    private String filePath;
    /** Apache Commons CLI Options object */
    private Options options;
    /** Apache Commons CLI CommandLine object */
    private CommandLine arguments;
    /** Parse error flag */
    private boolean parserError;

    public Arguments(String[] args) {
        options = new Options();
        buildOptions();
        parserError = parseArgs(args);
        if (!parserError) {
            if (arguments.hasOption("encrypt")) {
                selection = 'e';
                filePath = arguments.getOptionValue("encrypt");
            } else if (arguments.hasOption("decrypt")) {
                selection = (arguments.hasOption("checkkey")) ? 'c' : 'd';
                filePath = arguments.getOptionValue("decrypt");
            }
        }
    }

    /**
     * Prints the help text.
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JCloudCrypt [OPTION] -e <FILE> \nusage: JCloudCrypt -d <FILE>", options);
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
     * Returns the value assigned to this option.
     * 
     * @param opt Option name
     * @return returns the String value of the option if it was set, otherwise
     *         returns null
     */
    public String getOptionValue(String opt) {
        return arguments.getOptionValue(opt);
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
        // Checks for the XOR to see if one and only one option is present.
        return !(hasEncrypt ^ hasDecrypt);
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
     * Check if file does not exist and if it does, it is not a directory.
     * 
     * @param filePath String path to file
     * @return returns false if file exists
     */
    public boolean checkFileDoesNotExist(String filePath) {
        File file = new File(filePath);
        return !file.isFile();
    }

    public int runArgumentChecks(Arguments arguments) {
        if (arguments.checkForConflicts()) {
            return 2;
        }
        if (arguments.checkOutOfMemBounds()) {
            return 3;
        }
        if (arguments.checkOutOfParallelismBounds()) {
            return 4;
        }
        if (arguments.checkOutOfTimeCostBounds()) {
            return 5;
        }
        String filePath = arguments.getFilePath();
        if (checkFileDoesNotExist(filePath)) {
            return 6;
        }
        return 0;
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
        Option decrypt = Option.builder("d").longOpt("decrypt").argName("FILE").hasArg()
                .desc("File to be decrypted. Options will be read from file header.").build();
        options.addOption(decrypt);
        Option encrypt = Option.builder("e").longOpt("encrypt").argName("FILE").hasArg().desc("File to be encrypted")
                .build();
        options.addOption(encrypt);
        Option memCost = Option.builder("m").longOpt("memCost").argName("SIZE").hasArg()
                .desc("Password hashing memory cost in MB (default = 64)").build();
        options.addOption(memCost);
        Option parallelism = Option.builder("p").longOpt("parallelism").argName("NUMBER").hasArg()
                .desc("Number of lanes and threads to be used for password hashing (default = 4)").build();
        options.addOption(parallelism);
        Option timeCost = Option.builder("t").longOpt("timeCost").argName("NUMBER").hasArg()
                .desc("Number of passes through memory (default = 10)").build();
        options.addOption(timeCost);
        Option help = new Option("h", "help", false, "Prints this message");
        options.addOption(help);
        Option obfuscate = new Option("r", false, "Randomize filename");
        options.addOption(obfuscate);
        Option checkKey = Option.builder("c").longOpt("checkkey").argName("PASSWORD").desc(
                "Checks the encrypted password. Requires the decryption flag, but does not continue with decryption step. Returns 0 on success, 1 on failure.")
                .build();
        options.addOption(checkKey);
    }
}