#include <cassert>
#include <cmath>

#include "frcsim/gamepiece/ball_gamepiece_sim.hpp"

int main() {
    frcsim::BallGamepieceSim::FieldConfig field;
    field.net_boundary_user_id = 2026;

    frcsim::BallGamepieceSim sim(field);

    frcsim::BallGamepieceSim::RobotState robot_a;
    robot_a.position_m = frcsim::Vector3(1.0, 2.0, 0.0);
    robot_a.velocity_mps = frcsim::Vector3(3.0, 0.0, 0.0);
    robot_a.yaw_rad = 0.0;
    robot_a.intake_enabled = true;
    robot_a.intake_radius_m = 0.35;

    frcsim::BallGamepieceSim::RobotState robot_b;
    robot_b.position_m = frcsim::Vector3(1.7, 2.0, 0.0);
    robot_b.velocity_mps = frcsim::Vector3(-1.0, 0.0, 0.0);
    robot_b.yaw_rad = 0.0;

    const std::size_t robot_a_id = sim.addRobot(robot_a);
    const std::size_t robot_b_id = sim.addRobot(robot_b);

    const auto default_props = frcsim::BallGamepieceSim::season2026BallProperties();
    assert(std::fabs(default_props.mass_kg - 0.216) < 1e-9);
    assert(std::fabs(default_props.radius_m - 0.075) < 1e-9);

    // Add a cluster of loose balls near robot A to verify plowing + intake with many objects.
    for (int i = 0; i < 8; ++i) {
        frcsim::BallPhysicsSim3D::BallState state;
        state.position_m = frcsim::Vector3(1.35 + 0.08 * i, 2.0 + ((i % 2 == 0) ? 0.03 : -0.03), 0.075);
        sim.addBall(state, frcsim::BallGamepieceSim::season2026BallConfig(),
                    frcsim::BallGamepieceSim::season2026BallProperties());
    }

    assert(sim.countBalls() == 8);

    // Create a net boundary (user_id 2026) where scored balls should fall down.
    frcsim::EnvironmentalBoundary net;
    net.type = frcsim::BoundaryType::kBox;
    net.position_m = frcsim::Vector3(7.0, 2.0, 1.6);
    net.half_extents_m = frcsim::Vector3(0.35, 0.35, 0.45);
    net.user_id = 2026;
    net.is_active = true;
    sim.addFieldElement(net);

    // Add a rigid wall plane for field element bounce behavior.
    frcsim::EnvironmentalBoundary wall;
    wall.type = frcsim::BoundaryType::kPlane;
    wall.position_m = frcsim::Vector3(4.0, 0.0, 0.0);
    wall.orientation = frcsim::Quaternion::fromAxisAngle(frcsim::Vector3::unitY(), -1.57079632679);
    wall.restitution = 0.5;
    wall.friction_coefficient = 0.15;
    wall.is_active = true;
    sim.addFieldElement(wall);

    // Step a short time: robot A should pick up one ball and plow others.
    for (int i = 0; i < 15; ++i) {
        sim.step(0.02);
    }

    assert(sim.robots()[robot_a_id].carried_ball_index != frcsim::BallGamepieceSim::kNoBall);

    // Robot-to-robot contact should impede relative velocity.
    const double relative_speed_before =
        (robot_a.velocity_mps - robot_b.velocity_mps).norm();
    const double relative_speed_after =
        (sim.robots()[robot_a_id].velocity_mps - sim.robots()[robot_b_id].velocity_mps).norm();
    assert(relative_speed_after < relative_speed_before + 1e-6);

    // Fire carried ball with scalar speed and exit velocity override from robot-relative launch offset.
    frcsim::BallGamepieceSim::FireCommand fire;
    fire.launch_offset_m = frcsim::Vector3(0.45, 0.0, 0.55);
    fire.yaw_offset_rad = 0.0;
    fire.pitch_rad = 0.85;
    fire.mechanism_speed_mps = 12.0;
    fire.estimated_exit_velocity_mps = 16.5;
    fire.spin_radps = frcsim::Vector3(0.0, 40.0, 0.0);

    const bool fired = sim.fireBall(robot_a_id, fire);
    assert(fired);

    // March long enough for one of the balls to interact with net and for field/walls/gravity behavior.
    for (int i = 0; i < 240; ++i) {
        sim.step(0.02);
    }

    // Count API stays stable and net scoring can occur.
    assert(sim.countBalls() == 8);
    assert(sim.countScoredBalls() <= sim.countBalls());

    // Running into field perimeter should clamp and stop robot, not move the field.
    sim.robots()[robot_a_id].position_m = frcsim::Vector3(16.50, 2.0, 0.0);
    sim.robots()[robot_a_id].velocity_mps = frcsim::Vector3(2.0, 0.0, 0.0);
    sim.step(0.02);
    assert(sim.robots()[robot_a_id].velocity_mps.x == 0.0);

    // At least one loose ball should have moved from initial placement due to plowing/contacts.
    bool some_ball_moved = false;
    for (const auto& ball : sim.balls()) {
        if (ball.sim.state().position_m.x > 2.2) {
            some_ball_moved = true;
            break;
        }
    }
    assert(some_ball_moved);

    return 0;
}
