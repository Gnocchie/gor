import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.commons.cli.*;

public class pMain {
    public static void main(String[] args) {
        Options options = new Options();
        // Updated to use long options with double dashes
        options.addOption(Option.builder()
                .longOpt("model")
                .hasArg()
                .desc("trained model file")
                .build());
        options.addOption(Option.builder()
                .longOpt("format")
                .hasArg()
                .desc("output format")
                .build());
        options.addOption(Option.builder()
                .longOpt("seq")
                .hasArg()
                .desc("input file")
                .build());
        options.addOption(Option.builder()
                .longOpt("out")
                .hasArg()
                .desc("output path")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
            formatter.printHelp("java -jar predict.jar", options);
            return;
        }

        // Extract arguments using long options
        String modelPath = cmd.getOptionValue("model");
        String format = cmd.getOptionValue("format");
        String seqPath = cmd.getOptionValue("seq");
        String outPath = cmd.getOptionValue("out");

        // Validate arguments
        if (seqPath == null || format == null || modelPath == null) {
            System.out.println("Missing required arguments.");
            formatter.printHelp("java -jar train.jar", options);
            return;
        }

        // Start training based on selected method
        try {
            BufferedReader br = new BufferedReader(new FileReader(modelPath));
            String header = br.readLine().strip();
            br.close();

            GOR gor = null;
            if (header.startsWith("// Matrix3D")){
                gor = new GOR1();
            } else if (header.startsWith("// Matrix4D")) {
                gor = new GOR3();
            } else if (header.startsWith("// Matrix6D")) {
                gor = new GOR4();
            }
            predictFromGOR(gor, seqPath, modelPath, format, outPath);

        } catch (Exception e){e.printStackTrace();}
    }

    public static void predictFromGOR(GOR gor, String seqPath, String modelPath, String format, String outpath) {
        gor.setMatrix(modelPath);
        if (outpath != null) gor.runP(seqPath, format, outpath);
        else gor.runP(seqPath, format);
    }
}
