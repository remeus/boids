package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

//Collective behaviour
public class Flock extends JPanel {
	
	//Birds
	protected int nbBirds = 200; //300
	protected Bird[] birds = new Bird[this.nbBirds];
	protected static double birdRadius = 10; //Graphical size
	
	//Obstacles
	protected int nbObstacles = 0;
	List<Obstacle> obstacles = new ArrayList<Obstacle>();
	protected static double obstacleRadius = 10; //Graphical size
	
	//Predators
	protected int nbPredators = 0;
	List<Predator> predators = new ArrayList<Predator>();
	protected static double predatorRadius = 10; //Graphical size
	
	//Speed
	protected static double vMin = 1; //Minimum speed for a bird
	protected static double vMax = 4; //Maximum speed for a bird
	protected static double vMaxPredator = 10; //Maximum speed for a predator
	
	//Radius
	protected static double neighborCohesionRadius = 50; //Influence diameter for cohesion
	protected static double neighborSeparationRadius = 30; //Influence diameter for separation
	protected static double neighborAlignmentRadius = 45; //Influence diameter for alignment
	protected static double neighborObstacleRadius = 50; //Influence diameter of an obstacle
	protected static double neighborPredatorChaseRadius = 1000;  //Influence diameter for predator chasing
	protected static double neighborPredatorFleeRadius = 50;  //Influence diameter for bird fleeing
	
	//Weight for the different forces
	protected static double separationWeightIni = 4; //Separation weight 0.55
	protected static double alignmentWeightIni = 0.05; //Alignment weight 0.055
	protected static double cohesionWeightIni = 0.3; //Cohesion weight 0.55
	
	protected static double separationWeight = Flock.separationWeightIni;
	protected static double alignmentWeight = Flock.alignmentWeightIni;
	protected static double cohesionWeight = Flock.cohesionWeightIni;
	
	protected static double obstacleWeight = 3.5; //Separation coefficient for an obstacle 5
	protected static double predatorWeight = 3; //Separation coefficient (for bird) and cohesion one (for predator) 5
	protected static double predatorChaseWeight = 3.5; //Separation coefficient (for bird) and cohesion one (for predator) 5
	
	//Creation of the instances
	public Flock() {
		creationBirds();
		creationObstacles();
		creationPredators();
	}

	//Paint the map and components
	public void paintComponent(Graphics g) {
		
		//Reset
		super.paintComponent(g);
		
		//Display obstacles
		g.setColor(Color.BLUE);
		for (Obstacle obstacle : obstacles) {
			g.fillOval((int) obstacle.pos().x(), (int) obstacle.pos().y(), (int) Flock.obstacleRadius(), (int) Flock.obstacleRadius());
		}
		
		//Display birds & predators
		display(this.birds, g, Color.BLACK);
		display(this.predators.toArray(new Predator[nbPredators]), g, Color.RED);
		
	}
	
