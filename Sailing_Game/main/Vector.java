package main;

public class Vector{

	private double x, y, z;
	
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public Vector(double x, double y, double z) {
		this.x = x; 
		this.y = y;
		this.z = z;
	}
	
	public Vector(double angle) {
		this.x = Math.cos(angle);
		this.y = Math.sin(angle);
		this.z = 0;
	}
	
	public void clamp(double maxMagnitude) {
		if(magnitude() <= maxMagnitude) return;
		
		double tempX = getXComponent();
		double tempY = getYComponent();
		double tempZ = getZComponent();
		double tempMagnitude = magnitude();
		
		x = tempX * maxMagnitude / tempMagnitude;
		y = tempY * maxMagnitude / tempMagnitude;
		z = tempZ * maxMagnitude / tempMagnitude;
	}
	
	public void print() {
		System.out.println("x: " + x + ", y: " + y + ", z: " + z);
	}
	
	public double get2DDirection() {
		return (double)Math.atan2(y, x);
	}
	
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public void addToXComponent(double add) {
		x += add;
	}
	
	public void addToYComponent(double add) {
		y += add;
	}
	
	public void addToZComponent(double add) {
		z += add;
	}
	
	public void multiplyXComponentBy(double scalar) {
		x *= scalar;
	}
	
	public void multiplyYComponentBy(double scalar) {
		y *= scalar;
	}
	
	public void multiplyZComponentBy(double scalar) {
		z *= scalar;
	}
	
	public void multiplyAllComponentsBy(double scalar) {
		multiplyXComponentBy(scalar);
		multiplyYComponentBy(scalar);
		multiplyZComponentBy(scalar);
	}
	
	public void setXComponent(double newX) {
		x = newX;
	}
	
	public void setYComponent(double newY) {
		y = newY;
	}
	
	public void setZComponent(double newZ) {
		z = newZ;
	}
	
	public double getXComponent() {
		return x;
	}
	
	public double getYComponent() {
		return y;
	}
	
	public double getZComponent() {
		return z;
	}
	
	public void subtract(Vector b) {
		setXComponent(getXComponent() - b.getXComponent());
		setYComponent(getYComponent() - b.getYComponent());
		setZComponent(getZComponent() - b.getZComponent());
	}
}
