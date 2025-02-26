package model;

import skf.coder.HLAfloat64BECoder;
import skf.model.object.annotations.Attribute;

public class Position {
	
	@Attribute(name = "x", coder = HLAfloat64BECoder.class)
	private double x;
	
	@Attribute(name = "y", coder = HLAfloat64BECoder.class)
	private double y;
	
	@Attribute(name = "z", coder = HLAfloat64BECoder.class)
	private double z;
	
	public Position(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
}
