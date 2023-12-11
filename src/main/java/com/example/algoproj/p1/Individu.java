package com.example.algoproj.p1;

import java.util.Random;

public class Individu {
    private int[] monkey = null;
    private Data data = null;
    private double totalDist = -1;
    private double totalFitness = 0;

    public Individu(Data data) {
        this.data = data;
    }

    public Individu clone() {
        Individu cloning = new Individu(data);
        cloning.monkey = this.monkey.clone();
        this.calculateFitness();
        return cloning;
    }

    public Individu(Data data, int[] monkey) {
        this.data = data;
        this.monkey = monkey;
    }

    public Individu(int[] monkey) {
        this.monkey = monkey;
    }

    public int[] getMonkey() {
        return monkey;
    }

    public Data getData() {
        return data;
    }

    public double getTotalDist() {
        return totalDist;
    }

    public double getTotalFitness() {
        return totalFitness;
    }

    public static int randomBetween(int min, int max) {
        if (min >= max) {
            //tukar
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

            //random vertyex awal dan akhir
            int vertexAwal = randomBetween(min, max);
            int vertexAkhir = vertexAwal;

            //Monkey
            this.monkey = new int[n + 1];
            this.monkey[0] = vertexAwal;//vertex awal
            this.monkey[n] = vertexAkhir;//vertex akhir

            //random vertex antara
            for (int i = 1; i < n; i++) {
                boolean sama = true;
                while (sama) {
                    int r = randomBetween(min, max);
                    sama = false;
                    for (int j = 0; j < i; j++) {
                        if (r == this.monkey[j]) {
                            sama = true;
                            break;
                        }
                    }
                    if (!sama) {
                        this.monkey[i] = r;
                    }
                }
            }
        }
    }

    public void calculateTotalDist() {
        this.totalDist = -1;
        if (monkey != null) {
            double total = 0;
            for (int i = 1; i < monkey.length; i++) {
                int indexVertex1 = monkey[i - 1];
                int indexVertex2 = monkey[i];
                double dist = data.extractPenalty(indexVertex1, indexVertex2);
                total += dist;
            }
            this.totalDist = total/10;
        }
    }

    public void calculateFitness() {
        this.totalFitness = 0;
        this.calculateTotalDist();
        if (this.totalDist > 0) {
            this.totalFitness = 1.0 / this.totalDist;
        }
    }

    @Override
    public String toString() {
        String result = "NULL";
        if (monkey != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < monkey.length; i++) {
                if (i > 0) {
                    sb.append(" - ");
                }
                int indexVertex = monkey[i];
                String label = data.getArrayVertex()[indexVertex].label;
                sb.append(label);
            }
            sb.append(" Total Distance: ").append(this.totalDist);
            sb.append(" Fitness: ").append(this.totalFitness);
            result = sb.toString();
        }
        return result;
    }
}
