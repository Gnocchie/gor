import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AA_Matrix extends SS_Matrix {
    String AA;
    int pairIndex;
    HashMap<String, AA_Matrix> pairAA_list = new HashMap<>();
    HashMap<Integer, AA_Matrix> pairAA_indexlist = new HashMap<>();

    public AA_Matrix(String P, String AA){
        super(P);
        this.AA = AA;
    }
    public AA_Matrix getPairAA(String pairAA){
        return pairAA_list.get(pairAA);
    }
    public AA_Matrix getPairIndexAA(int index){
        return pairAA_indexlist.get(index);
    }

    @Override
    public void write(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write("=" + AA + "," + P + "=" + "\n\n");
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
    public void writePairs(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            for (String aa1 : aminoAcids){
                AA_Matrix aa1PairMatrix = pairAA_list.get(aa1);
                for (int i = -8; i < 9; i++) {
                    AA_Matrix aa1PairIndexMatrix = aa1PairMatrix.getPairIndexAA(i);
                    writer.write("=" + P + "," + AA + "," + aa1 + "," + i +  "=" + "\n\n");
                    for (Map.Entry<String, int[]> entry : aa1PairIndexMatrix.matrix.entrySet()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(entry.getKey()).append("\t");

                        for (int item : entry.getValue())
                            sb.append(String.valueOf(item)).append("\t");
                        sb.append("\n");
                        writer.write(sb.toString());
                    }
                    writer.write("\n");
                }
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void print() {

        System.out.println("=" + AA + "," + P + "=" + "\n");

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
