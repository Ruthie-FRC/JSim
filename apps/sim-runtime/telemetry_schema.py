from __future__ import annotations

from dataclasses import dataclass
from math import isfinite
from typing import Dict, Iterable, List, Literal


FrameTag = Literal["w", "b"]


def _require_frame_tag(value: object, field_name: str) -> FrameTag:
	text = str(value)
	if text not in ("w", "b"):
		raise ValueError(f"{field_name} must be 'w' or 'b'")
	return text  # type: ignore[return-value]


def _require_finite(value: float, field_name: str) -> float:
	if not isfinite(value):
		raise ValueError(f"{field_name} must be finite")
	return value


@dataclass(frozen=True)
class BodyTelemetry:
	"""Per-body telemetry payload for one simulation tick."""

	name: str
	x_m: float
	y_m: float
	vx_mps: float
	vy_mps: float
	speed_mps: float
	position_frame_tag: FrameTag = "w"
	velocity_frame_tag: FrameTag = "w"

	def __post_init__(self) -> None:
		if not self.name:
			raise ValueError("name must be non-empty")
		_require_finite(float(self.x_m), "x_m")
		_require_finite(float(self.y_m), "y_m")
		_require_finite(float(self.vx_mps), "vx_mps")
		_require_finite(float(self.vy_mps), "vy_mps")
		if self.speed_mps < 0.0:
			raise ValueError("speed_mps must be >= 0")
		_require_finite(float(self.speed_mps), "speed_mps")
		_require_frame_tag(self.position_frame_tag, "position_frame_tag")
		_require_frame_tag(self.velocity_frame_tag, "velocity_frame_tag")

	def to_dict(self) -> Dict[str, object]:
		return {
			"name": self.name,
			"x_m": self.x_m,
			"y_m": self.y_m,
			"vx_mps": self.vx_mps,
			"vy_mps": self.vy_mps,
			"speed_mps": self.speed_mps,
			"position_frame_tag": self.position_frame_tag,
			"velocity_frame_tag": self.velocity_frame_tag,
		}

	@staticmethod
	def from_dict(raw: Dict[str, object]) -> "BodyTelemetry":
		required = {
			"name",
			"x_m",
			"y_m",
			"vx_mps",
			"vy_mps",
			"speed_mps",
			"position_frame_tag",
			"velocity_frame_tag",
		}
		missing = required.difference(raw.keys())
		if missing:
			raise ValueError(f"body telemetry missing required SI fields: {sorted(missing)}")

		position_frame_tag = _require_frame_tag(raw["position_frame_tag"], "position_frame_tag")
		velocity_frame_tag = _require_frame_tag(raw["velocity_frame_tag"], "velocity_frame_tag")
		return BodyTelemetry(
			name=str(raw["name"]),
			x_m=float(raw["x_m"]),
			y_m=float(raw["y_m"]),
			vx_mps=float(raw["vx_mps"]),
			vy_mps=float(raw["vy_mps"]),
			speed_mps=float(raw["speed_mps"]),
			position_frame_tag=position_frame_tag,
			velocity_frame_tag=velocity_frame_tag,
		)


@dataclass(frozen=True)
class SensorPacket:
	"""Frame-level simulation telemetry payload."""

	tick: int
	time_s: float
	contact_count: int
	bodies: tuple[BodyTelemetry, ...]

	def __post_init__(self) -> None:
		if self.tick < 0:
			raise ValueError("tick must be >= 0")
		if self.time_s < 0.0:
			raise ValueError("time_s must be >= 0")
		if self.contact_count < 0:
			raise ValueError("contact_count must be >= 0")
		_require_finite(float(self.time_s), "time_s")

	def require_world_frames(self) -> None:
		for body in self.bodies:
			if body.position_frame_tag != "w":
				raise ValueError(f"position_frame_tag must be 'w' for flattening: {body.name}")
			if body.velocity_frame_tag != "w":
				raise ValueError(f"velocity_frame_tag must be 'w' for flattening: {body.name}")

	def to_dict(self) -> Dict[str, object]:
		return {
			"tick": self.tick,
			"time_s": self.time_s,
			"contact_count": self.contact_count,
			"bodies": [body.to_dict() for body in self.bodies],
		}

	@staticmethod
	def from_dict(raw: Dict[str, object]) -> "SensorPacket":
		required = {"tick", "time_s", "contact_count", "bodies"}
		missing = required.difference(raw.keys())
		if missing:
			raise ValueError(f"sensor packet missing required SI fields: {sorted(missing)}")

		bodies_raw = raw["bodies"]
		if not isinstance(bodies_raw, Iterable):
			raise TypeError("bodies must be iterable")
		return SensorPacket(
			tick=int(raw["tick"]),
			time_s=float(raw["time_s"]),
			contact_count=int(raw["contact_count"]),
			bodies=tuple(BodyTelemetry.from_dict(body) for body in bodies_raw),
		)


def packet_list_to_dicts(packets: List[SensorPacket]) -> List[Dict[str, object]]:
	"""Converts typed telemetry packets to JSON-friendly dictionaries."""

	return [packet.to_dict() for packet in packets]


def validate_sensor_packet_dict(raw: Dict[str, object]) -> None:
	"""Validates telemetry payload at API boundaries using SI fields and frame tags."""

	SensorPacket.from_dict(raw)
