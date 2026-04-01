A Currently Untitled FRC Physics Library

## Monorepo Structure

This repository is organized as a monorepo.

- **core/**
	- **core/physics-core/**: Core C++ physics engine source code.
	- **core/bindings-java/**: Java bindings for the physics engine.
	- **core/bindings-python/**: Python bindings for the physics engine.
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