/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * @author nagel
 *
 */
public class RunMatsim{

	private static String localDir = "src/main/java/org/matsim/";

	public static void main(String[] args) {

		Config config = ConfigUtils.createConfig();
		config.controler().setLastIteration(10);
		config.controler().setOverwriteFileSetting( OverwriteFileSetting.deleteDirectoryIfExists );
		config.network().setInputFile(localDir + "Network/TPMetropolitanRoadNetwork.xml");
		config.plans().setInputFile(localDir + "Demand/Demand.xml");
		config.qsim().setStartTime(0 * 60 * 60);
		config.qsim().setEndTime(0 * 60 * 60);
		config.qsim().setFlowCapFactor(0.1);
		config.qsim().setStorageCapFactor(0.1);

		//add scooter, train, bus, MRT as new (teleported) modes
		PlanCalcScoreConfigGroup.ModeParams scooter = new PlanCalcScoreConfigGroup.ModeParams("scooter");
		scooter.setConstant(0);
		scooter.setMarginalUtilityOfDistance(-2);
		scooter.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(scooter);
		PlansCalcRouteConfigGroup.ModeRoutingParams scooterTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("scooter");
		//scooterTeleport.setTeleportedModeFreespeedFactor(2.);
		scooterTeleport.setTeleportedModeSpeed(8.33);
		config.plansCalcRoute().addModeRoutingParams(scooterTeleport);

		PlanCalcScoreConfigGroup.ModeParams train = new PlanCalcScoreConfigGroup.ModeParams("train");
		train.setConstant(0);
		train.setMarginalUtilityOfDistance(-2);
		train.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(train);
		PlansCalcRouteConfigGroup.ModeRoutingParams trainTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("train");
		//trainTeleport.setTeleportedModeFreespeedFactor(2.);
		trainTeleport.setTeleportedModeSpeed(13.89);
		config.plansCalcRoute().addModeRoutingParams(trainTeleport);


		PlanCalcScoreConfigGroup.ModeParams taxi = new PlanCalcScoreConfigGroup.ModeParams("taxi");
		taxi.setConstant(0);
		taxi.setMarginalUtilityOfDistance(-2);
		taxi.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(taxi);
		PlansCalcRouteConfigGroup.ModeRoutingParams taxiTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("taxi");
		//taxiTeleport.setTeleportedModeFreespeedFactor(2.);
		taxiTeleport.setTeleportedModeSpeed(8.33);
		config.plansCalcRoute().addModeRoutingParams(taxiTeleport);

		PlanCalcScoreConfigGroup.ModeParams MRT = new PlanCalcScoreConfigGroup.ModeParams("MRT");
		MRT.setConstant(0);
		MRT.setMarginalUtilityOfDistance(-2);
		MRT.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(MRT);
		PlansCalcRouteConfigGroup.ModeRoutingParams MRTTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("MRT");
		//MRTTeleport.setTeleportedModeFreespeedFactor(2.);
		MRTTeleport.setTeleportedModeSpeed(9.44);
		config.plansCalcRoute().addModeRoutingParams(MRTTeleport);

		PlanCalcScoreConfigGroup.ModeParams bus = new PlanCalcScoreConfigGroup.ModeParams("bus");
		bus.setConstant(0);
		bus.setMarginalUtilityOfDistance(-2);
		bus.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(bus);
		PlansCalcRouteConfigGroup.ModeRoutingParams busTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("bus");
		//busTeleport.setTeleportedModeFreespeedFactor(1.3);
		busTeleport.setTeleportedModeSpeed(6.39);
		config.plansCalcRoute().addModeRoutingParams(busTeleport);

		PlanCalcScoreConfigGroup.ModeParams bike = new PlanCalcScoreConfigGroup.ModeParams("bike");
		bike.setConstant(0);
		bike.setMarginalUtilityOfDistance(-2);
		bike.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(bus);
		PlansCalcRouteConfigGroup.ModeRoutingParams bikeTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("bike");
		bikeTeleport.setTeleportedModeFreespeedFactor(2.5);
		//bikeTeleport.setTeleportedModeSpeed(3.33);
		config.plansCalcRoute().addModeRoutingParams(bikeTeleport);

		PlanCalcScoreConfigGroup.ModeParams walk = new PlanCalcScoreConfigGroup.ModeParams("walk");
		walk.setConstant(0);
		walk.setMarginalUtilityOfDistance(-2);
		walk.setMarginalUtilityOfTraveling(-4);
		config.planCalcScore().addModeParams(walk);
		PlansCalcRouteConfigGroup.ModeRoutingParams walkTeleport = new PlansCalcRouteConfigGroup.ModeRoutingParams("walk");
		//walkTeleport.setTeleportedModeFreespeedFactor(15);
		walkTeleport.setTeleportedModeSpeed(0.56);
		config.plansCalcRoute().addModeRoutingParams(walkTeleport);


		PlanCalcScoreConfigGroup.ActivityParams home = new PlanCalcScoreConfigGroup.ActivityParams("home");
		home.setTypicalDuration(16 * 60 * 60);
		config.planCalcScore().addActivityParams(home);
		PlanCalcScoreConfigGroup.ActivityParams work = new PlanCalcScoreConfigGroup.ActivityParams("work");
		work.setTypicalDuration(8 * 60 * 60);
		config.planCalcScore().addActivityParams(work);

		// define strategies:
		{
			StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
			strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute.toString());
			strat.setWeight(0.15);
			config.changeMode().setModes(new String[]{"car", "scooter", "train", "taxi", "MRT", "bus", "bike", "walk"});
			config.strategy().addStrategySettings(strat);
		}
		{
			StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
			strat.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta.toString());
			strat.setWeight(0.9);
			config.strategy().addStrategySettings(strat);
		}
		{
			StrategyConfigGroup.StrategySettings strat = new StrategyConfigGroup.StrategySettings();
			strat.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode.toString());
			strat.setWeight(0.1);
			config.strategy().addStrategySettings(strat);
		}
		config.strategy().setFractionOfIterationsToDisableInnovation(0.9);

		config.vspExperimental().setWritingOutputEvents(true);

		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles); //Avoid problem: https://matsim.atlassian.net/browse/MATSIM-686?oldIssueView=true

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		Controler controler = new Controler( scenario ) ;
		controler.run();
	}
	
}
