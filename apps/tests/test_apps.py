import pytest
from apps.sim_runtime.main_loop import SimLoop
from viewer_plugin.camera import Camera

def test_sim_loop_init():
    loop = SimLoop(tick_rate_hz=100.0)
    assert loop.tick_rate_hz == 100.0
    assert loop.dt == 0.01
    assert loop._running is False

def test_camera_matrices():
    cam = Camera()
    cam.update_position(1.0, 2.0, 3.0)
    assert cam.position == [1.0, 2.0, 3.0]
    # Verify matrices return lists safely without crashing
    assert isinstance(cam.get_view_matrix(), list)
    assert isinstance(cam.get_projection_matrix(), list)
