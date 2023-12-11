package com.example.algoproj;

import com.example.algoproj.p1.Data;
import com.example.algoproj.p1.DataReader;
import com.example.algoproj.p1.Vertex;
import com.example.algoproj.smo.SpiderMonkeyOptimization;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class SpiderMonkeyApplication extends Application {

    @Override
    public void start(Stage stage)  {
        // Dataset
        File file = new File("src/main/java/com/example/algoproj/dataset/burma14.tsp");
        Vertex[] arrayVertex = DataReader.read(file);
        Data data = new Data(arrayVertex);
        int numTest = 10;

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> fitnessDistanceChart = new BarChart<>(xAxis, yAxis);
        fitnessDistanceChart.setTitle("Fitness and Distance Values");
        fitnessDistanceChart.setCategoryGap(20);

        NumberAxis xAxisLine = new NumberAxis();
        NumberAxis yAxisLine = new NumberAxis();
        LineChart<Number, Number> fitnessDistanceLineChart = new LineChart<>(xAxisLine, yAxisLine);
        fitnessDistanceLineChart.setTitle("Fitness and Distance Curves");
        fitnessDistanceLineChart.setCreateSymbols(true);



        XYChart.Series<Number, Number> fitnessLineSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> distanceLineSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> perturbationLineSeries = new XYChart.Series<>();
        fitnessLineSeries.setName("Fitness");
        distanceLineSeries.setName("Distance");
        perturbationLineSeries.setName("Perturbation");

        XYChart.Series<String, Number> fitnessSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> distanceSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> perturbationSeries = new XYChart.Series<>();
        fitnessSeries.setName("Fitness");
        distanceSeries.setName("Distance");
        perturbationSeries.setName("Perturbation");



        double SumTourCostSMO = 0;
        double AverageTourCostSMO = -1;
        double MinTourCostSMO = Double.MAX_VALUE;
        double scaleFactor = 100;
        for (int test = 1; test <= numTest; test++) {
            SpiderMonkeyOptimization smo = getSpiderMonkeyOptimization(data);
            double fitness = smo.bestIndividu.getTotalFitness();
            double fitnessScaled = fitness * scaleFactor;
            double perturbation = smo.getPr();
            double distance = smo.bestIndividu.getTotalDist();
            SumTourCostSMO = SumTourCostSMO + distance;
            if(distance<MinTourCostSMO){
                MinTourCostSMO = distance;
            }
            fitnessSeries.getData().add(new XYChart.Data<>(String.valueOf(test), fitnessScaled));
            distanceSeries.getData().add(new XYChart.Data<>(String.valueOf(test), distance));
            perturbationSeries.getData().add(new XYChart.Data<>(String.valueOf(test), perturbation));
            fitnessLineSeries.getData().add(new XYChart.Data<>(test, fitness));
            distanceLineSeries.getData().add(new XYChart.Data<>(test, distance));
            perturbationLineSeries.getData().add(new XYChart.Data<>(test, perturbation));
            System.out.println("test "+test+"-> | fitness | : "+ fitness+"| perturbation| "+perturbation+"| distance |"+distance );
        }

        fitnessDistanceChart.getData().addAll(distanceSeries, fitnessSeries, perturbationSeries);
        fitnessDistanceLineChart.getData().addAll(fitnessLineSeries, distanceLineSeries);
        AverageTourCostSMO = SumTourCostSMO/numTest;
        VBox root = new VBox(fitnessDistanceChart, fitnessDistanceLineChart);
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Spider Monkey Optimization Visualization");
        stage.setScene(scene);
        stage.show();

        System.out.println("=======================================================================");
        System.out.println(" AVERAGE DISTANCE [ " + AverageTourCostSMO + " ]");
        System.out.println("=======================================================================");

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

    public static void main(String[] args) {
        launch();
    }

}