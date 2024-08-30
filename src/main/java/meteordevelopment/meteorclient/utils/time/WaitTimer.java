/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package meteordevelopment.meteorclient.utils.time;

public final class WaitTimer {
	public long time;

	public WaitTimer() {
		this.time = (System.nanoTime() / 1000000l);
	}

	public boolean hasTimeElapsed(long time, boolean reset) {
		if(time < 150) {
			if (((double)getTime()) >= ((double)time) / 1.63d) {
				if (reset) {
					reset();
				}
				return true;
			}
		}
		else {
			if (getTime() >= time) {
				if (reset) {
					reset();
				}
				return true;
			}
		}

		return false;
	}

	public long getTime() {
		return System.nanoTime() / 1000000l - this.time;
	}

	public void reset() {
		this.time = (System.nanoTime() / 1000000l);
	}
}
