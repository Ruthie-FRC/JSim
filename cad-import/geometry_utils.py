"""
Geometry utilities for CAD parsing and manipulation.
Handles transformations, scale conversions, and basic math required for importing models.
"""

import math
import logging

def euler_from_quaternion(x, y, z, w):
    """
    Convert a quaternion (x, y, z, w) to euler angles (roll, pitch, yaw) in radians.
    """
    t0 = +2.0 * (w * x + y * z)
    t1 = +1.0 - 2.0 * (x * x + y * y)
    roll = math.atan2(t0, t1)

    t2 = +2.0 * (w * y - z * x)
    t2 = +1.0 if t2 > +1.0 else t2
    t2 = -1.0 if t2 < -1.0 else t2
    pitch = math.asin(t2)

    t3 = +2.0 * (w * z + x * y)
    t4 = +1.0 - 2.0 * (y * y + z * z)
    yaw = math.atan2(t3, t4)

    return [roll, pitch, yaw]

def normalize_vector(v):
    """Normalizes a 3D vector."""
    mag = math.sqrt(v[0]**2 + v[1]**2 + v[2]**2)
    if mag == 0:
        return [0.0, 0.0, 0.0]
    return [v[0]/mag, v[1]/mag, v[2]/mag]

def apply_scale(vertices, scale_factor=1.0):
    """Applies a uniform scale factor to a list of vertices."""
    return [[v[0]*scale_factor, v[1]*scale_factor, v[2]*scale_factor] for v in vertices]
