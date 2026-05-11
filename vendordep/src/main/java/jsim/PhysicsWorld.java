// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import jsim.jni.JSimJNI;

/**
 * Thin Java wrapper around the native physics world implementation.
 */
public final class PhysicsWorld implements AutoCloseable {
	private long worldHandle;

	/**
	 * Creates a native physics world.
	 *
	 * @param fixedDtSeconds fixed simulation timestep in seconds
	 * @param enableGravity whether gravity is enabled for newly created bodies
	 */
	public PhysicsWorld(double fixedDtSeconds, boolean enableGravity) {
		JSimJNI.forceLoad();
		this.worldHandle = JSimJNI.createWorld(fixedDtSeconds, enableGravity);
		if (worldHandle == 0) {
			throw new IllegalStateException("Failed to create native PhysicsWorld");
		}
	}

	/**
	 * Creates a new body with the provided mass in kilograms.
	 *
	 * @param massKg the body mass in kilograms
	 * @return the created body handle
	 */
	public PhysicsBody createBody(double massKg) {
		ensureOpen();
		int index = JSimJNI.createBody(worldHandle, massKg);
		if (index < 0) {
			throw new IllegalStateException("Failed to create body");
		}
		return new PhysicsBody(this, index);
	}

	/**
	 * Creates a new ball in the world.
	 *
	 * @return the created ball handle
	 */
	public Ball createBall() {
		ensureOpen();
		int index = JSimJNI.createBall(worldHandle);
		if (index < 0) {
			throw new IllegalStateException("Failed to create ball");
		}
		return new Ball(this, index);
	}

