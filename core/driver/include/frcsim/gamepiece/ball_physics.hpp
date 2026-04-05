#pragma once

#include <algorithm>
#include <cmath>
#include <limits>

#include "frcsim/math/integrators.hpp"
#include "frcsim/math/vector.hpp"

namespace frcsim {

class BallPhysicsSim3D {
  public:
    struct Config {
        Vector3 gravity_mps2{0.0, 0.0, -9.81};
                double effective_gravity_scale{1.0};
        double air_density_kgpm3{1.225};
                double drag_scale{1.0};
        double magnus_coefficient{1e-4};
                double magnus_scale{1.0};
        double ground_height_m{0.0};
        double rolling_friction_per_s{1.2};
        double min_bounce_speed_mps{0.1};
                double max_substep_s{0.01};
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
        : config_(sanitizeConfig(config)) {}

    BallPhysicsSim3D(const Config& config, const BallProperties& ball_properties)
        : config_(sanitizeConfig(config)), ball_properties_(sanitizeBallProperties(ball_properties)) {}

    const Config& config() const { return config_; }
    void setConfig(const Config& config) { config_ = sanitizeConfig(config); }

    const BallProperties& ballProperties() const { return ball_properties_; }
    void setBallProperties(const BallProperties& props) { ball_properties_ = sanitizeBallProperties(props); }

    const BallState& state() const { return state_; }
    void setState(const BallState& state) {
        state_ = state;
        sanitizeState(state_);
    }

    void setCarrierPose(const Vector3& carrier_position_m, const Vector3& carrier_velocity_mps = Vector3::zero()) {
        carrier_position_m_ = carrier_position_m;
        carrier_velocity_mps_ = carrier_velocity_mps;
    }

    bool requestPickup(const PickupRequest& pickup_request) {
        if (state_.held) {
            return true;
        }
        const double capture_radius_m = sanitizeNonNegative(pickup_request.capture_radius_m, 0.2);
        const double distance_m = (state_.position_m - pickup_request.intake_position_m).norm();
        if (!std::isfinite(distance_m) || distance_m > capture_radius_m) {
            return false;
        }

        state_.held = true;
        carry_offset_m_ = pickup_request.carry_offset_m;
        state_.position_m = pickup_request.intake_position_m + carry_offset_m_;
        state_.velocity_mps = carrier_velocity_mps_;
        state_.spin_radps = Vector3::zero();
        sanitizeState(state_);
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
        sanitizeState(state_);
    }

    void step(double dt_s) {
        if (!std::isfinite(dt_s) || dt_s <= 0.0) {
            return;
        }

        sanitizeState(state_);

        if (state_.held) {
            state_.position_m = carrier_position_m_ + carry_offset_m_;
            state_.velocity_mps = carrier_velocity_mps_;
            sanitizeState(state_);
            return;
        }

        const double max_substep_s = std::clamp(config_.max_substep_s, 1e-4, 0.05);
        double remaining_s = dt_s;
        while (remaining_s > 0.0) {
            const double substep_s = std::min(remaining_s, max_substep_s);
            const Vector3 accel0_mps2 = computeAcceleration(state_.velocity_mps);
            const Vector3 mid_velocity_mps = state_.velocity_mps + accel0_mps2 * (0.5 * substep_s);
            const Vector3 accel_mid_mps2 = computeAcceleration(mid_velocity_mps);

            state_.velocity_mps += accel_mid_mps2 * substep_s;
            state_.position_m += mid_velocity_mps * substep_s;
            sanitizeState(state_);

            remaining_s -= substep_s;
        }

        resolveGroundContact(dt_s);
        sanitizeState(state_);
    }

  private:
    static bool finiteVector(const Vector3& v) {
        return std::isfinite(v.x) && std::isfinite(v.y) && std::isfinite(v.z);
    }

    static double sanitizeNonNegative(double value, double fallback) {
        if (!std::isfinite(value)) {
            return fallback;
        }
        return std::max(0.0, value);
    }

