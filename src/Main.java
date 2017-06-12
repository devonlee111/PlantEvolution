import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Main extends Application {
	double pxUnit = 5;
	double centerX = 500;
	double centerY = 1000;
	
	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, 1000, 1000, Color.SKYBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
		//Plant p1 = new Plant(30);
		//drawPlant(p1, root);
		double total = 0;
		for (int j = 0; j < 1000; j++) {
			int validGenes = 0;
			int generation = 0;
			Plant p1 = new Plant(30);
			Plant p2 = new Plant(-30);
			Plant child;
			while(validGenes < 2) {
				validGenes = 0;
				child = new Plant(0, p1, p2);
				p1 = child;
				p2 = child;
				generation++;
				for (int i = 0; i < 20; i++) {
					if (child.geneType(i * 8) != -1) {
						validGenes++;
					}
				}
				//drawPlant(p1, newRoot);
				//drawPlant(p2, newRoot);
				//drawPlant(child, root);
				//root.getChildren().clear();
			}
			total += generation;
		}
		System.out.println(total/1000);
	}
	
	public void calculateFitness() {

	}
	
	public void drawPlant(Plant plant, Group root) {
		double theta, length;
		Point startPos = plant.groundPos();
		Point endPos;
		for (int i = 0; i < 20; i++) {
			int geneType = plant.geneType(i * 8);
			if (geneType == 0) {
				theta = plant.branchAngle(i * 10);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				endPos = new Point(length * Math.cos(theta), length * Math.sin(theta));
				Line line = new Line((startPos.x * pxUnit) + centerX, -((startPos.y * pxUnit)) + centerY, ((endPos.x + startPos.x) * pxUnit) + centerX, -(((endPos.y + startPos.y) * pxUnit)) + centerY);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(5);
				root.getChildren().addAll(line);
				startPos = endPos;
			}
			else if (geneType == 1) {
				theta = plant.branchAngle(i * 8);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				//endPos = new Point(-(length * Math.cos(theta) * pxUnit) + startPos.x + centerX, -(length * Math.sin(theta) * pxUnit) + startPos.y + centerY);
				//Line line = new Line(startPos.x + centerX, startPos.y + centerY, endPos.x, endPos.y);
				endPos = new Point(length * Math.cos(theta), length * Math.sin(theta));
				Line line = new Line((startPos.x * pxUnit) + centerX, -((startPos.y * pxUnit)) + centerY, ((endPos.x + startPos.x) * pxUnit) + centerX, -(((endPos.y + startPos.y) * pxUnit)) + centerY);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(5);
				Circle leaf = new Circle(((endPos.x + startPos.x) * pxUnit) + centerX, -(((endPos.y + startPos.y) * pxUnit)) + centerY, 10);
				leaf.setFill(Color.GREEN);
				root.getChildren().addAll(line, leaf);
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}