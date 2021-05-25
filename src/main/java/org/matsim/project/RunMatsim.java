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

import java.io.IOException;
import java.util.Random;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import ch.sbb.matsim.analysis.skims.CalculateSkimMatrices;

/**
 * @author nagel
 *
 */
public class RunMatsim{

	public static void main(String[] args) {

		Config config;
		if ( args==null || args.length==0 || args[0]==null ){
			config = ConfigUtils.loadConfig( "..\\..\\scaper-sim-scenario\\matsim-config.xml" );
		} else {
			config = ConfigUtils.loadConfig( args );
		}
		config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
		
		/*
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		Controler controler = new Controler( scenario ) ;
		controler.run();
		*/

		//calculate skim matrices
		ConfigGroup smcg = config.getModule("skimMatrix");

		String zonesShapeFilename = smcg.getValue("zoneShapefile");
		String zonesIdAttributeName = smcg.getValue("zonesIdAttribute");
		String outputDirectory = smcg.getValue("outputDirectory");
		String networkFilename = config.network().getInputFile();
		String eventsFilename = null; //test without events first, then load events after running controler
		double[] times = new double[] { 7*3600 }; //7 am (seconds after midnight)
		int numThreads = 8;
		int numCoordsPerZone = 1;

		CalculateSkimMatrices skims = new CalculateSkimMatrices(outputDirectory, numThreads);
		
		try {
			skims.calculateSamplingPointsPerZoneFromNetwork(networkFilename, numCoordsPerZone, zonesShapeFilename, zonesIdAttributeName, new Random());
			skims.calculateNetworkMatrices(networkFilename, eventsFilename, times, config, null, link -> true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
}
