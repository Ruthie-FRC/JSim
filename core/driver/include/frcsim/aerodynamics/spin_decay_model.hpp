#pragma once

#include <algorithm>
#include <cmath>

#include "frcsim/math/vector.hpp"
#include "frcsim/math/matrix.hpp"
#include "frcsim/math/quaternion.hpp"

namespace frcsim {

/**
 * SpinDecayModel
 *
 * Applies angular damping to rigid bodies or gamepieces.
 * Works purely in angular velocity space, independent of orientation representation.
 */
class SpinDecayModel {
public:
    struct Config {
        // Linear damping (dominates at low speeds)
        double linear_damping = 0.05;

        // Quadratic damping (dominates at high speeds, air resistance)
        double quadratic_damping = 0.001;

        // Deadband threshold to zero-out tiny spin
        double deadband = 1e-2;

        // Optional anisotropic damping (per-axis scaling)
        // If identity, behaves isotropically
        Matrix3 axis_damping = Matrix3::Identity();
    };

    explicit SpinDecayModel(const Config& config = {})
        : config_(config) {}

    /**
     * Apply spin decay in world frame
     */
    Vector3 apply(const Vector3& angular_velocity, double dt) const {
        Vector3 damped = config_.axis_damping * angular_velocity;

        double omega_mag = damped.norm();

        if (omega_mag < config_.deadband) {
            return Vector3::Zero();
        }

        Vector3 direction = damped / omega_mag;

        double decay =
            config_.linear_damping * dt +
            config_.quadratic_damping * omega_mag * dt;

        double new_mag = omega_mag * (1.0 - decay);
        new_mag = std::max(0.0, new_mag);

        if (new_mag < config_.deadband) {
            return Vector3::Zero();
        }

        return direction * new_mag;
    }

    /**
     * Apply spin decay in BODY FRAME (this is where quaternions matter)
     *
     * Converts angular velocity into local frame,
     * applies anisotropic damping, then transforms back.
     */
    Vector3 applyBodyFrame(
        const Vector3& angular_velocity_world,
        const Quaternion& orientation,
        double dt
    ) const {
        // World -> body
        Matrix3 R = orientation.toRotationMatrix();
        Matrix3 R_inv = R.transpose();

        Vector3 omega_body = R_inv * angular_velocity_world;

        // Apply damping in body frame
        Vector3 omega_body_damped = apply(omega_body, dt);

        // Body -> world
        return R * omega_body_damped;
    }

    void applyInPlace(Vector3& angular_velocity, double dt) const {
        angular_velocity = apply(angular_velocity, dt);
    }

    const Config& config() const { return config_; }
    void setConfig(const Config& config) { config_ = config; }

private:
    Config config_;
};

} // namespace frcsim
