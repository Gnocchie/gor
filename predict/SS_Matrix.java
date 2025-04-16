import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SS_Matrix {
    HashMap<String, int[]> matrix =  new HashMap<>();;
    HashMap<String, AA_Matrix> aa_list = new HashMap<String, AA_Matrix>();
    String P;
    int freq;
    static String[] aminoAcids = {
            "A", "C", "D", "E", "F", "G", "H", "I", "K", "L",
            "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y"
    };


    public SS_Matrix(String P){
        this.P = P;
        matrix = new HashMap<>();

        for (String aa : aminoAcids) {
            matrix.put(aa, new int[17]);
        }
    }
    public SS_Matrix(String P, String gor){
        this.P = P;
        if (gor.matches("gor1")){
            for (String aa : aminoAcids) {
                matrix.put(aa, new int[17]);
            }
        }
        else if (gor.matches("gor3")){
            for (String aa : aminoAcids) {
                aa_list.put(aa, new AA_Matrix(P, aa));
            }
        }
        else if (gor.matches("gor4")){
            for (String caa : aminoAcids) {
                AA_Matrix aaMatrix = new AA_Matrix(P, caa);
                for (String aa1 : aminoAcids) {
                    AA_Matrix aa1Matrix = new AA_Matrix(P, aa1);
                    for (int i = -8; i < 9; i++) {
                        AA_Matrix aa2Matrix = new AA_Matrix(P, aa1);
                        aa1Matrix.pairAA_indexlist.put(i, aa2Matrix);
                    }
                    aaMatrix.pairAA_list.put(aa1, aa1Matrix);
                }
                aa_list.put(caa, aaMatrix);
            }
        }
    }

    public String getP() {
        return P;
    }
    public int[] getRow(String AA){
        return matrix.get(AA);
    }
    public AA_Matrix getAA(String aa) {
        return aa_list.get(aa);
    }
    public void setRow(String key, int[] values){
        matrix.replace(key, values);
    }
    public void setMatrix(String[] rows){
        int freq_matrix = 0;
        for (String row : rows){
            String[] cols = row.strip().split("\t");
            int[] valueCols = new int[17];
            for (int i = 0; i < valueCols.length; i++) {
                valueCols[i] = Integer.parseInt(cols[i+1]);
            }
            freq_matrix += valueCols[0];
            setRow(cols[0], valueCols);
        }
        this.freq = freq_matrix;
    }
    public void print() {

        if (aa_list != null){
            System.out.println("NO MATRIX HERE");
        }
        else {
            System.out.println("=" + P + "=" + "\n");

            for (Map.Entry<String, int[]> entry : matrix.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey()).append("\t");

                for (int item : entry.getValue())
                    sb.append(String.valueOf(item)).append("\t");
                System.out.println(sb);
            }
            System.out.println("\n");
        }
    }
    public void write(String file){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write("=" + P + "=" + "\n\n");
            for (Map.Entry<String, int[]> entry : matrix.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey()).append("\t");

                for (int item : entry.getValue())
                    sb.append(String.valueOf(item)).append("\t");
                sb.append("\n");
                writer.write(sb.toString());
            }
            writer.write("\n\n");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeAA(String file){
        for (String aa : aminoAcids){
            AA_Matrix currentAAM = aa_list.get(aa);
            currentAAM.write(file);
        }
    }
    public void writePair(String file){
        for (String aa : aminoAcids){
            AA_Matrix currentAAM = aa_list.get(aa);
            currentAAM.writePairs(file);
        }
    }
}

