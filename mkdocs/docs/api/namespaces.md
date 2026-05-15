# Namespaces & Packages — API Guide

Overview
- The native code uses C++ namespaces to separate subsystems. The Python and Java bindings expose idiomatic modules/packages corresponding to these namespaces.

Where to look
- C++ namespaces: `core/src` and `cad-import` source files.
- Python: the `python/` package exposes helpers and glue code.
- Java: `jsim/java` contains Java package layout and examples.

Practical tip
- Search for the symbol you need (class or function name) in the source tree to find the namespace and the header/source pair to inspect implementation and usage.

