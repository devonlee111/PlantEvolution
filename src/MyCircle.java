import java.awt.geom.*;

public class MyCircle {
	private double x, y, radius;
	
	public MyCircle(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public MyCircle(double radius) {
		this.radius = radius;
	}
	
	public double Area() {
		return Math.PI * Math.pow(radius, 2);
	}
	
	public void setCenter(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	// Return the x coordinate of the MyCircle center
	public double getX() {
		return x;
	}
	
	// Return the y coordinate of the MyCircle center
	public double getY() {
		return y;
	}

	// Returns true if the MyCircle contains a given point.
	public boolean contains(Point p) {
		return Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2)) <= radius;
	}
	
	// Find the points of intersection of a line and a MyCircle
	// The end points of the line are defined by points A, B.
	// The center of the MyCircle is defined by point C.
	// The point on the line closest to the MyCircle center is defined by point E.
	public Point[] intersect(Line2D line) {
		Point[] intersections = new Point[3];
		double dx = line.getX2() - line.getX1();
		double dy = line.getY2() - line.getY1();
		double A = dx * dx + dy * dy;
		double B = 2 * (dx * (line.getX1() - x) + dy * (line.getY1() - y));
		double C = (line.getX1() - x) * (line.getX1() - x) + (line.getY1() - y) * (line.getY1() - y) - radius * radius;
		double det = B * B - 4 * A * C;
		double t;
		
		if (det < 0) {
			System.out.println("missed");
		}
		else if (det == 0) {
			t = -B / (2 * A);
			Point p1 = new Point(line.getX1() + t * dx, line.getY1() + t * dy);
			//System.out.println("1 intersection at " + line.getX1() + t * dx + ", " + line.getX1() + t * dx);
			intersections[0] = p1;
		}
		else {
			t = (-B + Math.sqrt(det)) / (2 * A);
			Point p1 = new Point(line.getX1() + t * dx, line.getY1() + t * dy);
			t = (-B - Math.sqrt(det)) / (2 * A);
			Point p2 = new Point(line.getX1() + t * dx, line.getY1() + t * dy);
			Point p3 = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
			intersections[0] = p1;
			intersections[1] = p2;
			intersections[2] = p3;
			//System.out.println(segmentArea(p1, p2));
		}
		return intersections;
	}
	
	public boolean intersects(MyCircle c) {
		return Math.sqrt(Math.pow((c.x - x), 2) + Math.pow((c.y - y), 2)) < c.radius + radius;
	}
	
	// Calculate the area of the segment given the points of intersection of a line through a MyCircle.
	public double segmentArea(Point a, Point b) {
		double x1, y1, x2, y2;
		x1 = a.x;
		y1 = a.y;
		x2 = b.x;
		y2 = b.y;
		double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		double theta = Math.acos((Math.pow(radius, 2) + Math.pow(radius, 2) - Math.pow(length, 2)) / (2 * Math.pow(radius, 2)));
		System.out.println((Math.pow(radius, 2) + Math.pow(radius, 2) - Math.pow(length, 2)) / (2 * Math.pow(radius, 2)));
		System.out.println(length + " | " + radius + " | " + theta);
		theta *= (Math.PI / 180);
		double area = ((theta - Math.sin(theta)) / 2) * Math.pow(radius, 2);
		return area;
	}
	
	public double intersectionArea(MyCircle C) {
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

}