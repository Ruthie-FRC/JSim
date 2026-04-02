# Math Overview

This section explains the math behind RenSim models and how to validate behavior in tests.

## Read in this order

1. Units and Conventions
2. Vector3
3. Matrix3
4. Quaternion
5. Integrators
6. Aerodynamics
7. Magnus Effect
8. Collision Impulses
9. Drivetrain and Mechanism Dynamics

## Validation-first workflow

1. Start from the equation on each page.
2. Check units at each term.
3. Run the linked tests and compare expected sign and magnitude.
4. Tune coefficients only after signs, frames, and units are correct.

## Related tests

- vendordep/tests/math_test.cpp
- vendordep/tests/integration_test.cpp
- vendordep/tests/forces_test.cpp
- vendordep/tests/magnus_test.cpp
- vendordep/tests/boundary_test.cpp
