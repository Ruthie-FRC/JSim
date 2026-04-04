from __future__ import annotations

from typing import Dict, List


def build_sensor_packet(frame) -> Dict[str, object]:
	"""Converts a simulation frame into a telemetry packet for robot-side consumers."""

	bodies: List[Dict[str, object]] = []
	for body in frame.bodies:
		vx, vy = body.velocity_mps
		speed = (vx * vx + vy * vy) ** 0.5
		bodies.append(
			{
				"name": body.name,
				"x_m": body.position_m[0],
				"y_m": body.position_m[1],
				"vx_mps": vx,
				"vy_mps": vy,
				"speed_mps": speed,
			}
		)

	return {
		"tick": frame.tick,
		"time_s": frame.time_s,
		"contact_count": len(frame.contacts),
		"bodies": bodies,
	}
