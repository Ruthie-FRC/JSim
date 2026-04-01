#pragma once

#include "frcsim/math/vector.hpp"
#include "frcsim/rigidbody/rigid_body.hpp"

namespace frcsim {

class DragModel {
	public:
		DragModel(double drag_coefficient, double reference_area_m2, double air_density_kgpm3 = 1.225)
				: drag_coefficient_(drag_coefficient),
					reference_area_m2_(reference_area_m2),
					air_density_kgpm3_(air_density_kgpm3) {}

		Vector3 computeForce(const Vector3& velocity_mps) const {
				return Vector3::dragForce(velocity_mps, drag_coefficient_, reference_area_m2_, air_density_kgpm3_);
		}

		void apply(RigidBody& body) const {
				if (body.isStatic()) return;
				body.applyForce(computeForce(body.linearVelocity()));
		}

	private:
		double drag_coefficient_{0.47};
		double reference_area_m2_{0.02};
		double air_density_kgpm3_{1.225};
};

}  // namespace frcsim
