// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

#include "driverheader.h"

#include <algorithm>
#include <cstdint>
#include <memory>
#include <mutex>
#include <unordered_map>

#include "frcsim/physics_world.hpp"
#include "frcsim/joints/detail/joint_math.hpp"
#include "frcsim/rigidbody/material.hpp"

namespace {

std::mutex g_world_mutex;
std::unordered_map<std::uint64_t, std::unique_ptr<frcsim::PhysicsWorld>>
    g_worlds;
std::uint64_t g_next_handle = 1;

struct RevoluteJointRecord {
  std::uint64_t world_handle{0};
  int assembly_index{-1};
  int body_a_index{-1};
  int body_b_index{-1};
  frcsim::Vector3 axis_local{frcsim::Vector3::unitZ()};
  bool has_limits{false};
  double min_angle_rad{0.0};
  double max_angle_rad{0.0};
  bool has_motor{false};
  double target_velocity_radps{0.0};
  double max_torque_nm{0.0};
};

std::unordered_map<int, RevoluteJointRecord> g_joint_records;
int g_next_joint_id = 1;

frcsim::PhysicsWorld* getWorld(std::uint64_t handle) {
  const auto it = g_worlds.find(handle);
  if (it == g_worlds.end()) {
    return nullptr;
  }
  return it->second.get();
}

frcsim::RigidBody* getBody(frcsim::PhysicsWorld* world, int body_index) {
  if (!world || body_index < 0) {
    return nullptr;
  }
  auto& bodies = world->bodies();
  const std::size_t idx = static_cast<std::size_t>(body_index);
  if (idx >= bodies.size()) {
    return nullptr;
  }
  return &bodies[idx];
}

RevoluteJointRecord* getJoint(int joint_id, std::uint64_t world_handle) {
  const auto it = g_joint_records.find(joint_id);
  if (it == g_joint_records.end() || it->second.world_handle != world_handle) {
    return nullptr;
  }
  return &it->second;
}

void solveRevoluteJoint(const RevoluteJointRecord& joint,
                        frcsim::PhysicsWorld* world, double dt_s) {
  if (!world) {
    return;
  }

  auto& bodies = world->bodies();
  const std::size_t body_a_idx = static_cast<std::size_t>(joint.body_a_index);
  const std::size_t body_b_idx = static_cast<std::size_t>(joint.body_b_index);
  if (joint.body_a_index < 0 || joint.body_b_index < 0 ||
      body_a_idx >= bodies.size() || body_b_idx >= bodies.size()) {
    return;
  }

  frcsim::RigidBody* body_a = &bodies[body_a_idx];
  frcsim::RigidBody* body_b = &bodies[body_b_idx];
  if (!body_a || !body_b) {
    return;
  }

  using frcsim::detail::applyPositionCorrection;
  using frcsim::detail::applyVelocityCorrection;
  using frcsim::detail::clampValue;
  using frcsim::detail::signedTwistAngleRad;
  using frcsim::detail::worldAxisOrFallback;

  const frcsim::Vector3 axis_world =
      worldAxisOrFallback(body_a, joint.axis_local, frcsim::Vector3::unitZ());

  applyPositionCorrection(body_a, body_b,
                          body_b->position() - body_a->position(), 0.7);

  const frcsim::Vector3 rel_ang = body_b->angularVelocity() - body_a->angularVelocity();
  const frcsim::Vector3 orthogonal_rel_ang =
      rel_ang - axis_world * rel_ang.dot(axis_world);
  applyVelocityCorrection(body_a, body_b, orthogonal_rel_ang, 0.6);

  const double inv_a = body_a->flags().is_kinematic ? 0.0 : body_a->inverseMass();
  const double inv_b = body_b->flags().is_kinematic ? 0.0 : body_b->inverseMass();
  const double total_inv = inv_a + inv_b;

  if (total_inv > frcsim::detail::kJointEpsilon && joint.has_motor && dt_s > 0.0) {
    const double current_rel = rel_ang.dot(axis_world);
    const double target_delta = joint.target_velocity_radps - current_rel;
    const double max_step = std::max(0.0, joint.max_torque_nm) * dt_s;
    const double applied_delta = clampValue(target_delta, -max_step, max_step);
    if (!body_a->flags().is_kinematic) {
      body_a->setAngularVelocity(body_a->angularVelocity() -
                                 axis_world * (applied_delta * inv_a / total_inv));
    }
    if (!body_b->flags().is_kinematic) {
      body_b->setAngularVelocity(body_b->angularVelocity() +
                                 axis_world * (applied_delta * inv_b / total_inv));
    }
  }

  if (joint.has_limits && dt_s > 0.0) {
    const double rel_angle = signedTwistAngleRad(body_a->orientation(),
                                                 body_b->orientation(),
                                                 axis_world);
    double violation = 0.0;
    if (rel_angle < joint.min_angle_rad) {
      violation = joint.min_angle_rad - rel_angle;
    } else if (rel_angle > joint.max_angle_rad) {
      violation = joint.max_angle_rad - rel_angle;
    }

    if (std::abs(violation) > 1e-8 && total_inv > frcsim::detail::kJointEpsilon) {
      const double correction_speed = clampValue(violation / dt_s, -5.0, 5.0);
      if (!body_a->flags().is_kinematic) {
        body_a->setAngularVelocity(body_a->angularVelocity() -
                                   axis_world * (correction_speed * inv_a / total_inv));
      }
      if (!body_b->flags().is_kinematic) {
        body_b->setAngularVelocity(body_b->angularVelocity() +
                                   axis_world * (correction_speed * inv_b / total_inv));
      }
    }
  }
}

void solveRevoluteJoints(std::uint64_t world_handle, frcsim::PhysicsWorld* world,
                         double dt_s) {
  for (const auto& [joint_id, joint] : g_joint_records) {
    (void)joint_id;
    if (joint.world_handle != world_handle) {
      continue;
    }
    solveRevoluteJoint(joint, world, dt_s);
  }
}

}  // namespace

