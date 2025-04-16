import java.util.ArrayList;
import java.util.HashMap;

public class Predictor {
    String gor = "gor1";
    public Predictor(String gor){

    }
    public String predict(String seq, HashMap<String, SS_Matrix> matrices) {

        StringBuilder pred = new StringBuilder();
        String dashes = "--------";
        pred.append(dashes);
        int windowSize = 17;
        int halfwindow = 8;

        for (int i = 0; i < (seq.length() - windowSize + 1); i++) {
            String window = seq.substring(i, i + windowSize);
            if (gor.matches("gor1")) {
                pred.append(predictWindow1(window, matrices));
            }
            else if (gor.matches("gor3")) {
                pred.append(predictWindow3(window, matrices));
            }
            else if (gor.matches("gor4")) {
                pred.append(predictWindow4(window, matrices));
            }

        }
        pred.append(dashes);
        if (seq.length() < 17) return pred.substring(0, seq.length());
        return pred.toString();
    }

    private String predictWindow1(String window, HashMap<String, SS_Matrix> matrices) {

        String max_pred = "Nimbus";
        double max_prob = -99999999999.0;
        HashMap<String, Double> matrixIs = new HashMap<String, Double>();

        for (String p : new String[]{"C", "E", "H"}) {
            SS_Matrix currentMatrix = matrices.get(p);
            ArrayList<SS_Matrix> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    otherMatrices.add(matrices.get(q));
                }
            }
            SS_Matrix firstOther = otherMatrices.get(0);
            SS_Matrix secondOther = otherMatrices.get(1);
            double fSScurrent = currentMatrix.freq;
            double fSSothers = firstOther.freq + secondOther.freq;
            double matrixSUM = 0.0;

