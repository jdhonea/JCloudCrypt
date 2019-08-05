package jcloudcrypt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Arguments {
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
        Option decrypt = Option.builder("d").longOpt("decrypt").argName("file").hasArg().desc("file(s) to be decrypted")
                .build();
        options.addOption(decrypt);
        Option encrypt = Option.builder("e").longOpt("encrypt").argName("file").hasArg().desc("file(s) to be encrypted")
                .build();
        options.addOption(encrypt);
        Option memCost = Option.builder("m").argName("SIZE").hasArg()
                .desc("password hashing memory cost in KB (default = 65536)").build();
        options.addOption(memCost);
        Option parallelism = Option.builder("p").argName("NUMBER").hasArg()
                .desc("number of lanes and threads to be used for password hashing (default = 4)").build();
        options.addOption(parallelism);
        Option timeCost = Option.builder("t").argName("NUMBER").hasArg()
                .desc("number of passes through memory (default = 10)").build();
        options.addOption(timeCost);
        Option help = new Option("h", "help", false, "prints this message");
        options.addOption(help);
    }
}