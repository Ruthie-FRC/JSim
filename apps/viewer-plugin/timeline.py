from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List, Tuple


Vec2 = Tuple[float, float]


@dataclass(frozen=True)
class BodyFrame:
	"""Snapshot of one rigid body at a single simulation time."""

	name: str
	position_m: Vec2
	velocity_mps: Vec2
	radius_m: float
	color: str


@dataclass(frozen=True)
class SimFrame:
	"""Snapshot of arena state for one simulation tick."""

	tick: int
	time_s: float
	bodies: Tuple[BodyFrame, ...]
	contacts: Tuple[Tuple[str, str], ...]


class Timeline:
	"""Stores sequential simulation frames for playback and debugging."""

	def __init__(self) -> None:
		self._frames: List[SimFrame] = []

	def append(self, frame: SimFrame) -> None:
		self._frames.append(frame)

	def __len__(self) -> int:
		return len(self._frames)

	def frame(self, index: int) -> SimFrame:
		return self._frames[index]

	def frames(self) -> Tuple[SimFrame, ...]:
		return tuple(self._frames)

	def latest_positions(self) -> Dict[str, Vec2]:
		if not self._frames:
			return {}
		latest = self._frames[-1]
		return {body.name: body.position_m for body in latest.bodies}