    static Config sanitizeConfig(const Config& config) {
        Config sanitized = config;
        if (!finiteVector(sanitized.gravity_mps2)) {
            sanitized.gravity_mps2 = Vector3(0.0, 0.0, -9.81);
        }
        sanitized.effective_gravity_scale = std::clamp(sanitizeNonNegative(sanitized.effective_gravity_scale, 1.0), 0.0, 5.0);
        sanitized.air_density_kgpm3 = std::clamp(sanitizeNonNegative(sanitized.air_density_kgpm3, 1.225), 0.0, 5.0);
        sanitized.drag_scale = std::clamp(sanitizeNonNegative(sanitized.drag_scale, 1.0), 0.0, 10.0);
        sanitized.magnus_coefficient = std::isfinite(sanitized.magnus_coefficient) ? sanitized.magnus_coefficient : 1e-4;
        sanitized.magnus_scale = std::clamp(sanitizeNonNegative(sanitized.magnus_scale, 1.0), 0.0, 10.0);
        sanitized.ground_height_m = std::isfinite(sanitized.ground_height_m) ? sanitized.ground_height_m : 0.0;
        sanitized.rolling_friction_per_s = sanitizeNonNegative(sanitized.rolling_friction_per_s, 1.2);
        sanitized.min_bounce_speed_mps = sanitizeNonNegative(sanitized.min_bounce_speed_mps, 0.1);
        sanitized.max_substep_s = std::clamp(sanitizeNonNegative(sanitized.max_substep_s, 0.01), 1e-4, 0.05);
        return sanitized;
    }

    static BallProperties sanitizeBallProperties(const BallProperties& props) {
        BallProperties sanitized = props;
        sanitized.mass_kg = std::max(1e-6, sanitizeNonNegative(sanitized.mass_kg, 0.27));
        sanitized.radius_m = std::max(1e-4, sanitizeNonNegative(sanitized.radius_m, 0.12));
        sanitized.drag_coefficient = std::clamp(sanitizeNonNegative(sanitized.drag_coefficient, 0.47), 0.0, 5.0);
        sanitized.reference_area_m2 = sanitizeNonNegative(sanitized.reference_area_m2,
                                                          std::acos(-1.0) * sanitized.radius_m * sanitized.radius_m);
        if (sanitized.reference_area_m2 <= 0.0) {
            sanitized.reference_area_m2 = std::acos(-1.0) * sanitized.radius_m * sanitized.radius_m;
        }
        sanitized.restitution = std::clamp(std::isfinite(sanitized.restitution) ? sanitized.restitution : 0.45, 0.0, 1.0);
        return sanitized;
    }

    static void sanitizeState(BallState& state) {
        if (!finiteVector(state.position_m)) {
            state.position_m = Vector3::zero();
        }
        if (!finiteVector(state.velocity_mps)) {
            state.velocity_mps = Vector3::zero();
        }
        if (!finiteVector(state.spin_radps)) {
            state.spin_radps = Vector3::zero();
        }
    }

    Vector3 computeAcceleration(const Vector3& velocity_mps) const {
        const Vector3 gravity_mps2 = config_.gravity_mps2 * config_.effective_gravity_scale;
        const Vector3 drag_force_n =
            Vector3::dragForce(velocity_mps, ball_properties_.drag_coefficient,
                               ball_properties_.reference_area_m2, config_.air_density_kgpm3) * config_.drag_scale;
        const Vector3 magnus_force_n =
            Vector3::magnusForce(velocity_mps, state_.spin_radps, config_.magnus_coefficient) * config_.magnus_scale;
        const Vector3 accel_mps2 =
            gravity_mps2 + (drag_force_n + magnus_force_n) * (1.0 / std::max(1e-9, ball_properties_.mass_kg));
        return finiteVector(accel_mps2) ? accel_mps2 : gravity_mps2;
    }

    void resolveGroundContact(double dt_s) {
        const double floor_z = config_.ground_height_m + ball_properties_.radius_m;
        if (!std::isfinite(floor_z) || state_.position_m.z > floor_z) {
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
