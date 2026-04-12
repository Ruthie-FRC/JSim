"""
Physics engine for JSim - Drag, Friction, and Aerodynamics.

Comprehensive physics calculations for:
- Air drag (linear and quadratic)
- Friction models (kinetic, static, rolling)
- Aerodynamic forces (Magnus effect, pressure drag)
- Material interactions
- Energy dissipation

Based on discussion #36 and general FRC physics principles.
"""

import math
from dataclasses import dataclass
from typing import Dict, Tuple, Optional, Any
from enum import Enum
import logging

logger = logging.getLogger(__name__)


class DragModel(Enum):
    """Available drag models."""
    LINEAR = "linear"           # F_drag = -b*v (low speed, viscous)
    QUADRATIC = "quadratic"     # F_drag = -0.5*ρ*C_d*A*v²
    HYBRID = "hybrid"           # Transition between linear and quadratic
    MAGNUS = "magnus"           # Spinning ball Magnus effect


@dataclass
class AerodynamicProperties:
    """Aerodynamic properties of an object."""
    
    # Drag coefficient (dimensionless)
    drag_coefficient: float = 1.0
    
    # Cross-sectional area (m²)
    cross_sectional_area: float = 1.0
    
    # Linear drag coefficient (kg/s) for viscous drag
    linear_drag_coefficient: float = 0.1
    
    # Drag model to use
    drag_model: DragModel = DragModel.QUADRATIC
    
    # Magnus coefficient (for spinning objects)
    magnus_coefficient: float = 0.0
    
    # For spherical objects: diameter
    diameter: Optional[float] = None
    
    def calculate_cross_section(self) -> float:
        """Calculate cross-sectional area from diameter if not set."""
        if self.diameter:
            radius = self.diameter / 2.0
            return math.pi * radius * radius
        return self.cross_sectional_area


@dataclass
class FrictionProperties:
    """Friction properties for material interaction."""
    
    # Coefficient of kinetic friction
    kinetic_friction: float = 0.3
    
    # Coefficient of static friction
    static_friction: float = 0.4
    
    # Coefficient of rolling friction (for wheels/rolling objects)
    rolling_friction: float = 0.01
    
    # Friction model: "coulomb", "viscous", or "hybrid"
    friction_model: str = "coulomb"


@dataclass
class MaterialPhysicsProperties:
    """Complete physics properties for a material."""
    
    name: str
    density: float  # kg/m³
    friction: FrictionProperties
    aerodynamic: AerodynamicProperties
    restitution: float = 0.3  # Coefficient of restitution
    damping: float = 0.1      # Energy loss factor


# Physics constants
GRAVITY = 9.81  # m/s²
AIR_DENSITY = 1.225  # kg/m³ at sea level