	/**
	 * Sets the body's world-space position in meters.
	 *
	 * @param bodyIndex native body index
	 * @param xMeters x position in meters
	 * @param yMeters y position in meters
	 * @param zMeters z position in meters
	 */
	void setBodyPosition(int bodyIndex, double xMeters, double yMeters, double zMeters) {
		ensureOpen();
		int rc = JSimJNI.setBodyPosition(worldHandle, bodyIndex, xMeters, yMeters, zMeters);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body position: rc=" + rc);
		}
	}

	/**
	 * Sets the body's linear velocity in meters per second.
	 *
	 * @param bodyIndex native body index
	 * @param vxMetersPerSecond x velocity in meters per second
	 * @param vyMetersPerSecond y velocity in meters per second
	 * @param vzMetersPerSecond z velocity in meters per second
	 */
	void setBodyLinearVelocity(int bodyIndex, double vxMetersPerSecond, double vyMetersPerSecond,
			double vzMetersPerSecond) {
		ensureOpen();
		int rc = JSimJNI.setBodyLinearVelocity(worldHandle, bodyIndex, vxMetersPerSecond,
				vyMetersPerSecond, vzMetersPerSecond);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body linear velocity: rc=" + rc);
		}
	}

	/**
	 * Enables or disables gravity for the given body.
	 *
	 * @param bodyIndex native body index
	 * @param enabled true to enable gravity
	 */
	void setBodyGravityEnabled(int bodyIndex, boolean enabled) {
		ensureOpen();
		int rc = JSimJNI.setBodyGravityEnabled(worldHandle, bodyIndex, enabled);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body gravity enabled: rc=" + rc);
		}
	}

	/**
	 * Sets the body's collision filter.
	 *
	 * @param bodyIndex native body index
	 * @param collisionLayerBits collision layer bits
	 * @param collisionMaskBits collision mask bits
	 */
	void setBodyCollisionFilter(int bodyIndex, int collisionLayerBits, int collisionMaskBits) {
		ensureOpen();
		int rc = JSimJNI.setBodyCollisionFilter(worldHandle, bodyIndex, collisionLayerBits,
				collisionMaskBits);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body collision filter: rc=" + rc);
		}
	}

	/**
	 * Sets the body's aerodynamic sphere.
	 *
	 * @param bodyIndex native body index
	 * @param radiusMeters radius in meters
	 * @param dragCoefficient drag coefficient
	 */
	void setBodyAerodynamicSphere(int bodyIndex, double radiusMeters, double dragCoefficient) {
		ensureOpen();
		int rc = JSimJNI.setBodyAerodynamicSphere(worldHandle, bodyIndex, radiusMeters,
				dragCoefficient);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body collision sphere: rc=" + rc);
		}
	}

	/**
	 * Sets the body's aerodynamic box.
	 *
	 * @param bodyIndex native body index
	 * @param xMeters x dimension in meters
	 * @param yMeters y dimension in meters
	 * @param zMeters z dimension in meters
	 * @param dragCoefficient drag coefficient
	 */
	void setBodyAerodynamicBox(int bodyIndex, double xMeters, double yMeters, double zMeters,
			double dragCoefficient) {
		ensureOpen();
		int rc = JSimJNI.setBodyAerodynamicBox(worldHandle, bodyIndex, xMeters, yMeters,
				zMeters, dragCoefficient);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set body collision box: rc=" + rc);
		}
	}

	/**
	 * Sets the ball's world-space position in meters.
	 *
	 * @param ballIndex native ball index
	 * @param xMeters x position in meters
	 * @param yMeters y position in meters
	 * @param zMeters z position in meters
	 */
	void setBallPosition(int ballIndex, double xMeters, double yMeters, double zMeters) {
		ensureOpen();
		int rc = JSimJNI.setBallPosition(worldHandle, ballIndex, xMeters, yMeters, zMeters);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set ball position: rc=" + rc);
		}
	}

	/**
	 * Sets the ball's world-space linear velocity in meters per second.
	 *
	 * @param ballIndex native ball index
	 * @param vxMetersPerSecond x velocity in meters per second
	 * @param vyMetersPerSecond y velocity in meters per second
	 * @param vzMetersPerSecond z velocity in meters per second
	 */
	void setBallLinearVelocity(int ballIndex, double vxMetersPerSecond, double vyMetersPerSecond,
			double vzMetersPerSecond) {
		ensureOpen();
		int rc = JSimJNI.setBallLinearVelocity(worldHandle, ballIndex, vxMetersPerSecond,
				vyMetersPerSecond, vzMetersPerSecond);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set ball linear velocity: rc=" + rc);
		}
	}

	/**
	 * Gets the world position for the given body.
	 *
	 * @param bodyIndex native body index
	 * @return body position
	 */
	public Vec3 getBodyPosition(int bodyIndex) {
		double[] values = getBodyPositionArray(bodyIndex);
		return new Vec3(values[0], values[1], values[2]);
	}

	/**
	 * Gets the world position for the given body.
	 *
	 * @param bodyIndex native body index
	 * @return a length-3 array containing {x, y, z}
	 */
	public double[] getBodyPositionArray(int bodyIndex) {
		ensureOpen();
		double[] values = new double[3];
		int rc = JSimJNI.getBodyPosition(worldHandle, bodyIndex, values);
		if (rc != 0) {
			throw new IllegalStateException("Failed to get body position: rc=" + rc);
		}
		return values;
	}

	/**
	 * Gets the world linear velocity for the given body.
	 *
	 * @param bodyIndex native body index
	 * @return body linear velocity
	 */
	public Vec3 getBodyLinearVelocity(int bodyIndex) {
		double[] values = getBodyLinearVelocityArray(bodyIndex);
		return new Vec3(values[0], values[1], values[2]);
	}

	/**
	 * Gets the world linear velocity for the given body.
	 *
	 * @param bodyIndex native body index
	 * @return a length-3 array containing {vx, vy, vz}
	 */
	public double[] getBodyLinearVelocityArray(int bodyIndex) {
		ensureOpen();
		double[] values = new double[3];
		int rc = JSimJNI.getBodyLinearVelocity(worldHandle, bodyIndex, values);
		if (rc != 0) {
			throw new IllegalStateException("Failed to get body linear velocity: rc=" + rc);
		}
		return values;
	}

	/**
	 * Exports full body state blocks.
	 *
	 * <p>Layout per body is: [x, y, z, qw, qx, qy, qz, vx, vy, vz, wx, wy, wz].
	 *
	 * @param outState13 destination array sized for N*13 entries
	 * @return number of body blocks written
	 */
	public int getBodyState13Array(double[] outState13) {
		ensureOpen();
		int rc = JSimJNI.getBodyState13Array(worldHandle, outState13);
		if (rc < 0) {
			throw new IllegalStateException("Failed to get body state array: rc=" + rc);
		}
		return rc;
	}

	/**
	 * Gets the world position for the given ball.
	 *
	 * @param ballIndex native ball index
	 * @return ball position
	 */
	public Vec3 getBallPosition(int ballIndex) {
		double[] values = getBallPositionArray(ballIndex);
		return new Vec3(values[0], values[1], values[2]);
	}

	/**
	 * Gets the world position for the given ball.
	 *
	 * @param ballIndex native ball index
	 * @return a length-3 array containing {x, y, z}
	 */
	public double[] getBallPositionArray(int ballIndex) {
		ensureOpen();
		double[] values = new double[3];
		int rc = JSimJNI.getBallPosition(worldHandle, ballIndex, values);
		if (rc != 0) {
			throw new IllegalStateException("Failed to get ball position: rc=" + rc);
		}
		return values;
	}

	/**
	 * Gets the world linear velocity for the given ball.
	 *
	 * @param ballIndex native ball index
	 * @return ball linear velocity
	 */
	public Vec3 getBallLinearVelocity(int ballIndex) {
		double[] values = getBallLinearVelocityArray(ballIndex);
		return new Vec3(values[0], values[1], values[2]);
	}

	/**
	 * Gets the world linear velocity for the given ball.
	 *
	 * @param ballIndex native ball index
	 * @return a length-3 array containing {vx, vy, vz}
	 */
	public double[] getBallLinearVelocityArray(int ballIndex) {
		ensureOpen();
		double[] values = new double[3];
		int rc = JSimJNI.getBallLinearVelocity(worldHandle, ballIndex, values);
		if (rc != 0) {
			throw new IllegalStateException("Failed to get ball linear velocity: rc=" + rc);
		}
		return values;
	}

	/**
	 * Advances the simulation by one step.
	 */
	public void step() {
		step(1);
	}

	/**
	 * Advances the simulation by the requested number of steps.
	 *
	 * @param steps number of steps to advance
	 */
	public void step(int steps) {
		ensureOpen();
		int rc = JSimJNI.stepWorld(worldHandle, steps);
		if (rc != 0) {
			throw new IllegalStateException("Failed to step world: rc=" + rc);
		}
	}

	/**
	 * Applies a gravity vector to the world.
	 *
	 * @param gravity gravity vector in meters per second squared
	 */
	public void setGravity(Vec3 gravity) {
		setGravity(gravity.x(), gravity.y(), gravity.z());
	}

	/**
	 * Applies gravity components to the world.
	 *
	 * @param gxMetersPerSecondSquared x gravity in meters per second squared
	 * @param gyMetersPerSecondSquared y gravity in meters per second squared
	 * @param gzMetersPerSecondSquared z gravity in meters per second squared
	 */
	public void setGravity(double gxMetersPerSecondSquared, double gyMetersPerSecondSquared,
			double gzMetersPerSecondSquared) {
		ensureOpen();
		int rc = JSimJNI.setWorldGravity(worldHandle, gxMetersPerSecondSquared,
				gyMetersPerSecondSquared, gzMetersPerSecondSquared);
		if (rc != 0) {
			throw new IllegalStateException("Failed to set gravity: rc=" + rc);
		}
	}

	@Override
	public void close() {
		if (worldHandle != 0) {
			JSimJNI.destroyWorld(worldHandle);
			worldHandle = 0;
		}
	}

	private void ensureOpen() {
		if (worldHandle == 0) {
			throw new IllegalStateException("PhysicsWorld is closed");
		}
	}
}
