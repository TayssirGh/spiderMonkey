package com.example.algoproj.smo;
import com.example.algoproj.p1.Data;
import com.example.algoproj.p1.Monkey;
import com.example.algoproj.p1.Swap;
import com.example.algoproj.p1.DataValues;
import java.util.Random;

public class SpiderMonkeyOptimization {
    //INPUT-----------------------
    //Dataset
    private Data data = null;

    //Parameter SMO
    private final int seqMonkey;
    private final int numberOfIterations;
    private int maxGroup;
    private final double perturbationRate;
    private final int localLeaderLimit;
    private final int globalLeaderLimit;
    private final int nSpiderMonkey;

    public double getPr() {
        return perturbationRate;
    }

    public Monkey bestMonkey = null;

    private final Random random = new Random();

    public SpiderMonkeyOptimization(
            Data data, int MAX_ITERATION, int allowedMaximumGroup, double perturbationRate,
            int localLeaderLimit, int globalLeaderLimit, int totalNumberOfSpiderMonkey) {

        // dataset
        this.data = data;
        DataValues[] arrayDataValues = data.getArrayVertex();
        int nVertex = arrayDataValues.length;
        this.seqMonkey = nVertex + 1;

        this.numberOfIterations = MAX_ITERATION;
        this.maxGroup = allowedMaximumGroup;
        this.perturbationRate = perturbationRate;
        this.localLeaderLimit = localLeaderLimit;
        this.globalLeaderLimit = globalLeaderLimit;
        this.nSpiderMonkey = totalNumberOfSpiderMonkey;

        this.run();
    }

    public boolean validateParameter() {
        boolean valid = true;
        if (this.maxGroup >= this.nSpiderMonkey) {
            this.maxGroup = this.nSpiderMonkey / 2;
        }
        return valid;
    }

    public Monkey[] generatPopulation() {
        Monkey[] population = null;
        if (this.data != null && this.nSpiderMonkey > 0) {
            population = new Monkey[this.nSpiderMonkey];
            for (int p = 0; p < population.length; p++) {
                population[p] = new Monkey(data);
                population[p].generateRandomMonkey();
                population[p].calculateFitness();
            }
        }
        return population;
    }

    private int randomBetween(int min, int max) {
        if (min >= max) {
            //tukar
            int temp = min;
            min = max;
            max = temp;
        }
        return random.nextInt((max - min) + 1) + min;
    }


    public Monkey[] selectLocalLeader(Monkey[] spiderMonkey, int g) {
        Monkey[] LL = null;
        if (spiderMonkey != null && g > 0) {
            int groupSize = (int) Math.floor((double) nSpiderMonkey / (double) g);
            LL = new Monkey[g];
            for (int k = 0; k < g; k++) {
                LL[k] = null;
                int indexLL = -1;
                double bFitness = 0;
                indexLL = getIndexLL(spiderMonkey, g, groupSize, k, indexLL, bFitness);
                //set Local leader
                LL[k] = spiderMonkey[indexLL].clone();
                LL[k].calculateFitness();
            }
        }
        return LL;
    }

    private int getIndexLL(Monkey[] spiderMonkey, int g, int groupSize, int k, int indexLL, double bFitness) {
        boolean inGroup = true;
        int index = 0;
        while (true) {
            int i = groupSize * k + index;
            if (i >= spiderMonkey.length) {
                inGroup = false;
                break;
            }
            //System.out.println("TRACE " + groupSize + "*" + k + "+" + index + "=" + i);
            Monkey SMi = spiderMonkey[i];
            if (SMi.getTotalFitness() > bFitness) {
                bFitness = SMi.getTotalFitness();
                indexLL = i;
            }
            index++;
            if (index != 0 && index % groupSize == 0) {
                inGroup = false;
                if (k >= g - 1) {
                    inGroup = true;
                } else {
                    break;
                }
            }
        }
        return indexLL;
    }

    public Monkey selectGlobalLeader(Monkey[] LL) {
        Monkey GL = null;
        if (LL != null) {
            int indexGL = -1;
            double bestFitness = 0;
            int g = LL.length;
            for (int k = 0; k < g; k++) {
                if (LL[k].getTotalFitness() > bestFitness) {
                    bestFitness = LL[k].getTotalFitness();
                    indexGL = k;
                }
            }
            GL = LL[indexGL].clone();
            GL.calculateFitness();
        }
        return GL;
    }

