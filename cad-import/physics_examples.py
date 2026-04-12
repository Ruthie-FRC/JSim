"""
Physics simulation examples demonstrating drag, friction, collisions,
and robot CAD interaction with field elements and game pieces.
"""

import math
from typing import Dict, List, Any

from physics_engine import (
    DragCalculator, FrictionCalculator, CollisionPhysics, PhysicsIntegrator,
    AerodynamicProperties, FrictionProperties, DragModel, GRAVITY, AIR_DENSITY
)
from robot_physics import CADToPhysicsConverter, RobotPhysicsModel, PhysicsBody
from materials import MaterialSystem


def example_1_drag_calculations():
    """Example 1: Calculate drag on different objects at various speeds."""
    print("\n" + "="*70)
    print("Example 1: Drag Force Calculations")
    print("="*70)
    
    # Object with typical FRC game piece aerodynamics
    c_d = 0.47  # Sphere drag coefficient
    area = 0.03  # Cross-section in m²
    
    velocities = [1.0, 2.0, 5.0, 10.0, 15.0]  # m/s
    
    print(f"\nQuadratic Drag (Sphere, C_d={c_d}, A={area}m²):")
    print(f"{'Velocity (m/s)':<20} {'Speed (mph)':<20} {'Drag Force (N)':<20}")
    print("-" * 60)
    
    for v in velocities:
        velocity_vec = (v, 0, 0)
        drag_force = DragCalculator.calculate_quadratic_drag(velocity_vec, c_d, area)
        drag_magnitude = math.sqrt(sum(f**2 for f in drag_force))
        mph = v * 2.237  # Convert m/s to mph
        print(f"{v:<20.1f} {mph:<20.1f} {drag_magnitude:<20.3f}")
    
    # Compare drag models
    print(f"\n\nDrag Model Comparison (at v=5 m/s):")
    print(f"{'Model':<20} {'Force (N)':<20} {'Power Loss (W)':<20}")
    print("-" * 60)
    
    v = (5.0, 0, 0)
    
    # Linear drag
    linear_coeff = 0.5  # kg/s
    linear_force = DragCalculator.calculate_linear_drag(v, linear_coeff)
    linear_mag = math.sqrt(sum(f**2 for f in linear_force))
    linear_power = linear_mag * 5.0
    print(f"{'Linear':<20} {linear_mag:<20.3f} {linear_power:<20.3f}")
    
    # Quadratic drag
    quad_force = DragCalculator.calculate_quadratic_drag(v, c_d, area)
    quad_mag = math.sqrt(sum(f**2 for f in quad_force))
    quad_power = quad_mag * 5.0
    print(f"{'Quadratic':<20} {quad_mag:<20.3f} {quad_power:<20.3f}")
    
    # Hybrid drag
    hybrid_force = DragCalculator.calculate_hybrid_drag(v, linear_coeff, c_d, area)
    hybrid_mag = math.sqrt(sum(f**2 for f in hybrid_force))
    hybrid_power = hybrid_mag * 5.0
    print(f"{'Hybrid':<20} {hybrid_mag:<20.3f} {hybrid_power:<20.3f}")


def example_2_friction_on_field():
    """Example 2: Friction effects on game piece sliding across field."""
    print("\n" + "="*70)
    print("Example 2: Friction Effects on Field")
    print("="*70)
    
    # Game piece sliding on field surface
    piece_mass = 0.235  # kg (2024 Note)
    normal_force = piece_mass * GRAVITY  # Force perpendicular to surface
    
    # Different field surface friction coefficients
    surfaces = {
        "polycarbonate (floor)": 0.4,
        "aluminum (stage)": 0.3,
        "wood (carpeted)": 0.5,
        "rubber (bumper)": 0.6,
    }
    
    print(f"\nFriction Force on {piece_mass}kg game piece (v=5 m/s):")
    print(f"{'Surface':<30} {'μ_k':<15} {'Friction Force (N)':<20} {'Decel (m/s²)':<20}")
    print("-" * 85)
    
    for surface, mu in surfaces.items():
        friction_force = FrictionCalculator.calculate_coulomb_friction(
            normal_force, mu, mu * 1.1, velocity=5.0
        )
        deceleration = friction_force / piece_mass
        print(f"{surface:<30} {mu:<15.2f} {friction_force:<20.3f} {deceleration:<20.3f}")
    
    # Sliding distance calculation
    print(f"\nStopping distance with friction (initial velocity: 5 m/s):")
    print(f"{'Surface':<30} {'Stopping Distance (m)':<20} {'Time to Stop (s)':<20}")
    print("-" * 70)
    
    initial_velocity = 5.0
    
    for surface, mu in surfaces.items():
        friction_force = FrictionCalculator.calculate_coulomb_friction(
            normal_force, mu, mu * 1.1, velocity=initial_velocity
        )
        deceleration = friction_force / piece_mass
        
        # Using v² = v₀² - 2*a*d → d = v₀² / (2*a)
        stopping_distance = (initial_velocity ** 2) / (2 * deceleration)
        
        # v = v₀ - a*t → t = v₀ / a
        time_to_stop = initial_velocity / deceleration
        
        print(f"{surface:<30} {stopping_distance:<20.3f} {time_to_stop:<20.3f}")


