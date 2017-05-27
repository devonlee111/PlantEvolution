import java.awt.*;
import java.awt.geom.*;

public class Circle {
	private double x, y, radius;
	
	public Circle(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public Circle(double radius) {
		this.radius = radius;
	}
	
	public double Area() {
		return Math.PI * Math.pow(radius, 2);
	}
	
	public void setCenter(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	// Return the x coordinate of the circle center
	public double getX() {
		return x;
	}
	
	// Return the y coordinate of the circle center
	public double getY() {
		return y;
	}

	// Returns true if the circle contains a given point.
	public boolean contains(Point p) {
		return Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2)) <= radius;
	}
	
	// Find the points of intersection of a line and a circle
	// The end points of the line are defined by points A, B.
	// The center of the circle is defined by point C.
	// The point on the line closest to the circle center is defined by point E.
	public void intersect(Line2D line) {
		double aX = line.getX1();
		double aY = line.getY1();
		double bX = line.getX2();
		double bY = line.getY2();
		double ABDist = Math.sqrt(Math.pow((bX - aX), 2) + Math.pow((bY - aY), 2));
		double dX = (bX - aX) / ABDist;
		double dY = (bY - aY) / ABDist;
		double t = (dX * (x - aX)) + (dY * (y - aY));
		double eX = t * dX + aX;
		double eY = t * dY + aY;
		double ECDist = Math.sqrt(Math.pow((eX - x), 2) + Math.pow((eY - y), 2));
		if (ECDist < radius) {
			double dT = Math.sqrt(Math.pow(radius, 2) - Math.pow(ECDist, 2));
			double fX = (t - dT) * dX + aX;
			double fY = (t - dT) * dY + aY;
			
			double gX = (t + dT) * dY + aY;
			double gY = (t + dT) * dY + aY;
			System.out.println(fX + ", " + fY + " | " + gX + ", " + gY);
		}
		else if (ECDist == radius) {
			System.out.println("Tangent");
		}
		else {
			System.out.println("Missed");
		}
	}
	
	public boolean intersects(Circle c) {
		return Math.sqrt(Math.pow((c.x - x), 2) + Math.pow((c.y - y), 2)) < c.radius + radius;
	}
	
	// Calculate the area of the segment given the points of intersection of a line through a circle.
	public double segmentArea(Point a, Point b) {
		double x1, y1, x2, y2;
		x1 = a.x;
		y1 = a.y;
		x2 = b.x;
		y2 = b.y;
		double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		double theta = Math.acos(((Math.pow(radius, 2) * 2) - Math.pow(length, 2)) / (2 * Math.pow(radius, 2)));
		theta *= (Math.PI / 180);
		double area = ((theta - Math.sin(theta)) / 2) * Math.pow(radius, 2);
		return area;
	}
	
	public double intersectionArea(Circle C) {
		if (!intersects(C)) {
			return -1;
		}
		else if (C.x == x && C.y == y) {
			double rad = radius;
			if (C.radius > rad) {
				rad = C.radius;
			}
			return Math.PI * (rad * rad);
		}
		double r = radius;
		double R = C.radius;
		double d = Math.sqrt(Math.pow((C.x - x), 2) + Math.pow((C.y - y), 2));
		if (R < r) {
			r = C.radius;
			R = radius;
		}
		double a = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
		double b = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
		double c = 0.5 * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));
		return a + b + c;
	}
	
	class Point {
		public double x, y;
		private Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
}
