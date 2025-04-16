import org.apache.commons.cli.*;

import java.io.IOException;
import java.lang.reflect.Array;

public class vMain {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt("p")
                .hasArg()
                .desc("prediction file")
                .build());
        options.addOption(Option.builder()
                .longOpt("r")
                .hasArg()
                .desc("seclib")
                .build());
        options.addOption(Option.builder()
                .longOpt("s")
                .hasArg()
                .desc("summary output")
                .build());
        options.addOption(Option.builder()
                .longOpt("d")
                .hasArg()
                .desc("detailed output")
                .build());
        options.addOption(Option.builder()
                .longOpt("f")
                .hasArg()
                .desc("output format")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            formatter.printHelp("java -jar validate.jar", options);
            return;
        }

        // Extract arguments using long options
        String prediction = cmd.getOptionValue("p");
        String seclib = cmd.getOptionValue("r");
        String sumPath = cmd.getOptionValue("s");
        String detPath = cmd.getOptionValue("d");
        String format = cmd.getOptionValue("f");

        // Validate arguments
        if (prediction == null || seclib == null || sumPath == null || detPath == null || format == null ) {
            System.out.println("Missing required arguments.");
            formatter.printHelp("java -jar validate.jar", options);
        }

        Validator v = new Validator();
        v.validate(seclib, prediction, sumPath, detPath);
    }
}
