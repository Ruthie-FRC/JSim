"""
Python interface to JSim C++ physics engine.

Acts as a bridge between CAD import system and the high-performance C++ physics simulation.
The actual physics calculations are performed by the C++ engine - this module provides:

- CAD data to C++ RigidBody mapping
- Material property conversion
- Physics scene management
- AdvantageScope visualization bridge

The C++ engine (core/driver) provides:
- PhysicsWorld: Scene manager
- RigidBody: Individual physics bodies
- RigidAssembly: Multi-body constraints
- Material: Friction, restitution, damping
- Force generators: Motors, springs, gravity, aerodynamics
- Integration: Semi-implicit Euler
"""

import logging
from typing import Dict, List, Optional, Tuple, Any
from ctypes import CDLL, c_double, c_int, POINTER, c_uint64
from pathlib import Path

logger = logging.getLogger(__name__)


class CppPhysicsBindings:
    """Low-level C/C++ physics engine bindings via ctypes.
    
    Wraps the C interface defined in driverheader.h
    """
    
    def __init__(self, lib_path: Optional[str] = None):
        """Initialize C++ physics library bindings.
        
        Args:
            lib_path: Path to compiled physics library (.so/.dll)
                     If None, attempts to load from standard locations
        """
        if lib_path is None:
            # Try common locations
            possible_paths = [
                "build/Release/libJSimPhysics.so",
                "build/libJSimPhysics.so",
                "libJSimPhysics.so",
                "JSimPhysics.dll",
            ]
            lib_path = self._find_library(possible_paths)
        
        try:
            self.lib = CDLL(lib_path)
            logger.info(f"Loaded C++ physics library: {lib_path}")
        except OSError as e:
            logger.error(f"Failed to load physics library: {e}")
            raise
        
        # Define C function signatures
        self._define_c_functions()
    
    def _find_library(self, paths: List[str]) -> str:
        """Find physics library from possible paths."""
        for path_str in paths:
            path = Path(path_str)
            if path.exists():
                return str(path)
        
        # Return first path and let it fail with clear error
        return paths[0]
    
    def _define_c_functions(self):
        """Define C function signatures."""
        # Physics world management
        self.lib.c_rsCreateWorld.argtypes = [c_double, c_int]
        self.lib.c_rsCreateWorld.restype = c_uint64
        
        self.lib.c_rsDestroyWorld.argtypes = [c_uint64]
        self.lib.c_rsDestroyWorld.restype = c_int
        
        self.lib.c_rsStepWorld.argtypes = [c_uint64, c_int]
        self.lib.c_rsStepWorld.restype = c_int
        
        # Body management
        self.lib.c_rsCreateBody.argtypes = [c_uint64, c_double]
        self.lib.c_rsCreateBody.restype = c_int
        
        self.lib.c_rsSetBodyPosition.argtypes = [c_uint64, c_int, c_double, c_double, c_double]
        self.lib.c_rsSetBodyPosition.restype = c_int
        
        self.lib.c_rsGetBodyPosition.argtypes = [c_uint64, c_int, POINTER(c_double), POINTER(c_double), POINTER(c_double)]
        self.lib.c_rsGetBodyPosition.restype = c_int
        
        self.lib.c_rsSetBodyLinearVelocity.argtypes = [c_uint64, c_int, c_double, c_double, c_double]
        self.lib.c_rsSetBodyLinearVelocity.restype = c_int
        
        self.lib.c_rsGetBodyLinearVelocity.argtypes = [c_uint64, c_int, POINTER(c_double), POINTER(c_double), POINTER(c_double)]
        self.lib.c_rsGetBodyLinearVelocity.restype = c_int
        
        # Gravity
        self.lib.c_rsSetWorldGravity.argtypes = [c_uint64, c_double, c_double, c_double]
        self.lib.c_rsSetWorldGravity.restype = c_int
        
        # Body properties
        self.lib.c_rsSetBodyGravityEnabled.argtypes = [c_uint64, c_int, c_int]
        self.lib.c_rsSetBodyGravityEnabled.restype = c_int


