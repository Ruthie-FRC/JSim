package rensim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import rensim.jni.VendorJNI;

/**
 * Thin Java wrapper around the native physics world implementation.
 */
public final class PhysicsWorld implements AutoCloseable, WorldStateView {
	private long worldHandle;
	private int nextBodyId = 0;
	private final List<PhysicsBody> bodies = new ArrayList<>();
	private Vec3 gravity = new Vec3(0.0, 0.0, -9.81);
	private long stepCount = 0;
	private double accumulatedTimeSeconds = 0.0;
	private double fixedDtSeconds;

	/**
	 * Creates a native physics world.
	 *
	 * @param fixedDtSeconds fixed simulation timestep in seconds
	 * @param enableGravity whether gravity is enabled for newly created bodies
	 */
	public PhysicsWorld(double fixedDtSeconds, boolean enableGravity) {
		VendorJNI.forceLoad();
		this.fixedDtSeconds = fixedDtSeconds;
		this.worldHandle = VendorJNI.createWorld(fixedDtSeconds, enableGravity);
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
		int index = VendorJNI.createBody(worldHandle, massKg);
		if (index < 0) {
		this.gravity = Objects.requireNonNull(gravityMps2, "gravityMps2 cannot be null");
			throw new IllegalStateException("Failed to create body");
		}
		int bodyId = nextBodyId++;
		PhysicsBody body = new PhysicsBody(this, index, bodyId);
		bodies.add(body);
		return body;
	}

	/**
	 * Sets the world's gravity vector in meters per second squared.
	 *
	 * @param gravityMps2 the gravity vector in meters per second squared
	 */
	public void setGravity(Vec3 gravityMps2) {
		ensureOpen();
		int rc = VendorJNI.setWorldGravity(worldHandle, gravityMps2.x(), gravityMps2.y(), gravityMps2.z());
		if (rc != 0) {
			throw new IllegalStateException("Failed to set world gravity");
		}
	}

	/** Advances the simulation by one step. */
	public void step() {
		step(1);
	}

	/**
	 * Advances the simulation by the requested number of steps.
	 *
	 * @param steps the number of simulation steps to advance
	 */
	public void step(int steps) {
		ensureOpen();
		int rc = VendorJNI.stepWorld(worldHandle, steps);
		if (rc != 0) {
			throw new IllegalStateException("Failed to step world");
		stepCount += steps;
		accumulatedTimeSeconds += steps * fixedDtSeconds;
		}
	}

	void setBodyPosition(int bodyIndex, Vec3 positionMeters) {
		ensureOpen();
		int rc = VendorJNI.setBodyPosition(worldHandle, bodyIndex, positionMeters.x(), positionMeters.y(), positionMeters.z());
		if (rc != 0) {
			throw new IllegalArgumentException("Invalid body index for setBodyPosition");
		}
	}

	void setBodyLinearVelocity(int bodyIndex, Vec3 velocityMps) {
		ensureOpen();
		int rc = VendorJNI.setBodyLinearVelocity(worldHandle, bodyIndex, velocityMps.x(), velocityMps.y(), velocityMps.z());
		if (rc != 0) {
			throw new IllegalArgumentException("Invalid body index for setBodyLinearVelocity");
		}
	}

	void setBodyGravityEnabled(int bodyIndex, boolean enabled) {
		ensureOpen();
		int rc = VendorJNI.setBodyGravityEnabled(worldHandle, bodyIndex, enabled);
		if (rc != 0) {
			throw new IllegalArgumentException("Invalid body index for setBodyGravityEnabled");
		}
	}

	Vec3 bodyPosition(int bodyIndex) {
		ensureOpen();
		double[] out = new double[3];
		int rc = VendorJNI.getBodyPosition(worldHandle, bodyIndex, out);
		if (rc != 0) {
			throw new IllegalArgumentException("Invalid body index for getBodyPosition");
		}
		return new Vec3(out[0], out[1], out[2]);
	}

	Vec3 bodyLinearVelocity(int bodyIndex) {
		ensureOpen();
		double[] out = new double[3];
		int rc = VendorJNI.getBodyLinearVelocity(worldHandle, bodyIndex, out);
		if (rc != 0) {
			throw new IllegalArgumentException("Invalid body index for getBodyLinearVelocity");
		}
	@Override
	public int bodyCount() {
		ensureOpen();
		return bodies.size();
	}

	@Override
	public List<BodyStateView> bodies() {
		ensureOpen();
		List<BodyStateView> views = new ArrayList<>();
		for (PhysicsBody body : bodies) {
			Vec3 pos = bodyPosition(body.bodyIndex());
			Vec3 vel = bodyLinearVelocity(body.bodyIndex());
			views.add(new BodyStateImpl(body.id(), 1.0, pos, vel, true));
		}
		return Collections.unmodifiableList(views);
	}

	@Override
	public BodyStateView findBody(int bodyId) {
		ensureOpen();
		for (PhysicsBody body : bodies) {
			if (body.id() == bodyId) {
				Vec3 pos = bodyPosition(body.bodyIndex());
				Vec3 vel = bodyLinearVelocity(body.bodyIndex());
				return new BodyStateImpl(bodyId, 1.0, pos, vel, true);
			}
		}
		return null;
	}

	@Override
	public Vec3 gravity() {
		ensureOpen();
		return gravity;
	}

	@Override
	public long stepCount() {
		ensureOpen();
		return stepCount;
	}

	@Override
	public double accumulatedTimeSeconds() {
		ensureOpen();
		return accumulatedTimeSeconds;
	}

	@Override
	public FrameSnapshot captureFrame() {
		ensureOpen();
		List<FrameSnapshot.BodySnapshot> bodySnapshots = new ArrayList<>();
		for (PhysicsBody body : bodies) {
			Vec3 pos = bodyPosition(body.bodyIndex());
			Vec3 vel = bodyLinearVelocity(body.bodyIndex());
			bodySnapshots.add(new FrameSnapshot.BodySnapshot(body.id(), 1.0, pos, vel));
		}
		return new FrameSnapshot(stepCount, accumulatedTimeSeconds, Collections.unmodifiableList(bodySnapshots));
	}

		return new Vec3(out[0], out[1], out[2]);
	}

	/** Releases the native world handle. */
	@Override
	public void close() {
		if (worldHandle != 0) {
			VendorJNI.destroyWorld(worldHandle);
			worldHandle = 0;
		}
	}

	private void ensureOpen() {
		if (worldHandle == 0) {
			throw new IllegalStateException("PhysicsWorld is closed");
		}
	}
}