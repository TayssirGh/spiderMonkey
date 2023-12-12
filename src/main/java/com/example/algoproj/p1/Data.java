package com.example.algoproj.p1;

public class Data {
    private DataValues[] arrayDataValues = null;

    public Data(DataValues[] arrayDataValues) {
        this.arrayDataValues = arrayDataValues;
    }

    public DataValues[] getArrayVertex() {
        return arrayDataValues;
    }


    public double extractDistance(int indexVertex1, int indexVertex2) {
        double distance = -1;
        if (arrayDataValues != null
                && indexVertex1 >= 0
                && indexVertex1 < arrayDataValues.length
                && indexVertex2 >= 0
                && indexVertex2 < arrayDataValues.length
        ) {
            DataValues v1 = arrayDataValues[indexVertex1];
            DataValues v2 = arrayDataValues[indexVertex2];




            distance = Math.sqrt(Math.pow(v1.x-v2.x, 2) + Math.pow(v1.y-v2.y, 2));

        }
        return distance;
    }
}
