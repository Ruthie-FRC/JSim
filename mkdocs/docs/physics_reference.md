# Physics Reference

This page lists the core assumptions and equations used by JSim.

Coordinate frames and units
- Right-handed coordinates are used unless otherwise noted.
- SI units are preferred: meters, seconds, kilograms, radians.

Equations
- Motion: Newton–Euler equations for rigid bodies
- Contacts: impulse-based collision resolution with restitution and friction models

Numerical notes
- Timestepping choices and integrator stability affect energy conservation and drift. See `integrators.md`.
