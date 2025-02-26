package model;

public class Quaternion {
    private double w;
    private double x;
    private double y;
    private double z;

    // Default constructor for the identity quaternion
    public Quaternion() {
        this.w = 1.0;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    // Constructor with all components
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Getters and setters
    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
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

    public void normalize() {
        double norm = Math.sqrt(w * w + x * x + y * y + z * z);
        w /= norm;
        x /= norm;
        y /= norm;
        z /= norm;
    }

    @Override
    public String toString() {
        return "Quaternion [w=" + w + ", x=" + x + ", y=" + y + ", z=" + z + "]";
    }

}

