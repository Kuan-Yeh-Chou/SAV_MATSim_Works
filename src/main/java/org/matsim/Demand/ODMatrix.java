package org.matsim.Demand;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.util.Comparator;

public class ODMatrix implements Comparable<ODMatrix> {

    private int population;
    private double shareOfCar;
    private double shareOfScooter;
    private double shareOfTrain;
    private double shareOfTaxi;
    private double shareOfMRT;
    private double shareOfBus;
    private double shareOfBike;
    private double shareOfWalk;
    private String origin;
    private String destination;
    private String toFromPrefix;

    public ODMatrix(int population, String origin, String destination, String toFromPrefix, double shareOfCar, double shareOfScooter, double shareOfTrain, double shareOfTaxi, double shareOfMRT,
                    double shareOfBus, double shareOfBike, double shareOfWalk) {
        this.population = population;
        this.shareOfCar = shareOfCar;
        this.shareOfScooter = shareOfScooter;
        this.shareOfTrain = shareOfTrain;
        this.shareOfTaxi = shareOfTaxi;
        this.shareOfMRT = shareOfMRT;
        this.shareOfBus = shareOfBus;
        this.shareOfBike = shareOfBike;
        this.shareOfWalk = shareOfWalk;
        this.origin = origin;
        this.destination = destination;
        this.toFromPrefix = toFromPrefix;
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(population), origin, destination, toFromPrefix,
                String.valueOf(shareOfCar), String.valueOf(shareOfScooter), String.valueOf(shareOfTrain),
                String.valueOf(shareOfTaxi), String.valueOf(shareOfMRT), String.valueOf(shareOfBus),
                String.valueOf(shareOfBike), String.valueOf(shareOfWalk));
    }

    public int getPopulation() {
        return this.population;
    }

    public double getShareOfCar() {
        return this.shareOfCar;
    }

    public double getShareOfScooter() {
        return this.shareOfScooter;
    }

    public double getShareOfTrain() {
        return this.shareOfTrain;
    }

    public double getShareOfTaxi() {
        return this.shareOfTaxi;
    }

    public double getShareOfMRT() {
        return this.shareOfMRT;
    }

    public double getShareOfBus() {
        return this.shareOfBus;
    }

    public double getShareOfBike() {
        return this.shareOfBike;
    }

    public double getShareOfWalk() {
        return this.shareOfWalk;
    }

    public String getOrigin() {
        return this.origin;
    }

    ;

    public String getDestination() {
        return this.destination;
    }

    ;

    public String getToFromPrefix() {
        return toFromPrefix;
    }

    @Override
    public int compareTo(ODMatrix odMatrix) {
        int compareOrder = (odMatrix).getPopulation();
        //For Ascending order
        return Integer.compare(getPopulation(), compareOrder);
    }
}
