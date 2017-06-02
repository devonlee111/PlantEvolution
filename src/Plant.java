import java.util.ArrayList;

public class Plant implements Comparable<Plant>{
	// Genome is represented by a 43 bit string.
	// First gene from bits 0 - 2 represents stem length.
	// Second gene from bits 3 - 5 represents leaf size.
	// Third gene from bits 6 - 12 represents angle of new stems.
	// Fourth gene from bits 13 - 42 represents a array implemented heap for structure of plant.
	// Fifth gene from bits 43 - 74 represents whether an intersection of 2 branches contains a leaf.
	ArrayList<Point> leaves = new ArrayList<Point>();
	private String genome = "";
	private double fitness;
	
	// Create a new random plant.
	public Plant() {
		for (int i = 0; i < 74; i++) {
			genome += Integer.toString((int)(Math.random() * 2));
		}
		fitness = Math.random();
	}
	
	// Create a new plant with a predetermined genome.
	public Plant(String genome) {
		this.genome = genome;
	}
	
	// Comparison for the sorting of plants based on their fitness.
	@Override
	public int compareTo(Plant p) {
		if (p.fitness < fitness) {
			return -1;
		}
		else if (p.fitness == fitness) {
			return 0;
		}
		return 1;
	}
	
	// Produce a new plant by combining the genomes of different plants and mutating them.
	public Plant reproduce(Plant mate) {
		String newGenome = "";
		String gene11, gene12, gene21, gene22, gene31, gene32, gene41, gene42, gene51, gene52;
		gene11 = genome.substring(0, 3);
		gene21 = genome.substring(3, 6);
		gene31 = genome.substring(6, 13);
		gene41 = genome.substring(13, 42);
		gene51 = genome.substring(42);
		
		gene12 = mate.genome.substring(0, 3);
		gene22 = mate.genome.substring(3, 6);
		gene32 = mate.genome.substring(6, 13);
		gene42 = mate.genome.substring(13, 42);
		gene52 = mate.genome.substring(42);
		
		newGenome += combineGenes(gene11, gene12);
		newGenome += combineGenes(gene21, gene22);
		newGenome += combineGenes(gene31, gene32);
		newGenome += combineGenes(gene41, gene42);
		newGenome += combineGenes(gene51, gene52);
		char[] genomeArr = newGenome.toCharArray();
		for (int i = 0; i < 74; i++) {
			// Have a 1% chance of mutating any single bit.
			if ((int)(Math.random() * 101) == 0) {
				if (genomeArr[i] == '1') {
					genomeArr[i] = '0';
				}
				else {
					genomeArr[i] = '1';
				}
			}
		}
		newGenome = new String(genomeArr);
		Plant offspring = new Plant(newGenome);
		return offspring;
	}
	
	// Given 2 genes take half of one gene, starting at a random point, and swap it to the same position in the other gene.
	public String combineGenes(String gene1, String gene2) {
		char[] g1 = gene1.toCharArray();
		char[] g2 = gene2.toCharArray();
		int geneLength = gene1.length();
		int numSwap = geneLength/2;
		int startPos = (int)(Math.random() * ((geneLength - numSwap) + 1));
		int endPos = startPos + (numSwap);
		for (int i = startPos; i < endPos; i++) {
			g1[i] = g2[i];
		}
		String newGene = new String(g1);
		return newGene;
	}
	
	public int stemLength() {
		return Integer.parseInt(genome.substring(0, 3), 2) + 7;
	}
	
	public int leafSize() {
		return Integer.parseInt(genome.substring(3,  6), 2) + 1;
	}
	
	public int stemAngle() {
		return Integer.parseInt(genome.substring(6, 13), 2) + 1;
	}
	
	// Process the genome to get the structure of the plant
	public ArrayList<Point> processPlant() {
		ArrayList<Point> positions = new ArrayList<Point>();
		Point start = new Point(0, 0);
		getBranchPositions(positions, 1, start, 90, 0);
		return positions;
	}
	
	public ArrayList<Point> getLeafPos() {
		return leaves;
	}
	
	// Recursively step through the part of the genome that indicates the structure of the plant.
	// Add the starting and ending points of each branch to the given ArrayList.
	// Add the positions of the leaves to the stored ArrayList.
	public void getBranchPositions(ArrayList<Point> positions, int index, Point startPoint, int theta, int LR) {
		String gene = genome.substring(13, 43);
		String leafGene = genome.substring(43);
		int leftIndex = (index * 2);
		int rightIndex = (index * 2) + 1;
		double x, y, thetaR;
		
		switch(LR) {
		case -1:
			theta += stemAngle();
			theta = theta % 360;
			break;
		case 1:
			theta -= stemAngle();
			if (theta < 0) {
				theta = 360 + theta;
			}
			break;
		}
		
		thetaR = theta * (Math.PI / 180);
		x = Math.cos(thetaR);
		y = Math.sin(thetaR);
		
		if (Math.abs(x) < 0.00001) {
			x = 0;
		}
		if (Math.abs(y) < 0.00001) {
			y = 0;
		}
		if (1 - Math.abs(x) < 0.00001) {
			if (x < 1) {
				x = -1;
			}
			else {
				x = 1;
			}
		}
		if (1 - Math.abs(y) < 0.00001) {
			if (y < 1) {
				y = -1;
			}
			else {
				y = 1;
			}
		}
		
		x *= stemLength();
		y *= stemLength();
		x += startPoint.x;
		y += startPoint.y;
		Point endPoint = new Point(x, y);
		positions.add(startPoint);
		positions.add(endPoint);
		
		if (leafGene.charAt(index - 1) == '1' && (y - (leafSize()/2)) > 0) {
			leaves.add(endPoint);
		}
		
		if (leftIndex <= 30 && gene.charAt(leftIndex - 2) == '1' && endPoint.y > 0) {
			getBranchPositions(positions, leftIndex, endPoint, theta, -1);
		}
		if (rightIndex <= 31 && gene.charAt(rightIndex - 2) == '1' && endPoint.y > 0) {
			getBranchPositions(positions, rightIndex, endPoint, theta, 1);
		}
	}
}