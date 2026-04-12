"""
Robot CAD to Physics conversion system.

Converts CAD component data to physics-enabled bodies with:
- Mass distribution and center of mass
- Moments of inertia
- Drag and friction properties
- Collision geometry
- Material interactions

Bridges CAD import system with physics engine.
"""

from dataclasses import dataclass, field
from typing import Dict, List, Tuple, Optional, Any
import math
import logging

from physics_engine import (
    AerodynamicProperties, FrictionProperties, MaterialPhysicsProperties,
    GRAVITY, DragModel
)
from materials import MaterialSystem

logger = logging.getLogger(__name__)


@dataclass
class BoundingBox:
    """Axis-aligned bounding box for collision detection."""
    min_x: float
    max_x: float
    min_y: float
    max_y: float
    min_z: float
    max_z: float
    
    def center(self) -> Tuple[float, float, float]:
        """Get bounding box center."""
        return (
            (self.min_x + self.max_x) / 2,
            (self.min_y + self.max_y) / 2,
            (self.min_z + self.max_z) / 2
        )
    
    def dimensions(self) -> Tuple[float, float, float]:
        """Get bounding box dimensions."""
        return (
            self.max_x - self.min_x,
            self.max_y - self.min_y,
            self.max_z - self.min_z
        )


@dataclass
class MomentOfInertia:
    """Moment of inertia tensor (3x3 symmetric matrix)."""
    ixx: float  # Moment around x-axis
    iyy: float  # Moment around y-axis
    izz: float  # Moment around z-axis
    ixy: float = 0.0  # Cross terms (usually zero for symmetric objects)
    ixz: float = 0.0
    iyz: float = 0.0
    
    def as_matrix(self) -> List[List[float]]:
        """Get as 3x3 matrix."""
        return [
            [self.ixx, self.ixy, self.ixz],
            [self.ixy, self.iyy, self.iyz],
            [self.ixz, self.iyz, self.izz],
        ]


@dataclass
class PhysicsBody:
    """Physics-enabled body derived from CAD component."""
    name: str
    mass: float  # kg
    
    # Geometry
    bounding_box: BoundingBox
    center_of_mass: Tuple[float, float, float]
    moments_of_inertia: MomentOfInertia
    
    # Physics properties
    aerodynamic_properties: AerodynamicProperties
    friction_properties: FrictionProperties
    material: str
    
    # State
    position: Tuple[float, float, float] = (0.0, 0.0, 0.0)
    velocity: Tuple[float, float, float] = (0.0, 0.0, 0.0)
    angular_velocity: Tuple[float, float, float] = (0.0, 0.0, 0.0)
    
    # Collision
    collision_enabled: bool = True
    collision_radius: Optional[float] = None
    
    # Relationships
    parent_joint: Optional[str] = None
    is_fixed: bool = False
    
    # Metadata
    metadata: Dict[str, Any] = field(default_factory=dict)
    
    def get_surface_area(self) -> float:
        """Estimate surface area from bounding box."""
        dx, dy, dz = self.bounding_box.dimensions()
        return 2 * (dx*dy + dy*dz + dz*dx)
    
    def get_effective_radius(self) -> float:
        """Get effective collision radius (from bounding box diagonal)."""
        if self.collision_radius:
            return self.collision_radius
        
        dx, dy, dz = self.bounding_box.dimensions()
        return math.sqrt(dx*dx + dy*dy + dz*dz) / 2.0


