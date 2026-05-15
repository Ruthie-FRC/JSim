# Macros & Compile-time Helpers — Guide

Where macros are used
- Macros are primarily used for header guards, conditional compilation, and small helper macros in the native C++ code under `core/include` and `python/` binding glue.

Advice
- Prefer functions or inline templates for functionality; reserve macros for preprocessor-level concerns.

