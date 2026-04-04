from __future__ import annotations

from dataclasses import dataclass
from typing import Tuple


Vec2 = Tuple[float, float]


@dataclass(frozen=True)
class TopDownCamera:
	"""Simple orthographic camera for converting world meters to screen pixels."""

	field_width_m: float
	field_height_m: float
	padding_px: float = 30.0

	def world_to_screen(self, point_m: Vec2, width_px: int, height_px: int) -> Vec2:
		usable_w = max(width_px - 2.0 * self.padding_px, 1.0)
		usable_h = max(height_px - 2.0 * self.padding_px, 1.0)
		sx = self.padding_px + (point_m[0] / self.field_width_m) * usable_w
		sy = self.padding_px + ((self.field_height_m - point_m[1]) / self.field_height_m) * usable_h
		return sx, sy