class CADToPhysicsConverter:
    """Converts CAD component data to physics bodies."""
    
    def __init__(self, material_system: Optional[MaterialSystem] = None):
        """Initialize converter.
        
        Args:
            material_system: Material system for property lookup
        """
        self.material_system = material_system or MaterialSystem()
    
    def convert_component(self, component_data: Dict[str, Any],
                        position: Tuple[float, float, float] = (0, 0, 0)
                        ) -> PhysicsBody:
        """Convert CAD component to physics body.
        
        Args:
            component_data: CAD component data dictionary
            position: Initial position in world frame
        
        Returns:
            PhysicsBody ready for physics simulation
        """
        name = component_data.get("name", "unknown")
        mass = component_data.get("mass", 0.1)
        material = component_data.get("material", "aluminum")
        
        # Get material properties
        density, friction_coeff, restitution = self.material_system.get_material_properties(material)
        
        # Create bounding box from dimensions
        dims = component_data.get("dimensions", {})
        bbox = self._create_bounding_box(
            dims.get("length", 0.1),
            dims.get("width", 0.1),
            dims.get("height", 0.1)
        )
        
        # Calculate center of mass (assume centered in bounding box)
        com = bbox.center()
        
        # Calculate moments of inertia
        doi = self._calculate_moments_of_inertia(mass, bbox)
        
        # Get aerodynamic properties
        aero = self._get_aerodynamic_properties(material, bbox)
        
        # Get friction properties
        friction = self._get_friction_properties(material, friction_coeff)
        
        body = PhysicsBody(
            name=name,
            mass=mass,
            bounding_box=bbox,
            center_of_mass=com,
            moments_of_inertia=doi,
            aerodynamic_properties=aero,
            friction_properties=friction,
            material=material,
            position=position,
            collision_enabled=component_data.get("collision_enabled", True),
        )
        
        logger.info(f"Converted component '{name}' ({mass} kg) to physics body")
        return body
    
    def _create_bounding_box(self, length: float, width: float, height: float) -> BoundingBox:
        """Create bounding box centered at origin."""
        return BoundingBox(
            min_x=-length / 2, max_x=length / 2,
            min_y=-width / 2, max_y=width / 2,
            min_z=-height / 2, max_z=height / 2
        )
    
    def _calculate_moments_of_inertia(self, mass: float, bbox: BoundingBox) -> MomentOfInertia:
        """Calculate moments of inertia for rectangular box.
        
        For uniform rectangular box:
        I_xx = m/12 * (w² + h²)
        I_yy = m/12 * (l² + h²)
        I_zz = m/12 * (l² + w²)
        """
        l, w, h = bbox.dimensions()
        
        ixx = mass / 12.0 * (w*w + h*h)
        iyy = mass / 12.0 * (l*l + h*h)
        izz = mass / 12.0 * (l*l + w*w)
        
        return MomentOfInertia(ixx=ixx, iyy=iyy, izz=izz)
    
    def _get_aerodynamic_properties(self, material: str, bbox: BoundingBox) -> AerodynamicProperties:
        """Get aerodynamic properties for material.
        
        Maps material types to typical drag properties.
        """
        # Default drag properties by material
        drag_defaults = {
            "aluminum": {"c_d": 0.3, "model": DragModel.QUADRATIC},
            "steel": {"c_d": 0.35, "model": DragModel.QUADRATIC},
            "plastic": {"c_d": 0.4, "model": DragModel.QUADRATIC},
            "carbon_fiber": {"c_d": 0.25, "model": DragModel.QUADRATIC},
            "rubber": {"c_d": 0.5, "model": DragModel.HYBRID},
        }
        
        defaults = drag_defaults.get(material.lower(), {"c_d": 0.3, "model": DragModel.QUADRATIC})
        
        # Calculate cross-sectional area (use largest face)
        dx, dy, dz = bbox.dimensions()
        cross_section = max(dx*dy, dy*dz, dz*dx)
        
        return AerodynamicProperties(
            drag_coefficient=defaults["c_d"],
            cross_sectional_area=cross_section,
            linear_drag_coefficient=0.1,
            drag_model=defaults["model"],
        )
    
    def _get_friction_properties(self, material: str, friction_coeff: float) -> FrictionProperties:
        """Get friction properties for material."""
        return FrictionProperties(
            kinetic_friction=friction_coeff * 0.95,
            static_friction=friction_coeff,
            rolling_friction=friction_coeff * 0.05,
        )


