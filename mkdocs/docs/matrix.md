# Matrix3 (`frcsim::Matrix3`)

This page explains how `Matrix3` is used mathematically in RenSim, including frame transforms, inertia-tensor rotation, and common numerical pitfalls.

## Why this page exists

`Matrix3` is central to rotational dynamics, coordinate transforms, and body-to-world inertia conversion. This page focuses on meaning and usage, not just API signatures.

## Conventions Used in RenSim

Before applying formulas, keep these conventions consistent.

- Vectors are treated as column vectors.
- Matrix-vector multiplication is written as $$v' = Rv$$.
- Rotation matrices map vectors between frames depending on definition of $R$:
  - If $R_{wb}$ is body-to-world, then $$v_w = R_{wb} v_b$$.
  - Its inverse is transpose for pure rotations: $$R_{bw} = R_{wb}^T$$.
- Rotation matrices should satisfy:
  - Orthogonality: $$R^TR = I$$
  - Unit determinant: $$\det(R) = 1$$

## Core Operations and Physical Meaning

### Determinant

For a 3x3 matrix, determinant measures orientation/volume scaling.

- $$\det(R)=1$$ for a proper rotation.
- $$\det(R) \approx 0$$ indicates singular/near-singular behavior for general matrices.

In simulation, an unexpected determinant can indicate drift or an invalid transform.

### Transpose

- For general matrices, transpose swaps rows/columns.
- For rotation matrices, transpose is inverse.

That makes transpose the fast path for switching transform direction:

- $$v_b = R_{wb}^T v_w$$

### Inverse

For general matrices, inverse recovers original vectors via $$v = A^{-1}(Av)$$.

In rigid-body math, the most common inverse usage is with inertia tensors or transform matrices. Avoid inverting near-singular matrices without conditioning checks.

## Frame Transform Worked Example

Assume a body-frame force:

$$
F_b = \begin{bmatrix}2\\0\\0\end{bmatrix}\,\text{N}
$$

and a 90 degree rotation around +Z from body to world:

$$
R_{wb} =
\begin{bmatrix}
0 & -1 & 0 \\
1 & 0 & 0 \\
0 & 0 & 1
\end{bmatrix}
$$

Then:

$$
F_w = R_{wb}F_b =
\begin{bmatrix}
0\\2\\0
\end{bmatrix}\,\text{N}
$$

So a body +X force appears as world +Y after this orientation.

## Inertia Tensor Transform (Body to World)

A rigid body usually stores inertia in body principal axes as $I_b$.

To integrate world-frame angular dynamics, convert to world frame:

$$
I_w = R_{wb} I_b R_{wb}^T
$$

This is the standard congruence transform that preserves positive definiteness and physical meaning under rotation.

### Why this matters

Angular acceleration in world frame is approximately:

$$
\alpha_w = I_w^{-1}\tau_w
$$

Using the wrong frame or wrong transform order will produce incorrect spin response.

## Inertia Transform Numeric Example

Let:

$$
I_b = \operatorname{diag}(2, 1, 3)\,\text{kg·m}^2
$$

with the same 90 degree +Z rotation matrix above. Applying
$$I_w = R_{wb}I_bR_{wb}^T$$ swaps X and Y principal moments for this case:

$$
I_w = \operatorname{diag}(1, 2, 3)\,\text{kg·m}^2
$$

Interpretation: the body's principal inertia axes rotated with the body, so world-axis inertias change accordingly.

## Numerical Stability Notes

- Keep rotation matrices orthonormal. If generated from quaternions, ensure quaternion normalization is maintained upstream.
- Guard near-singular inversions with determinant/condition checks.
- Use tolerances for equality checks in tests (never exact float compare unless expected exact identity construction).
- Prefer frame-consistent pipelines:
  - accumulate torques in one frame
  - compute inertia in the same frame
  - integrate in that frame

## Common mistakes

- Mixing row-vector and column-vector conventions in the same derivation.
- Applying transform in wrong order, such as $R^TIR$ when code expects $RIR^T$ for body-to-world.
- Treating a non-orthonormal matrix as a rotation matrix.
- Combining body-frame torque with world-frame inertia.
- Inverting matrices repeatedly in hot loops when transpose or cached forms suffice.

## Validation in RenSim

Use math and dynamics tests to sanity-check conventions and transforms:

- `vendordep/tests/math_test.cpp`
- `vendordep/tests/integration_test.cpp`

A practical validation sequence:

1. Start with identity orientation and diagonal inertia tensor.
2. Apply a known quaternion rotation.
3. Recompute transformed inertia and verify expected axis swap/rotation behavior.
4. Apply a torque and verify angular acceleration direction and magnitude are plausible.

## Related Pages

- [Vector3](vector.md)
- [Quaternion](quaternion.md)
- [Integrators](integrators.md)
- [Physics Reference](physics_reference.md)