class PhysicsWorldBridge:
    """High-level bridge to C++ PhysicsWorld.
    
    Manages creation and interaction with C++ physics simulation.
    """
    
    def __init__(self, bindings: Optional[CppPhysicsBindings] = None,
                 fixed_dt: float = 0.01,
                 enable_gravity: bool = True):
        """Initialize physics world bridge.
        
        Args:
            bindings: C++ physics bindings (creates default if None)
            fixed_dt: Fixed timestep in seconds
            enable_gravity: Enable gravity
        """
        self.bindings = bindings
        self.fixed_dt = fixed_dt
        
        # Create C++ world
        try:
            if self.bindings is None:
                self.bindings = CppPhysicsBindings()
            
            self.world_handle = self.bindings.lib.c_rsCreateWorld(
                c_double(fixed_dt),
                c_int(1 if enable_gravity else 0)
            )
            logger.info(f"Created physics world (handle={self.world_handle})")
            
        except Exception as e:
            logger.error(f"Failed to create physics world: {e}")
            self.world_handle = None
    
    def create_body(self, mass: float) -> int:
        """Create a rigid body in the physics world.
        
        Args:
            mass: Body mass in kg
        
        Returns:
            Body index/handle
        """
        if self.world_handle is None:
            logger.error("Physics world not initialized")
            return -1
        
        body_id = self.bindings.lib.c_rsCreateBody(
            c_uint64(self.world_handle),
            c_double(mass)
        )
        logger.debug(f"Created physics body: id={body_id}, mass={mass}kg")
        return body_id
    
    def set_body_position(self, body_id: int, position: Tuple[float, float, float]) -> bool:
        """Set body position in world frame.
        
        Args:
            body_id: Body handle
            position: (x, y, z) in meters
        
        Returns:
            True if successful
        """
        if self.world_handle is None:
            return False
        
        result = self.bindings.lib.c_rsSetBodyPosition(
            c_uint64(self.world_handle),
            c_int(body_id),
            c_double(position[0]),
            c_double(position[1]),
            c_double(position[2])
        )
        return result == 0
    
    def get_body_position(self, body_id: int) -> Optional[Tuple[float, float, float]]:
        """Get body position.
        
        Args:
            body_id: Body handle
        
        Returns:
            (x, y, z) tuple or None if failed
        """
        if self.world_handle is None:
            return None
        
        x = c_double()
        y = c_double()
        z = c_double()
        
        result = self.bindings.lib.c_rsGetBodyPosition(
            c_uint64(self.world_handle),
            c_int(body_id),
            POINTER(c_double)(x),
            POINTER(c_double)(y),
            POINTER(c_double)(z)
        )
        
        if result == 0:
            return (x.value, y.value, z.value)
        return None
    
    def set_body_velocity(self, body_id: int, velocity: Tuple[float, float, float]) -> bool:
        """Set body linear velocity.
        
        Args:
            body_id: Body handle
            velocity: (vx, vy, vz) in m/s
        
        Returns:
            True if successful
        """
        if self.world_handle is None:
            return False
        
        result = self.bindings.lib.c_rsSetBodyLinearVelocity(
            c_uint64(self.world_handle),
            c_int(body_id),
            c_double(velocity[0]),
            c_double(velocity[1]),
            c_double(velocity[2])
        )
        return result == 0
    
    def get_body_velocity(self, body_id: int) -> Optional[Tuple[float, float, float]]:
        """Get body linear velocity.
        
        Args:
            body_id: Body handle
        
        Returns:
            (vx, vy, vz) tuple or None if failed
        """
        if self.world_handle is None:
            return None
        
        vx = c_double()
        vy = c_double()
        vz = c_double()
        
        result = self.bindings.lib.c_rsGetBodyLinearVelocity(
            c_uint64(self.world_handle),
            c_int(body_id),
            POINTER(c_double)(vx),
            POINTER(c_double)(vy),
            POINTER(c_double)(vz)
        )
        
        if result == 0:
            return (vx.value, vy.value, vz.value)
        return None
    
    def set_gravity(self, gravity: Tuple[float, float, float]) -> bool:
        """Set world gravity vector.
        
        Args:
            gravity: (gx, gy, gz) in m/s²
        
        Returns:
            True if successful
        """
        if self.world_handle is None:
            return False
        
        result = self.bindings.lib.c_rsSetWorldGravity(
            c_uint64(self.world_handle),
            c_double(gravity[0]),
            c_double(gravity[1]),
            c_double(gravity[2])
        )
        return result == 0
    
    def step(self, num_steps: int = 1) -> bool:
        """Step physics simulation.
        
        Args:
            num_steps: Number of fixed timesteps to simulate
        
        Returns:
            True if successful
        """
        if self.world_handle is None:
            return False
        
        result = self.bindings.lib.c_rsStepWorld(
            c_uint64(self.world_handle),
            c_int(num_steps)
        )
        return result == 0
    
    def destroy(self) -> bool:
        """Destroy physics world.
        
        Returns:
            True if successful
        """
        if self.world_handle is None:
            return True
        
        result = self.bindings.lib.c_rsDestroyWorld(c_uint64(self.world_handle))
        self.world_handle = None
        return result == 0
    
    def __del__(self):
        """Cleanup on deletion."""
        self.destroy()


