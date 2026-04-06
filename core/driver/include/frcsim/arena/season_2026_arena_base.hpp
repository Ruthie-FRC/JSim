#pragma once

#include "frcsim/arena/simulated_arena.hpp"
#include "frcsim/gamepiece/ball_gamepiece_presets.hpp"

namespace frcsim {

class Season2026ArenaBase : public SimulatedArena {
  public:
    Season2026ArenaBase() : SimulatedArena(seasonFieldConfig()) {
    BallGamepiecePresets::configureSeason2026Field(gamepieceSim());
        gamepieceSim().registerGamePieceType(defaultBallGamePiece());
    }

    static const BallGamepieceSim::FieldConfig& seasonFieldConfig() {
    static const BallGamepieceSim::FieldConfig config = BallGamepiecePresets::season2026FieldConfig();
        return config;
    }

    static BallGamepieceSim::GamePieceInfo defaultBallGamePiece() {
        BallGamepieceSim::GamePieceInfo season_ball;
        season_ball.type = "Ball";
        season_ball.physics_config = BallGamepiecePresets::season2026BallConfig();
        season_ball.ball_properties = BallGamepiecePresets::season2026BallProperties();
        season_ball.spawn_on_ground_after_projectile = true;
        return season_ball;
    }
};

}  // namespace frcsim