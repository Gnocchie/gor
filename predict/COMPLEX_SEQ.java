import java.util.ArrayList;
import java.util.HashMap;

public class COMPLEX_SEQ {
    String ID;
    String AS;
    String SS;
    String PS;
    HashMap<String, Double> stats = new HashMap<>();

    public COMPLEX_SEQ(){}

    public void calculateQs(){
        if (SS.length() < 17 || PS.length() < 17){
            stats.put("Q3", -1.0);
            stats.put("QH", -1.0);
            stats.put("QE", -1.0);
            stats.put("QC", -1.0);
        }
        HashMap<String, Integer> guessCount = new HashMap<>();
        HashMap<String, Integer> realCount = new HashMap<>();
        for (String P : new String[]{"H", "E", "C", "ALL"}) {
            guessCount.put(P, 0);
            realCount.put(P, 0);
        }
        for (int i = 8; i < AS.length() - 8; i++) {
            String realSS = String.valueOf(SS.charAt(i));
            String predSS = String.valueOf(PS.charAt(i));
            realCount.replace(realSS, realCount.get(realSS) + 1);
            realCount.replace("ALL", realCount.get("ALL") + 1);

            if (predSS.matches("-")) System.out.println("DASH ACCEPTED !!");  //TODO test

            if (realSS.matches(predSS)){
                guessCount.replace(predSS, guessCount.get(predSS) + 1);
                guessCount.replace("ALL", guessCount.get("ALL") + 1);
            }
        }
        stats.put("Q3", guessCount.get("ALL") / realCount.get("ALL").doubleValue());
        stats.put("QH", (realCount.get("H") != 0 ? guessCount.get("H") / realCount.get("H").doubleValue() : -1));
        stats.put("QE", (realCount.get("E") != 0 ? guessCount.get("E") / realCount.get("E").doubleValue() : -1));
        stats.put("QC", (realCount.get("C") != 0 ? guessCount.get("C") / realCount.get("C").doubleValue() : -1));
    }

    public void calculateSOVs(){
        ArrayList<Segment> observed = getSegment(SS);
        ArrayList<Segment> predicted = getSegment(PS);
        if (observed.isEmpty() || predicted.isEmpty()){
            stats.put("SOVH", -1.0);
            stats.put("SOVE", -1.0);
            stats.put("SOVC", -1.0);
            stats.put("SOV", 0.0);
            return;
        }
        double sumH = Sov_score('H', observed, predicted);
        double sumE = Sov_score('E', observed, predicted);
        double sumC = Sov_score('C', observed, predicted);
        double sumAll = sumH + sumC + sumE;

        int totalResH = totalResidue('H', observed, predicted);
        int totalResE = totalResidue('E', observed, predicted);
        int totalResC = totalResidue('C', observed, predicted);

        /*System.out.println("total res c: " + totalResC);
        System.out.println("total res h: " + totalResH);
        System.out.println("total res e: " + totalResE);*/

        int totalAll = totalResH  + totalResC + totalResE;
        if (totalResH == 0) stats.put("SOVH", -1.0);
        else stats.put("SOVH", sumH / totalResH);
        if (totalResE == 0) stats.put("SOVE", -1.0);
        else stats.put("SOVE", sumE / totalResE);
        if (totalResC == 0) stats.put("SOVC", -1.0);
        else stats.put("SOVC", sumC / totalResC);
        if (totalAll == 0) stats.put("SOV", 0.0);
        stats.put("SOV", sumAll / totalAll);
    }

    private int totalResidue(char ss, ArrayList<Segment> observed, ArrayList<Segment> predicted) {
        // System.out.println("CALCULATING FOR " + ss);
        int totalresidues = 0;
        for (Segment obs : observed){
            if (obs.ss == ss){
                int overlapCount = -1;
                for (Segment pred : predicted){
                    if (pred.ss == ss) {
                        if (obs.overlap(pred) > 0) {
                            overlapCount++;
                        }
                    }
                }
                totalresidues += obs.length();
                // System.out.println("segment with : " + obs.length());
                if (overlapCount > 0) {
                    // System.out.println("EXTRA" + obs.length() * overlapCount);
                    totalresidues += obs.length() * overlapCount;
                }
            }
        }
        return totalresidues;
    }

    private ArrayList<Segment> getSegment(String sequence){
        ArrayList<Segment> segments = new ArrayList<>();
        if (sequence.length() < 17) return segments;
        char currentSS = sequence.charAt(8);
        int start = 8;

        for (int i = 8; i < sequence.length() - 8; i++) {
            if (sequence.charAt(i) != currentSS){
                segments.add(new Segment(start, i - 1, currentSS));
                start = i;
                currentSS = sequence.charAt(i);
            }
        }
        segments.add(new Segment(start, sequence.length() - 9, currentSS));
        return segments;
    }

    public double Sov_score(char ss, ArrayList<Segment> observed, ArrayList<Segment> predicted){
        // System.out.println("Starting with: " + ss);
        double overlapSum = 0;

        for (Segment obs : observed){
            if (obs.ss != ss) continue;
            double maxOverlap = 0;
            for (Segment pred : predicted){
                if (pred.ss != ss) continue;
                int overlap = obs.overlap(pred);
                if (overlap > 0){
                    int minov = obs.overlap(pred);
                    int maxov = obs.length() + pred.length() - obs.overlap(pred);
                    int delta = obs.delta(pred);


/*                  System.out.println("minov: " + minov);
                    System.out.println("maxov: " + maxov);
                    System.out.println("delta: " + delta);
                    System.out.println("Obs length: " + obs.length());
*/

                    maxOverlap += (((minov + delta) / (double) maxov) * obs.length());
                }
            }
            overlapSum += maxOverlap;
        }
        return overlapSum;
    }

    static class Segment{
        int start;
        int end;
        char ss;

        Segment(int start, int end, char ss){
            this.start = start;
            this.end = end;
            this.ss = ss;
        }

        int length(){
            return end - start + 1;
        }

        int maxov(Segment seg){
            return length() + seg.length() - overlap(seg);
        }

        int minov(Segment seg){return overlap(seg);}

        int overlap(Segment segment){
            int startOverlap = Math.max(this.start, segment.start);
            int endOverlap = Math.min(this.end, segment.end);
            if (startOverlap <= endOverlap) {
                return endOverlap - startOverlap + 1;
            }
            return 0;
        }

        int delta(Segment seg){
            if (minov(seg) == 0) return 0;
            return Math.min(
                    Math.min(maxov(seg) - minov(seg), minov(seg)),
                    Math.min(this.length() / 2, seg.length() / 2)
            );
        }
    }

    public void calculateStats(){
        this.calculateQs();
        this.calculateSOVs();
    }
}
