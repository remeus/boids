package com.main;

//Individual behaviour
public class Bird implements Cloneable {
	
	protected Vector pos = new Vector(); //Position on the map
	protected Vector speed = new Vector(); //Absolute speed
	
	//Create a new bird randomly with -vMax < v < -vMin or vMin < v < vMax
	public Bird() {
		
		this.iniBird();
		
	}
	
	
	//Initialize
	public void iniBird() {
		this.setPos(new Vector(Math.random() * Window.panelWidth(), Math.random() * Window.panelHeight()));
		boolean positiveX = (Math.random() >= 0.5);
		double speedX = Math.random() * (Flock.vMax - Flock.vMin) + Flock.vMin;
		if (!positiveX)
			speedX = -speedX;
		boolean positiveY = (Math.random() >= 0.5);
		double speedY = Math.random() * (Flock.vMax - Flock.vMin) + Flock.vMin;
		if (!positiveY)
			speedY = -speedY;
		this.setSpeed(new Vector(speedX, speedY));
	}
	
	
	//Difference vector with another bird
	public Vector distance(Bird b) {
		return this.pos().add(b.pos().multiply(-1));
	}
	
	public boolean compare(Bird b) {
		if (b.pos().equals(this.pos()) && b.speed().equal(this.speed))
			return true;
		else
			return false;
	}
	
	@Override
	public Bird clone() {
		Bird bird = null;
        try {
            bird = (Bird)super.clone();
            bird.pos = pos.clone();
            bird.speed = speed.clone();
        }
        catch (CloneNotSupportedException e) {
            // This should never happen
        }
        return bird;
    }
	
	//Average speed with another vector
	public static Vector averageSpeed(Bird[] birds) {
		Vector[] speeds = new Vector[birds.length];
		for (int i = 0 ; i < birds.length ; i++) {
			speeds[i] = birds[i].speed().clone();
		}
		return Vector.average(speeds);
	}
	
	//Average position between birds
	public static Vector averagePos(Bird[] birds) {
		Vector[] pos = new Vector[birds.length];
		for (int i = 0 ; i < birds.length ; i++) {
			pos[i] = birds[i].pos().clone();
		}
		return Vector.average(pos);
	}
	
	
	//Getters
	
	public Vector pos() {
		return this.pos;
	}
	
	public Vector speed() {
		return this.speed;
	}
	
	//Setters
	
	public void setPos (Vector newPos) {
		
		if (newPos.x() < Window.panelWidth() && newPos.x() > -Flock.birdRadius())
			this.pos.setX(newPos.x());
		else if (newPos.x() >= Window.panelWidth())
			this.pos.setX(-Flock.birdRadius() + 1);
		else if (newPos.x() <= -Flock.birdRadius())
			this.pos.setX(Window.panelWidth() - 1);
		
		if (newPos.y() < Window.panelHeight() && newPos.y() > -Flock.birdRadius())
			this.pos.setY(newPos.y());
		else if (newPos.y() >= Window.panelHeight())
			this.pos.setY(-Flock.birdRadius() + 1);
		else if (newPos.y() <= -Flock.birdRadius())
			this.pos.setY(Window.panelHeight() - 1);
		
	}
	
	public void setSpeed (Vector newSpeed) {
		
		if (newSpeed.x() > Flock.vMax || newSpeed.x() < -Flock.vMax) {
			this.speed.setX(Flock.vMax);
		}
		else {
			this.speed.setX(newSpeed.x());
		}
		
		if (newSpeed.y() > Flock.vMax || newSpeed.y() < -Flock.vMax) {
			this.speed.setY(Flock.vMax);
		}
		else {
			this.speed.setY(newSpeed.y());
		}
		
		
	}
	

}