def example_3_collision_physics():
    """Example 3: Collision between robot and game piece."""
    print("\n" + "="*70)
    print("Example 3: Collision Physics (Robot vs Game Piece)")
    print("="*70)
    
    # Robot properties
    robot_mass = 50.0  # kg
    robot_velocity = (2.0, 0, 0)  # m/s, moving toward piece
    
    # Game piece properties
    piece_mass = 0.235  # kg
    piece_velocity = (0, 0, 0)  # m/s, at rest
    
    # Collision parameters
    restitutions = [0.3, 0.5, 0.8]  # Different material interactions
    
    print(f"\nBefore collision:")
    print(f"  Robot: {robot_mass}kg @ {robot_velocity} m/s")
    print(f"  Piece: {piece_mass}kg @ {piece_velocity} m/s")
    
    for e in restitutions:
        print(f"\n  Restitution (e) = {e}:")
        
        # Calculate collision
        v_robot_after, v_piece_after = CollisionPhysics.calculate_impulse(
            robot_mass, piece_mass,
            robot_velocity, piece_velocity,
            restitution=e
        )
        
        print(f"    Robot after:  {v_robot_after[0]:.4f} m/s")
        print(f"    Piece after:  {v_piece_after[0]:.4f} m/s")
        
        # Energy analysis
        energy_before = 0.5 * robot_mass * (robot_velocity[0]**2) + 0.5 * piece_mass * (piece_velocity[0]**2)
        energy_after = 0.5 * robot_mass * (v_robot_after[0]**2) + 0.5 * piece_mass * (v_piece_after[0]**2)
        energy_lost = energy_before - energy_after
        
        print(f"    Energy lost:  {energy_lost:.3f} J ({100*energy_lost/energy_before:.1f}%)")


def example_4_robot_cad_physics():
    """Example 4: Convert robot CAD to physics model."""
    print("\n" + "="*70)
    print("Example 4: Robot CAD to Physics Conversion")
    print("="*70)
    
    # Create material system
    material_system = MaterialSystem()
    
    # Create robot physics model
    robot = RobotPhysicsModel("Team1690Robot", material_system)
    
    # Add chassis
    chassis_data = {
        "name": "chassis_frame",
        "mass": 15.0,
        "material": "aluminum",
        "dimensions": {
            "length": 0.8,
            "width": 0.8,
            "height": 0.1
        },
        "collision_enabled": True,
    }
    robot.add_component(chassis_data)
    
    # Add drivetrain
    drivetrain_data = {
        "name": "drivetrain_motor",
        "mass": 2.0,
        "material": "aluminum",
        "dimensions": {
            "length": 0.15,
            "width": 0.15,
            "height": 0.3
        },
        "collision_enabled": False,  # Mounted inside
    }
    robot.add_component(drivetrain_data)
    
    # Add shooter mechanism
    shooter_data = {
        "name": "shooter_wheel",
        "mass": 3.0,
        "material": "aluminum",
        "dimensions": {
            "length": 0.2,
            "width": 0.2,
            "height": 0.2
        },
        "collision_enabled": True,
    }
    robot.add_component(shooter_data)
    
    # Add bumpers
    for i in range(4):
        bumper_data = {
            "name": f"bumper_{i}",
            "mass": 0.5,
            "material": "plastic",
            "dimensions": {
                "length": 0.8,
                "width": 0.05,
                "height": 0.3
            },
            "collision_enabled": True,
        }
        robot.add_component(bumper_data)
    
    # Print summary
    summary = robot.get_summary()
    print(f"\nRobot Physics Model Summary:")
    print(f"  Name: {summary['name']}")
    print(f"  Total Mass: {summary['total_mass']:.2f} kg")
    print(f"  Bodies: {summary['total_bodies']}")
    print(f"  Center of Mass: ({summary['center_of_mass'][0]:.3f}, {summary['center_of_mass'][1]:.3f}, {summary['center_of_mass'][2]:.3f})")
    print(f"\n  Moments of Inertia:")
    for axis, value in summary['moments_of_inertia'].items():
        print(f"    I_{axis}: {value:.3f} kg⋅m²")


