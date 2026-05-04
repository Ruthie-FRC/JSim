// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include "frcsim/physics_world.hpp"
#include "frcsim/rigidbody/rigid_body.hpp"
#include "frcsim/math/vector.hpp"

namespace py = pybind11;

PYBIND11_MODULE(jsim_core, m) {
    m.doc() = "JSim Core Physics bindings";

    py::class_<frcsim::Vector3>(m, "Vector3")
        .def(py::init<double, double, double>())
        .def_readwrite("x", &frcsim::Vector3::x)
        .def_readwrite("y", &frcsim::Vector3::y)
        .def_readwrite("z", &frcsim::Vector3::z);

    py::class_<frcsim::RigidBody>(m, "RigidBody")
        .def(py::init<>())
        .def("get_position", &frcsim::RigidBody::position)
        .def("set_position", &frcsim::RigidBody::setPosition)
        .def("get_linear_velocity", &frcsim::RigidBody::linearVelocity)
        .def("set_linear_velocity", &frcsim::RigidBody::setLinearVelocity);

    py::class_<frcsim::PhysicsWorld>(m, "PhysicsWorld")
        .def(py::init<const frcsim::PhysicsConfig&>())
        .def("step", &frcsim::PhysicsWorld::step)
        .def("create_body", &frcsim::PhysicsWorld::createBody);
}
