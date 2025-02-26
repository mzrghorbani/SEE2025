import java.io.File;

import core.LanderFederate;
import core.LanderFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.Lander;
import model.Position;
import model.Quaternion;


public class LanderMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		Lander lander = new Lander(
				"Lander", 
				"Lander", 
				"AitkenBasinLocalFixed",
				new Position(400, -7200, -1000),
				new Quaternion(0, 0, 0, 1));
		
		LanderFederateAmbassador ambassador = new LanderFederateAmbassador();
		LanderFederate federate = new LanderFederate(ambassador, lander);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
