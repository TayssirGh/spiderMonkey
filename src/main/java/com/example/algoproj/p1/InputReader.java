package com.example.algoproj.p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class InputReader {
    public static DataValues[] read(File file) {
        DataValues[] arrayDataValues = null;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String baris;
            ArrayList<DataValues> listDataValues = new ArrayList<>();

            //read header data set
            boolean bacaVertex = false;
            while ((baris = br.readLine()) != null) {

                if (baris.startsWith("NODE_COORD_SECTION")) {
                    bacaVertex = true;
                } else if (baris.equals("EOF")) {
                    bacaVertex = false;
                    break;
                }
                if (bacaVertex) {
                    baris = baris.replaceAll("\\s+", " ");
                    if (baris.charAt(0) == ' ') {
                        baris = baris.substring(1);
                    }
                    String[] dBaris = baris.split("\\s");
                    if (dBaris.length == 3) {
                        String label = dBaris[0];
                        double x = Double.parseDouble(dBaris[1]);
                        double y = Double.parseDouble(dBaris[2]);
                        DataValues v = new DataValues(label, x, y);
                        listDataValues.add(v);
                    }
                }
            }//end of while

            if (!listDataValues.isEmpty()) {
                //convert to arrayVertex
                int n = listDataValues.size();
                arrayDataValues = new DataValues[n];
                for (int i = 0; i < arrayDataValues.length; i++) {
                    arrayDataValues[i] = listDataValues.get(i);
                }
            }
        } catch (IOException ignored) {
        }
        return arrayDataValues;
    }
}
