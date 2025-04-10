package core;

import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.Lander;
import model.Position;
import model.Quaternion;
import model.Spaceport;
import interactions.ConsoleColors;
import interactions.FederateMessage;
import interactions.KeyPressListener;
import siso.smackdown.frame.FrameType;
import siso.smackdown.frame.ReferenceFrame;
import skf.config.Configuration;
import skf.core.SEEAbstractFederate;
import skf.core.SEEAbstractFederateAmbassador;
import skf.exception.PublishException;
import skf.exception.SubscribeException;
import skf.exception.UnsubscribeException;
import skf.exception.UpdateException;
import skf.model.interaction.annotations.InteractionClass;
import skf.model.interaction.modeTransitionRequest.ModeTransitionRequest;
import skf.model.object.annotations.ObjectClass;
import skf.model.object.executionConfiguration.ExecutionConfiguration;
import skf.model.object.executionConfiguration.ExecutionMode;
import skf.synchronizationPoint.SynchronizationPoint;

public class LanderFederate extends SEEAbstractFederate implements Observer {

	private static final int MAX_WAIT_TIME = 10000;
	
	private Lander lander = null;
	private Spaceport spaceport = null;
	
	private String local_settings_designator = null;
	
	private ModeTransitionRequest mtr = null;
	
	private SimpleDateFormat format = null;
	
    private ReferenceFrame currentReferenceFrame = null;
    
    private FederateMessage message = null;
    private KeyPressListener keyListener = new KeyPressListener("Lander Input"); 
    
    private ScheduledExecutorService scheduler;
    private volatile boolean awaitingResponse = false; 
	
	public LanderFederate(SEEAbstractFederateAmbassador seefedamb, Lander lander) {
		super(seefedamb);
		this.lander  = lander;
		this.mtr = new ModeTransitionRequest();
		this.format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Initialize the message object with a empty message
		this.message = new FederateMessage();
	}

	@SuppressWarnings("unchecked")
	public void configureAndStart(Configuration config) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, 
														CallNotAllowedFromWithinCallback, RTIinternalError, CouldNotCreateLogicalTimeFactory, 
														FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, 
														SaveInProgress, RestoreInProgress, NotConnected, MalformedURLException, 
														FederateNotExecutionMember, NameNotFound, InvalidObjectClassHandle, AttributeNotDefined, 
														ObjectClassNotDefined, InstantiationException, IllegalAccessException, IllegalName, 
														ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ObjectClassNotPublished, 
														AttributeNotOwned, ObjectInstanceNotKnown, PublishException, UpdateException, 
														SubscribeException, InvalidInteractionClassHandle, InteractionClassNotDefined, 
														InteractionClassNotPublished, InteractionParameterNotDefined, UnsubscribeException, 
														InterruptedException, SynchronizationPointLabelNotAnnounced, FederateServiceInvocationsAreBeingReportedViaMOM {
		// 1. configure the SKF framework
		super.configure(config);

		// 2. Connect on RTI
		/*
		 *For MAK local_settings_designator = "";
		 *For PITCH local_settings_designator = "crcHost=" + <crc_host> + "\ncrcPort=" + <crc_port>;
		 */
		local_settings_designator = "crcHost="+config.getCrcHost()+"\ncrcPort="+config.getCrcPort();
		super.connectToRTI(local_settings_designator);

		// 3. join the SEE Federation execution
		super.joinFederationExecution();
		
		// 4. Subscribe the Subject
		super.subscribeSubject(this);
		
		/*
		 * 5. Check if the federate is a Late Joiner Federate. 
		 * All the Federates of the SEE Teams must be Late Joiner.
		 */
		if(!SynchronizationPoint.INITIALIZATION_STARTED.isAnnounced()){
			
			// 6. Wait for the announcement of the Synch-Point "initialization_completed"
			super.waitingForAnnouncement(SynchronizationPoint.INITIALIZATION_COMPLETED, MAX_WAIT_TIME);

			/* 7. Wait for announcement of "objects_discovered", and Federation
			 * Specific Mutiphase Initialization Synch-Points
			 */
			// -> skipped
			
			/* 8. Subscribe Execution Control Object Class Attributes
			 * and wait for ExCO Discovery
			 */
			super.subscribeElement((Class<? extends ObjectClass>) ExecutionConfiguration.class);
			super.waitForElementDiscovery((Class<? extends ObjectClass>) ExecutionConfiguration.class, MAX_WAIT_TIME);
			
			// 9. Request ExCO update
			while(super.getExecutionConfiguration() == null){
				super.requestAttributeValueUpdate((Class<? extends ObjectClass>) ExecutionConfiguration.class);
				Thread.sleep(10);
			}
				
			// 10. Publish MTR Interaction
			super.publishInteraction(this.mtr);
			
			 /* 
			 * 12. Reserve All Federate Object Instance Names
			 * 
			 * 13. Wait for All Federate Object Instance Name Reservation
			 * Success/Failure Callbacks
			 * 
			 * 14. Register Federate Object Instances
			 */
			
			super.subscribeReferenceFrame(FrameType.AitkenBasinLocalFixed);
			
			super.publishElement(lander, "Lander");
			super.subscribeElement((Class<? extends ObjectClass>) Spaceport.class);
			
			super.publishInteraction(this.message);
			super.subscribeInteraction((Class<? extends InteractionClass>) FederateMessage.class);
			
			// 15. Wait for All Required Objects to be Discovered
			// -> Skipped
			
			/* 16. Setup HLA Time Management and Query GALT, Compute HLTB and 
			 * Time Advance to HLTB
			 */
			super.setupHLATimeManagement();
			
			// 17. Achieve "objects_discovered" Sync-Point and wait for synchronization
			// -> Skipped
		}
		else
			throw new RuntimeException("The federate " + config.getFederateName() + "is not a Late Joiner Federate");
		
		//18. Start simulation execution
		super.startExecution();
		
	}

