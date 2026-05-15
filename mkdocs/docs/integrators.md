# Integrators

Supported numerical integrators and guidance:

- Explicit Euler: simple, fast, but unstable for stiff systems
- Semi-implicit (symplectic) Euler: better energy behaviour for many robotics systems
- Runge–Kutta 4 (RK4): higher accuracy, more expensive — useful for offline accuracy checks

Recommendation: use fixed timestep with semi-implicit Euler for real-time simulations; use RK4 for verification.
