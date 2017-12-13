package com.main;

public class Predator extends Bird {
	
	@Override
	public void setSpeed (Vector newSpeed) {
			
			if (newSpeed.x() > Flock.vMaxPredator || newSpeed.x() < -Flock.vMaxPredator) {
				this.speed.setX(Flock.vMaxPredator);
			}
			else {
				this.speed.setX(newSpeed.x());
			}
			
			if (newSpeed.y() > Flock.vMaxPredator || newSpeed.y() < -Flock.vMaxPredator) {
				this.speed.setY(Flock.vMaxPredator);
			}
			else {
				this.speed.setY(newSpeed.y());
			}
			
			
		}
	
}
