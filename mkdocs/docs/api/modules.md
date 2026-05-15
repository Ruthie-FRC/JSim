# Modules — API Guide

Concept
- The repository is arranged in logical modules: `core` (physics kernel), `cad-import` (geometry, URDF/STL handling), `apps` (runtimes and examples), `python` and `jsim/java` (bindings and examples).

Typical module responsibilities
- `core`: integrators, collision, constraints, and low-level solvers.
- `cad-import`: parsing and converting CAD artifacts to runtime geometry.
- `apps`: example robot runtimes, sensor pipelines, and robot loaders.

Using modules
- Build the whole project with the top-level build scripts. Use examples in each module to learn API usage: `examples/cpp`, `examples/python`, `jsim/examples`.

