package org.matsim.Demand;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

public class Demand {
    // Specify all input files
    private static String localDir = "src/main/java/org/matsim/Demand/";
    private static final String TOWNSFILE = "SHP/TPMetropolitanDistricts.shp"; // Polygon shapefile for demand generation
    private static final String TOWNID = "TOWNID";
    private static final String PLANSFILEOUTPUT = "Demand.xml"; // The output file of demand generation
    private static List<ODMatrix> odMatrixList = new ArrayList<>();


    // Due to the huge number of commuters, it's preferable to decrease the size of simulating agents (sampling).
    private static double SCALEFACTOR = 0.1;

    // Define the coordinate transformation function
    // If an error occurs here with geotools, you need to run this using Java 8
    private final CoordinateTransformation transformation = TransformationFactory
            .getCoordinateTransformation("EPSG:3826", "EPSG:3824");

    // Define objects and parameters
    private Scenario scenario;
    private Map<String, Geometry> shapeMap;

    // Entering point of the class "Generate Random Demand"
    public static void main(String[] args) {
        ODMatrixReader odMatrixReader = new ODMatrixReader();
        odMatrixReader.read();
        odMatrixList = odMatrixReader.getOdMatrixList();
        Demand demand = new Demand();
        demand.run();
    }

    // A constructor for this class, which is to set up the scenario container.
    Demand() {
        this.scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
    }

    // Generate randomly sampling demand
    private void run() {
        this.shapeMap = readShapeFile(localDir + TOWNSFILE, TOWNID);
        for (ODMatrix odMatrix : odMatrixList) {
            createOD(odMatrix);
        }

        // Write the population file to specified folder
        PopulationWriter pw = new PopulationWriter(scenario.getPopulation(), scenario.getNetwork());
        pw.write(localDir + PLANSFILEOUTPUT);
    }

    // Create od relations for each counties
    private void createOD(ODMatrix odMatrix) {
        // Specify the number of commuters and the modal split of this relation
        double commuters = odMatrix.getPopulation() * SCALEFACTOR;

        double carCommuters = odMatrix.getShareOfCar() * commuters;
        double scooterCommuter = odMatrix.getShareOfScooter() * commuters;
        double trainCommuter = odMatrix.getShareOfTrain() * commuters;
        double taxiCommuter = odMatrix.getShareOfTaxi() * commuters;
        double MRTCommuter = odMatrix.getShareOfMRT() * commuters;
        double busCommuter = odMatrix.getShareOfBus() * commuters;
        double bikeCommuter = odMatrix.getShareOfBike() * commuters;
        double walkCommuter = odMatrix.getShareOfWalk() * commuters;

        // Specify the ID of these two cities, which is the TOWN attribute.
        Geometry home = this.shapeMap.get(odMatrix.getOrigin());
        Geometry work = this.shapeMap.get(odMatrix.getDestination());

        // Randomly creating the home and work location of each commuters, be aware that
        for (int i = 1; i <= commuters; i++) {
            // Specify the transportation mode
            String mode = "car";
            if (i > carCommuters & i < carCommuters + scooterCommuter) mode = "scooter";
            if (i > carCommuters + scooterCommuter & i < carCommuters + scooterCommuter + trainCommuter) mode = "train";
            if (i > carCommuters + scooterCommuter + trainCommuter & i < carCommuters + scooterCommuter + trainCommuter + taxiCommuter)
                mode = "taxi";
            if (i > carCommuters + scooterCommuter + trainCommuter + taxiCommuter & i < carCommuters + scooterCommuter + trainCommuter + taxiCommuter + MRTCommuter)
                mode = "MRT";
            if (i > commuters - busCommuter - bikeCommuter - walkCommuter & i < commuters - bikeCommuter - walkCommuter)
                mode = "bus";
            if (i > commuters - bikeCommuter - walkCommuter & i < commuters - walkCommuter) mode = "bike";
            if (i > commuters - walkCommuter & i < commuters) mode = "walk";
            // Specify the home location randomly and transform the coordinate
            Coord homec = drawRandomPointFromGeometry(home);
            homec = transformation.transform(homec);

            // Specify the working location randomly and transform the coordinate
            Coord workc = drawRandomPointFromGeometry(work);
            workc = transformation.transform(workc);

            // Create plan for each commuter
            createOnePerson(i, homec, workc, mode, odMatrix.getToFromPrefix());
        }
    }

    // Create plan for each commuters
    private void createOnePerson(int i, Coord coord, Coord coordWork, String mode, String toFromPrefix) {
        // Time always in seconds
        double variance = Math.random() * 60 * 60;

        Id<Person> personId = Id.createPersonId(toFromPrefix + i);
        Person person = scenario.getPopulation().getFactory().createPerson(personId);

        Plan plan = scenario.getPopulation().getFactory().createPlan();

        Activity home = scenario.getPopulation().getFactory().createActivityFromCoord("home", coord);
        home.setEndTime(9 * 60 * 60 + variance);
        plan.addActivity(home);

        Leg legtowork = scenario.getPopulation().getFactory().createLeg(mode);
        plan.addLeg(legtowork);

        Activity work = scenario.getPopulation().getFactory().createActivityFromCoord("work", coordWork);
        work.setEndTime(17 * 60 * 60 + variance);
        plan.addActivity(work);

        Leg legbackhome = scenario.getPopulation().getFactory().createLeg(mode);
        plan.addLeg(legbackhome);

        Activity home2 = scenario.getPopulation().getFactory().createActivityFromCoord("home", coord);
        plan.addActivity(home2);

        person.addPlan(plan);
        scenario.getPopulation().addPerson(person);
    }

    // Read in shapefile (exact functioning can be ignored for now)
    public Map<String, Geometry> readShapeFile(String filename, String attrString) {
        Map<String, Geometry> shapeMap = new HashMap<>();
        for (SimpleFeature ft : ShapeFileReader.getAllFeatures(filename)) {
            GeometryFactory geometryFactory = new GeometryFactory();
            WKTReader wktReader = new WKTReader(geometryFactory);
            Geometry geometry;
            try {
                geometry = wktReader.read((ft.getAttribute("the_geom")).toString());
                shapeMap.put(ft.getAttribute(attrString).toString(), geometry);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return shapeMap;
    }

    // Create random coordinates within a given polygon (exact functioning can be ignored for now)
    private Coord drawRandomPointFromGeometry(Geometry g) {
        Random rnd = MatsimRandom.getLocalInstance();
        Point p;
        double x, y;
        do {
            x = g.getEnvelopeInternal().getMinX()
                    + rnd.nextDouble() * (g.getEnvelopeInternal().getMaxX() - g.getEnvelopeInternal().getMinX());
            y = g.getEnvelopeInternal().getMinY()
                    + rnd.nextDouble() * (g.getEnvelopeInternal().getMaxY() - g.getEnvelopeInternal().getMinY());
            p = MGC.xy2Point(x, y);
        } while (!g.contains(p));
        Coord coord = new Coord(p.getX(), p.getY());
        return coord;
    }

}
