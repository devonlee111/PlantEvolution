import java.awt.geom.Line2D;

public class Circle {
	private Point center;
	private double radius;
	
	public Circle(double x, double y, double radius) {
		center.setX(x);
		center.setY(y);
		this.radius = radius;
	}
	
	public Circle(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Circle(double radius) {
		this.radius = radius;
	}
	
	// Calculate and return the area of this circle.
	public double Area() {
		return Math.PI * Math.pow(radius, 2);
	}
	
	// Set the center of this circle to the given coordinates.
	public void setCenter(double x, double y) {
		center.setX(x);
		center.setY(y);
	}
	
	// Return the x coordinate of the Circle center
	public double getX() {
		return center.getX();
	}
	
	// Return the y coordinate of the Circle center
	public double getY() {
		return center.getY();
	}

	// Returns true if the Circle contains a given point.
	public boolean contains(Point p) {
		return Math.pow(p.getX() - this.center.getX(), 2) + Math.pow(p.getY() - this.center.getY(), 2) <= radius;
	}
	
	/* Find the points of intersection of a line and a Circle
	 * The end points of the line are defined by points A, B.
	 * The center of the Circle is defined by point C.
	 * The point on the line closest to the Circle center is defined by point E.
	*/
	public Point[] intersect(Line2D line) {
		Point[] intersections = new Point[3];
		double dx = line.getX2() - line.getX1();
		double dy = line.getY2() - line.getY1();
		double A = dx * dx + dy * dy;
		double B = 2 * (dx * (line.getX1() - center.getX()) + dy * (line.getY1() - center.getY()));
		double C = (line.getX1() - center.getX()) * (line.getX1() - center.getX()) + (line.getY1() - center.getY()) * (line.getY1() - center.getY()) - radius * radius;
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
			Point p3 = new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
			intersections[0] = p1;
			intersections[1] = p2;
			intersections[2] = p3;
			//System.out.println(segmentArea(p1, p2));
		}
		return intersections;
	}
	
	// Check if another circle intersects this circle.
	public boolean intersects(Circle c) {
		return Math.sqrt(Math.pow((c.getX() - center.getX()), 2) + Math.pow((c.getY() - center.getY()), 2)) < c.radius + radius;
	}
	
	// Calculate the area of the segment given the points of intersection of a line through a Circle.
	public double segmentArea(Point a, Point b) {
		double x1, y1, x2, y2;
		x1 = a.getX();
		y1 = a.getY();
		x2 = b.getX();
		y2 = b.getY();
		double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		double theta = Math.acos((Math.pow(radius, 2) + Math.pow(radius, 2) - Math.pow(length, 2)) / (2 * Math.pow(radius, 2)));
		System.out.println((Math.pow(radius, 2) + Math.pow(radius, 2) - Math.pow(length, 2)) / (2 * Math.pow(radius, 2)));
		System.out.println(length + " | " + radius + " | " + theta);
		theta *= (Math.PI / 180);
		double area = ((theta - Math.sin(theta)) / 2) * Math.pow(radius, 2);
		return area;
	}
	
	// Get the area of intersection of this circle and another circle.
	public double intersectionArea(Circle circle) {
		if (!intersects(circle)) {
			return -1;
		}
		else if (circle.getX() == center.getX() && circle.getY() == center.getY()) {
			double rad = radius;
			if (circle.radius > rad) {
				rad = circle.radius;
			}
			return Math.PI * (rad * rad);
		}
		double r = radius;
		double R = circle.radius;
		double d = Math.sqrt(Math.pow((circle.getX() - center.getX()), 2) + Math.pow((circle.getY() - center.getY()), 2));
		if (R < r) {
			r = circle.radius;
			R = radius;
		}
		double a = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
		double b = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
		double c = 0.5 * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));
		return a + b + c;
	}

}