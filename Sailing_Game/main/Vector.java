package main;

public class Vector{

	private double x, y, z;
	
	public Vector() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector(Point3D p) {
		this.x = p.getX(); 
		this.y = p.getY();
		this.z = p.getZ();
	}
	
	public Vector(double angle) {
		this.x = Math.cos(angle);
		this.y = Math.sin(angle);
		this.z = 0;
	}
	
	public Vector(double angle, double mag) {
		this.x = mag * Math.cos(angle);
		this.y = mag * Math.sin(angle);
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
	
	public void setMagnitude(double d) {
		if(magnitude() == 0) return;
		multiplyAllComponentsBy(d / magnitude());
	}
	
	public void increaseMagnitude(double d) {
		setMagnitude(magnitude() + d);
	}
	
	public void decreaseMagnitude(double d) {
		setMagnitude(magnitude() - d);
	}
	
	public void turn(double angle) {
		double newAngle = this.get2DDirection() + angle;
		Vector newVector = new Vector(newAngle);
		newVector.setMagnitude(Math.sqrt(x * x + y * y));
		setXComponent(newVector.getXComponent());
		setYComponent(newVector.getYComponent());
	}
	
	public void subtractVector(Vector v) {
		setXComponent(getXComponent() - v.getXComponent());
		setYComponent(getYComponent() - v.getYComponent());
		setZComponent(getZComponent() - v.getZComponent());
	}
	
	public void addVector(Vector v) {
		setXComponent(getXComponent() + v.getXComponent());
		setYComponent(getYComponent() + v.getYComponent());
		setZComponent(getZComponent() + v.getZComponent());
	}
	
	public Point3D movePoint(Point3D p) {
		return new Point3D(p.getX() + x, p.getY() + y, p.getZ() + z);
	}
	
	/*
	public Vector projectAOnB(Vector a, Vector b) {
		Vector unitB = b;
		unitB.multiplyAllComponentsBy(b.magnitude());
		
		double angleDifference = a.get2DDirection() - b.get2DDirection();
		Vector ans = unitB;
		ans.multiplyAllComponentsBy(a.magnitude() * Math.cos(angleDifference));
		return ans;
	}
	
	public Vector rejectAOnB(Vector a, Vector b) {
		Vector projAOnB = projectAOnB(a, b);
		Vector ans = a;
		ans.subtract(projAOnB);
		return ans;
	}
	*/
	 
}
