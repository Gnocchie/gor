import org.apache.commons.cli.*;

public class tMain {
    public static void main(String[] args) {

        Options options = new Options();
        // Updated to use long options with double dashes
        options.addOption(Option.builder()
                .longOpt("db")
                .hasArg()
                .desc("Path to training file (seclib format)")
                .build());
        options.addOption(Option.builder()
                .longOpt("method")
                .hasArg()
                .desc("Method to use: gor1, gor3, or gor4")
                .build());
        options.addOption(Option.builder()
                .longOpt("model")
                .hasArg()
                .desc("Output path for model file")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            formatter.printHelp("java -jar train.jar", options);
            return;
        }

        // Extract arguments using long options
        String dbPath = cmd.getOptionValue("db");
        String method = cmd.getOptionValue("method");
        String modelPath = cmd.getOptionValue("model");

        // Validate arguments
        if (dbPath == null || method == null || modelPath == null) {
            System.out.println("Missing required arguments.");
            formatter.printHelp("java -jar train.jar", options);
            return;
        }

        if (!method.matches("gor1|gor3|gor4")) {
            System.out.println("Invalid method! Choose from: gor1, gor3, or gor4.");
            return;
        }

        GOR gor = null;
        if (method.matches("gor1")) {
            gor = new GOR1();
        }
        else if (method.matches("gor3")){
            gor = new GOR3();
        }
        else if (method.matches("gor4")){
            gor = new GOR4();
        }
        assert gor != null;
        trainGORModel(gor, dbPath, modelPath);
    }
    public static void trainGORModel(GOR gor, String dbPath, String modelPath) {
        gor.runT(dbPath);
        gor.writeM(modelPath);
    }
}