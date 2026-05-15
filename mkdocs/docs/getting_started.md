# Getting Started

This page helps you build and run examples for JSim.

Prerequisites
- Java 17+ (or the version the project targets)
- C++ toolchain (clang/gcc) for native builds
- Python 3.8+ for Python examples

Quick build (recommended)

```bash
scripts/run-tests.sh
```

Run a single example (Python)

```bash
python3 examples/python/simple_world_demo.py
```

Running Java examples

Use Gradle in `vendordep` or the top-level Gradle wrapper:

```bash
./gradlew :examples:java:run
```

Where to go next
- API Usage: api_usage.md
- Examples: check `examples/` and `jsim/examples/` for language-specific samples