    public Monkey[] updateSpiderMonkeyBaseOnLocalLeader(Monkey[] spiderMonkey, Monkey[] LL, Monkey GL) {
        Monkey[] SM = null;
        if (spiderMonkey != null && LL != null && GL != null) {
            double min_cost = Double.MAX_VALUE;
            SM = new Monkey[spiderMonkey.length];
            int g = LL.length;
            if (g > 0) {
                int groupSize = (int) Math.floor((double) nSpiderMonkey / (double) g);
                //Update Spider Monkey base on Local Leader---------------------
                for (int k = 0; k < g; k++) {
                    boolean inGroup = true;
                    int index = 0;
                    while (true) {
                        int i = groupSize * k + index;
                        if (i >= spiderMonkey.length) {
                            inGroup = false;
                            break;
                        }
                        double U = random.nextDouble();
                        if (U >= perturbationRate) {
                            int MIN = k * groupSize;
                            int MAX = (k + 1) * groupSize - 1;
                            if (spiderMonkey.length - 1 - MAX < groupSize) {
                                MAX = spiderMonkey.length - 1;
                            }
                            int indexRandom = randomBetween(MIN, MAX);
                            while (indexRandom == i) {
                                indexRandom = randomBetween(MIN, MAX);
                            }
                            Monkey LLk = LL[k];//LocalLeader ke k
                            SM[i] = spiderMonkey[i];
                            Monkey RSM = spiderMonkey[indexRandom];

                            int[][] LLk_SMi = Swap.subtract(LLk, SM[i]);//LLk - SMi
                            int[][] RSM_SMi = Swap.subtract(RSM, SM[i]);//RSM - SMi

                            int[][] SSi = Swap.mergeSwapSequence(LLk_SMi, RSM_SMi);
                            int[][] BSSi = Swap.callBasicSwapSequence(SSi, seqMonkey);
                            Monkey SMnewi = Swap.add(SM[i], BSSi);
                            if (SMnewi.getTotalFitness() > SM[i].getTotalFitness()) {
                                SM[i] = SMnewi;
                            }
                        } else {
                            SM[i] = spiderMonkey[i];
                        }
                        if (SM[i].getTotalDist() >= 0 && spiderMonkey[index].getTotalDist() < min_cost) {
                            min_cost = SM[i].getTotalDist();
                        }
                        //increment index
                        index++;
                        if (index != 0 && index % groupSize == 0) {
                            inGroup = false;
                            if (k >= g - 1) {
                                inGroup = true;
                            } else {
                                break;
                            }
                        }
                    }
                }
                //Update Spider Monkey base on Global Leader---------------------
                for (int k = 0; k < g; k++) {
                    boolean inGroup = true;
                    int index = 0;
                    while (true) {
                        int i = groupSize * k + index;
                        if (i >= SM.length) {
                            inGroup = false;
                            break;
                        }
                        double U = random.nextDouble();
                        double cost_i = SM[i].getTotalDist();
                        double prob_i = 0.9 * (min_cost / cost_i) + 0.1;
                        if (U <= prob_i) {
                            int MIN = 0;
                            int MAX = SM.length - 1;
                            int indexRandom = randomBetween(MIN, MAX);
                            while (indexRandom == i) {
                                indexRandom = randomBetween(MIN, MAX);
                            }
                            Monkey RSM = SM[indexRandom];

                            int[][] GL_SMi = Swap.subtract(GL, SM[i]);//LLk - SMi
                            int[][] RSM_SMi = Swap.subtract(RSM, SM[i]);//RSM - SMi

                            int[][] SSi = Swap.mergeSwapSequence(GL_SMi, RSM_SMi);
                            int[][] BSSi = Swap.callBasicSwapSequence(SSi, seqMonkey);
                            Monkey SMnewi = Swap.add(SM[i], BSSi);
                            SMnewi.calculateFitness();
                            if (SMnewi.getTotalFitness() > SM[i].getTotalFitness()) {
                                SM[i] = SMnewi;
                            }
                        }

                        //increment index
                        index++;
                        if (index != 0 && index % groupSize == 0) {
                            inGroup = false;
                            if (k >= g - 1) {
                                inGroup = true;
                            } else {
                                break;
                            }
                        }
                    }
                }//end of for (int k = 0; k < g; k++)//end of Update Spider Monkey base on Global Leader
            }//end of if(g>0)
        }//end of if
        return SM;
    }

