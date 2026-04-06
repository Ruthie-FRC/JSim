#pragma once

#include "frcsim/gamepiece/ball_gamepiece_presets.hpp"

namespace frcsim {

class Season2026BallSim : public BallGamepieceSim {
  public:
	using BallGamepieceSim::BallGamepieceSim;

	static BallPhysicsSim3D::BallProperties defaultSeasonBallProperties() {
		return BallGamepiecePresets::season2026BallProperties();
	}

	static BallPhysicsSim3D::Config defaultSeasonBallConfig() {
		return BallGamepiecePresets::season2026BallConfig();
	}
};

}  // namespace frcsim
