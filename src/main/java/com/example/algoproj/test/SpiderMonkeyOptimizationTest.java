package com.example.algoproj.test;

import com.example.algoproj.p1.Data;
import com.example.algoproj.p1.DataReader;
import com.example.algoproj.p1.Vertex;
import com.example.algoproj.smo.SpiderMonkeyOptimization;

import java.io.File;

public class SpiderMonkeyOptimizationTest {
    public static void main(String[] args) {
        //Dataset
        File file = new File("src/main/java/com/example/algoproj/dataset/burma14.tsp");
        Vertex[] arrayVertex = DataReader.read(file);
        Data data = new Data(arrayVertex);

        int numTest = 10;

        double SumTourCostSMO = 0;

        double AverageTourCostSMO = -1;

        double MinTourCostSMO = Double.MAX_VALUE;

        for (int test = 1; test <= numTest; test++) {
            System.out.println("Test_"+test+"------------------------------------");

            //SMO-------------------------------------------------------------------
            SpiderMonkeyOptimization smo = getSpiderMonkeyOptimization(data);
            System.out.print("Iteration " + test+" : ");
            System.out.println(smo.bestIndividu.toString());
            double totalJarakSMO = smo.bestIndividu.getTotalDist();
            SumTourCostSMO = SumTourCostSMO + totalJarakSMO;
            if(totalJarakSMO<MinTourCostSMO){
                MinTourCostSMO = totalJarakSMO;
            }
            //END OF SMO------------------------------------------------------------
        }//end of loop for testing

        //Hitung rata-rata pencapaian total jarak
        AverageTourCostSMO = SumTourCostSMO/numTest;

        System.out.println("=========================================================");
        System.out.println("S P I D E R   M O N K E Y   O P T I M I Z A T I O N");
        System.out.println("=========================================================");
        System.out.println("Average Tour Cost  = "+AverageTourCostSMO);
        System.out.println("---------------------------------------------------------");
        System.out.println("Minimum Tour Cost  = "+MinTourCostSMO);
        System.out.println("---------------------------------------------------------");

    }

    private static SpiderMonkeyOptimization getSpiderMonkeyOptimization(Data data) {
        int MAX_ITERATION = 100;
        int allowedMaximumGroup = 4;
        double perturbationRate = 0.5;//pr
        int localLeaderLimit = 10;
        int globalLeaderLimit = 10;
        int totalNumberOfSpiderMonkey = 1000;
        return new SpiderMonkeyOptimization(data, MAX_ITERATION, allowedMaximumGroup, perturbationRate, localLeaderLimit, globalLeaderLimit, totalNumberOfSpiderMonkey);
    }
}