def example_5_trajectory_simulation():
    """Example 5: Simulate game piece trajectory with drag and gravity."""
    print("\n" + "="*70)
    print("Example 5: Game Piece Trajectory with Physics")
    print("="*70)
    
    # Launcher parameters
    launch_angle = 45.0  # degrees
    launch_speed = 10.0  # m/s
    angle_rad = math.radians(launch_angle)
    
    # Initial velocity
    vx = launch_speed * math.cos(angle_rad)
    vy = launch_speed * math.sin(angle_rad)
    vz = 0.0
    
    # Game piece (2024 Note)
    mass = 0.235  # kg
    drag_coefficient = 0.47
    diameter = 0.375  # m
    cross_section = math.pi * (diameter / 2)**2
    
    print(f"\nLaunched at {launch_angle}° with {launch_speed} m/s")
    print(f"Game piece: {mass}kg, C_d={drag_coefficient}")
    
    # Simulation parameters
    dt = 0.01  # 10ms time steps
    t_max = 3.0  # 3 seconds
    
    # Track trajectory
    positions = []
    velocities = []
    
    x, y, z = 0.0, 0.0, 0.0
    vx, vy, vz = vx, vy, vz
    
    t = 0.0
    while t < t_max and y > -0.1:  # Stop if hits ground
        positions.append((x, y, z))
        velocities.append((vx, vy, vz))
        
        # Calculate forces
        acceleration_x = 0
        acceleration_y = -GRAVITY  # Gravity
        acceleration_z = 0
        
        # Add drag
        drag_force = DragCalculator.calculate_quadratic_drag(
            (vx, vy, vz), drag_coefficient, cross_section
        )
        acceleration_x += drag_force[0] / mass
        acceleration_y += drag_force[1] / mass
        acceleration_z += drag_force[2] / mass
        
        # Integrate
        new_pos, new_vel = PhysicsIntegrator.verlet_step(
            (x, y, z), (vx, vy, vz),
            (acceleration_x, acceleration_y, acceleration_z),
            dt
        )
        
        x, y, z = new_pos
        vx, vy, vz = new_vel
        t += dt
    
    # Print trajectory
    print(f"\nTrajectory points (selected):")
    print(f"{'Time (s)':<15} {'X (m)':<15} {'Y (m)':<15} {'Speed (m/s)':<15}")
    print("-" * 60)
    
    for i in range(0, len(positions), max(1, len(positions)//10)):
        t_val = i * dt
        x, y, z = positions[i]
        vx, vy, vz = velocities[i]
        speed = math.sqrt(vx**2 + vy**2 + vz**2)
        print(f"{t_val:<15.2f} {x:<15.2f} {y:<15.2f} {speed:<15.2f}")
    
    # Final stats
    max_height = max(pos[1] for pos in positions)
    max_distance = max(pos[0] for pos in positions)
    
    print(f"\nTrajectory Statistics:")
    print(f"  Max height: {max_height:.2f} m")
    print(f"  Max distance: {max_distance:.2f} m")
    print(f"  Flight time: {t:.2f} s")


def example_6_material_interactions():
    """Example 6: Material interactions and friction combinations."""
    print("\n" + "="*70)
    print("Example 6: Material Interaction Matrix")
    print("="*70)
    
    material_system = MaterialSystem()
    
    # Get properties for various materials
    materials = ["aluminum", "steel", "plastic", "rubber"]
    
    print(f"\nMaterial Properties:")
    print(f"{'Material':<15} {'Density (kg/m³)':<20} {'Friction':<15} {'Restitution':<15}")
    print("-" * 65)
    
    for mat in materials:
        density, friction, restitution = material_system.get_material_properties(mat)
        print(f"{mat:<15} {density:<20.0f} {friction:<15.2f} {restitution:<15.2f}")


if __name__ == "__main__":
    print("="*70)
    print("Physics Simulation Examples")
    print("Drag, Friction, Collisions, Robot CAD Interactions")
    print("="*70)
    
    try:
        example_1_drag_calculations()
        example_2_friction_on_field()
        example_3_collision_physics()
        example_4_robot_cad_physics()
        example_5_trajectory_simulation()
        example_6_material_interactions()
        
        print("\n" + "="*70)
        print("✓ All physics examples completed!")
        print("="*70)
        
    except Exception as e:
        print(f"\n✗ Example failed: {e}")
        import traceback
        traceback.print_exc()
