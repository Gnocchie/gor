import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Validator {
    HashMap<String, COMPLEX_SEQ> sequences = new HashMap<>();       // ID TO SEQUENCE INFO
    HashMap<String, ArrayList<Double>> countLists = new HashMap<>();    // STAT TO LIST OF VALUES FOR EACH ID
    HashMap<String, Double> statMeans = new HashMap<>();
    HashMap<String, Double> statSDs = new HashMap<>();

    public Validator(){
        for (String key : new String[]{"Q3", "QH", "QC", "QE", "SOV", "SOVH", "SOVC", "SOVE"}){
            countLists.put(key, new ArrayList<Double>());
        }
    }
    public Validator(ArrayList<COMPLEX_SEQ> sequences){
        for (String key : new String[]{"Q3", "QH", "QC", "QE", "SOV", "SOVH", "SOVC", "SOVE"}){
            countLists.put(key, new ArrayList<Double>());
        }
        for (COMPLEX_SEQ seq : sequences) {
            this.sequences.put(seq.ID, seq);
        }
    }

    public void validate(String seclib, String prediction, String sumPath, String detPath) throws IOException {
        read(seclib, prediction);
        System.out.println("Protein Count:\t" + sequences.size());
        calculateStats();
        System.out.println("Protein Stat Count\t" + countLists.size());
        writeS(sumPath);
        writeD(detPath);
    }
    public void validate(String sumPath, String detPath) throws IOException {
        System.out.println("Protein Count:\t" + sequences.size());
        calculateStats();
        System.out.println("Protein Stat Count\t" + countLists.size());
        writeS(sumPath);
        writeD(detPath);
    }
    public void validate_COMPLEX(ArrayList<COMPLEX_SEQ> sequences){
    }
    public void calculateStats(){
        for (COMPLEX_SEQ seq : sequences.values()) {
            seq.calculateStats();
            for (String key : new String[]{"Q3", "QH", "QC", "QE", "SOV", "SOVH", "SOVC", "SOVE"}){
                double value = seq.stats.get(key);
                if (value>=0) countLists.get(key).add(value);
            }
        }
        for (String key : new String[]{"Q3", "QH", "QC", "QE", "SOV", "SOVH", "SOVC", "SOVE"}){
            double[] currentList = countLists.get(key).stream().mapToDouble(Double::doubleValue).toArray();
            Double mean = StatUtils.mean(currentList);
            statMeans.put(key, mean);
            Double sd = StatUtils.variance(currentList);
            statSDs.put(key, sd);
        }
    }
    public void read(String seclib, String prediction) throws IOException {
        String line;
        String currentID = "";
        COMPLEX_SEQ currentSEQ = new COMPLEX_SEQ();
        BufferedReader br = new BufferedReader(new FileReader(prediction));
        while((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                currentID = (line.substring(1).strip());
                currentSEQ = new COMPLEX_SEQ();
                currentSEQ.ID = currentID;
            } else if (line.startsWith("PS")){
                currentSEQ.PS = (line.split(" ")[1].strip());
                sequences.put(currentID, currentSEQ);
            }
        }
        br.close();
        br = new BufferedReader(new FileReader(seclib));
        while((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                currentID = (line.substring(1).strip());
                currentSEQ = sequences.get(currentID);
            } else if (line.startsWith("AS")) {
                if (currentSEQ == null) continue;
                currentSEQ.AS = (line.split(" ")[1].strip());
            } else if (line.startsWith("SS")) {
                if (currentSEQ == null) continue;
                currentSEQ.SS = (line.split(" ")[1].strip());
                currentSEQ.calculateStats();
            }
        }
        br.close();
    }

    public void writeS(String sumPath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(sumPath, true));

        for (String key : new String[]{"Q3", "QH", "QC", "QE", "SOV", "SOVH", "SOVC", "SOVE"}){
            double[] values = countLists.get(key).stream().mapToDouble(Double::doubleValue).toArray();
            Percentile percentile = new Percentile();
            double q0 = Arrays.stream(values).min().orElse(Double.NaN);
            double q1 = percentile.evaluate(values, 25);  // First quartile (Q1)
            double q2 = percentile.evaluate(values, 50);  // Median (Q2)
            double q3 = percentile.evaluate(values, 75);  // Third quartile (Q3)
            double q4 = Arrays.stream(values).max().orElse(Double.NaN);

            bw.write(key + "\t"
                    + "Mean: " + String.format("%.1f", statMeans.get(key)*100) + "\t"
                    + "Deviation: " + String.format("%.1f", statSDs.get(key)*100) + "\t"
                    + "Min: " + String.format("%.1f", q0 * 100) + "\t"
                    + "Q1: " + String.format("%.1f", q1 * 100) + "\t"
                    + "Median: " + String.format("%.1f", q2 * 100) + "\t"
                    + "Q3: " + String.format("%.1f", q3 * 100) + "\t"
                    + "Max: " + String.format("%.1f", q4 * 100) + "\t");

            bw.newLine();
        }
        bw.newLine();
        bw.newLine();
        bw.close();
        System.out.println("sum written");
    }

    public void writeD(String detPath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(detPath, true));
        for (COMPLEX_SEQ seq: sequences.values()){
            HashMap<String, Double> tempStats = seq.stats;
            bw.write("> " + seq.ID + "  ");
            for (String key : new String[]{"Q3", "SOV", "QH", "QE", "QC", "SOVH", "SOVE", "SOVC"}) {
                double value = tempStats.get(key);
                if (value < 0) bw.write("   -  ");
                else bw.write(String.format("%6.1f", value * 100));
            }
            bw.newLine();

            bw.write("AS " + seq.AS);
            bw.newLine();

            bw.write("PS " + seq.PS);
            bw.newLine();

            bw.write("SS " + seq.SS);
            bw.newLine();
            bw.newLine();
            bw.newLine();

        }
        bw.close();
        System.out.println("detailed written");
    }
}
