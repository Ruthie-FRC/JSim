import pytest
import math
from geometry_utils import euler_from_quaternion, normalize_vector, apply_scale
from urdf_parser import URDFParser
from stl_loader import STLLoader

def test_quaternion_to_euler():
    # Identity quaternion
    r, p, y = euler_from_quaternion(0, 0, 0, 1)
    assert r == 0.0
    assert p == 0.0
    assert y == 0.0

def test_normalize_vector():
    v = normalize_vector([3.0, 0.0, 4.0])
    assert v == [0.6, 0.0, 0.8]

def test_apply_scale():
    verts = [[1.0, 2.0, 3.0], [0.0, 0.0, 0.0]]
    scaled = apply_scale(verts, 2.0)
    assert scaled == [[2.0, 4.0, 6.0], [0.0, 0.0, 0.0]]

def test_urdf_parser_missing_file():
    parser = URDFParser()
    result = parser.parse("nonexistent_file.urdf")
    assert result is False

def test_stl_loader_missing_file():
    loader = STLLoader()
    result = loader.load("nonexistent_file.stl")
    assert result is False