extern "C" {
void c_doThing(void) {}

uint64_t c_rsCreateWorld(double fixed_dt_s, int enable_gravity) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsConfig config;
  config.fixed_dt_s = (fixed_dt_s > 0.0) ? fixed_dt_s : 0.01;
  config.enable_gravity = (enable_gravity != 0);

  const std::uint64_t handle = g_next_handle++;
  g_worlds.emplace(handle, std::make_unique<frcsim::PhysicsWorld>(config));
  return handle;
}

void c_rsDestroyWorld(uint64_t world_handle) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  for (auto it = g_joint_records.begin(); it != g_joint_records.end();) {
    if (it->second.world_handle == world_handle) {
      it = g_joint_records.erase(it);
    } else {
      ++it;
    }
  }
  g_worlds.erase(world_handle);
}

int c_rsCreateAssembly(uint64_t world_handle) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  world->createAssembly();
  world->config().enable_joint_constraints = true;
  return static_cast<int>(world->assemblies().size() - 1);
}

int c_rsAddRevoluteJoint(uint64_t world_handle, int assembly_index,
                         int body_a_idx, int body_b_idx,
                         double axis_x, double axis_y, double axis_z) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world || assembly_index < 0) {
    return -1;
  }

  const std::size_t assembly_idx = static_cast<std::size_t>(assembly_index);
  if (assembly_idx >= world->assemblies().size()) {
    return -1;
  }
  if (!getBody(world, body_a_idx) || !getBody(world, body_b_idx)) {
    return -1;
  }

  RevoluteJointRecord record;
  record.world_handle = world_handle;
  record.assembly_index = assembly_index;
  record.body_a_index = body_a_idx;
  record.body_b_index = body_b_idx;
  record.axis_local = frcsim::Vector3{axis_x, axis_y, axis_z};

  const int joint_id = g_next_joint_id++;
  g_joint_records.emplace(joint_id, record);
  world->config().enable_joint_constraints = true;
  return joint_id;
}

int c_rsSetJointLimits(uint64_t world_handle, int joint_id,
                       double min_angle_rad, double max_angle_rad) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  RevoluteJointRecord* joint = getJoint(joint_id, world_handle);
  if (!joint) {
    return -1;
  }

  if (min_angle_rad <= max_angle_rad) {
    joint->has_limits = true;
    joint->min_angle_rad = min_angle_rad;
    joint->max_angle_rad = max_angle_rad;
  } else {
    joint->has_limits = true;
    joint->min_angle_rad = max_angle_rad;
    joint->max_angle_rad = min_angle_rad;
  }
  return 0;
}

int c_rsSetJointMotorTarget(uint64_t world_handle, int joint_id,
                           double target_velocity_radps, double max_torque_nm) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  RevoluteJointRecord* joint = getJoint(joint_id, world_handle);
  if (!joint) {
    return -1;
  }

  joint->has_motor = true;
  joint->target_velocity_radps = target_velocity_radps;
  joint->max_torque_nm = std::max(0.0, max_torque_nm);
  return 0;
}