class DragCalculator:
    """Calculates drag forces on objects."""
    
    @staticmethod
    def calculate_linear_drag(velocity: Tuple[float, float, float],
                            linear_drag_coefficient: float) -> Tuple[float, float, float]:
        """Calculate linear (viscous) drag force.
        
        F_drag = -b * v (Stokes drag)
        
        Args:
            velocity: Velocity vector (vx, vy, vz) in m/s
            linear_drag_coefficient: Drag coefficient b (kg/s)
        
        Returns:
            Drag force vector (Fx, Fy, Fz) in Newtons
        """
        vx, vy, vz = velocity
        return (
            -linear_drag_coefficient * vx,
            -linear_drag_coefficient * vy,
            -linear_drag_coefficient * vz
        )
    
    @staticmethod
    def calculate_quadratic_drag(velocity: Tuple[float, float, float],
                                drag_coefficient: float,
                                cross_section: float,
                                air_density: float = AIR_DENSITY
                                ) -> Tuple[float, float, float]:
        """Calculate quadratic (pressure) drag force.
        
        F_drag = -0.5 * ρ * C_d * A * v * |v|
        
        Args:
            velocity: Velocity vector (vx, vy, vz) in m/s
            drag_coefficient: Dimensionless drag coefficient
            cross_section: Cross-sectional area in m²
            air_density: Air density in kg/m³
        
        Returns:
            Drag force vector (Fx, Fy, Fz) in Newtons
        """
        vx, vy, vz = velocity
        speed = math.sqrt(vx*vx + vy*vy + vz*vz)
        
        if speed < 1e-6:  # Avoid division by zero
            return (0.0, 0.0, 0.0)
        
        drag_magnitude = 0.5 * air_density * drag_coefficient * cross_section * speed * speed
        
        # Apply drag opposite to velocity direction
        return (
            -drag_magnitude * (vx / speed),
            -drag_magnitude * (vy / speed),
            -drag_magnitude * (vz / speed)
        )
    
    @staticmethod
    def calculate_hybrid_drag(velocity: Tuple[float, float, float],
                            linear_coeff: float,
                            drag_coefficient: float,
                            cross_section: float,
                            transition_speed: float = 1.0
                            ) -> Tuple[float, float, float]:
        """Calculate hybrid drag (transition between linear and quadratic).
        
        At low speeds: F_drag ≈ -b*v (linear)
        At high speeds: F_drag ≈ -0.5*ρ*C_d*A*v² (quadratic)
        
        Args:
            velocity: Velocity vector
            linear_coeff: Linear drag coefficient
            drag_coefficient: Quadratic drag coefficient
            cross_section: Cross-sectional area
            transition_speed: Speed at which to transition models
        
        Returns:
            Drag force vector
        """
        speed = math.sqrt(velocity[0]*velocity[0] + velocity[1]*velocity[1] + velocity[2]*velocity[2])
        
        if speed < transition_speed * 0.5:
            # Low speed: use linear
            return DragCalculator.calculate_linear_drag(velocity, linear_coeff)
        elif speed > transition_speed * 2.0:
            # High speed: use quadratic
            return DragCalculator.calculate_quadratic_drag(velocity, drag_coefficient, cross_section)
        else:
            # Transition region: blend both
            blend_factor = (speed - transition_speed * 0.5) / (transition_speed)
            blend_factor = max(0.0, min(1.0, blend_factor))
            
            linear = DragCalculator.calculate_linear_drag(velocity, linear_coeff)
            quadratic = DragCalculator.calculate_quadratic_drag(velocity, drag_coefficient, cross_section)
            
            return (
                linear[0] * (1 - blend_factor) + quadratic[0] * blend_factor,
                linear[1] * (1 - blend_factor) + quadratic[1] * blend_factor,
                linear[2] * (1 - blend_factor) + quadratic[2] * blend_factor,
            )
    
    @staticmethod
    def calculate_magnus_force(velocity: Tuple[float, float, float],
                              angular_velocity: Tuple[float, float, float],
                              magnus_coefficient: float,
                              cross_section: float
                              ) -> Tuple[float, float, float]:
        """Calculate Magnus effect force (spinning ball).
        
        F_magnus = C_l * 0.5 * ρ * A * v * ω
        
        Used for spinning balls/game pieces.
        
        Args:
            velocity: Linear velocity (vx, vy, vz)
            angular_velocity: Angular velocity (ωx, ωy, ωz)
            magnus_coefficient: Magnus coefficient (dimensionless)
            cross_section: Cross-sectional area
        
        Returns:
            Magnus force vector
        """
        # Magnus force is perpendicular to both velocity and spin axis
        vx, vy, vz = velocity
        wx, wy, wz = angular_velocity
        
        # Cross product: v × ω
        cross_x = vy * wz - vz * wy
        cross_y = vz * wx - vx * wz
        cross_z = vx * wy - vy * wx
        
        # Magnitude of Magnus force
        magnitude = magnus_coefficient * 0.5 * AIR_DENSITY * cross_section
        
        return (magnitude * cross_x, magnitude * cross_y, magnitude * cross_z)


class FrictionCalculator:
    """Calculates friction forces."""
    
    @staticmethod
    def calculate_coulomb_friction(normal_force: float,
                                   kinetic_friction: float,
                                   static_friction: float,
                                   velocity: float,
                                   velocity_threshold: float = 0.001
                                   ) -> float:
        """Calculate Coulomb friction force.
        
        f_k = μ_k * N  (kinetic friction)
        f_s ≤ μ_s * N  (static friction)
        
        Args:
            normal_force: Normal force (N)
            kinetic_friction: Kinetic friction coefficient
            static_friction: Static friction coefficient
            velocity: Current velocity (m/s)
            velocity_threshold: Threshold to consider object at rest
        
        Returns:
            Friction force magnitude (N)
        """
        if abs(velocity) < velocity_threshold:
            # Object at rest: static friction
            return 0.0  # Would be up to μ_s * N, but we don't update velocity here
        else:
            # Object moving: kinetic friction
            return kinetic_friction * normal_force
    
    @staticmethod
    def calculate_rolling_friction(normal_force: float,
                                  rolling_friction: float,
                                  radius: float) -> float:
        """Calculate rolling friction (for wheels/rolling objects).
        
        τ = μ_r * N * r
        
        Args:
            normal_force: Normal force (N)
            rolling_friction: Coefficient of rolling friction
            radius: Radius of wheel/sphere (m)
        
        Returns:
            Friction torque (N⋅m)
        """
        return rolling_friction * normal_force * radius
    
    @staticmethod
    def apply_friction_to_velocity(velocity: Tuple[float, float, float],
                                  friction_force: float,
                                  mass: float,
                                  dt: float) -> Tuple[float, float, float]:
        """Apply friction force to velocity.
        
        Args:
            velocity: Current velocity
            friction_force: Friction force magnitude
            mass: Object mass (kg)
            dt: Time step (s)
        
        Returns:
            Updated velocity
        """
        speed = math.sqrt(velocity[0]**2 + velocity[1]**2 + velocity[2]**2)
        
        if speed < 1e-6:
            return velocity
        
        # Friction decelerates object
        deceleration = friction_force / mass
        new_speed = max(0, speed - deceleration * dt)
        
        # Maintain direction
        factor = new_speed / speed if speed > 0 else 0
        return (velocity[0] * factor, velocity[1] * factor, velocity[2] * factor)


