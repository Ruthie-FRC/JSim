# JSim Physics Library

[![CI](https://github.com/Ruthie-FRC/JSim/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Ruthie-FRC/JSim/actions/workflows/ci.yml)
[![jsim.dev](https://github.com/Ruthie-FRC/JSim/actions/workflows/deploy.yml/badge.svg?branch=main)](https://github.com/Ruthie-FRC/JSim/actions/workflows/deploy.yml)
[![License](https://img.shields.io/github/license/Ruthie-FRC/JSim)](LICENSE.txt)
[![Open Issues](https://img.shields.io/github/issues/Ruthie-FRC/JSim)](https://github.com/Ruthie-FRC/JSim/issues)

JSim is an open source FRC physics library for simulation.

## Overview

This repository is organized as a monorepo containing the core simulation engine, language bindings, runtime apps, examples, and documentation tooling.

## Vendordep URL
```
https://jsim.dev/JSim.json
```

### Build and Test (Gradle)

```bash
./scripts/build-all.sh
```

### Run Tests

Run the vendordep Gradle test task used in CI:

```bash
cd vendordep
./gradlew test
```

This command runs Java tests plus the standalone native verification suite in `vendordep/tests/` through Gradle.

Vendordep build tooling requires Java 17.

### Preview Docs Locally

```bash
pip install mkdocs mkdocs-material mkdoxy
sudo apt-get install doxygen
PYTHONPATH=mkdocs mkdocs serve --config-file mkdocs/mkdocs.yml
```

## Repository Structure

### Core Libraries

- `core/driver/`: C++ physics engine implementation and headers
- `core/java/`: Java-side code and bindings
- `core/python/`: Python-side code and bindings
- `core/bindings-java/`: Java binding support
- `core/gamepiece-models/`: gamepiece model definitions

### Applications

- `apps/sim-runtime/`: Python runtime integration app
- `apps/viewer-plugin/`: visualization and rendering plugin

### Tooling and Integration

- `cad-import/`: CAD and geometry import utilities, including JSim-owned snapshot tracking for simulation integrations
- `examples/`: language-specific examples (C++, Java, Python)
- `mkdocs/`: docs source and MkDocs configuration
- `vendordep/`: WPILib vendordep packaging and Gradle-based build/testing

## Documentation

- Docs source and MkDoxy config: `mkdocs/docs/` and `mkdocs/mkdocs.yml`
- Deployed docs: https://jsim.dev
- Java API (Javadocs): https://jsim.dev/api/javadoc/
- Native and mixed-language API: https://jsim.dev/api/

## Contributing

Contributions are welcome. For code and process expectations, please review:

- `CODE_OF_CONDUCT.md`
- open issues and discussions in this repository

## License

This project is licensed under the terms in `LICENSE.txt`.
