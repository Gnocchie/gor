import org.apache.commons.cli.Options;

import java.io.IOException;
import org.apache.commons.cli.*;

public class cvMain {
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(Option.builder()
                .longOpt("seq")
                .hasArg()
                .desc("train file")
                .build());
        options.addOption(Option.builder()
                .longOpt("sout")
                .hasArg()
                .desc("summary output")
                .build());
        options.addOption(Option.builder()
                .longOpt("dout")
                .hasArg()
                .desc("detail output")
                .build());
        options.addOption(Option.builder()
                .longOpt("k")
                .hasArg()
                .desc("fold count")
                .build());
        options.addOption(Option.builder()
                .longOpt("method")
                .hasArg()
                .desc("gor method")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            formatter.printHelp("java -jar cross_validate.jar", options);
            return;
        }

        // Extract arguments using long options
        String train = cmd.getOptionValue("seq");
        String fold = cmd.getOptionValue("k");
        String sumPath = cmd.getOptionValue("sout");
        String detPath = cmd.getOptionValue("dout");
        String method = cmd.getOptionValue("method");

        // Validate arguments
        if (train == null || fold == null || sumPath == null || detPath == null || method == null ) {
            System.out.println("Missing required arguments.");
            formatter.printHelp("java -jar cross_validate.jar", options);
        }
        GOR gor = new GOR1();

        if (method.matches("gor1")) {
            System.out.println("gored 1");
            gor = new GOR1();
        }
        else if (method.matches("gor3")) {
            System.out.println("gored 3");
            gor = new GOR3();
        }
        else if (method.matches("gor4")) {
            System.out.println("gored 4");
            gor = new GOR4();
        }

        Cross_Validator cv = new Cross_Validator();
        cv.cross_validate(gor, train, Integer.parseInt(fold), sumPath, detPath);
    }
}
