package org.matsim.Network;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;
import org.matsim.api.core.v01.network.Network;


public class TPMetroplitanRoadNetwork {

    public static void main(String[] args) {
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Network network = scenario.getNetwork();

        CoordinateTransformation ct =
                TransformationFactory.getCoordinateTransformation(
                        TransformationFactory.WGS84, "EPSG:3826");
        new OsmNetworkReader(network, ct).parse("src/main/java/org/matsim/Network/TPMetropolitanRoadNetworkFixed.osm.bz2");
        new NetworkWriter(network).write("src/main/java/org/matsim/Network/TPMetropolitanRoadNetworkFixed.xml");
    }
}