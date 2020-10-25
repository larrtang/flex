package io.flex.commons;

public class BlackScholes {

    // Black-Scholes formula
    public static double callPrice(double s, double x, double r, double sigma, double t) {
        double d1 = (Math.log(s/x) + (r + sigma * sigma/2) * t) / (sigma * Math.sqrt(t));
        double d2 = d1 - sigma * Math.sqrt(t);
        return s * Gaussian.cdf(d1) - x * Math.exp(-r*t) * Gaussian.cdf(d2);
    }

    // estimate by Monte Carlo simulation
    public static double call(double s, double x, double r, double sigma, double t) {
        int n = 10000;
        double sum = 0.0 ;
        for (int i = 0; i < n; i++) {
            double eps = StdRandom.gaussian();
            double price = s * Math.exp(r*t - 0.5*sigma*sigma*t + sigma*eps*Math.sqrt(t));
            double value = Math.max(price - x, 0);
            sum += value;
        }
        double mean = sum / n;

        return Math.exp(-r*t) * mean;
    }

    // estimate by Monte Carlo simulation
    public static double call2(double s, double x, double r, double sigma, double t) {
        int n = 10000;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double price = s;
            double dt = t/10000.0;
            for (double time = 0; time <= t; time += dt) {
                price += r*price*dt +sigma*price*Math.sqrt(dt)*StdRandom.gaussian();
            }
            double value = Math.max(price - x, 0);
            sum += value;
        }
        double mean = sum / n;

        return Math.exp(-r*t) * mean;
    }



    public static void main(String[] args) {
        double s     = 23.75;
        double x     = 15.00;
        double r     = 0.01;
        double sigma = 0.35;
        double t     = 0.5;
        System.out.println(callPrice(s, x, r, sigma, t));
        System.out.println(call(s, x, r, sigma, t));
        System.out.println(call2(s, x, r, sigma, t));
    }
}