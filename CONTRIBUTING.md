# Contributing to JSim

Welcome to the JSim project! This guide outlines the conventions and rules to follow when making contributions.

## Codebase Organization Conventions

### Core Physics & C++ Driver (`core/driver/`)
- **Headers & Implementations:** All `.hpp` files belong in `include/` and `.cpp` files in `src/`. 
- **Doxygen Documentation:** Every class, struct, and method MUST be fully documented using standard Doxygen tags (`@brief`, `@param`, `@return`).
- **Bindings:** Any new feature (bodies, joint constraints, simulation ticks) must be exposed cleanly in both `core/python/bindings.cpp` (PyBind11) and `vendordep/src/` (Java JNI).

### Apps & Simulation Runtime (`apps/`)
- Python simulation entry points (Main Loop, Vis, Assets) reside here.
- Maintain determinism: The main loop (`apps/sim-runtime/main_loop.py`) forces fixed-timestep loops. Do not drift `dt` with raw system time.
- Settings are exposed via typed dataclasses or parsed configuration, *not* hardcoded values.

### CAD Import (`cad-import/`)
- Keep URDF/STL parsers robust. Always assume malformed XML/binary input and fail gracefully.
- **Field Definitions:** Historic Python scripts with hardcoded bounding boxes are deprecated. We use JSON/YAML configuration to define fields and environments.

### Testing & CI (`vendordep/tests/` and `.github/`)
- FRC C++ logic matches standard `googletest` syntax. Add tests for all new mechanisms (`drivetrain`, `collision`).
- All code must pass the GitHub Actions CI pipelines (Windows, Linux, macOS).
- Use `./scripts/run-tests.sh` to validate locally before making a PR.
