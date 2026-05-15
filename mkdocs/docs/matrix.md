# Matrix3

3x3 matrix utilities used for rotations, inertia tensors, and transforms.

Examples

```py
R = Matrix3.from_axis_angle(axis, angle)
I_world = R * I_body * R.transpose()
```
