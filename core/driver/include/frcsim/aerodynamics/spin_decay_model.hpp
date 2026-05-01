#pragma once

#include <cmath>

#include "frcsim/math/vector.hpp"
#include "frcsim/math/matrix.hpp"
#include "frcsim/math/quaternion.hpp"
#include "frcsim/aerodynamics/magnus_model.hpp"

namespace frcsim::aerodynamics {

/**
 * @brief Spin evolution model using existing Magnus + Drag ecosystem
 *
 * Responsibilities:
 *   - Angular damping (linear + nonlinear)
 *   - Magnus torque coupling via MagnusModel
 *
 * DOES NOT:
 *   - recompute drag or magnus physics manually
 */
class SpinDecayModel {
 public:
  SpinDecayModel(const MagnusModel& magnus_model = MagnusModel())
      : magnus_model_(magnus_model) {}

  /**
   * @brief Advance angular velocity
   */
  Vector3 step(const Vector3& omegaLocal,
               const Vector3& velocityWorld,
               const Quaternion& orientation,
               double dt) const noexcept {
    // --- Rotation transforms ---
    Matrix3 R = Matrix3::fromQuaternion(orientation);
    Matrix3 Rinv = R.transpose();

    Vector3 omegaWorld = R * omegaLocal;

    double vMag = velocityWorld.norm();
    double wMag = omegaWorld.norm();

    // --- Base damping ---
    Vector3 linearDamping = omegaWorld * m_linearDecay;

    // --- Velocity-coupled damping ---
    Vector3 velocityDamping = omegaWorld * (m_velocityCoupling * vMag);

    // --- Nonlinear spin drag ---
    Vector3 nonlinearDamping = omegaWorld * (m_nonlinearDecay * wMag);

    // --- Magnus force (USE EXISTING MODEL) ---
    Vector3 magnusForce =
        magnus_model_.computeForce(velocityWorld, omegaWorld);

    // --- Convert force → torque ---
    Vector3 magnusTorque = m_radiusVector.cross(magnusForce);

    // --- Total angular change ---
    Vector3 domega =
        magnusTorque
        - linearDamping
        - velocityDamping
        - nonlinearDamping;

    omegaWorld = omegaWorld + domega * dt;

    return Rinv * omegaWorld;
  }

  // --- Tunables (because you will tweak these obsessively) ---

  void setLinearDecay(double k) { m_linearDecay = k; }
  void setVelocityCoupling(double k) { m_velocityCoupling = k; }
  void setNonlinearDecay(double k) { m_nonlinearDecay = k; }
  void setRadiusVector(const Vector3& r) { m_radiusVector = r; }

 private:
  MagnusModel magnus_model_;

  double m_linearDecay{0.05};
  double m_velocityCoupling{0.01};
  double m_nonlinearDecay{0.02};

  // lever arm for torque (body-space assumption)
  Vector3 m_radiusVector{0.0, 0.0, 0.05};
};

} // namespace frcsim::aerodynamics
