// Minimal C API wrapper for basic world/ball/body operations.
#pragma once

#include "frcsim/physics_world.hpp"

extern "C" {

using PhysicsWorld_t = frcsim::PhysicsWorld;
using RigidBody_t = frcsim::RigidBody;
using Ball_t = frcsim::BallPhysicsSim3D;

PhysicsWorld_t* frcsim_create_world();
void frcsim_destroy_world(PhysicsWorld_t* w);

RigidBody_t* frcsim_create_body(PhysicsWorld_t* w, double mass_kg);
Ball_t* frcsim_create_ball(PhysicsWorld_t* w,
                           const frcsim::BallPhysicsSim3D::Config* config,
                           const frcsim::BallPhysicsSim3D::BallProperties* props);

void frcsim_step_world(PhysicsWorld_t* w, double dt_s);

void frcsim_set_body_box_geometry(RigidBody_t* body, double dim_x, double dim_y,
                                  double dim_z);
void frcsim_set_body_sphere_geometry(RigidBody_t* body, double radius);
void frcsim_set_body_position(RigidBody_t* body, double x, double y, double z);

void frcsim_get_ball_state(Ball_t* ball, double* px, double* py, double* pz,
                           double* vx, double* vy, double* vz);
void frcsim_ball_shoot(Ball_t* ball, double px, double py, double pz,
                       double vx, double vy, double vz);

}