int c_rsGetJointAngle(uint64_t world_handle, int joint_id, double* out_angle_rad) {
  if (!out_angle_rad) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  RevoluteJointRecord* joint = getJoint(joint_id, world_handle);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!joint || !world) {
    return -1;
  }

  auto& bodies = world->bodies();
  const std::size_t body_a_idx = static_cast<std::size_t>(joint->body_a_index);
  const std::size_t body_b_idx = static_cast<std::size_t>(joint->body_b_index);
  if (body_a_idx >= bodies.size() || body_b_idx >= bodies.size()) {
    return -1;
  }

  const frcsim::Vector3 axis_world =
      frcsim::detail::worldAxisOrFallback(&bodies[body_a_idx], joint->axis_local,
                                           frcsim::Vector3::unitZ());
  *out_angle_rad = frcsim::detail::signedTwistAngleRad(
      bodies[body_a_idx].orientation(), bodies[body_b_idx].orientation(), axis_world);
  return 0;
}

int c_rsGetJointVelocity(uint64_t world_handle, int joint_id, double* out_vel_radps) {
  if (!out_vel_radps) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  RevoluteJointRecord* joint = getJoint(joint_id, world_handle);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!joint || !world) {
    return -1;
  }

  auto& bodies = world->bodies();
  const std::size_t body_a_idx = static_cast<std::size_t>(joint->body_a_index);
  const std::size_t body_b_idx = static_cast<std::size_t>(joint->body_b_index);
  if (body_a_idx >= bodies.size() || body_b_idx >= bodies.size()) {
    return -1;
  }

  const frcsim::Vector3 axis_world =
      frcsim::detail::worldAxisOrFallback(&bodies[body_a_idx], joint->axis_local,
                                           frcsim::Vector3::unitZ());
  const frcsim::Vector3 rel_ang = bodies[body_b_idx].angularVelocity() -
                                  bodies[body_a_idx].angularVelocity();
  *out_vel_radps = rel_ang.dot(axis_world);
  return 0;
}

int c_rsCreateBody(uint64_t world_handle, double mass_kg) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  world->createBody(mass_kg);
  return static_cast<int>(world->bodies().size() - 1);
}

int c_rsCreateBall(uint64_t world_handle) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  world->createBall();
  return static_cast<int>(world->balls().size() - 1);
}

int c_rsSetBodyPosition(uint64_t world_handle, int body_index,
                        double x_m, double y_m, double z_m) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }
  body->setPosition(frcsim::Vector3{x_m, y_m, z_m});
  return 0;
}

int c_rsSetBodyLinearVelocity(uint64_t world_handle, int body_index,
                              double vx_mps, double vy_mps, double vz_mps) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }
  body->setLinearVelocity(frcsim::Vector3{vx_mps, vy_mps, vz_mps});
  return 0;
}

int c_rsSetBodyGravityEnabled(uint64_t world_handle, int body_index,
                              int enabled) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }
  body->flags().enable_gravity = (enabled != 0);
  return 0;
}

int c_rsSetBodyMaterial(uint64_t world_handle, int body_index,
                        double restitution, double friction_kinetic,
                        double friction_static, double collision_damping) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  frcsim::Material material;
  material.coefficient_of_restitution =
      std::clamp(restitution, 0.0, 1.0);
  material.coefficient_of_friction_kinetic =
      std::max(0.0, friction_kinetic);
  material.coefficient_of_friction_static =
      std::max(0.0, friction_static);
  material.collision_damping = std::clamp(collision_damping, 0.0, 1.0);

  body->setMaterial(material);
  return 0;
}

int c_rsSetBodyMaterialId(uint64_t world_handle, int body_index,
                          int32_t material_id) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  body->setMaterialId(material_id);
  return 0;
}

int c_rsSetBodyCollisionFilter(uint64_t world_handle, int body_index,
                               uint32_t collision_layer_bits,
                               uint32_t collision_mask_bits) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  body->setCollisionLayer(collision_layer_bits);
  body->setCollisionMask(collision_mask_bits);
  return 0;
}

int c_rsSetBodyAerodynamicSphere(uint64_t world_handle, int body_index,
                                 double radius_m, double drag_coefficient) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  frcsim::RigidBody::AerodynamicGeometry geometry;
  geometry.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kSphere;
  geometry.radius_m = std::max(0.0, radius_m);
  body->setAerodynamicGeometry(geometry);

  world->config().default_drag_coefficient =
      std::max(0.0, drag_coefficient);
  return 0;
}

