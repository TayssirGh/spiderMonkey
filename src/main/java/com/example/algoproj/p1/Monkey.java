package com.example.algoproj.p1;

import java.util.Random;

public class Monkey {
    private int[] monkey = null;
    private Data data = null;
    private double totalDist = -1;
    private double totalFitness = 0;

    public Monkey(Data data) {
        this.data = data;
    }

    public Monkey clone() {
        Monkey cloning = new Monkey(data);
        cloning.monkey = this.monkey.clone();
        this.calculateFitness();
        return cloning;
    }

    public Monkey(int[] monkey) {
        this.monkey = monkey;
    }

    public int[] getMonkey() {
        return monkey;
    }

    public double getTotalDist() {
        return totalDist;
    }

    public double getTotalFitness() {
        return totalFitness;
    }

    public static int randomGeneration(int min, int max) {
        if (min >= max) {
            int temp = min;
            min = max;
            max = temp;
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void generateRandomMonkey() {
        if (data != null && data.getArrayVertex().length > 0) {
            int n = data.getArrayVertex().length;
            int min = 0;
            int max = n - 1;

            int firstValue = randomGeneration(min, max);

            this.monkey = new int[n + 1];
            this.monkey[0] = firstValue;
            this.monkey[n] = firstValue;

            for (int i = 1; i < n; i++) {
                boolean test = true;
                while (test) {
                    int r = randomGeneration(min, max);
                    test = false;
                    for (int j = 0; j < i; j++) {
                        if (r == this.monkey[j]) {
                            test = true;
                            break;
                        }
                    }
                    if (!test) {
                        this.monkey[i] = r;
                    }
                }
            }
        }
    }


    public void calculateFitness() {
        this.totalFitness = 0;
        this.totalDist = -1;
        if (monkey != null) {
            double total = 0;
            for (int i = 1; i < monkey.length; i++) {
                int value1 = monkey[i - 1];
                int value2 = monkey[i];
                double dist = data.extractDistance(value1, value2);
                total += dist;
            }
            this.totalDist = total;
        }
        if (this.totalDist > 0) {
            this.totalFitness = 1.0 / this.totalDist;
        }
    }


}