            for (int i = 0; i < window.length(); i++) {
                String AA = window.substring(i, i + 1);
                int[] currentrow = currentMatrix.getRow(AA);
                if (currentrow == null) {
                    continue;
                }
                double fcurrent = currentrow[i];
                double fothers = firstOther.getRow(AA)[i] + secondOther.getRow(AA)[i];

                if (fSScurrent == 0 || fcurrent <= 0 || fothers <= 0) {

                    continue;
                }
                matrixSUM += Math.log(fcurrent / fothers) + Math.log(fSSothers / fSScurrent);

            }
            matrixIs.put(p, matrixSUM);
        }

        for (String p : new String[]{"C", "E", "H"}) {
            ArrayList<String> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    otherMatrices.add(q);
                }
            }
            String firstOther = otherMatrices.get(0);
            String secondOther = otherMatrices.get(1);
            double prob = matrixIs.get(p) - matrixIs.get(firstOther) - matrixIs.get(secondOther);
            if (prob > max_prob){
                max_pred = p;
                max_prob = prob;
            }
        }
        return max_pred;
    }
    private String predictWindow3(String window, HashMap<String, SS_Matrix> matrices) {
        String max_pred = "Nimbus";
        double max_prob = -99999999999.0;
        String aa_center = window.substring(8, 9);
        HashMap<String, Double> matrixIs = new HashMap<>();

        for (String p : new String[]{"C", "E", "H"}) {

            SS_Matrix matrix = matrices.get(p);
            if (matrix == null) {
                continue;
            }
            AA_Matrix currentMatrix = matrix.getAA(aa_center);
            if (currentMatrix == null) {
                continue;
            }

            ArrayList<AA_Matrix> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    AA_Matrix other = matrices.get(q).getAA(aa_center);
                    if (other != null) {
                        otherMatrices.add(other);
                    }
                }
            }
            if (otherMatrices.size() < 2) continue;
            AA_Matrix firstOther = otherMatrices.get(0);
            AA_Matrix secondOther = otherMatrices.get(1);

            double fSScurrent = currentMatrix.freq;
            double fSSothers = firstOther.freq + secondOther.freq;
            double matrixSUM = 0.0;

            for (int i = 0; i < window.length(); i++) {
                String AA = window.substring(i, i + 1);
                int[] currentrow = currentMatrix.getRow(AA);
                if (currentrow == null) {
                    continue;
                }
                double fcurrent = currentrow[i];
                double fothers = firstOther.getRow(AA)[i] + secondOther.getRow(AA)[i];

                if (fSScurrent == 0 || fcurrent <= 0 || fothers <= 0) {

                    continue;
                }
                matrixSUM += Math.log(fcurrent / fothers) + Math.log(fSSothers / fSScurrent);

            }
            matrixIs.put(p, matrixSUM);
        }
        for (String p : new String[]{"C", "E", "H"}) {
            ArrayList<String> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    otherMatrices.add(q);
                }
            }
            String firstOther = otherMatrices.get(0);
            String secondOther = otherMatrices.get(1);
            Double scoreP = matrixIs.get(p);
            Double scoreOther1 = matrixIs.get(firstOther);
            Double scoreOther2 = matrixIs.get(secondOther);

            if (scoreP == null) {
                scoreP = 0.0;
            }
            if (scoreOther1 == null) {
                scoreOther1 = 0.0;
            }
            if (scoreOther2 == null) {
                scoreOther2 = 0.0;
            }

            double prob = scoreP - scoreOther1 - scoreOther2;
            if (prob > max_prob) {
                max_pred = p;
                max_prob = prob;
            }
        }
        return max_pred;
    }
    private String predictWindow4(String window, HashMap<String, SS_Matrix> matrices) {
        String max_pred = "Nimbus";
        double max_prob = -Double.MAX_VALUE;
        String aa_center = window.substring(8, 9);

        HashMap<String, Double> matrixIs = new HashMap<>();
        for (String p : new String[]{"C", "E", "H"}) {


            SS_Matrix currentMatrix = matrices.get(p);
            ArrayList<SS_Matrix> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    SS_Matrix other = matrices.get(q);
                    if (other != null) {
                        otherMatrices.add(other);
                    }
                }
            }
            SS_Matrix firstOther = otherMatrices.get(0);
            SS_Matrix secondOther = otherMatrices.get(1);

            double d6sum = 0;
            for (int i = 0; i < window.length(); i++) {
                for (int j = i; j < window.length(); j++) {
                    double pairCurrent = 1;
                    double pairOthers = 1;
                    try{
                        pairCurrent = currentMatrix.getAA(aa_center).getPairAA(String.valueOf(window.charAt(i))).getPairIndexAA(i - 8).getRow(String.valueOf(window.charAt(j)))[j];
                        pairOthers = firstOther.getAA(aa_center).getPairAA(String.valueOf(window.charAt(i))).getPairIndexAA(i - 8).getRow(String.valueOf(window.charAt(j)))[j]
                                + secondOther.getAA(aa_center).getPairAA(String.valueOf(window.charAt(i))).getPairIndexAA(i - 8).getRow(String.valueOf(window.charAt(j)))[j];
                    } catch (NullPointerException ignored) {}
                    d6sum += Math.log((pairCurrent + 0.0000001) / (pairOthers + 1));
                }
            }

            double d4sum = 0;
            for (int i = 0; i < window.length(); i++) {
                double pairCurrent=1;
                double pairOthers=1;
                try{
                    pairCurrent = currentMatrix.getAA(aa_center).getRow(String.valueOf(window.charAt(i)))[i];
                    pairOthers = firstOther.getAA(aa_center).getRow(String.valueOf(window.charAt(i)))[i]
                            + secondOther.getAA(aa_center).getRow(String.valueOf(window.charAt(i)))[i];
                } catch (NullPointerException ignored){}
                d4sum += Math.log((pairCurrent + 0.0000001) / (pairOthers + 1));
            }
            double currentI = 2f/17 * d6sum - 15f/17 * d4sum;

            matrixIs.put(p, currentI);
        }
        for (String p : new String[]{"C", "E", "H"}) {          // ITERATE THROUGH Is
            ArrayList<String> otherMatrices = new ArrayList<>();
            for (String q : new String[]{"C", "E", "H"}) {
                if (!q.equals(p)) {
                    otherMatrices.add(q);
                }
            }
            String firstOther = otherMatrices.get(0);

            String secondOther = otherMatrices.get(1);
            Double scoreP = matrixIs.get(p);
            Double scoreOther1 = matrixIs.get(firstOther);
            Double scoreOther2 = matrixIs.get(secondOther);

            if (scoreP == null) {
                scoreP = 0.0;
            }
            if (scoreOther1 == null) {
                scoreOther1 = 0.0;
            }
            if (scoreOther2 == null) {
                scoreOther2 = 0.0;
            }

            double prob = scoreP - scoreOther1 - scoreOther2;
            if (prob > max_prob) {
                max_pred = p;
                max_prob = prob;
            }
        }        return max_pred;
    }
}
