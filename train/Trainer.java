import java.util.HashMap;


public class Trainer {
    String gor;
    public Trainer(String gor){
        this.gor = gor;
    }

    public void train(String ass, String sss, HashMap<String, SS_Matrix> matrices){
       int windowSize = 17;

        for (int i=0; i<ass.length() - windowSize + 1; i++){
            String ass_window = ass.substring(i, i + windowSize);
            String sss_window = sss.substring(i, i + windowSize);
            if (gor.matches("gor1")) trainWindowG1(ass_window, sss_window, matrices);
            else if (gor.matches("gor3")) trainWindowG3(ass_window, sss_window, matrices);
            else if (gor.matches("gor4")) trainWindowG4(ass_window, sss_window, matrices);
        }

    }
    public void trainWindowG1(String ass_window, String sss_window, HashMap<String, SS_Matrix> matrices){
        String SS = String.valueOf(sss_window.charAt(8));
        SS_Matrix corresponding_matrix = matrices.get(SS);

        for (int i=0; i<ass_window.length(); i++){
            String AA = String.valueOf(ass_window.charAt(i));

            int[] row = corresponding_matrix.getRow(AA);
            if (row != null)
                corresponding_matrix.getRow(AA)[i]++;
        }
    }
    public void trainWindowG3(String ass_window, String sss_window, HashMap<String, SS_Matrix> matrices){
        String cSS = String.valueOf(sss_window.charAt(8));
        String cAA = String.valueOf(ass_window.charAt(8));
        SS_Matrix corresponding_matrix = matrices.get(cSS).getAA(cAA);
        if (corresponding_matrix != null) {
            for (int i=0; i<ass_window.length(); i++){
                String currentAA = String.valueOf(ass_window.charAt(i));
                int[] row = corresponding_matrix.getRow(currentAA);
                if (row != null)
                    row[i]++;
            }

        }
    }
    public void trainWindowG4(String ass_window, String sss_window, HashMap<String, SS_Matrix> matrices){
        String cSS = String.valueOf(sss_window.charAt(8));
        String cAA = String.valueOf(ass_window.charAt(8));
        AA_Matrix corresponding_matrix = matrices.get(cSS).getAA(cAA);
        if (corresponding_matrix != null) {
            for (int i=0; i<ass_window.length(); i++){
                String currentAA = String.valueOf(ass_window.charAt(i));
                int[] row1 = corresponding_matrix.getRow(currentAA);
                if (row1!=null) row1[i]++;
                AA_Matrix corresponding_pair_matrix = corresponding_matrix.getPairAA(currentAA);
                if (corresponding_pair_matrix != null){
                    AA_Matrix corresponding_pair_index_matrix = corresponding_pair_matrix.getPairIndexAA(i-8);
                    for (int j = i + 1; j < ass_window.length(); j++) {
                        String currentPairAA = String.valueOf(ass_window.charAt(j));
                        int[] row2 = corresponding_pair_index_matrix.getRow(currentPairAA);
                        if (row2 != null) {
                            row2[j]++;
                        }

                    }
                }
            }
        }
    }
}
