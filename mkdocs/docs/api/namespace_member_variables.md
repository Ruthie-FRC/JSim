# Namespace Variables — Guide

Constants and global configuration
- Global constants and configuration flags (e.g., default timestep, gravity) are declared in header files in `core/include` or module-level config files in `cad-import`.

Changing defaults
- Prefer passing configuration via constructors or configuration objects rather than mutating globals at runtime.

