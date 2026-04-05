#pragma once

#include "frcsim/gamepiece/ball_gamepiece_sim.hpp"

namespace frcsim {

class Season2026BallSim : public BallGamepieceSim {
  public:
	using BallGamepieceSim::BallGamepieceSim;

	static BallPhysicsSim3D::BallProperties defaultSeasonBallProperties() {
		return BallGamepieceSim::season2026BallProperties();
	}

	static BallPhysicsSim3D::Config defaultSeasonBallConfig() {
		return BallGamepieceSim::season2026BallConfig();
	}
};

}  // namespace frcsim
