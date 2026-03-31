A Currently Untitled FRC Physics Library

## Monorepo Structure

This repository is organized as a monorepo:

- **examples/**: Contains example projects, each in its own language-specific subfolder (e.g., `cpp/`, `java/`, `python/`).
- **physics-core/**: Core C++ physics engine source code.
- **bindings-java/**: Java bindings for the physics engine.
- **bindings-python/**: Python bindings for the physics engine.
- **cad-import/**: CAD import utilities and scripts.
- **sim-runtime/**: Python simulation runtime and robot interface code.
- **viewer-plugin/**: Visualization and rendering plugins.
- **gamepiece-models/**: C++ models for FRC game pieces.
- **vendordep/**: Reserved for vendordep generator code and related files (currently empty).
- **docs/** and **mkdocs/**: Documentation sources and site generator config.
- **tests/**: C++ unit and integration tests.

All source code is kept in its own dedicated folder. Example projects are separated by language and purpose. Any vendordep generator code should be placed under `/vendordep`.