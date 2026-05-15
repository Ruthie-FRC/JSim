# Annotated Classes — API Guide

This page summarizes the project's core class concepts and how to find and use annotated classes in the source.

Overview
- The codebase is organized into a native C++ core, CAD import and model layers, and language bindings. Core classes represent simulation state (world, bodies, constraints), model definitions (drivetrains, mechanisms), and helpers (math primitives, integrators).

Where to find classes
- Native C++: see `core/src` for the core simulation types and solvers.
- Python helpers and wrappers: see `python/` and `examples/python/simple_world_demo.py` for usage patterns.
- Java examples and bindings: see `jsim/examples` and `jsim/java`.

How to use
- Instantiate a simulation "world", create bodies or model instances, configure their properties (mass, inertia, joints), and advance the simulation with a fixed timestep.
- Collect state (pose, velocity) each step and feed it to logging, telemetry, or a robot runtime.

Notes
- This page is a human-oriented summary; for API reference consult the implementation files in `core/src` and the example programs in `examples/` for concrete call patterns.

