# Architecture

JSim is organized into layered subsystems:

- Core physics engine: numerical integrators, collision handling, contacts
- Models: drivetrain, mechanisms, aerodynamics, ball and field elements
- IO and CAD: URDF/STL import, material definitions
- Language bindings: Python, Java, and native C++ APIs
- Examples and runtime: robotics runtime, sensor pipeline, and sample robots

Extension points
- Add new models by implementing the model interface in `cad-import/mechanisms.py` or C++ equivalents.
- Bindings are generated from the native API; update `python/bindings.cpp` and build scripts to expose new functions.
