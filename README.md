# RenSim Physics Library

Modular FRC physics infrastructure for simulation, analysis, and robotics workflow integration.

[![CI](https://github.com/Ruthie-FRC/RenSim/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Ruthie-FRC/RenSim/actions/workflows/ci.yml)
[![Docs Deploy](https://github.com/Ruthie-FRC/RenSim/actions/workflows/deploy-mkdocs.yml/badge.svg?branch=main)](https://github.com/Ruthie-FRC/RenSim/actions/workflows/deploy-mkdocs.yml)
[![Latest Release](https://img.shields.io/github/v/release/Ruthie-FRC/RenSim)](https://github.com/Ruthie-FRC/RenSim/releases)
[![License](https://img.shields.io/github/license/Ruthie-FRC/RenSim)](LICENSE.txt)

## Overview

RenSim is organized as a monorepo spanning the physics core, runtime apps, language bindings, examples, and documentation.

## Repository Layout

| Path | Purpose |
| --- | --- |
| `core/driver/` | C++ physics engine implementation and headers |
| `core/java/` | Java code and bindings |
| `core/python/` | Python code and bindings |
| `core/bindings-java/` | Java binding support utilities |
| `core/gamepiece-models/` | Gamepiece model definitions |
| `core/physics-core/` | Shared physics core components |
| `apps/sim-runtime/` | Python runtime integration app |
| `apps/viewer-plugin/` | Visualization and rendering plugin |
| `cad-import/` | CAD and geometry import utilities |
| `examples/` | C++, Java, and Python examples |
| `mkdocs/` | Documentation source and MkDocs config |
| `vendordep/` | WPILib vendordep packaging, Gradle config, and tests |
| `physics-core/` | Additional physics core headers and source |

## Quick Start

### Build (C++)

```bash
cmake -S . -B build
cmake --build build -j
```

### Run Tests

Run the main repository test pipeline:

```bash
./scripts/run-tests.sh
```

Pass through additional Gradle flags when needed:

```bash
./scripts/run-tests.sh --info
```

Run CTest-native tests from the CMake build directory:

```bash
ctest --test-dir build --output-on-failure
```

### Build and Preview Docs

```bash
pip install mkdocs mkdocs-material
mkdocs serve --config-file mkdocs/mkdocs.yml
```

## License

This project is licensed under the terms in `LICENSE.txt`.
