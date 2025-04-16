import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Cross_Validator {
    GOR_parser gp = new GOR_parser();
    public Cross_Validator(){}
    public void cross_validate(GOR gor, String trainFile, int k, String sumPath, String detPath) throws IOException {
        cleanFile(sumPath);
        cleanFile(detPath);
        ArrayList<COMPLEX_SEQ> sequences = gp.db_parser(trainFile);
        if (k == 1){
            single_validate(gor, sequences, sumPath, detPath);
            return;
        }
        Collections.shuffle(sequences, new Random(420));
        int foldSize = sequences.size() / k;
        ArrayList<ArrayList<COMPLEX_SEQ>> folds = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            int start = i * foldSize;
            int end = (i + 1) * foldSize;
            folds.add(new ArrayList<>(sequences.subList(start, end)));
        }

        for (ArrayList<COMPLEX_SEQ> predSet : folds){
            ArrayList<COMPLEX_SEQ> trainSet = new ArrayList<>();

            for (ArrayList<COMPLEX_SEQ> trainFold : folds) {
                if (predSet != trainFold) {
                    trainSet.addAll(trainFold);
                }
            }       // TRAINING SET CREATED
            gor.reset();
            gor.runT_COMPLEX(trainSet);
            gor.runP_COMPLEX(predSet);

            Validator v = new Validator(predSet);
            v.validate(sumPath, detPath);
        }
    }
    public static void cleanFile(String file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("");
        bw.close();
    }
    public void single_validate(GOR gor, ArrayList<COMPLEX_SEQ> sequences, String sumPath, String detPath) throws IOException {
        gor.reset();
        gor.runT_COMPLEX(sequences);
        gor.runP_COMPLEX(sequences);

        Validator v = new Validator(sequences);
        v.validate(sumPath, detPath);
    }
}