	//Display birds & predators
	public void display(Bird[] birdsToDisplay, Graphics g, Color c) {
		Bird[] birdsAfterMoving = new Bird[birdsToDisplay.length]; //We need to update all the birds at the same time because of interdependencies
		for (int i = 0 ; i < birdsToDisplay.length ; i++) {
			Bird bird = birdsToDisplay[i];
			//Display bird's body
			g.setColor(c);
			g.fillOval((int) bird.pos().x(), (int) bird.pos().y(), (int) Flock.birdRadius(), (int) Flock.birdRadius());
			g.setColor(Color.white);
			g.fillOval((int) (bird.pos().x() + Flock.birdRadius()/3), (int) (bird.pos().y() + Flock.birdRadius()/4), (int) Flock.birdRadius()/3, (int) Flock.birdRadius()/3);
			g.setColor(Color.black);
			//Display bird's beak (orientation)
			g.drawLine(
					(int) (bird.pos().x() + Flock.birdRadius()/2), 
					(int) (bird.pos().y() + Flock.birdRadius()/2), 
					(int) (bird.pos().x() + Flock.birdRadius()/2 + bird.speed().x() / bird.speed().norm() * Flock.birdRadius()), 
					(int) (bird.pos().y() + Flock.birdRadius()/2 + bird.speed().y() / bird.speed().norm() * Flock.birdRadius())
			);
			//Move the bird
			if (bird instanceof Predator) {
				birdsAfterMoving[i] = (Predator)bird.clone();
				this.movePredator((Predator)birdsAfterMoving[i]);
			}
			else {
				birdsAfterMoving[i] = (Bird)bird.clone();
				this.move(birdsAfterMoving[i]);
			}
		}
		//Update all the positions and speeds
		for (int i = 0 ; i < birdsToDisplay.length ; i++) {
			if (birdsAfterMoving[i] instanceof Predator) {
				this.predators.set(i, (Predator)birdsAfterMoving[i].clone());
			}
			else
				birdsToDisplay[i] = (Bird)birdsAfterMoving[i].clone();
		}
	}
	
	
	//Move a bird
	public void move(Bird b) {
		
		//Neighbor birds
		Bird[] neighborsCohesion = this.neighbors(b, Flock.neighborCohesionRadius);
		Bird[] neighborsSeparation = this.neighbors(b, Flock.neighborSeparationRadius);
		Bird[] neighborsAlignment = this.neighbors(b, Flock.neighborAlignmentRadius);
		//Forces between birds
		Vector cohesionForce = calculateCohesionForce(b, neighborsCohesion).multiply(Flock.cohesionWeight);
		Vector separationForce = calculateSeparationForce(b, neighborsSeparation).multiply(Flock.separationWeight);
		Vector alignmentForce = calculateAlignmentForce(b, neighborsAlignment).multiply(Flock.alignmentWeight);
		//Neighbor obstacles
		Obstacle[] neighborObstacles = this.neighborObstacles(b);
		Vector obstacleForce = calculateSeparationForce(b, neighborObstacles).multiply(Flock.obstacleWeight);
		//Neighbor predators
		Predator[] neighborPredators = this.neighborPredators(b, Flock.neighborPredatorFleeRadius);
		Vector predatorForce = calculateSeparationPredatorForce(b, neighborPredators).multiply(Flock.predatorWeight);
		//New speed
		Vector newSpeed = new Vector();
		newSpeed.set(newSpeed.add(obstacleForce)); //Repulsed by close obstacles
		if (neighborPredators.length == 0) {
			System.out.println("Chill");
			newSpeed.set(newSpeed.add(b.speed()));
			newSpeed.set(newSpeed.add(cohesionForce)); //Attracted by close birds
			newSpeed.set(newSpeed.add(separationForce)); //Repulsed by close birds
			newSpeed.set(newSpeed.add(alignmentForce)); //Align on close birds
		}
		else {
			newSpeed.set(newSpeed.add(predatorForce)); //Repulsed by close predators
			System.out.println("Danger");
		}
		b.setSpeed(newSpeed);
		//New position
		Vector newPos = new Vector();
		newPos.set(newPos.add(b.pos()));
		newPos.set(newPos.add(newSpeed));
		b.setPos(newPos);
		
	}
	
	
	//Move a predator
	public void movePredator(Predator p) {
		
		//Closest bird = target
		Bird[] neighborBirds = this.neighbors(p, Flock.neighborPredatorChaseRadius);
		Bird[] closestBird = {this.closestBird(p, neighborBirds)};
		//Cohesion force between bird and predator
		Vector cohesionForce = calculateCohesionForce(p, closestBird).multiply(Flock.predatorChaseWeight);
		//Neighbor obstacles
		Obstacle[] neighborObstacles = this.neighborObstacles(p);
		//Forces produces by obstacles
		Vector obstacleForce = calculateSeparationForce(p, neighborObstacles).multiply(Flock.obstacleWeight);
		//New speed
		Vector newSpeed = new Vector();
		newSpeed.set(newSpeed.add(cohesionForce));
		newSpeed.set(newSpeed.add(obstacleForce));
		p.setSpeed(newSpeed);
		//New position
		Vector newPos = new Vector();
		newPos.set(newPos.add(p.pos()));
		newPos.set(newPos.add(p.speed()));
		p.setPos(newPos);
		//Kill the bird
		if (p.distance(closestBird[0]).norm() < Flock.birdRadius)
			kill(closestBird[0]);
	}
	
	
	//Calculate cohesion force
	public Vector calculateCohesionForce(Bird b, Bird[] neighbors) {
		Vector force = new Vector();
		if (neighbors.length > 0) {
			Vector averagePos = Bird.averagePos(neighbors);
			force.set(averagePos.add(b.pos().multiply(-1)));
			force.set(force.normalize());
		}
		return force;
	}
	
	//Calculate separation force
	public Vector calculateSeparationForce(Bird b, Bird[] neighbors) {
		Vector force = new Vector();
		int n = neighbors.length;
		double[] distance = new double[n];
		for(int i = 0 ; i < n ; i++) {
			distance[i] = b.distance(neighbors[i]).norm();
			if (distance[i] > 0) {
				Vector separation = b.pos().add(neighbors[i].pos().multiply(-1));
				separation.set(separation.normalize());
				separation.set(separation.multiply(1/distance[i]));
				force.set(force.add(separation));
			}
		}
		return force;
	}
	
	//Calculate alignment force
	public Vector calculateAlignmentForce(Bird b, Bird[] neighbors) {
		Vector force = new Vector();
		if (neighbors.length > 0) {
			Vector averageSpeed = Bird.averageSpeed(neighbors);
			force = averageSpeed.add(b.speed().multiply(-1));
			force.set(force.normalize());
		}
		return force;
	}
	
