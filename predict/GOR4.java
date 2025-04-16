import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GOR4 extends GOR {
    String[] predictions = {
            "C", "E", "H"
    };

    public GOR4() {
        this.matrices = new HashMap<>();
        this.T.gor = "gor4";
        this.P.gor = "gor4";
        for (String p : predictions) {
            matrices.put(p, new SS_Matrix(p, "gor4"));
        }
    }

    public void reset() {
        for (String p : predictions) {
            matrices.replace(p, new SS_Matrix(p, "gor4"));
        }
    }
    @Override
    public void setMatrix(String modelPath) {
        HashMap<String, String[]> MXs = gp.model_parser(modelPath);
        for (Map.Entry<String, String[]> entry : MXs.entrySet()) {
            String key = entry.getKey();
            String[] headers = key.split(",");
            if (key.length() > 6) {
                String currentP = headers[0];
                String currentAA = headers[1];
                String currentPair = headers[2];
                int currentIndex = Integer.parseInt(headers[3]);

                SS_Matrix currentSS = matrices.get(currentP);
                AA_Matrix aaMatrix = currentSS.getAA(currentAA);
                AA_Matrix pairMatrix = aaMatrix.getPairAA(currentPair);
                AA_Matrix indexMatrix = pairMatrix.getPairIndexAA(currentIndex);
                indexMatrix.setMatrix(entry.getValue());
            } else {
                String currentAA = headers[0];
                String currentP = headers[1];

                SS_Matrix currentSS = matrices.get(currentP);
                AA_Matrix aaMatrix = currentSS.getAA(currentAA);
                aaMatrix.setMatrix(entry.getValue());
            }
        }
    }
    @Override
    public void writeM(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("// Matrix6D\n\n");
            writer.flush();
            writer.close();

            for (String p : new String[]{"C", "E", "H"}) {
                SS_Matrix currentSSMatrix = matrices.get(p);
                currentSSMatrix.writePair(file);
            }
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(file, true));
            writer2.write("// Matrix4D\n\n");
            writer2.flush();
            writer2.close();
            for (String aa : SS_Matrix.aminoAcids) {
                for (String p : new String[]{"C", "E", "H"}) {
                    SS_Matrix currentAAMatrix = matrices.get(p).getAA(aa);
                    currentAAMatrix.write(file);
                }
            }
            System.out.println(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
