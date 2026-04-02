# Integrators (frcsim::Integrator)

This page explains the numerical integration methods used in RenSim and how to choose between them.

## Source and scope

Primary implementation:

- core/driver/include/frcsim/math/integrators.hpp

Validation tests:

- vendordep/tests/integration_test.cpp

## State equations

Linear dynamics are integrated from:

$$
\dot{x} = v, \quad \dot{v} = a
$$

Angular dynamics use quaternion kinematics:

$$
\dot{q} = \frac{1}{2} \omega_q q
$$

with $$\omega_q = (0, \omega_x, \omega_y, \omega_z)$$.

## Method comparison

| Method | Order (global) | Cost | Typical stability in real-time sims | Notes |
|---|---|---|---|---|
| Explicit Euler | 1 | Low | Weak for stiff dynamics | Good for debugging and simple predictors |
| Semi-Implicit Euler | 1 | Low | Better for energy behavior in mechanics | Default-style choice in many engines |
| RK2 (midpoint) | 2 | Medium | Better accuracy per step | Useful for fast projectiles and smoother trajectories |

## Linear integration methods

### Semi-Implicit Euler (integrateLinear)

Update order:

$$
v_{n+1} = v_n + a_n dt
$$

$$
x_{n+1} = x_n + v_{n+1} dt
$$

Why it is commonly used:

- more stable than explicit Euler for many mechanical systems
- better long-run behavior for velocity-coupled motion

### Explicit Euler (integrateLinearExplicit)

Update order:

$$
x_{n+1} = x_n + v_n dt
$$

$$
v_{n+1} = v_n + a_n dt
$$

Use when:

- you need the old-state predictor style update
- you are debugging step ordering

### RK2 midpoint (integrateLinearRK2)

RenSim midpoint-style update:

$$
v_{mid} = v_n + a_n \frac{dt}{2}
$$

$$
x_{n+1} = x_n + v_{mid} dt
$$

$$
v_{n+1} = v_n + a_n dt
$$

Use when:

- projectile motion needs less trajectory error at same dt
- you want better accuracy without moving to full RK4 cost

## Angular integration

### Angular velocity update (integrateAngularVelocity)

$$
\omega_{n+1} = \omega_n + \alpha_n dt
$$

### Quaternion update (integrateAngular)

RenSim computes:

$$
dq = 0.5 (\omega_q q)
$$

$$
q_{n+1} = q_n + dq dt
$$

and then normalizes if needed.

Why normalization matters:

- floating-point drift causes $$|q| \neq 1$$ over time
- non-unit quaternions produce scaling/rotation artifacts

## Error and timestep guidance

- Explicit and semi-implicit Euler are first-order globally: error scales approximately with dt.
- RK2 is second-order globally: error scales approximately with $$dt^2$$.
- Halving dt usually improves Euler methods notably, but cost doubles.

Practical guidance for FRC-like simulation loops:

- keep fixed dt
- start near 0.01 s to 0.02 s for many robot dynamics loops
- reduce dt for stiff spring contacts, high-speed impacts, or high angular rates

## Energy behavior and drift expectations

- Explicit Euler can over-inject or dissipate energy depending on system.
- Semi-implicit Euler often gives better qualitative energy behavior in rigid-body motion.
- RK2 typically reduces trajectory drift but still needs appropriate dt.

## Choose this method when

- choose Semi-Implicit Euler for general real-time rigid-body stepping
- choose Explicit Euler for diagnostics and predictor workflows
- choose RK2 when projectile or fast mechanism accuracy needs improvement at same dt

## Validation in RenSim

Use vendordep/tests/integration_test.cpp to verify:

- explicit vs semi-implicit ordering differences
- expected free-fall position/velocity bounds over fixed simulated time
- damping trends and kinematic body behavior

## Related Pages

- [Units and Conventions](units_and_conventions.md)
- [Vector3](vector.md)
- [Quaternion](quaternion.md)
- [Matrix3](matrix.md)
