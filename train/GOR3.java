

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GOR3 extends GOR {
    String[] predictions = {
            "C", "E", "H"
    };
    GOR_parser gp = new GOR_parser();

    public GOR3() {
        this.matrices = new HashMap<>();
        this.T.gor = "gor3";
        this.P.gor = "gor3";
        for (String p : predictions) {
            matrices.put(p, new SS_Matrix(p, "gor3"));
        }
    }
    public void reset(){
        for (String p : predictions) {
            matrices.replace(p, new SS_Matrix(p, "gor3"));
        }
    }
    public void setMatrix(String modelPath) {
        HashMap<String, String[]> MXs = gp.model_parser(modelPath);
        for (Map.Entry<String, String[]> entry : MXs.entrySet()) {
            String key = entry.getKey();
            String[] headers = key.split(",");
            String currentAA = headers[0];
            String currentP = headers[1];

            SS_Matrix currentSS = matrices.get(currentP);
            AA_Matrix aaMatrix = currentSS.getAA(currentAA);
            aaMatrix.setMatrix(entry.getValue());
        }
    }


    public void writeM(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("// Matrix4D\n\n");
            writer.flush();
            writer.close();

            for (String aa: SS_Matrix.aminoAcids) {
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
