# JSim

JSim is a modular FRC physics and simulation library for building, testing, and visualizing robot mechanisms and games.

This documentation provides an overview of the project, quickstart instructions to get a local development environment running, and links to the generated API reference and design docs.

!!! info "Generated API"
    The API reference is generated from Doxygen comments and source snippets using MkDoxy.

## Features

- Modular simulation building blocks for robot subsystems and mechanisms
- Field and gamepiece definitions used by examples and tests
- Python and C++ bindings with examples and demos
- Tools for importing CAD, exporting assets, and visualizing simulations

## Quickstart

1. Build and run tests:

    ```bash
    scripts/run-tests.sh
    ```

2. Install docs preview tools (recommended inside a virtualenv):

    ```bash
    pip install mkdocs mkdocs-material mkdoxy
    sudo apt-get install doxygen
    ```

3. Preview the docs locally:

    ```bash
    PYTHONPATH=mkdocs mkdocs serve --config-file mkdocs/mkdocs.yml
    ```

## Documentation

- [Getting Started](getting_started.md)
- [Architecture](architecture.md)
- [API Reference](api/links.md)

## Contributing

See the contribution guidelines in the repository root for instructions on development workflow, testing, and submitting changes.

For issues or questions, open an issue on the GitHub repository.


