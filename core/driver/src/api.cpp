#include "frcsim/api.h"

#include "frcsim/physics_world.hpp"

extern "C" {

PhysicsWorld_t* frcsim_create_world() {
  return new PhysicsWorld_t;
}

void frcsim_destroy_world(PhysicsWorld_t* w) {
  delete w;
}

RigidBody_t* frcsim_create_body(PhysicsWorld_t* w, double mass_kg) {
  if (!w) return nullptr;
  return &w->createBody(mass_kg);
}

Ball_t* frcsim_create_ball(PhysicsWorld_t* w,
                           const frcsim::BallPhysicsSim3D::Config* config,
                           const frcsim::BallPhysicsSim3D::BallProperties* props) {
  if (!w) return nullptr;
  if (config && props) {
    return &w->createBall(*config, *props);
  }
  if (config) {
    return &w->createBall(*config, frcsim::BallPhysicsSim3D::BallProperties());
  }
  if (props) {
    return &w->createBall(frcsim::BallPhysicsSim3D::Config(), *props);
  }
  return &w->createBall(frcsim::BallPhysicsSim3D::Config(), frcsim::BallPhysicsSim3D::BallProperties());
}

void frcsim_step_world(PhysicsWorld_t* w, double dt_s) {
  if (!w) return;
  // Use configured fixed_dt_s if dt_s <= 0
  if (dt_s > 0.0) {
    auto cfg = w->config();
    cfg.fixed_dt_s = dt_s;
    w->config() = cfg;
  }
  w->step();
}

void frcsim_set_body_box_geometry(RigidBody_t* body, double dim_x, double dim_y, double dim_z) {
  if (!body) return;
  frcsim::RigidBody::AerodynamicGeometry g;
  g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kBox;
  g.box_dimensions_m = frcsim::Vector3(dim_x, dim_y, dim_z);
  body->setAerodynamicGeometry(g);
}

void frcsim_set_body_sphere_geometry(RigidBody_t* body, double radius) {
  if (!body) return;
  frcsim::RigidBody::AerodynamicGeometry g;
  g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kSphere;
  g.radius_m = radius;
  body->setAerodynamicGeometry(g);
}

void frcsim_set_body_position(RigidBody_t* body, double x, double y, double z) {
  if (!body) return;
  body->setPosition(x, y, z);
}

void frcsim_get_ball_state(Ball_t* ball, double* px, double* py, double* pz,
                           double* vx, double* vy, double* vz) {
  if (!ball) return;
  const auto& s = ball->state();
  if (px) *px = s.position_m.x;
  if (py) *py = s.position_m.y;
  if (pz) *pz = s.position_m.z;
  if (vx) *vx = s.velocity_mps.x;
  if (vy) *vy = s.velocity_mps.y;
  if (vz) *vz = s.velocity_mps.z;
}

void frcsim_ball_shoot(Ball_t* ball, double px, double py, double pz,
                       double vx, double vy, double vz) {
  if (!ball) return;
  frcsim::Vector3 pos(px, py, pz);
  frcsim::Vector3 vel(vx, vy, vz);
  ball->shoot(pos, vel);
}

}