class CADToCppPhysicsConverter:
    """Converts CAD components to C++ physics bodies."""
    
    def __init__(self, physics_world: PhysicsWorldBridge, 
                 material_system=None):
        """Initialize converter.
        
        Args:
            physics_world: PhysicsWorldBridge instance
            material_system: Material system for property mapping
        """
        self.world = physics_world
        self.material_system = material_system
        self.body_mapping: Dict[str, int] = {}  # CAD name -> physics body ID
    
    def add_cad_component_as_body(self, component_data: Dict[str, Any],
                                  position: Tuple[float, float, float] = (0, 0, 0)
                                  ) -> Optional[int]:
        """Convert CAD component to physics body in C++ world.
        
        Args:
            component_data: CAD component dictionary
            position: Initial position in world
        
        Returns:
            Physics body ID or None if failed
        """
        name = component_data.get("name", "unknown")
        mass = component_data.get("mass", 0.1)
        
        # Skip massless objects
        if mass < 1e-6:
            logger.debug(f"Skipping massless component: {name}")
            return None
        
        # Create body in C++ physics world
        body_id = self.world.create_body(mass)
        if body_id < 0:
            logger.error(f"Failed to create physics body for {name}")
            return None
        
        # Set initial position
        self.world.set_body_position(body_id, position)
        
        # Store mapping
        self.body_mapping[name] = body_id
        
        logger.info(f"Added CAD component '{name}' as physics body {body_id}")
        return body_id
    
    def set_component_velocity(self, component_name: str,
                              velocity: Tuple[float, float, float]) -> bool:
        """Set velocity of a component's physics body.
        
        Args:
            component_name: Name of CAD component
            velocity: (vx, vy, vz) in m/s
        
        Returns:
            True if successful
        """
        if component_name not in self.body_mapping:
            logger.warning(f"Component not found: {component_name}")
            return False
        
        body_id = self.body_mapping[component_name]
        return self.world.set_body_velocity(body_id, velocity)
    
    def get_component_state(self, component_name: str) -> Optional[Dict[str, Any]]:
        """Get current state of a component's physics body.
        
        Args:
            component_name: Name of CAD component
        
        Returns:
            Dictionary with position and velocity, or None if not found
        """
        if component_name not in self.body_mapping:
            return None
        
        body_id = self.body_mapping[component_name]
        
        pos = self.world.get_body_position(body_id)
        vel = self.world.get_body_velocity(body_id)
        
        if pos and vel:
            return {
                "name": component_name,
                "body_id": body_id,
                "position": pos,
                "velocity": vel,
            }
        
        return None


class RobotCppPhysicsModel:
    """Complete robot with all bodies in C++ physics world."""
    
    def __init__(self, name: str, physics_world: PhysicsWorldBridge):
        """Initialize robot physics model.
        
        Args:
            name: Robot identifier
            physics_world: Physics world bridge
        """
        self.name = name
        self.world = physics_world
        self.converter = CADToCppPhysicsConverter(physics_world)
        self.components: Dict[str, Dict[str, Any]] = {}
    
    def add_cad_mechanism(self, mechanism_data: Dict[str, Any]) -> bool:
        """Add grouped mechanism from CAD as physics bodies.
        
        Args:
            mechanism_data: Mechanism definition from CAD import
        
        Returns:
            True if successfully added
        """
        mech_name = mechanism_data.get("name", "unknown")
        components = mechanism_data.get("components", [])
        
        logger.info(f"Adding mechanism '{mech_name}' with {len(components)} components")
        
        for comp in components:
            body_id = self.converter.add_cad_component_as_body(comp)
            if body_id is not None:
                self.components[comp.get("name")] = {
                    "data": comp,
                    "body_id": body_id,
                    "mechanism": mech_name,
                }
        
        return len(self.components) > 0
    
    def step_simulation(self, num_steps: int = 1) -> bool:
        """Step robot physics simulation.
        
        Args:
            num_steps: Number of timesteps
        
        Returns:
            True if successful
        """
        return self.world.step(num_steps)
    
    def get_robot_state(self) -> Dict[str, Any]:
        """Get state of all robot components.
        
        Returns:
            Dictionary with all component states
        """
        state = {
            "name": self.name,
            "components": []
        }
        
        for comp_name in self.components:
            comp_state = self.converter.get_component_state(comp_name)
            if comp_state:
                state["components"].append(comp_state)
        
        return state
    
    def get_summary(self) -> Dict[str, Any]:
        """Get model summary."""
        total_mass = sum(
            c["data"].get("mass", 0) 
            for c in self.components.values()
        )
        
        return {
            "name": self.name,
            "total_mass": total_mass,
            "components": len(self.components),
            "component_names": list(self.components.keys()),
        }
