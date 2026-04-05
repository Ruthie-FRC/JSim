#include <cassert>
#include <cmath>
#include <vector>

#include "frcsim/mechanisms/shot_calculator.hpp"
#include "frcsim/mechanisms/turret_shooter_sim.hpp"

int main() {
    frcsim::ShotCalculator3D::Config config;
    config.min_distance_m = 1.0;
    config.max_distance_m = 6.0;
    config.phase_delay_s = 0.03;
    config.tof_scale = 1.0;
    config.recent_pose_band_m = 0.5;

    frcsim::ShotCalculator3D calculator(config);
    calculator.setLookupTable(std::vector<frcsim::ShotCalculator3D::TablePoint>{
        {1.0, 0.65, 12.0, 0.22},
        {3.0, 0.80, 16.0, 0.36},
        {6.0, 1.02, 22.0, 0.58},
    });

    const frcsim::Vector3 target(5.0, 0.0, 2.1);
    const frcsim::Vector3 shooter_origin(0.0, 0.0, 1.0);
    const frcsim::Vector3 robot_velocity(1.0, 0.4, 0.0);

    auto shot = calculator.calculateShot(shooter_origin, robot_velocity, target, 1.0);
    assert(shot.is_valid);
    assert(shot.flywheel_speed_mps > 15.0 && shot.flywheel_speed_mps < 22.5);
    assert(shot.hood_pitch_rad > 0.7 && shot.hood_pitch_rad < 1.05);

    // With lateral velocity, yaw should bias opposite the lookahead direction enough to differ from zero.
    assert(std::fabs(shot.turret_yaw_rad) > 1e-3);

    // Recent-shot blending path: second query near first pose should stay valid and finite.
    auto shot_blended = calculator.calculateShot(
        shooter_origin + frcsim::Vector3(0.04, 0.01, 0.0), robot_velocity, target, 1.15);
    assert(shot_blended.is_valid);
    assert(std::isfinite(shot_blended.hood_pitch_rad));
    assert(std::isfinite(shot_blended.flywheel_speed_mps));

    // Integration with turret differential math.
    frcsim::TurretShooterSim sim;
    const bool applied = sim.applyAim(shot.turret_yaw_rad, shot.hood_pitch_rad);
    assert(applied);

    const auto solved = sim.jointState();
    assert(std::fabs(solved.yaw_rad - shot.turret_yaw_rad) < 1e-6);
    assert(std::fabs(solved.pitch_rad - shot.hood_pitch_rad) < 1e-6);

    return 0;
}
