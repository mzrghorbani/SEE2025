import java.io.File;

import core.SpaceportFederate;
import core.SpaceportFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.Spaceport;
import model.Position;
import model.Quaternion;


public class SpaceportMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		// -1498534.0
		Spaceport spaceport = new Spaceport(
				"Spaceport", 
				"Spaceport", 
				"AitkenBasinLocalFixed",
				new Position(0, -7000, -5600),
				new Quaternion(0, 0, 0, 1));
		
		SpaceportFederateAmbassador ambassador = new SpaceportFederateAmbassador();
		SpaceportFederate federate = new SpaceportFederate(ambassador, spaceport);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
