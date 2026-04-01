# RemSim Physics Library

RemSim is a modular FRC physics library for simulation, analysis, and robotics workflow integration.

## Monorepo Structure

This repository is organized as a monorepo.

- **core/**
	- **core/cpp/**: Core C++ physics engine source code.
	- **core/java/**: Java bindings for the physics engine.
	- **core/python/**: Python bindings for the physics engine.
	- **core/gamepiece-models/**: C++ gamepiece models used by the simulation core (season-specific models may be split later if needed).
- **apps/**
	- **apps/sim-runtime/**: Separate Python runtime application that integrates the engine with robot-side simulation workflows.
	- **apps/viewer-plugin/**: Separate visualization/rendering application plugin for simulation output.
- **examples/**: Example projects in language-specific subfolders (e.g., `cpp/`, `java/`, `python/`).
- **docs/** and **mkdocs/**: Documentation sources and site generator config.
- **vendordep/**: Reserved for vendordep generator code and related files.
- **vendordep/tests/**: Unit and integration tests.
- **cad-import/**: CAD import utilities and scripts; this can be split into its own repository later if it remains loosely coupled to the core.

All source code is kept in dedicated folders by responsibility.