int c_rsSetBodyAerodynamicBox(uint64_t world_handle, int body_index,
                              double x_m, double y_m, double z_m,
                              double drag_coefficient) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  frcsim::RigidBody::AerodynamicGeometry geometry;
  geometry.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kBox;
  geometry.box_dimensions_m =
      frcsim::Vector3(std::max(0.0, x_m), std::max(0.0, y_m), std::max(0.0, z_m));
  body->setAerodynamicGeometry(geometry);

  world->config().default_drag_coefficient =
      std::max(0.0, drag_coefficient);
  return 0;
}

int c_rsSetBallPosition(uint64_t world_handle, int ball_index,
                        double x_m, double y_m, double z_m) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world || ball_index < 0) {
    return -1;
  }

  auto& balls = world->balls();
  const std::size_t idx = static_cast<std::size_t>(ball_index);
  if (idx >= balls.size()) {
    return -1;
  }

  auto state = balls[idx].state();
  state.position_m = frcsim::Vector3{x_m, y_m, z_m};
  balls[idx].setState(state);
  return 0;
}

int c_rsSetBallLinearVelocity(uint64_t world_handle, int ball_index,
                              double vx_mps, double vy_mps, double vz_mps) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world || ball_index < 0) {
    return -1;
  }

  auto& balls = world->balls();
  const std::size_t idx = static_cast<std::size_t>(ball_index);
  if (idx >= balls.size()) {
    return -1;
  }

  auto state = balls[idx].state();
  state.velocity_mps = frcsim::Vector3{vx_mps, vy_mps, vz_mps};
  balls[idx].setState(state);
  return 0;
}

int c_rsSetWorldAerodynamics(uint64_t world_handle, int enabled,
                             double air_density_kgpm3,
                             double linear_drag_coefficient_n_per_mps,
                             double magnus_coefficient,
                             double default_drag_coefficient,
                             double default_drag_reference_area_m2) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  auto& cfg = world->config();
  cfg.enable_aerodynamics = (enabled != 0);
  cfg.air_density_kgpm3 = std::max(0.0, air_density_kgpm3);
  cfg.linear_drag_coefficient_n_per_mps =
      std::max(0.0, linear_drag_coefficient_n_per_mps);
  cfg.magnus_coefficient = magnus_coefficient;
  cfg.default_drag_coefficient = std::max(0.0, default_drag_coefficient);
  cfg.default_drag_reference_area_m2 =
      std::max(0.0, default_drag_reference_area_m2);

  return 0;
}

int c_rsSetMaterialInteraction(uint64_t world_handle, int32_t material_a_id,
                               int32_t material_b_id, double restitution,
                               double friction, int enabled) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  frcsim::PhysicsWorld::MaterialInteraction interaction;
  interaction.material_a_id = material_a_id;
  interaction.material_b_id = material_b_id;
  interaction.restitution = std::clamp(restitution, 0.0, 1.0);
  interaction.friction = std::max(0.0, friction);
  interaction.enabled = (enabled != 0);
  world->setMaterialInteraction(interaction);
  return 0;
}

int c_rsStepWorld(uint64_t world_handle, int steps) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  const int safe_steps = std::max(steps, 1);
  const double dt_s = world->config().fixed_dt_s;
  for (int i = 0; i < safe_steps; ++i) {
    world->step();
    solveRevoluteJoints(world_handle, world, dt_s);
  }
  return 0;
}

int c_rsSetWorldGravity(uint64_t world_handle, double gx_mps2,
                        double gy_mps2, double gz_mps2) {
  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  world->config().gravity_mps2 = frcsim::Vector3{gx_mps2, gy_mps2, gz_mps2};
  world->config().enable_gravity = true;
  return 0;
}

int c_rsGetBallPosition(uint64_t world_handle, int ball_index,
                        double* x_m, double* y_m, double* z_m) {
  if (!x_m || !y_m || !z_m) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world || ball_index < 0) {
    return -1;
  }

  const auto& balls = world->balls();
  const std::size_t idx = static_cast<std::size_t>(ball_index);
  if (idx >= balls.size()) {
    return -1;
  }

  const frcsim::Vector3 p = balls[idx].state().position_m;
  *x_m = p.x;
  *y_m = p.y;
  *z_m = p.z;
  return 0;
}