class RobotPhysicsModel:
    """Complete physics model for a robot from CAD."""
    
    def __init__(self, name: str, material_system: Optional[MaterialSystem] = None):
        """Initialize robot physics model.
        
        Args:
            name: Robot name/identifier
            material_system: Material system for property lookup
        """
        self.name = name
        self.material_system = material_system or MaterialSystem()
        self.converter = CADToPhysicsConverter(self.material_system)
        
        self.bodies: Dict[str, PhysicsBody] = {}
        self.joints: List[Dict[str, Any]] = []
        
        self.total_mass = 0.0
        self.center_of_mass: Optional[Tuple[float, float, float]] = None
    
    def add_component(self, component_data: Dict[str, Any]) -> str:
        """Add CAD component as physics body.
        
        Args:
            component_data: CAD component data
        
        Returns:
            Body name
        """
        body = self.converter.convert_component(component_data)
        self.bodies[body.name] = body
        self.total_mass += body.mass
        
        logger.debug(f"Added body '{body.name}' to robot (total mass: {self.total_mass:.2f} kg)")
        return body.name
    
    def add_joint(self, parent: str, child: str, joint_type: str,
                 axis: Tuple[float, float, float] = (0, 0, 1),
                 limits: Optional[Tuple[float, float]] = None):
        """Add joint between two bodies.
        
        Args:
            parent: Parent body name
            child: Child body name
            joint_type: "revolute", "prismatic", "fixed"
            axis: Joint axis
            limits: (min, max) limits or None for unlimited
        """
        joint = {
            "parent": parent,
            "child": child,
            "type": joint_type,
            "axis": axis,
            "limits": limits,
        }
        self.joints.append(joint)
        
        logger.debug(f"Added {joint_type} joint: {parent} -> {child}")
    
    def calculate_center_of_mass(self) -> Tuple[float, float, float]:
        """Calculate overall center of mass.
        
        Returns:
            Center of mass position
        """
        if self.total_mass < 1e-6:
            return (0.0, 0.0, 0.0)
        
        com_x = sum(body.mass * body.center_of_mass[0] for body in self.bodies.values()) / self.total_mass
        com_y = sum(body.mass * body.center_of_mass[1] for body in self.bodies.values()) / self.total_mass
        com_z = sum(body.mass * body.center_of_mass[2] for body in self.bodies.values()) / self.total_mass
        
        self.center_of_mass = (com_x, com_y, com_z)
        return self.center_of_mass
    
    def get_total_inertia(self) -> MomentOfInertia:
        """Get total moment of inertia for all bodies.
        
        Uses parallel axis theorem to combine individual moments.
        """
        if not self.center_of_mass:
            self.calculate_center_of_mass()
        
        ixx_total = 0.0
        iyy_total = 0.0
        izz_total = 0.0
        
        for body in self.bodies.values():
            ixx_total += body.moments_of_inertia.ixx
            iyy_total += body.moments_of_inertia.iyy
            izz_total += body.moments_of_inertia.izz
            
            # Parallel axis theorem
            dx = body.center_of_mass[0] - self.center_of_mass[0]
            dy = body.center_of_mass[1] - self.center_of_mass[1]
            dz = body.center_of_mass[2] - self.center_of_mass[2]
            
            ixx_total += body.mass * (dy*dy + dz*dz)
            iyy_total += body.mass * (dx*dx + dz*dz)
            izz_total += body.mass * (dx*dx + dy*dy)
        
        return MomentOfInertia(ixx=ixx_total, iyy=iyy_total, izz=izz_total)
    
    def get_summary(self) -> Dict[str, Any]:
        """Get physics model summary."""
        self.calculate_center_of_mass()
        inertia = self.get_total_inertia()
        
        return {
            "name": self.name,
            "total_mass": self.total_mass,
            "center_of_mass": self.center_of_mass,
            "moments_of_inertia": {
                "ixx": inertia.ixx,
                "iyy": inertia.iyy,
                "izz": inertia.izz,
            },
            "total_bodies": len(self.bodies),
            "total_joints": len(self.joints),
            "body_names": list(self.bodies.keys()),
        }
