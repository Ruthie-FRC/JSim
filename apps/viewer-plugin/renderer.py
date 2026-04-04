from __future__ import annotations

from collections import defaultdict
from typing import Dict, List, Tuple

from overlays import draw_overlay_text


def render_timeline_matplotlib(
	timeline,
	field_width_m: float,
	field_height_m: float,
	output_png: str,
) -> None:
	"""Renders a top-down frame of the latest simulation state and recent trajectories."""

	try:
		import matplotlib.pyplot as plt
	except ImportError as exc:
		raise RuntimeError("matplotlib is required for rendering") from exc

	if len(timeline) == 0:
		raise ValueError("timeline is empty")

	fig, ax = plt.subplots(figsize=(12, 6), dpi=120)
	ax.set_xlim(0.0, field_width_m)
	ax.set_ylim(0.0, field_height_m)
	ax.set_aspect("equal", adjustable="box")
	ax.set_facecolor("#f7fbff")
	ax.grid(color="#d6dde5", linewidth=0.7, alpha=0.7)
	ax.set_title("RenSim Arena Visual", fontsize=14)
	ax.set_xlabel("X (m)")
	ax.set_ylabel("Y (m)")

	traces: Dict[str, List[Tuple[float, float]]] = defaultdict(list)
	for frame in timeline.frames():
		for body in frame.bodies:
			traces[body.name].append(body.position_m)

	for name, pts in traces.items():
		if len(pts) < 2:
			continue
		xs = [p[0] for p in pts[-150:]]
		ys = [p[1] for p in pts[-150:]]
		ax.plot(xs, ys, linewidth=1.3, alpha=0.65, label=f"{name} path")

	latest = timeline.frame(len(timeline) - 1)
	for body in latest.bodies:
		circle = plt.Circle(body.position_m, body.radius_m, color=body.color, alpha=0.9)
		ax.add_patch(circle)
		vx, vy = body.velocity_mps
		ax.arrow(
			body.position_m[0],
			body.position_m[1],
			vx * 0.15,
			vy * 0.15,
			head_width=max(0.03, body.radius_m * 0.22),
			head_length=max(0.05, body.radius_m * 0.30),
			length_includes_head=True,
			color="#2c3e50",
			alpha=0.8,
		)
		ax.text(
			body.position_m[0],
			body.position_m[1] + body.radius_m + 0.06,
			body.name,
			ha="center",
			va="bottom",
			fontsize=9,
			color="#1f2b38",
		)

	for body_a, body_b in latest.contacts:
		ax.text(
			0.99,
			0.02,
			f"contact: {body_a} <-> {body_b}",
			transform=ax.transAxes,
			ha="right",
			va="bottom",
			fontsize=9,
			color="#9f2f2f",
		)

	draw_overlay_text(ax, time_s=latest.time_s, tick=latest.tick, contacts=latest.contacts)

	handles, labels = ax.get_legend_handles_labels()
	if handles:
		unique = dict(zip(labels, handles))
		ax.legend(unique.values(), unique.keys(), loc="upper right", fontsize=8)

	fig.tight_layout()
	fig.savefig(output_png)
	plt.close(fig)
