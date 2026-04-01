#include <cassert>

#include "frcsim/physics_world.hpp"
#include "frcsim/rigidbody/deformable_body.hpp"

int main() {
    frcsim::PhysicsConfig config;
    config.fixed_dt_s = 0.01;
    config.enable_collision_detection = false;
    config.enable_joint_constraints = false;
    config.linear_damping_per_s = 0.0;
    config.angular_damping_per_s = 0.0;

    {
        // Test 1: Body with gravity disabled doesn't fall
        frcsim::PhysicsWorld world1(config);
        frcsim::RigidBody& body = world1.createBody(1.0);
        body.setPosition(frcsim::Vector3(0.0, 0.0, 5.0));
        body.flags().enable_gravity = false;

        for (int i = 0; i < 100; ++i) {
            world1.step();
        }

        // Since gravity is disabled and no forces applied, z should not decrease
        assert(body.position().z >= 4.99);  // Allow for floating-point jitter
    }

    {
        // Test 2: Material properties are queryable
        frcsim::PhysicsWorld world2(config);
        frcsim::RigidBody& body = world2.createBody(1.0);

        frcsim::Material mat;
        mat.coefficient_of_restitution = 0.9;
        mat.coefficient_of_friction_kinetic = 0.5;
        body.setMaterial(mat);

        assert(body.material()->coefficient_of_restitution == 0.9);
        assert(body.material()->coefficient_of_friction_kinetic == 0.5);
    }

    {
        // Test 3: Kinematic bodies don't respond to forces
        frcsim::PhysicsWorld world3(config);
        frcsim::RigidBody& body = world3.createBody(1.0);
        body.setPosition(frcsim::Vector3(0.0, 0.0, 0.0));
        body.flags().is_kinematic = true;

        // Apply force (should be ignored)
        body.applyForce(frcsim::Vector3(100.0, 0.0, 0.0));

        for (int i = 0; i < 10; ++i) {
            world3.step();
        }

        // Position should not change since kinematic
        assert(body.position().x == 0.0);
        assert(body.position().y == 0.0);
        assert(body.position().z == 0.0);
    }

    {
        // Test 4: Deformable body initialization
        frcsim::DeformableBody def_body(2.0);
        def_body.enableDeformation(true);
        def_body.setBendStiffness(100.0);
        def_body.setWarpDamping(20.0);

        assert(def_body.isDeformationEnabled());
        assert(def_body.bendStiffness() == 100.0);
        assert(def_body.warpDamping() == 20.0);
    }

    {
        // Test 5: Environmental boundaries are queryable
        frcsim::PhysicsWorld world5(config);
        frcsim::EnvironmentalBoundary& boundary = world5.addBoundary();
        boundary.type = frcsim::BoundaryType::kBox;
        boundary.behavior = frcsim::BoundaryBehavior::kRigidBody;
        boundary.position_m = frcsim::Vector3(1.0, 2.0, 3.0);

        assert(world5.boundaries().size() == 1);
        assert(world5.boundaries()[0].type == frcsim::BoundaryType::kBox);
        assert(world5.boundaries()[0].behavior == frcsim::BoundaryBehavior::kRigidBody);
        assert(world5.boundaries()[0].position_m.x == 1.0);
    }

    return 0;
}
