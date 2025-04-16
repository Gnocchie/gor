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
    public void runP(String file) {
        ArrayList<COMPLEX_SEQ> sequences = gp.fasta_parser(file);

        for (COMPLEX_SEQ seq : sequences) {
            seq.PS = predict(seq.AS);
        }
        printP(sequences);
    }
    public void runP(String file, String outPath) {
        ArrayList<COMPLEX_SEQ> sequences = gp.fasta_parser(file);

        for (COMPLEX_SEQ seq : sequences) {
            seq.PS = predict(seq.AS);
        }
        writeP(sequences, outPath);
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
    public void printP(ArrayList<COMPLEX_SEQ> sequences){
        try {
            for (COMPLEX_SEQ seq : sequences) {
                System.out.println("> " + seq.ID);

                System.out.println("AS " + seq.AS);

                System.out.println("PS " + seq.PS);

            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
    public void writeP(ArrayList<COMPLEX_SEQ> sequences, String outPath){
        try {
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
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
