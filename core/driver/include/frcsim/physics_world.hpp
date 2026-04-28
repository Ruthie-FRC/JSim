// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

#pragma once

#include <array>
#include <cstdint>
#include <memory>
#include <vector>

#include "frcsim/aerodynamics/drag_model.hpp"
#include "frcsim/field/boundary.hpp"
// #include "frcsim/physics/physics_config.hpp" // Removed: file no longer exists
#include "frcsim/rigidbody/material.hpp"

namespace frcsim {

class RigidBody;
class RigidAssembly;
class BallPhysicsSim3D;

/**
 * @brief Manages the physics simulation world, including time steps, global
 * forces, and dynamic object interactions.
 */
class PhysicsWorld {
 public:
  /** @brief Constructs a physics world with the given configuration. */
  explicit PhysicsWorld(const PhysicsConfig& config) : config_(config) {}

  /** @brief Creates a dynamic rigid body with the given mass. */
  RigidBody& createBody(double mass_kg);

  /** @brief Creates a rigid assembly (group of bodies with constraints). */
  RigidAssembly& createAssembly();

  /** @brief Creates a ball entity for simulating spherical game pieces. */
  BallPhysicsSim3D& createBall(
      const BallPhysicsSim3D::Config& config,
      const BallPhysicsSim3D::BallProperties& properties);

  /** @brief Adds a boundary (e.g., wall, floor) to the simulation world. */
  EnvironmentalBoundary& addBoundary();

  /** @brief Adds a global force generator (e.g., gravity) to the world. */
  void addGlobalForceGenerator(
      const std::shared_ptr<ForceGenerator>& generator);

  /** @brief Sets material interaction properties between two materials. */
  void setMaterialInteraction(
      const MaterialInteraction& interaction);

  /** @brief Clears all material interactions, resetting to defaults. */
  void clearMaterialInteractions();

  /** @brief Steps the simulation forward by one time step. */
  void step();

  /** @brief Steps the simulation forward by a specified number of steps. */
  void step(int steps);

  /** @brief Applies a global gravity vector to the world. */
  void setGravity(const Vector3& gravity);

  /** @brief Applies gravity components to the world. */
  void setGravity(double gxMetersPerSecondSquared, double gyMetersPerSecondSquared,
                  double gzMetersPerSecondSquared);

  /** @brief Returns the configuration settings for this physics world. */
  const PhysicsConfig& config() const { return config_; }

  /** @brief Returns the current simulation time in seconds. */
  double accumulatedSimTimeS() const { return accumulated_sim_time_s_; }

  /** @brief Returns the number of steps taken in the simulation. */
  int stepCount() const { return step_count_; }

  /** @brief Returns the list of bodies in the world. */
  const std::vector<RigidBody>& bodies() const { return bodies_; }

  /** @brief Returns the list of assemblies in the world. */
  const std::vector<RigidAssembly>& assemblies() const { return assemblies_; }

  /** @brief Returns the list of ball entities in the world. */
  const std::vector<BallPhysicsSim3D>& balls() const { return balls_; }

 private:
  PhysicsConfig config_;

  std::vector<RigidBody> bodies_;
  std::vector<RigidAssembly> assemblies_;
  std::vector<BallPhysicsSim3D> balls_;

  std::vector<std::shared_ptr<ForceGenerator>> global_force_generators_;

  std::vector<MaterialInteraction> material_interactions_;

  int step_count_ = 0;
  double accumulated_sim_time_s_ = 0.0;
};

}  // namespace frcsim