import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GOR_parser {
    public GOR_parser() {}


    public  ArrayList<COMPLEX_SEQ> db_parser(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            ArrayList<COMPLEX_SEQ> sequences = new ArrayList<COMPLEX_SEQ>();

            COMPLEX_SEQ currentSEQ = new COMPLEX_SEQ();
            String line;
            while((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    currentSEQ = new COMPLEX_SEQ();
                    currentSEQ.ID = (line.substring(1).strip());
                } else if (line.startsWith("AS")) {
                    currentSEQ.AS = (line.split(" ")[1].strip());
                } else if (line.startsWith("SS")) {
                    currentSEQ.SS = (line.split(" ")[1].strip());
                    sequences.add(currentSEQ);
                }
            }
            br.close();
            return sequences;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<COMPLEX_SEQ> fasta_parser(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            ArrayList<COMPLEX_SEQ> sequences = new ArrayList<COMPLEX_SEQ>();
            StringBuilder currentAS = new StringBuilder();
            COMPLEX_SEQ currentSEQ = null;

            String line;
            while((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (currentSEQ != null) {
                        currentSEQ.AS = currentAS.toString();
                        sequences.add(currentSEQ);
                    }
                    currentSEQ = new COMPLEX_SEQ();
                    currentSEQ.ID = line.substring(1).strip();
                    currentAS = new StringBuilder();
                } else {
                    currentAS.append(line.strip());
                }
            }
            if (currentSEQ != null) {
                currentSEQ.AS = currentAS.toString();
                sequences.add(currentSEQ);
            }

            br.close();
            return sequences;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public  HashMap<String, String[]> model_parser(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            HashMap<String, String[]> MXs = new HashMap<>();


            String line;
            String currentMatrix = null;
            String[] currentAAs = new String[17];
            int i = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("=") && line.endsWith("=")) {
                    currentMatrix = line.substring(line.indexOf("=")+1, line.lastIndexOf("="));
                    currentAAs = new String[17];
                    MXs.put(currentMatrix, currentAAs);
                    i = 0;

                } else if (!line.isEmpty() && currentMatrix != null && i<17) {
                   MXs.get(currentMatrix)[i] = line;
                   i++;
                }
            }
            br.close();
            return MXs;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
