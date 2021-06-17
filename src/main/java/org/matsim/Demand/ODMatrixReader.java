package org.matsim.Demand;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ODMatrixReader {

    private List<ODMatrix> odMatrixList = new ArrayList<>();

    public List<ODMatrix> getOdMatrixList() {
        return odMatrixList;
    }

    public void read() {
        //Relative path

        String localDir = "src/main/java/org/matsim/Demand/";
        File file = new File(localDir + "ODMatrix.csv");
        System.out.println("Reading input data.");

        try {
            readFile(file);
        } catch (IOException e) {
            System.err.println("Provided file (via argument): " + file + " not found.");
            System.exit(-1);
        }
    }

    public void readFile(File fileName) throws IOException {
        //Need InputStreamReader to show Chinese characters
        InputStreamReader read = new InputStreamReader(new FileInputStream(fileName), "Big5");
        BufferedReader br = new BufferedReader(read);

        String line;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                continue;
            }
            String[] lineElements = line.split("[;,]+");
            odMatrixList.add(new ODMatrix(Integer.parseInt(lineElements[0]),
                    lineElements[1], lineElements[2], lineElements[3], Double.parseDouble(lineElements[4]),
                    Double.parseDouble(lineElements[5]), Double.parseDouble(lineElements[6]), Double.parseDouble(lineElements[7]),
                    Double.parseDouble(lineElements[8]), Double.parseDouble(lineElements[9]), Double.parseDouble(lineElements[10]), Double.parseDouble(lineElements[11])));
        }
        br.close();
    }
}
