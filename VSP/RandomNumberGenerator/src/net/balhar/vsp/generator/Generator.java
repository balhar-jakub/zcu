package net.balhar.vsp.generator;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * This object takes care of generating Poisson numbers used in further computations.
 */
public class Generator {
    private ExponentialDistribution exponential;

    public Generator(double meanPoisson, int amountOfGeneratedNumbers){
        exponential = new ExponentialDistribution(1.0d);
        Histogram histogram = new Histogram();
        long nextPoisson;
        long sumOfResults = 0;
        for(int i =0; i < amountOfGeneratedNumbers; i++){
            nextPoisson = nextPoisson(meanPoisson);
            histogram.addNew(nextPoisson);
            sumOfResults += nextPoisson;
        }
        double EReal = sumOfResults / (double) amountOfGeneratedNumbers;
        System.out.println(String.format("E_teoreticke=%s",String.valueOf(meanPoisson)));
        System.out.println(String.format("E_realne=%s",String.valueOf(EReal)));
        System.out.println("");
        System.out.println(histogram.show());
    }

    /**
     * Rejection sampling. Therefore using accept-reject algorithm.
     *
     * @param meanPoisson Mean number of Poisson distribution
     * @return Next number of poisson dsitribution.
     */
    private long nextPoisson(double meanPoisson){
        double u;
        double poissonBaseNumber;
        double samplePoint;
        for(;;) {
            samplePoint = exponential.sample();
            // This is number on rectangle from 0 to 1
            u = Math.random();
            // This is always less than one. Therefore it is good enough to assume this as
            // a candidate number
            poissonBaseNumber =
                    (FastMath.exp(-meanPoisson) * FastMath.pow(meanPoisson, samplePoint)) /
                            ArithmeticUtils.factorial((int) FastMath.floor(meanPoisson));
            // I need to find max of poisson.
            if(u < poissonBaseNumber) {
                return (long) Math.floor(samplePoint);
            }
        }
    }

    public static void main(String[] args){
        if(args.length == 0) {
            double meanPoisson = 10d;
            int amountOfGenerated = 100;
            new Generator(meanPoisson,amountOfGenerated);

            meanPoisson = 3d;
            amountOfGenerated = 1000;
            new Generator(meanPoisson,amountOfGenerated);

            return;
        }
        if(args.length != 2){
            throw new RuntimeException("Wrong usage of program. It is expected to be run with two parameters.\n" +
                    "amountOfGeneratedNumbers and meanNumber");
        }
        double meanPoisson = Double.parseDouble(args[1]);
        int amountOfGenerated = Integer.parseInt(args[0]);
        new Generator(meanPoisson,amountOfGenerated);
    }
}
