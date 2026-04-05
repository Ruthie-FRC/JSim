#pragma once

#include <algorithm>
#include <cmath>

#include "frcsim/math/integrators.hpp"
#include "frcsim/math/vector.hpp"

namespace frcsim {

class BallPhysicsSim3D {
  public:
    struct Config {
        Vector3 gravity_mps2{0.0, 0.0, -9.81};
        double air_density_kgpm3{1.225};
        double magnus_coefficient{1e-4};
        double ground_height_m{0.0};
        double rolling_friction_per_s{1.2};
        double min_bounce_speed_mps{0.1};
    };

    struct BallProperties {
        double mass_kg{0.27};
        double radius_m{0.12};
        double drag_coefficient{0.47};
        double reference_area_m2{0.045};
        double restitution{0.45};
    };

    struct BallState {
        Vector3 position_m{};
        Vector3 velocity_mps{};
        Vector3 spin_radps{};
        bool held{false};
    };

    struct PickupRequest {
        Vector3 intake_position_m{};
        double capture_radius_m{0.2};
        Vector3 carry_offset_m{};
    };

    BallPhysicsSim3D() = default;

    explicit BallPhysicsSim3D(const Config& config)
        : config_(config) {}

    BallPhysicsSim3D(const Config& config, const BallProperties& ball_properties)
        : config_(config), ball_properties_(ball_properties) {}

    const Config& config() const { return config_; }
    void setConfig(const Config& config) { config_ = config; }

    const BallProperties& ballProperties() const { return ball_properties_; }
    void setBallProperties(const BallProperties& props) { ball_properties_ = props; }

    const BallState& state() const { return state_; }
    void setState(const BallState& state) { state_ = state; }

    void setCarrierPose(const Vector3& carrier_position_m, const Vector3& carrier_velocity_mps = Vector3::zero()) {
        carrier_position_m_ = carrier_position_m;
        carrier_velocity_mps_ = carrier_velocity_mps;
    }

    bool requestPickup(const PickupRequest& pickup_request) {
        if (state_.held) {
            return true;
        }
        const double distance_m = (state_.position_m - pickup_request.intake_position_m).norm();
        if (distance_m > pickup_request.capture_radius_m) {
            return false;
        }

        state_.held = true;
        carry_offset_m_ = pickup_request.carry_offset_m;
        state_.position_m = pickup_request.intake_position_m + carry_offset_m_;
        state_.velocity_mps = carrier_velocity_mps_;
        state_.spin_radps = Vector3::zero();
        return true;
    }

    void release() {
        state_.held = false;
    }

    void shoot(const Vector3& muzzle_position_m, const Vector3& muzzle_velocity_mps,
               const Vector3& muzzle_spin_radps = Vector3::zero()) {
        state_.held = false;
        state_.position_m = muzzle_position_m;
        state_.velocity_mps = muzzle_velocity_mps;
        state_.spin_radps = muzzle_spin_radps;
    }

    void step(double dt_s) {
        if (dt_s <= 0.0) {
            return;
        }

        if (state_.held) {
            state_.position_m = carrier_position_m_ + carry_offset_m_;
            state_.velocity_mps = carrier_velocity_mps_;
            return;
        }

        const Vector3 drag_force_n =
            Vector3::dragForce(state_.velocity_mps, ball_properties_.drag_coefficient,
                               ball_properties_.reference_area_m2, config_.air_density_kgpm3);
        const Vector3 magnus_force_n = Vector3::magnusForce(state_.velocity_mps, state_.spin_radps,
                                                            config_.magnus_coefficient);
        const Vector3 accel_mps2 =
            config_.gravity_mps2 + (drag_force_n + magnus_force_n) * (1.0 / std::max(1e-9, ball_properties_.mass_kg));

        Integrator::integrateLinear(state_.position_m, state_.velocity_mps, accel_mps2, dt_s);
        resolveGroundContact(dt_s);
    }

  private:
    void resolveGroundContact(double dt_s) {
        const double floor_z = config_.ground_height_m + ball_properties_.radius_m;
        if (state_.position_m.z > floor_z) {
            return;
        }

        state_.position_m.z = floor_z;
        if (state_.velocity_mps.z < -config_.min_bounce_speed_mps) {
            state_.velocity_mps.z = -state_.velocity_mps.z * std::clamp(ball_properties_.restitution, 0.0, 1.0);
        } else {
            state_.velocity_mps.z = 0.0;
        }

        const double planar_speed = state_.velocity_mps.planarSpeed();
        if (planar_speed <= 1e-9) {
            state_.velocity_mps.x = 0.0;
            state_.velocity_mps.y = 0.0;
            return;
        }

        const double decay = std::max(0.0, 1.0 - config_.rolling_friction_per_s * dt_s);
        state_.velocity_mps.x *= decay;
        state_.velocity_mps.y *= decay;
    }

    Config config_{};
    BallProperties ball_properties_{};
    BallState state_{};

    Vector3 carrier_position_m_{};
    Vector3 carrier_velocity_mps_{};
    Vector3 carry_offset_m_{};
};

}  // namespace frcsim
