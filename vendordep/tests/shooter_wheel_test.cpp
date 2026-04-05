#include <cassert>

#include "frcsim/mechanisms/shooter_wheel.hpp"
#include "frcsim/mechanisms/turret_shooter_sim.hpp"

int main() {
    frcsim::ShooterWheelSim::WheelConfig wheel;
    wheel.radius_m = 0.050;
    wheel.inertia_kgm2 = 0.0025;
    wheel.viscous_friction_nm_per_radps = 0.0007;
    wheel.ball_coupling = 0.9;

    frcsim::ShooterWheelSim::MotorConfig high_torque;
    high_torque.free_speed_radps = 520.0;
    high_torque.stall_torque_nm = 3.2;

    frcsim::ShooterWheelSim::MotorConfig high_speed;
    high_speed.free_speed_radps = 760.0;
    high_speed.stall_torque_nm = 1.9;

    frcsim::ShooterWheelSim torque_sim(high_torque, wheel);
    frcsim::ShooterWheelSim speed_sim(high_speed, wheel);

    frcsim::ShooterWheelSim::ControlInput cmd;
    cmd.velocity_closed_loop = true;
    cmd.target_speed_radps = 400.0;
    cmd.velocity_kp = 0.06;

    for (int i = 0; i < 250; ++i) {
        torque_sim.step(0.01, cmd);
        speed_sim.step(0.01, cmd);
    }

    assert(torque_sim.angularSpeedRadps() > 0.0);
    assert(speed_sim.angularSpeedRadps() > 0.0);
    assert(torque_sim.estimatedExitVelocityMps() > 0.0);
    assert(speed_sim.estimatedExitVelocityMps() > 0.0);

    frcsim::TurretShooterSim turret;
    frcsim::BallPhysicsSim3D::BallState preload;
    preload.held = true;
    turret.ball().setState(preload);

    turret.shootWithWheel(speed_sim);
    assert(!turret.ball().state().held);
    assert(turret.ball().state().velocity_mps.norm() > 0.1);

    return 0;
}
