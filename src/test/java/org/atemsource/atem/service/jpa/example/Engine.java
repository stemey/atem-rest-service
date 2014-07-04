package org.atemsource.atem.service.jpa.example;

import javax.persistence.Entity;

@Entity
public class Engine extends Feature {
	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	private int volume;
}
