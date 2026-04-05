#include <cassert>
#include <cmath>

#include "frcsim/gamepiece/ball_physics.hpp"

int main() {
    frcsim::BallPhysicsSim3D::Config config;
    config.gravity_mps2 = frcsim::Vector3(0.0, 0.0, -9.81);
    config.ground_height_m = 0.0;
    config.rolling_friction_per_s = 1.0;
    config.min_bounce_speed_mps = 0.05;

    frcsim::BallPhysicsSim3D::BallProperties properties;
    properties.mass_kg = 0.27;
    properties.radius_m = 0.09;
    properties.drag_coefficient = 0.47;
    properties.reference_area_m2 = 0.025;
    properties.restitution = 0.6;

    frcsim::BallPhysicsSim3D sim(config, properties);

    frcsim::BallPhysicsSim3D::BallState initial;
    initial.position_m = frcsim::Vector3(0.0, 0.0, properties.radius_m);
    sim.setState(initial);

    // Pickup when intake is close enough.
    sim.setCarrierPose(frcsim::Vector3(1.0, 0.0, 0.5), frcsim::Vector3(2.0, 0.0, 0.0));
    frcsim::BallPhysicsSim3D::PickupRequest request;
    request.intake_position_m = frcsim::Vector3(0.0, 0.0, properties.radius_m);
    request.capture_radius_m = 0.15;
    request.carry_offset_m = frcsim::Vector3(0.2, 0.0, 0.1);

    const bool picked_up = sim.requestPickup(request);
    assert(picked_up);
    assert(sim.state().held);

    // While held, ball should move with carrier and offset.
    sim.setCarrierPose(frcsim::Vector3(2.0, 0.0, 0.8), frcsim::Vector3(1.5, 0.0, 0.0));
    sim.step(0.02);
    assert(std::fabs(sim.state().position_m.x - 2.2) < 1e-9);
    assert(std::fabs(sim.state().position_m.z - 0.9) < 1e-9);

    // Shoot transitions to free-flight at muzzle state.
    const frcsim::Vector3 muzzle_position(2.2, 0.0, 1.0);
    const frcsim::Vector3 muzzle_velocity(8.0, 0.0, 5.0);
    const frcsim::Vector3 muzzle_spin(0.0, 35.0, 0.0);
    sim.shoot(muzzle_position, muzzle_velocity, muzzle_spin);
    assert(!sim.state().held);

    const frcsim::Vector3 start_position = sim.state().position_m;
    for (int i = 0; i < 30; ++i) {
        sim.step(0.01);
    }

    // Should have moved downrange and still be above floor after a short flight.
    assert(sim.state().position_m.x > start_position.x + 1.0);
    assert(sim.state().position_m.z > properties.radius_m);

    // Continue long enough for ground contact and bounce/roll behavior.
    for (int i = 0; i < 220; ++i) {
        sim.step(0.01);
    }

    assert(sim.state().position_m.z >= properties.radius_m - 1e-9);
    // Ground contact should eventually reduce vertical speed and keep it finite.
    assert(std::isfinite(sim.state().velocity_mps.z));

    return 0;
}