	//Calculate separation force from a predator
	public Vector calculateSeparationPredatorForce(Bird b, Bird[] neighbors) {
		Vector force = new Vector();
		if (neighbors.length > 0) {
			Vector averagePos = Bird.averagePos(neighbors);
			force.set(averagePos.add(b.pos().multiply(-1)));
			force.set(force.multiply(-1));
			force.set(force.normalize());
		}
		System.out.println(force.norm());
		return force;
	}
	
	
	//Neighborhood of a given bird
	public Bird[] neighbors(Bird b, double distance) {
		List<Bird> neighbors = new ArrayList<Bird>();
		for (int i = 0 ; i < this.nbBirds ; i++) {
			double distanceNeighbor = b.distance(this.birds[i]).norm();
			if (distanceNeighbor <= distance && distanceNeighbor >= 0) {
				if (!b.compare(this.birds[i])) { //A bird is not a neighbor of itself
					neighbors.add(this.birds[i]);
				}
			}
		}
		return neighbors.toArray(new Bird[neighbors.size()]);
	}
	
	//Closest bird to be chased by a predator
	public Bird closestBird(Predator p, Bird[] neighbors) {
		if (neighbors.length == 0)
			return null;
		else {
			Bird closestBird = neighbors[0];
			double distance = p.distance(neighbors[0]).norm();
			for (int i = 1 ; i < neighbors.length ; i++) {
				double distanceNext = p.distance(neighbors[i]).norm();
				if (distanceNext < distance) {
					distance = distanceNext;
					closestBird = neighbors[i];
				}
			}
			return closestBird;
		}
	}
	
	//Predating neighborhood of a given bird
	public Predator[] neighborPredators(Bird b, double distance) {
		List<Predator> neighbors = new ArrayList<Predator>();
		for (int i = 0 ; i < this.nbPredators ; i++) {
			double distanceNeighbor = b.distance(this.predators.get(i)).norm();
			if (distanceNeighbor <= distance && distanceNeighbor >= 0) {
				neighbors.add(this.predators.get(i));
			}
		}
		return neighbors.toArray(new Predator[neighbors.size()]);
	}
	
	//Neighbor obstacles
	public Obstacle[] neighborObstacles(Bird b) {
		List<Obstacle> neighbors = new ArrayList<Obstacle>();
		for (int i = 0 ; i < this.nbObstacles ; i++) {
			double distanceNeighbor = b.distance(this.obstacles.get(i)).norm();
			if (distanceNeighbor <= Flock.neighborObstacleRadius && distanceNeighbor >= 0) {
				neighbors.add(this.obstacles.get(i));
			}
		}
		return neighbors.toArray(new Obstacle[neighbors.size()]);
	}
	
	//Reload bird
	public void kill(Bird b) {
		b.iniBird();
	}
	
	//Initialize birds
	public void creationBirds() {
		for (int i = 0 ; i < this.nbBirds ; i++) {
			this.birds[i] = new Bird();
		}
	}
	
	//Initialize obstacles
	public void creationObstacles() {
		for (int i = 0 ; i < this.nbObstacles ; i++) {
			this.obstacles.add(new Obstacle());
		}
	}
	
	//Initialize predators
	public void creationPredators() {
		for (int i = 0 ; i < this.nbPredators ; i++) {
			this.predators.add(new Predator());
		}
	}
	
	//Add one obstacle
	public void addObstacle() {
		this.nbObstacles++;
		this.obstacles.add(new Obstacle());
	}
	
	//Remove all obstacles
	public void removeObstacles() {
		this.nbObstacles = 0;
		this.obstacles.clear();
	}
	
	//Add one predator
	public void addPredator() {
		this.nbPredators++;
		this.predators.add(new Predator());
	}
	
	//Remove all predators
	public void removePredators() {
		this.nbPredators = 0;
		this.predators.clear();
	}
	
	//Getters
	
	public int nbBirds() {
		return this.nbBirds;
	}
	
	public Bird[] birds() {
		return this.birds;
	}
	
	public static double birdRadius() {
		return Flock.birdRadius;
	}
	
	public static double obstacleRadius() {
		return Flock.obstacleRadius;
	}
	
	public static double predatorRadius() {
		return Flock.predatorRadius;
	}
	
	//Setters
	
	public static void setSeparationWeight(double weight) {
		Flock.separationWeight = weight;
	}
	
	public static void setCohesionWeight(double weight) {
		Flock.cohesionWeight = weight;
	}
	
	public static void setAlignmentWeight(double weight) {
		Flock.alignmentWeight = weight;
	}
	
	
}
