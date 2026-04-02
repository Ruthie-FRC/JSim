# Magnus Effect Math

This page explains the spin-induced side force model used by RenSim.

## Implemented model

Relevant code and tests:

- core/physics-core/include/frcsim/aerodynamics/magnus_model.hpp
- core/driver/include/frcsim/math/vector.hpp
- vendordep/tests/magnus_test.cpp
- vendordep/tests/forces_test.cpp

RenSim computes Magnus force direction from cross product:

$$
F_m = k_m (\omega \times v)
$$

where:

- omega: angular velocity vector (rad/s)
- v: linear velocity vector (m/s)
- k_m: Magnus coefficient

## Direction convention

Use right-hand rule for $$\omega \times v$$.

Example used by test:

- spin +Z
- velocity +X
- result +Y

So side force is positive Y in that frame.

## Sign table quick check

- +Z x +X = +Y
- +X x +Y = +Z
- +Y x +Z = +X

If measured curve bends opposite expected side, either spin sign or frame transform is wrong.

## Units and coefficient meaning

k_m sets force magnitude scale and is tuned empirically in this implementation.

Practical behavior:

- larger |omega| increases side force
- larger |v| increases side force
- changing spin direction flips side force direction

## Interaction with drag and gravity

Trajectory acceleration combines effects:

$$
a = g + \frac{F_d}{m} + \frac{F_m}{m}
$$

Magnus mainly deflects path laterally while drag reduces speed and gravity drives vertical drop.

## Validation recipe

1. Set velocity along +X.
2. Set spin along +Z and verify +Y force.
3. Flip spin to -Z and verify -Y force.
4. Double spin and confirm force magnitude roughly doubles.
5. Disable drag to isolate Magnus-only behavior.

## Common mistakes

- Using v x omega instead of omega x v.
- Mixing world and body spin vectors.
- Tuning k_m before confirming sign and frame correctness.
