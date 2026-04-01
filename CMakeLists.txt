cmake_minimum_required(VERSION 3.20)

project(FRCSimPhysicsLibrary VERSION 0.1.0 LANGUAGES CXX)

option(FRCSIM_BUILD_EXAMPLES "Build examples" ON)
option(FRCSIM_BUILD_TESTS "Build tests" ON)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_subdirectory(core/physics-core)

if(FRCSIM_BUILD_EXAMPLES)
add_executable(frcsim_minimal_world examples/cpp/minimal_world.cpp)
target_link_libraries(frcsim_minimal_world PRIVATE frcsim_physics)

add_executable(frcsim_modular_bodies examples/cpp/modular_bodies_demo.cpp)
target_link_libraries(frcsim_modular_bodies PRIVATE frcsim_physics)

add_executable(frcsim_robot_assembly examples/cpp/robot_assembly_demo.cpp)
target_link_libraries(frcsim_robot_assembly PRIVATE frcsim_physics)
endif()

if(FRCSIM_BUILD_TESTS)
add_executable(frcsim_rigidbody_test vendordep/tests/rigidbody_test.cpp)
target_link_libraries(frcsim_rigidbody_test PRIVATE frcsim_physics)

add_executable(frcsim_magnus_test vendordep/tests/magnus_test.cpp)
target_link_libraries(frcsim_magnus_test PRIVATE frcsim_physics)

add_executable(frcsim_modular_body_test vendordep/tests/modular_body_test.cpp)
target_link_libraries(frcsim_modular_body_test PRIVATE frcsim_physics)

# New comprehensive test suites
add_executable(frcsim_math_test vendordep/tests/math_test.cpp)
target_link_libraries(frcsim_math_test PRIVATE frcsim_physics)

add_executable(frcsim_integration_test vendordep/tests/integration_test.cpp)
target_link_libraries(frcsim_integration_test PRIVATE frcsim_physics)

add_executable(frcsim_assembly_test vendordep/tests/assembly_test.cpp)
target_link_libraries(frcsim_assembly_test PRIVATE frcsim_physics)

add_executable(frcsim_boundary_test vendordep/tests/boundary_test.cpp)
target_link_libraries(frcsim_boundary_test PRIVATE frcsim_physics)

add_executable(frcsim_deformable_test vendordep/tests/deformable_test.cpp)
target_link_libraries(frcsim_deformable_test PRIVATE frcsim_physics)

add_executable(frcsim_forces_test vendordep/tests/forces_test.cpp)
target_link_libraries(frcsim_forces_test PRIVATE frcsim_physics)
endif()