int c_rsGetBallLinearVelocity(uint64_t world_handle, int ball_index,
                              double* vx_mps, double* vy_mps, double* vz_mps) {
  if (!vx_mps || !vy_mps || !vz_mps) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world || ball_index < 0) {
    return -1;
  }

  const auto& balls = world->balls();
  const std::size_t idx = static_cast<std::size_t>(ball_index);
  if (idx >= balls.size()) {
    return -1;
  }

  const frcsim::Vector3 v = balls[idx].state().velocity_mps;
  *vx_mps = v.x;
  *vy_mps = v.y;
  *vz_mps = v.z;
  return 0;
}

int c_rsGetBodyPosition(uint64_t world_handle, int body_index,
                        double* x_m, double* y_m, double* z_m) {
  if (!x_m || !y_m || !z_m) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  const frcsim::Vector3 p = body->position();
  *x_m = p.x;
  *y_m = p.y;
  *z_m = p.z;
  return 0;
}

int c_rsGetBodyLinearVelocity(uint64_t world_handle, int body_index,
                              double* vx_mps, double* vy_mps, double* vz_mps) {
  if (!vx_mps || !vy_mps || !vz_mps) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  frcsim::RigidBody* body = getBody(world, body_index);
  if (!body) {
    return -1;
  }

  const frcsim::Vector3 v = body->linearVelocity();
  *vx_mps = v.x;
  *vy_mps = v.y;
  *vz_mps = v.z;
  return 0;
}

int c_rsGetBodyPose7Array(uint64_t world_handle, double* out_pose7,
                          int max_bodies) {
  if (!out_pose7 || max_bodies < 0) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  const auto& bodies = world->bodies();
  const int count = std::min(max_bodies, static_cast<int>(bodies.size()));
  for (int i = 0; i < count; ++i) {
    const auto& body = bodies[static_cast<std::size_t>(i)];
    const auto p = body.position();
    const auto q = body.orientation();
    const int base = i * 7;
    out_pose7[base + 0] = p.x;
    out_pose7[base + 1] = p.y;
    out_pose7[base + 2] = p.z;
    out_pose7[base + 3] = q.w;
    out_pose7[base + 4] = q.x;
    out_pose7[base + 5] = q.y;
    out_pose7[base + 6] = q.z;
  }
  return count;
}

int c_rsGetBodyVelocity6Array(uint64_t world_handle, double* out_velocity6,
                              int max_bodies) {
  if (!out_velocity6 || max_bodies < 0) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  const auto& bodies = world->bodies();
  const int count = std::min(max_bodies, static_cast<int>(bodies.size()));
  for (int i = 0; i < count; ++i) {
    const auto& body = bodies[static_cast<std::size_t>(i)];
    const auto v = body.linearVelocity();
    const auto w = body.angularVelocity();
    const int base = i * 6;
    out_velocity6[base + 0] = v.x;
    out_velocity6[base + 1] = v.y;
    out_velocity6[base + 2] = v.z;
    out_velocity6[base + 3] = w.x;
    out_velocity6[base + 4] = w.y;
    out_velocity6[base + 5] = w.z;
  }
  return count;
}

int c_rsGetBodyState13Array(uint64_t world_handle, double* out_state13,
                            int max_bodies) {
  if (!out_state13 || max_bodies < 0) {
    return -1;
  }

  std::lock_guard<std::mutex> lock(g_world_mutex);
  frcsim::PhysicsWorld* world = getWorld(world_handle);
  if (!world) {
    return -1;
  }

  const auto& bodies = world->bodies();
  const int count = std::min(max_bodies, static_cast<int>(bodies.size()));
  for (int i = 0; i < count; ++i) {
    const auto& body = bodies[static_cast<std::size_t>(i)];
    const auto p = body.position();
    const auto q = body.orientation();
    const auto v = body.linearVelocity();
    const auto w = body.angularVelocity();

    const int base = i * 13;
    out_state13[base + 0] = p.x;
    out_state13[base + 1] = p.y;
    out_state13[base + 2] = p.z;
    out_state13[base + 3] = q.w;
    out_state13[base + 4] = q.x;
    out_state13[base + 5] = q.y;
    out_state13[base + 6] = q.z;
    out_state13[base + 7] = v.x;
    out_state13[base + 8] = v.y;
    out_state13[base + 9] = v.z;
    out_state13[base + 10] = w.x;
    out_state13[base + 11] = w.y;
    out_state13[base + 12] = w.z;
  }
  return count;
}
}  // extern "C"
