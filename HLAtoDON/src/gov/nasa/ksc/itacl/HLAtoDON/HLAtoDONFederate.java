package gov.nasa.ksc.itacl.HLAtoDON;

import gov.nasa.ksc.itacl.Utilities.*;
import gov.nasa.ksc.itacl.mpc.models.Telemetry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class HLAtoDONFederate {
	
	private Controller controller;
	private Telemetry telemetry;
	private String[] args; 
	
	public HLAtoDONFederate(Telemetry telemetry, String[] args) {
		this.telemetry = telemetry;
		this.args = args;
	}
	
	public int start() { 
						
		if (this.args.length < 1) {
		    Utils.warn("No configuration file provided. Attempting to locate default config...");
		    
		    // Try to find the default configuration file
		    String defaultConfigPath = "config/FederateConfig.xml";
		    File configFile = new File(defaultConfigPath);
		    
		    if (!configFile.exists()) {
		        Utils.error("Default configuration file not found: " + defaultConfigPath);
		        return -1;
		    }
		    
		    Utils.info("Using default configuration file: " + defaultConfigPath);
		    this.args = new String[]{defaultConfigPath};
		}
		
		CommandInputRunner runner = new CommandInputRunner(this);
		(new Thread(runner)).start();
		
		FederationConfig config = FederationConfig.createConfig(this.args[0]);
		if(config == null) {
			Utils.error("Unable to create federation config file for " + this.args[0]);
			return -1;
		}
		
		this.controller = new Controller(config, this.telemetry);
		
		int startCode = this.controller.start(); // Store return value
		
		if (startCode == 0) {
		    Utils.info("HLA2DON successfully started. Monitoring data transfer...");
		} else {
		    Utils.error("HLA2DON failed to start.");
		}
		
		return startCode;
	}

	public void stop() {
		this.controller.stop();  	
		
		//wait until the system has shut down;
		while(!this.controller.isShutdown()) {	
			try {
				Thread.sleep(Utils.THREAD_SLEEP_TIME);
			} 
			catch (InterruptedException ignoreIGuess) {}
		}
		
		Utils.info("System shut down");
   }

	public static void main(String[] args) {
		Telemetry telemetry = new Telemetry();
		HLAtoDONFederate federate = new HLAtoDONFederate(telemetry, args);
		int code = federate.start();
		if(code != 0) {
			Utils.error("Failed to start HAA2DON Federate");
		}
	}
	   
	private class CommandInputRunner implements Runnable {
		
		private HLAtoDONFederate hlaToDONFederate;

		public CommandInputRunner(HLAtoDONFederate HLAToDONFederate) {
			this.hlaToDONFederate = HLAToDONFederate;
		}
		
		@Override
		public void run() {
		
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String in = inputReader.readLine();
					if (in.equalsIgnoreCase("q")) {
						this.hlaToDONFederate.stop();
						break;
					}
				} 
				catch (IOException ignore) {}
			}
		}
	}
}

