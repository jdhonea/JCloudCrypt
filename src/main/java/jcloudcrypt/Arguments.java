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
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JCloudCryptCLI", options);
    }

    public boolean hasOption(String opt) {
        return arguments.hasOption(opt);
    }

    public boolean hasArgumentErrors() {
        return parserError;
    }

    public char getSelection() {
        return selection;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean checkForConflicts() {
        boolean hasEncrypt = arguments.hasOption("encrypt");
        boolean hasDecrypt = arguments.hasOption("decrypt");
        // Has both encrypt and decrypt flag
        if (hasEncrypt && hasDecrypt)
            return true;
        // Has neither encrypt or decrypt flag
        else if (!(hasEncrypt || hasDecrypt))
            return true;
        else if (hasEncrypt) {
            selection = 'e';
            filePath = arguments.getOptionValue("encrypt");
        } else if (hasDecrypt) {
            selection = 'd';
            filePath = arguments.getOptionValue("decrypt");
        }
        return false;
    }

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

    public void fixVariables() {
        if (arguments.hasOption("memCost"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("memCost")) * 1024;
        if (arguments.hasOption("parallelism"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("parallelism"));
        if (arguments.hasOption("timeCost"))
            Variables.MEMORYCOST = Integer.parseInt(arguments.getOptionValue("timeCost"));
    }

    private boolean parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            arguments = parser.parse(options, args);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

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