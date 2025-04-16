import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GOR {
    Trainer T = new Trainer("gor1");
    Predictor P = new Predictor("gor1");
    GOR_parser gp = new GOR_parser();


    HashMap<String, SS_Matrix> matrices;


    public void train(String seq, String ss) {
        T.train(seq, ss, matrices);
    }

    public String predict(String seq) {
        return P.predict(seq, matrices);
    }
    public void runT(String file) {
        ArrayList<COMPLEX_SEQ> sequences = gp.db_parser(file);
        for (COMPLEX_SEQ seq : sequences) {
            train(seq.AS, seq.SS);
        }
    }
    public void runT_COMPLEX(ArrayList<COMPLEX_SEQ> sequences){
        for (COMPLEX_SEQ seq : sequences){
            train(seq.AS, seq.SS);
        }
    }
    public void runP(String file, String format) {
        ArrayList<COMPLEX_SEQ> sequences = gp.fasta_parser(file);

        for (COMPLEX_SEQ seq : sequences) {
            seq.PS = predict(seq.AS);
        }
        printP(sequences, format);
    }
    public void runP(String file, String format, String outPath) {
        ArrayList<COMPLEX_SEQ> sequences = gp.fasta_parser(file);

        for (COMPLEX_SEQ seq : sequences) {
            seq.PS = predict(seq.AS);
        }
        if (format.matches("html")){
            printP(sequences, format);
            writeP(sequences, "txt", outPath);
        }
        writeP(sequences, format, outPath);
    }
    public void runP_COMPLEX(ArrayList<COMPLEX_SEQ> sequences) {
        for (COMPLEX_SEQ seq : sequences) {
            seq.PS = predict(seq.AS);
        }
    }
    public void reset(){}
    public void setMatrix(String modelPath) {}
    public void printM() {
        for (String p : new String[]{"C", "E", "H"}) {
            SS_Matrix currentMatrix = matrices.get(p);
            currentMatrix.print();
        }
    }
    public void writeM(String outPath){}
    public void printP(ArrayList<COMPLEX_SEQ> sequences, String format){

        try {
            if (format.matches("txt")) {
                for (COMPLEX_SEQ seq : sequences) {
                    System.out.println("> " + seq.ID);
                    System.out.println("AS " + seq.AS);
                    System.out.println("PS " + seq.PS);
                }
            } else if (format.matches("html")) {
                System.out.println("<html><head><title>Prediction Output</title>");
                System.out.println("<style>");
                System.out.println("body { font-family: monospace; }");  // Monospace font
                System.out.println(".helix { color: #FF5733; }");       // Orange-Red for H
                System.out.println(".sheet { color: #33A1FD; }");       // Sky Blue for E
                System.out.println(".coil { color: #2ECC71; }");        // Green for C
                System.out.println("</style></head><body>");

                System.out.println("<h2>Prediction Results</h2>");

                // Get the first sequence only
                COMPLEX_SEQ seq = sequences.get(0);

                System.out.println("<b>ID:</b> " + seq.ID + "<br><br>");

                int lineWidth = 60;  // Max characters per line before wrapping

                for (int i = 0; i < seq.AS.length(); i += lineWidth) {
                    int end = Math.min(i + lineWidth, seq.AS.length());

                    // Print sequence
                    System.out.println("<pre>" + seq.AS.substring(i, end) + "</pre>");

                    // Print prediction with colors
                    System.out.print("<pre>");
                    for (int j = i; j < end; j++) {
                        char c = seq.PS.charAt(j);
                        String cssClass = (c == 'H') ? "helix" :
                                (c == 'E') ? "sheet" :
                                        "coil";
                        System.out.print("<span class='" + cssClass + "'>" + c + "</span>");
                    }
                    System.out.println("</pre>");
                }

                System.out.println("</body></html>");
            }

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
    public void writeP(ArrayList<COMPLEX_SEQ> sequences, String format, String outPath){
        try {
            if (format.matches("txt")){
                BufferedWriter bw = new BufferedWriter(new FileWriter(outPath));
                for (COMPLEX_SEQ seq : sequences) {
                    bw.write("> " + seq.ID);
                    bw.newLine();
                    bw.write("AS " + seq.AS);
                    bw.newLine();
                    bw.write("PS " + seq.PS);
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();
                }
                bw.close();
            } else if (format.matches("html")) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(outPath));

                // Write HTML header
                bw.write("<html><head><title>Prediction Output</title>");
                bw.write("<style>");
                bw.write("body { font-family: monospace; }");  // Monospace font
                bw.write(".helix { color: #FF5733; }");       // Orange-Red for H
                bw.write(".sheet { color: #33A1FD; }");       // Sky Blue for E
                bw.write(".coil { color: #2ECC71; }");        // Green for C
                bw.write("</style></head><body>");
                bw.newLine();

                bw.write("<h2>Prediction Results</h2>");
                bw.newLine();

                // Max sequence width per line before wrapping
                int lineWidth = 60;

                COMPLEX_SEQ seq = sequences.get(0);
                bw.write("<div>");
                bw.newLine();
                bw.write("<b>ID:</b> " + seq.ID + "<br>");
                bw.newLine();

                // Split sequence and prediction into multiple lines
                for (int i = 0; i < seq.AS.length(); i += lineWidth) {
                    int end = Math.min(i + lineWidth, seq.AS.length());

                    // Print sequence
                    bw.write("<pre>" + seq.AS.substring(i, end) + "</pre>");
                    bw.newLine();

                    // Print prediction with colors
                    bw.write("<pre>");
                    for (int j = i; j < end; j++) {
                        char c = seq.PS.charAt(j);
                        String cssClass = (c == 'H') ? "helix" :
                                (c == 'E') ? "sheet" :
                                        "coil";
                        bw.write("<span class='" + cssClass + "'>" + c + "</span>");
                    }
                    bw.write("</pre>");
                    bw.newLine();
                }

                bw.write("<br>");  // Extra space between sequences
                bw.write("</div>");
                bw.newLine();


                // Close the HTML
                bw.write("</body></html>");
                bw.newLine();
                bw.close();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
