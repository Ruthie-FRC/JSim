from __future__ import annotations

import json
from typing import Dict, Iterable, List


def flatten_for_networktables(packet: Dict[str, object]) -> Dict[str, float]:
	"""Flattens structured sensor packets into scalar keys for NT-like transport."""

	out: Dict[str, float] = {
		"sim/tick": float(packet["tick"]),
		"sim/time_s": float(packet["time_s"]),
		"sim/contact_count": float(packet["contact_count"]),
	}

	for i, body in enumerate(packet["bodies"]):
		out[f"sim/body/{i}/x_m"] = float(body["x_m"])
		out[f"sim/body/{i}/y_m"] = float(body["y_m"])
		out[f"sim/body/{i}/vx_mps"] = float(body["vx_mps"])
		out[f"sim/body/{i}/vy_mps"] = float(body["vy_mps"])
		out[f"sim/body/{i}/speed_mps"] = float(body["speed_mps"])

	return out


def export_packets_jsonl(packets: Iterable[Dict[str, object]], output_path: str) -> None:
	"""Writes sensor packets as JSON Lines for offline analysis and replay."""

	with open(output_path, "w", encoding="utf-8") as f:
		for packet in packets:
			f.write(json.dumps(packet, separators=(",", ":")) + "\n")


def summarize_packets(packets: List[Dict[str, object]]) -> Dict[str, float]:
	"""Computes simple run statistics for logging and CI assertions."""

	if not packets:
		return {"frames": 0.0, "duration_s": 0.0, "max_contacts": 0.0}

	duration_s = float(packets[-1]["time_s"])
	max_contacts = max(float(packet["contact_count"]) for packet in packets)
	return {"frames": float(len(packets)), "duration_s": duration_s, "max_contacts": max_contacts}
