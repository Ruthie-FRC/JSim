from __future__ import annotations

from typing import Iterable


def draw_overlay_text(ax, *, time_s: float, tick: int, contacts: Iterable[tuple[str, str]]) -> None:
	"""Renders HUD text with timing and collision counts."""

	contact_count = sum(1 for _ in contacts)
	ax.text(
		0.01,
		0.99,
		f"t={time_s:5.2f}s  tick={tick:04d}  contacts={contact_count}",
		transform=ax.transAxes,
		va="top",
		ha="left",
		fontsize=10,
		color="#203040",
		bbox={"facecolor": "#ffffffdd", "edgecolor": "#203040", "boxstyle": "round,pad=0.25"},
	)