	@Override
	protected void doAction() {

	    Position currentPos = this.lander.getPosition();
	    double x = currentPos.getX() + 0; 
	    double y = currentPos.getY() - 0;
	    
	    // Advancing in Z direction
	    double targetZ = -5300;
	    double currentZ = currentPos.getZ();

	    double newZ = currentZ;

	    if (currentZ > targetZ) {
	        newZ = currentZ - 100;

	        // Ensure we don't overshoot the platform
	        if (newZ < targetZ) {
	            newZ = targetZ;
	        }
	    } else {
	        // Already at or below platform — stay put
	        newZ = targetZ;
	    }
	    
	    if (newZ == targetZ) {
	        System.out.println("[Lander] Touchdown! Lander has reached the platform.");
	    } else {
	        double distance = Math.abs(targetZ - newZ);
	        System.out.printf("[Lander Status] Descending — %.2f metres to platform...\n", distance);
	    }
   
	    Quaternion currentQuat = this.lander.getQuaternion();
	    double w = currentQuat.getW(); 
	    double qx = currentQuat.getX(); 
	    double qy = currentQuat.getY();
	    double qz = currentQuat.getZ();
	    
	    this.lander.updateState(x, y, newZ, w, qx, qy, qz);
	    
	    try {
	        super.updateElement(this.lander);
	        System.out.println("[Lander] Published " + this.lander.getPosition());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    if (keyListener.hasKeyPress()) {
	    	sendMessages(keyListener.getKey());  
	        keyListener.reset();  // Prevents repeated execution
	    }
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {

	    if (arg1 instanceof ExecutionConfiguration) {
	        super.setExecutionConfiguration((ExecutionConfiguration) arg1);

	        ExecutionMode currentMode = super.getExecutionConfiguration().getCurrent_execution_mode();
	        ExecutionMode nextMode = super.getExecutionConfiguration().getNext_execution_mode();

	        System.out.println("[Lander Federate] Received ExecutionConfiguration update: ");
	        System.out.println("    Current Mode: " + currentMode);
	        System.out.println("    Next Mode: " + nextMode);

	        if (currentMode == ExecutionMode.EXEC_MODE_RUNNING &&
	            nextMode == ExecutionMode.EXEC_MODE_FREEZE) {
	            System.out.println("[Lander Federate] Freezing execution...");
	            super.freezeExecution();
	        } 
	        else if (currentMode == ExecutionMode.EXEC_MODE_FREEZE &&
	                 nextMode == ExecutionMode.EXEC_MODE_RUNNING) {
	            System.out.println("[Lander Federate] Resuming execution...");
	            super.resumeExecution();
	        } 
	        else if ((currentMode == ExecutionMode.EXEC_MODE_FREEZE ||
	                  currentMode == ExecutionMode.EXEC_MODE_RUNNING) &&
	                 nextMode == ExecutionMode.EXEC_MODE_SHUTDOWN) {
	            System.out.println("[Lander Federate] Shutting down execution...");
	            super.shudownExecution();
	        } 
	        else if (currentMode == ExecutionMode.EXEC_MODE_RUNNING && 
	                 nextMode == ExecutionMode.EXEC_MODE_RUNNING) {
	            System.out.println("[Lander Federate] Execution is already running. No state change needed.");
	        }
	        else {
	            System.out.println("[Lander Federate] ExecutionConfiguration status unknown");
	        }
	    } 
	    else if (arg1 instanceof FederateMessage) {
	    	receiveMessages((FederateMessage) arg1);
	    } 
	    else if (arg1 instanceof ReferenceFrame) {
	        this.currentReferenceFrame = (ReferenceFrame) arg1;
	    }
	    else if (arg1 instanceof Spaceport) {
	    	this.spaceport = (Spaceport) arg1; 
	        
	        if (this.spaceport.getPosition() != null) {
				System.out.println("[Lander] Recieved Spaceport " + this.spaceport.getPosition()); // Debugging

	        } else {
	            System.out.println("[Lander] Position is NULL, no position data received.");
	        }
	    }
	    else {
	        System.out.println("[Lander Federate] received an unknown update type: " + arg1.getClass().getSimpleName());
	    }
	}

	private void sendMessage(String sender, String receiver, String type, String content) {
		
	    // Modify the existing `message` object
	    this.message.setSender(sender);
	    this.message.setReceiver(receiver);
	    this.message.setMessageType(type);
	    this.message.setContent(content);

	    try {
	        super.updateInteraction(this.message); 
	        ConsoleColors.logInfo("[Lander] Sent " + content + " to " + receiver);
	    } catch (Exception e) {
	        ConsoleColors.logError("[Lander] Error sending message: " + e.getMessage());
	    }
	}

	private void sendMessages(String key) {
	    switch (key.toUpperCase()) {

	        // Phase C: Lander → Spaceport (Requesting Landing)
	        case "C":
	            sendMessage("Lander", "Spaceport", "BRUNEL_LANDER_SPACEPORT_REQUEST_LANDING", "Landing Request...");
	            ConsoleColors.logInfo("[Lander] [Phase C] [To Spaceport] Landing Request sent. Awaiting acknowledgment...");
	            startAwaitingResponseTask();
	            break;

	        default:
	            ConsoleColors.logInfo("[Lander] Unknown key pressed: '" + key + "'. Valid key: C");
	            break;
	    }
	}
	
	private void receiveMessages(FederateMessage message) {
	    switch (message.getMessageType()) {

	        // Phase C: From Spaceport → Lander (Acknowledging Landing)
	        case "BRUNEL_SPACEPORT_LANDER_ACKNOWLEDGED":
	            ConsoleColors.logInfo("[Lander] [Phase C] [From Spaceport] " + message.getContent() + " — Press 'C' to proceed.");
	            stopAwaitingResponseTask();
	            break;

	        default:
	            ConsoleColors.logInfo("[Lander] Unknown message type: " + message.getMessageType());
	            break;
	    }
	}
	
	private void startAwaitingResponseTask() {
		awaitingResponse = true; // Make sure flag is updated first
	    if (scheduler != null && !scheduler.isShutdown()) {
	        return; // Avoid creating multiple schedulers
	    }

	    scheduler = Executors.newSingleThreadScheduledExecutor();

	    scheduler.scheduleAtFixedRate(() -> {
	        if (!awaitingResponse) {
	            stopAwaitingResponseTask(); // Stop scheduler when response is received
	            return;
	        }
	        ConsoleColors.logInfo("[Lander] Still awaiting Spaceport response..."); // Fixed incorrect log
	    }, 0, 5, TimeUnit.SECONDS); // Runs every 5 seconds
	}
	
	private void stopAwaitingResponseTask() {
	    awaitingResponse = false; // Make sure flag is updated first
	    if (scheduler != null && !scheduler.isShutdown()) {
	        scheduler.shutdownNow(); // Forces immediate shutdown
	        ConsoleColors.logInfo("[Lander] Stopped waiting for response.");
	    }
	}
	
}
