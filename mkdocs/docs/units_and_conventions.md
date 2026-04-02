# Units and Conventions

This page defines common units, axis conventions, and frame rules used across RenSim math docs.

## Base units

- Position: meters (m)
- Velocity: meters per second (m/s)
- Acceleration: meters per second squared (m/s^2)
- Force: newtons (N)
- Torque: newton-meters (N*m)
- Mass: kilograms (kg)
- Time step: seconds (s)
- Angle: radians (rad)
- Angular velocity: radians per second (rad/s)
- Angular acceleration: radians per second squared (rad/s^2)

## Core vector conventions

- Dot product: $$a \cdot b = |a||b|\cos\theta$$
- Cross product: right-hand rule
- Torque from lever arm and force: $$\tau = r \times F$$

## Frame naming

- Body frame quantities use subscript b.
- World frame quantities use subscript w.
- Example transform:
  - $$v_w = R_{wb} v_b$$

## Quaternion convention

- Quaternion is ordered as (w, x, y, z).
- Vector rotation uses:
  - $$v' = q v q^{-1}$$
- q and -q encode the same orientation.

## Integrator conventions

- Fixed dt is preferred for repeatability.
- Semi-implicit Euler updates velocity before position.
- Explicit Euler updates position before velocity.

## Aerodynamics conventions

- Drag force points opposite velocity direction.
- Dynamic pressure:
  - $$q = \frac{1}{2}\rho v^2$$
- Drag magnitude (quadratic term):
  - $$|F_d| = q C_d A$$

## Boundary and collision sign conventions

- Contact normal points from surface toward permitted space.
- Positive separation means no penetration.
- Impulses along normal oppose closing velocity.

## Common mistakes checklist

- Mixing body and world frame terms in one equation.
- Using degrees in trig where radians are expected.
- Using force where acceleration is required (missing divide by mass).
- Forgetting sign direction for drag and friction.