class CollisionPhysics:
    """Handles collision physics between objects."""
    
    @staticmethod
    def calculate_impulse(mass1: float, mass2: float,
                         velocity1: Tuple[float, float, float],
                         velocity2: Tuple[float, float, float],
                         restitution: float = 0.5,
                         normal: Tuple[float, float, float] = (0, 0, 1)
                         ) -> Tuple[Tuple[float, float, float], Tuple[float, float, float]]:
        """Calculate post-collision velocities using impulse-based dynamics.
        
        Args:
            mass1: Mass of object 1
            mass2: Mass of object 2
            velocity1: Velocity of object 1 before collision
            velocity2: Velocity of object 2 before collision
            restitution: Coefficient of restitution (0-1)
            normal: Surface normal at collision point
        
        Returns:
            (velocity1_after, velocity2_after)
        """
        # Relative velocity
        rel_vel = (
            velocity2[0] - velocity1[0],
            velocity2[1] - velocity1[1],
            velocity2[2] - velocity1[2]
        )
        
        # Relative velocity along normal
        vel_along_normal = sum(rel_vel[i] * normal[i] for i in range(3))
        
        # Don't calculate if velocities are separating
        if vel_along_normal > 0:
            return velocity1, velocity2
        
        # Calculate impulse magnitude
        e = restitution
        total_mass_inverse = 1.0 / mass1 + 1.0 / mass2
        impulse_magnitude = -(1 + e) * vel_along_normal / total_mass_inverse
        
        # Impulse vector
        impulse = (
            impulse_magnitude * normal[0],
            impulse_magnitude * normal[1],
            impulse_magnitude * normal[2]
        )
        
        # Update velocities
        v1_after = (
            velocity1[0] + impulse[0] / mass1,
            velocity1[1] + impulse[1] / mass1,
            velocity1[2] + impulse[2] / mass1
        )
        
        v2_after = (
            velocity2[0] - impulse[0] / mass2,
            velocity2[1] - impulse[1] / mass2,
            velocity2[2] - impulse[2] / mass2
        )
        
        return v1_after, v2_after
    
    @staticmethod
    def is_collision(pos1: Tuple[float, float, float],
                    radius1: float,
                    pos2: Tuple[float, float, float],
                    radius2: float) -> bool:
        """Check if two spheres are colliding.
        
        Args:
            pos1: Position of object 1
            radius1: Radius of object 1
            pos2: Position of object 2
            radius2: Radius of object 2
        
        Returns:
            True if colliding
        """
        dx = pos2[0] - pos1[0]
        dy = pos2[1] - pos1[1]
        dz = pos2[2] - pos1[2]
        
        distance = math.sqrt(dx*dx + dy*dy + dz*dz)
        return distance < (radius1 + radius2)


class PhysicsIntegrator:
    """Integrates forces to update position and velocity."""
    
    @staticmethod
    def euler_step(position: Tuple[float, float, float],
                  velocity: Tuple[float, float, float],
                  acceleration: Tuple[float, float, float],
                  dt: float) -> Tuple[Tuple[float, float, float], Tuple[float, float, float]]:
        """Euler integration step.
        
        Simple but fast. Lower accuracy.
        
        Args:
            position: Current position
            velocity: Current velocity
            acceleration: Current acceleration
            dt: Time step
        
        Returns:
            (new_position, new_velocity)
        """
        new_velocity = (
            velocity[0] + acceleration[0] * dt,
            velocity[1] + acceleration[1] * dt,
            velocity[2] + acceleration[2] * dt
        )
        
        new_position = (
            position[0] + velocity[0] * dt,
            position[1] + velocity[1] * dt,
            position[2] + velocity[2] * dt
        )
        
        return new_position, new_velocity
    
    @staticmethod
    def verlet_step(position: Tuple[float, float, float],
                   velocity: Tuple[float, float, float],
                   acceleration: Tuple[float, float, float],
                   dt: float) -> Tuple[Tuple[float, float, float], Tuple[float, float, float]]:
        """Verlet integration step.
        
        More stable than Euler. Better energy conservation.
        
        Args:
            position: Current position
            velocity: Current velocity
            acceleration: Current acceleration
            dt: Time step
        
        Returns:
            (new_position, new_velocity)
        """
        # Verlet: x(t+dt) = 2*x(t) - x(t-dt) + a(t)*dt²
        # But we need previous position, so we use velocity form
        
        new_velocity = (
            velocity[0] + acceleration[0] * dt,
            velocity[1] + acceleration[1] * dt,
            velocity[2] + acceleration[2] * dt
        )
        
        avg_velocity = (
            (velocity[0] + new_velocity[0]) / 2,
            (velocity[1] + new_velocity[1]) / 2,
            (velocity[2] + new_velocity[2]) / 2
        )
        
        new_position = (
            position[0] + avg_velocity[0] * dt,
            position[1] + avg_velocity[1] * dt,
            position[2] + avg_velocity[2] * dt
        )
        
        return new_position, new_velocity
