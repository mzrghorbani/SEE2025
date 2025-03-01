package model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.siso.spacefom.frame.ReferenceFrameTranslation;
import org.siso.spacefom.frame.SpaceTimeCoordinateState;

import referenceFrame.coder.*;
import siso.smackdown.frame.ReferenceFrame;
import skf.coder.HLAunicodeStringCoder;
import skf.model.object.annotations.Attribute;
import skf.model.object.annotations.ObjectClass;


@ObjectClass(name = "PhysicalEntity.Lander")
public class Lander {

	@Attribute(name = "name", coder = HLAunicodeStringCoder.class)
	private String name = null;
	
	@Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
	private String parent_name = null;
	
	@Attribute(name = "state", coder = SpaceTimeCoordinateStateCoder.class)
	private SpaceTimeCoordinateState state = null;
	
	@Attribute(name = "type", coder = HLAunicodeStringCoder.class)
	private String type = null;
	
	@Attribute(name = "position", coder = HLAPositionCoder.class)
	private Position position = null;
	
	private Quaternion quaternion = null;
	
	public Lander(){}

	public Lander(String name, String type, String parent_name, Position position, Quaternion quaternion) {
		this.name = name;
		this.type = type;
		this.parent_name = parent_name;
		this.state = new SpaceTimeCoordinateState();
		this.position = position;
		this.quaternion = quaternion;
	}
	
	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}
	

	public SpaceTimeCoordinateState getState() {
	    return state;
	}

	public void setState(SpaceTimeCoordinateState state) {
	    this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Quaternion getQuaternion() {
		return quaternion;
	}

	public void setQuaternion(Quaternion quaternion) {
		this.quaternion = quaternion;
	}
	
	public void info(ReferenceFrame referenceFrame) {

	    SpaceTimeCoordinateState currentState = this.getState();
	    ReferenceFrameTranslation currentTransState = currentState.getTranslationalState();
	    Vector3D landerPosition = currentTransState.getPosition();

	    Vector3D landingPadPosition = new Vector3D(1000.0, 2000.0, -500.0); 

	    double distanceToLandingPad = landerPosition.subtract(landingPadPosition).getNorm();

	    String logMessage = String.format(
	        "[Lander Status] Distance to Landing Pad: %.2f meters", distanceToLandingPad
	    );

	    System.out.println(logMessage);
	}
	
	public void updateState(double x, double y, double z, double w, double qx, double qy, double qz) {
	    if (this.state == null) {
	        this.state = new SpaceTimeCoordinateState();
	    }

	    ReferenceFrameTranslation translationalState = this.state.getTranslationalState();
	    Vector3D newPosition = new Vector3D(x, y, z);
	    translationalState.setPosition(newPosition);

	    // ReferenceFrameRotation rotationState = this.state.getRotationState();
	    // org.apache.commons.math3.complex.Quaternion newQuaternion = new org.apache.commons.math3.complex.Quaternion(w, x, y, z);
		// rotationState.setAttitudeQuaternion(newQuaternion);

	    updateCurrentPosition(x, y, z);
	    updateCurrentQuaternion(w, qx, qy, qz);
	}
	
	private void updateCurrentPosition(double x, double y, double z) {
	    if (this.position != null) {
	        this.position.setX(x);
	        this.position.setY(y);
	        this.position.setZ(z);
	    } else {
	        this.position = new Position(x, y, z);
	    }
	}

	private void updateCurrentQuaternion(double w, double qx, double qy, double qz) {
	    if (this.quaternion != null) {
	        this.quaternion.setW(w);
	        this.quaternion.setX(qx);
	        this.quaternion.setY(qy);
	        this.quaternion.setZ(qz);
	    } else {
	        this.quaternion = new Quaternion(w, qx, qy, qz); // This would need to be your model.Quaternion if used elsewhere
	    }

	}
}
