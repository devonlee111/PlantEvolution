import java.util.ArrayList;

public class Plant implements Comparable<Plant>{
	// Genome is represented by a 43 bit string.
	// First gene from bits 0 - 2 represents stem length
	// Second gene from bits 3 - 6 represents leaf size
	// Third gene from bits 7 - 13 represents angle of new stems
	// Fourth gene from bits 14 - 42 represents a array implemented heap for structure of plant
	// Fifth gene from bits 43 - 57 represents whether an intersection of 2 branches contains a leaf
	ArrayList<Point> leafPos = new ArrayList<Point>();
	private String genome = "";
	public double fitness;
	
	// Create a new random plant
	public Plant() {
		for (int i = 0; i < 43; i++) {
			genome += Integer.toString((int)(Math.random() * 2));
		}
		fitness = Math.random();
		System.out.println(genome.substring(14, 43));
	}
	
	public Plant(String genome) {
		this.genome = genome;
		System.out.println(genome);
	}
	
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
	
	// Produce a new plant by combining genomes and mutating
	public Plant reproduce(Plant mate) {
		String newGenome = "";
		String genome2 = mate.genome;
		String gene11, gene12, gene21, gene22, gene31, gene32, gene41, gene42, gene51, gene52;
		gene11 = genome.substring(0, 3);
		gene21 = genome.substring(3, 7);
		gene31 = genome.substring(7, 14);
		gene41 = genome.substring(14, 43);
		gene51 = genome.substring(43);
		
		gene12 = mate.genome.substring(0, 3);
		gene22 = mate.genome.substring(3, 7);
		gene32 = mate.genome.substring(7, 14);
		gene42 = mate.genome.substring(14, 43);
		gene52 = mate.genome.substring(43);
		
		newGenome += combineGenes(gene11, gene12);
		newGenome += combineGenes(gene21, gene22);
		newGenome += combineGenes(gene31, gene32);
		newGenome += combineGenes(gene41, gene42);
		newGenome += combineGenes(gene51, gene52);
		char[] genomeArr = newGenome.toCharArray();
		for (int i = 0; i < 58; i++) {
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
		return Integer.parseInt(genome.substring(0, 3), 2) + 3;
	}
	
	public int leafSize() {
		return Integer.parseInt(genome.substring(3,  7), 2) + 1;
	}
	
	public int stemAngle() {
		return Integer.parseInt(genome.substring(7, 14), 2) + 1;
	}
	
	public ArrayList<Point> processPlant() {
		ArrayList<Point> positions = new ArrayList<Point>();
		Point start = new Point(0, 0);
		getBranchPositions(positions, 1, start, 90, 0);
		return positions;
	}
	
	public void getBranchPositions(ArrayList<Point> positions, int index, Point startPoint, int theta, int LR) {
		//System.out.print(theta + " | ");
		String gene = genome.substring(14, 43);
		System.out.println(index);
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
		//System.out.println(LR + " | " + theta);
		thetaR = theta * (Math.PI / 180);
		x = Math.cos(thetaR);
		y = Math.sin(thetaR);
		//System.out.println(x + ", " + y);
		
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
		//System.out.println(x + ", " + y);
		x += startPoint.x;
		y += startPoint.y;
		//System.out.println("starting point is " + startPoint.x + ", " + startPoint.y + " | end point is " + x + ", " + y);
		Point endPoint = new Point(x, y);
		positions.add(startPoint);
		positions.add(endPoint);
		if (leftIndex <= 14 && gene.charAt(leftIndex - 2) == '1') {
			//System.out.println("left " + leftIndex);
			getBranchPositions(positions, leftIndex, endPoint, theta, -1);
		}
		if (rightIndex <= 15 && gene.charAt(rightIndex - 2) == '1') {
			//System.out.println("right " + rightIndex);
			getBranchPositions(positions, rightIndex, endPoint, theta, 1);
		}
	}
}