    public void run() {
        //Variables
        if (validateParameter()) {
            Monkey[] spiderMonkey = generatPopulation();
            int g = 1;
            Monkey[] localLeader = selectLocalLeader(spiderMonkey, g);
            Monkey globalLeader = selectGlobalLeader(localLeader);
            int[] LLLc = new int[localLeader.length];
            int GLLc = 0;

            
            //update phase 
            for (int t = 1; t <= numberOfIterations; t++) {
                spiderMonkey = updateSpiderMonkeyBaseOnLocalLeader(spiderMonkey, localLeader, globalLeader);

                //Update phase local leader------------------------------------
                if (spiderMonkey != null && g > 0) {
                    int groupSize = (int) Math.floor((double) nSpiderMonkey / (double) g);
                    for (int k = 0; k < g; k++) {
                        int indexLL = -1;
                        double bFitness = localLeader[k].getTotalFitness();
                        indexLL = getIndexLL(spiderMonkey, g, groupSize, k, indexLL, bFitness);
                        //set Local leader
                        if (indexLL >= 0) {
                            localLeader[k] = spiderMonkey[indexLL].clone();
                            localLeader[k].calculateFitness();
                            LLLc[k] = 0;
                        } else {
                            LLLc[k]++;
                        }
                    }
                }

                //Update phase global leader------------------------------------
                if (localLeader != null) {
                    int indexGL = -1;
                    double bestFitness = globalLeader.getTotalFitness();
                    for (int k = 0; k < g; k++) {
                        if (localLeader[k].getTotalFitness() > bestFitness) {
                            bestFitness = localLeader[k].getTotalFitness();
                            indexGL = k;
                        }
                    }
                    if (indexGL >= 0) {
                        globalLeader = localLeader[indexGL].clone();
                        globalLeader.calculateFitness();
                        GLLc = 0;
                    } else {
                        GLLc++;
                    }
                }

                //Decision Phase Local Leader-----------------------------------
                if (spiderMonkey != null && g > 0) {
                    int groupSize = (int) Math.floor((double) nSpiderMonkey / (double) g);
                    for (int k = 0; k < g; k++) {
                        if (LLLc[k] > localLeaderLimit) {
                            LLLc[k] = 0;
                        }
                        boolean inGroup = true;
                        int index = 0;
                        while (true) {
                            int i = groupSize * k + index;
                            if (i >= spiderMonkey.length) {
                                inGroup = false;
                                break;
                            }
                            double U = random.nextDouble();
                            if (U >= perturbationRate) {
                                spiderMonkey[i] = new Monkey(data);
                                spiderMonkey[i].generateRandomMonkey();
                                spiderMonkey[i].calculateFitness();
                            } else {
                                //initialize SMi using EQ 13
                                Monkey SMi = spiderMonkey[i].clone();
                                U = random.nextDouble();
                                Monkey SMi_A = null;
                                Monkey SMi_B = null;
                                if (U >= perturbationRate) {
                                    int[][] ss = Swap.subtract(globalLeader, SMi);
                                    SMi_A = Swap.add(SMi, ss);
                                }
                                if (U >= perturbationRate) {
                                    int[][] ss = Swap.subtract(SMi, localLeader[k]);
                                    SMi_B = Swap.add(SMi_A, ss);
                                }
                                Monkey SMi_new = null;
                                if (SMi_B != null) {
                                    SMi_new = SMi_B;
                                } else if (SMi_A != null) {
                                    SMi_new = SMi_A;
                                } else {
                                    SMi_new = SMi;
                                }
                                if (SMi_new != null) {
                                    SMi_new.calculateFitness();
                                    spiderMonkey[i] = SMi_new;
                                }
                            }
                            index++;
                            if (index != 0 && index % groupSize == 0) {
                                inGroup = false;
                                if (k >= g - 1) {
                                    inGroup = true;
                                } else {
                                    break;
                                }
                            }
                        }//end of while
                    }
                }
                //End of Decision Phase Local Leader----------------------------

                //Decision Phase Global Leader----------------------------------
                if (GLLc > globalLeaderLimit) {
                    //System.out.println("DECISION PHASE");
                    if (g < maxGroup) {
                        g++;//
                    } else {
                        g = 1;
                    }
                    //reset group
                    //Limit
                    LLLc = new int[g];//Local Leader Limit
                    GLLc = 0;//Global Leader Limit

                    //Select local leader and global leader
                    localLeader = selectLocalLeader(spiderMonkey, g);
                    globalLeader = selectGlobalLeader(localLeader);//Global Leader

                }//End of Decision Phase Global Leader---------------------------

            }

            //System.out.println("Global Leader: " + GL.toString());
            bestMonkey = globalLeader;

        }
    }//end of run()


}
