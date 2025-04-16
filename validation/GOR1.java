import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GOR1 extends GOR {
    String[] predictions = {
            "C", "E", "H"
    };
    GOR_parser gp = new GOR_parser();

    public GOR1() {
        this.matrices = new HashMap<>();
        this.P.gor = "gor1";
        for (String p : predictions) {
            matrices.put(p, new SS_Matrix(p, "gor1"));
        }
    }
    public void reset(){
        for (String p : predictions) {
            matrices.replace(p, new SS_Matrix(p, "gor1"));
        }
    }
    public void setMatrix(String modelPath) {
        HashMap<String, String[]> MXs = gp.model_parser(modelPath);
        for (String p : new String[]{"C", "E", "H"}) {
            matrices.get(p).setMatrix(MXs.get(p));
        }
    }

    public void writeM(String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("// Matrix3D\n\n");
            writer.flush();
            writer.close();
            for (String p : new String[]{"C", "E", "H"}) {
                SS_Matrix currentMatrix = matrices.get(p);
                currentMatrix.write(file);
            }
            System.out.println(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

