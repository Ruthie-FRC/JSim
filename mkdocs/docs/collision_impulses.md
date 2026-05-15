# Collision Impulses

JSim uses an impulse-based collision model for resolving penetrations and contacts.

Key points
- Impulses are computed using relative velocities, effective mass, restitution, and friction.
- Contacts may be resolved with sequential impulses or a constraint solver depending on the scenario.

For deep dives, examine the core solver implementation in `core/src`.
