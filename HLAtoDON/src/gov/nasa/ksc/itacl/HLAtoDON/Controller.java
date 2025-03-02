package gov.nasa.ksc.itacl.HLAtoDON;

import gov.nasa.ksc.itacl.Utilities.*;
import gov.nasa.ksc.itacl.mpc.MPCSocketServer;
import gov.nasa.ksc.itacl.mpc.models.Telemetry;

public class Controller {
	
	private HlaInterface hlaInterface;
	private MPCSocketServer mpcStreamingServer;
	private Telemetry telemetry;
	private boolean run = true;
	private FederationConfig federationConfig;
	
	private static int logCounter = 0;
	private static final int LOG_INTERVAL = 10; 

	public Controller(FederationConfig config, Telemetry telemetry) {
		this.federationConfig = config;
		this.telemetry = telemetry;
	}

	public int start(){
		
		initMPCStreamingServer();
		
		// Create the RTI Ambassador
		hlaInterface = HlaInterface.Factory.HlaAmbassador();
		
		Utils.info("Trying to connect to " + federationConfig.hostAndPortSettings);	
		
		// Start the Federation Execution
		if(!hlaInterface.start(federationConfig, telemetry)) {
			return -1;
		}	
		Utils.info("Connected");
		
		// Initialize RTI Handles
		Utils.info("initializing objects and attribute handles");
		if(!hlaInterface.initHandles()) {
			return -1;
		}
		
		// Subscribe to Object Attributes and/or Interactions
		Utils.info("Subscribing to objects, attributes and interactions");
		if(!hlaInterface.subscribe()) {
			return -1;
		}
				
		// Enable Asynchronous Delivery
		Utils.info("enabling AsynchonousDelivery");
		if(!hlaInterface.enableAsynchonousDelivery()) {
			return -1;
		}
		
		// Enable Time Constrained;
		Utils.info("Enabling Time constrained");
		if(!hlaInterface.enableTimeConstrained()) {
			return -1;
		}

		// Query and Advance to to GALT
		Utils.info("Advancing to GALT");
		if(!hlaInterface.advanceToGALT()) {
			return -1;
		}
		
		ConsoleColors.logInfo("Starting simulation");
		
		while(run) {
			double time = hlaInterface.getDoubleTime();
			mpcStreamingServer.streamUpdate(time);
			
			if (logCounter % LOG_INTERVAL == 0) { 
			    ConsoleColors.logInfo("HLA2DON Execution Time: " + time);
			}
			logCounter++;
			
			hlaInterface.advanceTime();
		}
		
		// Disable time constrained. I don't care if it works or not!
		hlaInterface.disableTimeConstrained();
		
		// Resign Federation Execution. Do disconnect and shutdown even this fails. 
		hlaInterface.resignFederationExecution();
		
		// Destroy FederationExecution
		hlaInterface.disconnect();
		
		// Shutdown the MPC streaming server. 
		mpcStreamingServer.end();
		
		return 0;
	}
	
	public void stop() {
		run = false;
	}
	
	public boolean isShutdown() {
		return ( mpcStreamingServer.isShutdown() && hlaInterface.isShutdown() );
	}

	private void initMPCStreamingServer() {		
		
		Utils.info("Starting MPC Streaming Socket Server");
		
		// Create and start the server
		mpcStreamingServer = new MPCSocketServer(telemetry);
		mpcStreamingServer.SetPort(this.federationConfig.federateMPCSocketServerPortNum);
		mpcStreamingServer.start();
	}
}
