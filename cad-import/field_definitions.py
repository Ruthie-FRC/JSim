"""
Season-specific field definitions for JSim.

Contains comprehensive examples of how to define and recreate each FRC season's
field elements, constraints, and interactions.

From Issue #45: Each season you need to recreate the arena and game elements.
This module provides templates and examples for quick season setup.
"""

import json
from typing import Dict, List, Any
from pathlib import Path


class Field2024Definition:
    """CRESCENDO 2024 field definition.
    
    2024 FRC Game: CRESCENDO
    Field dimensions: 16.54m x 8.21m (54 x 27 feet)
    
    Key elements:
    - Stage (centerfield)
    - Speaker amp zone (wings)
    - Note drop zones
    - Wings for robot positioning
    """
    
    FIELD_LENGTH = 16.54  # meters
    FIELD_WIDTH = 8.21    # meters
    
    @staticmethod
    def get_field_definition() -> Dict[str, Any]:
        """Get complete field definition as JSON-serializable dict."""
        return {
            "year": 2024,
            "game": "CRESCENDO",
            "field_dimensions": {
                "length": Field2024Definition.FIELD_LENGTH,
                "width": Field2024Definition.FIELD_WIDTH,
            },
            "elements": [
                # Stage (center)
                {
                    "name": "stage_platform",
                    "type": "platform",
                    "pose": {
                        "x": 8.27,
                        "y": 4.105,
                        "z": 0.108,  # 4.25 inches high
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 2.286,  # 7 feet 6 inches
                        "width": 2.286,
                        "height": 0.108,
                    },
                    "material": "wood",
                    "mass": 50.0,
                },
                # Blue Speaker
                {
                    "name": "speaker_blue",
                    "type": "goal",
                    "pose": {
                        "x": 0.0,
                        "y": 4.105,
                        "z": 2.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "opening_width": 1.05,
                        "opening_height": 0.87,
                    },
                    "material": "aluminum",
                    "mass": 100.0,
                    "alliance": "blue",
                },
                # Red Speaker
                {
                    "name": "speaker_red",
                    "type": "goal",
                    "pose": {
                        "x": 16.54,
                        "y": 4.105,
                        "z": 2.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 3.14159,
                    },
                    "dimensions": {
                        "opening_width": 1.05,
                        "opening_height": 0.87,
                    },
                    "material": "aluminum",
                    "mass": 100.0,
                    "alliance": "red",
                },
                # Amp zones
                {
                    "name": "amp_zone_blue",
                    "type": "zone",
                    "pose": {
                        "x": 1.9,
                        "y": 7.7,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 0.8,
                        "width": 0.8,
                    },
                    "material": "painted_metal",
                    "mass": 0.0,  # Not physical
                },
                {
                    "name": "amp_zone_red",
                    "type": "zone",
                    "pose": {
                        "x": 14.64,
                        "y": 7.7,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 0.8,
                        "width": 0.8,
                    },
                    "material": "painted_metal",
                    "mass": 0.0,
                },
            ],
            "game_pieces": [
                {
                    "type": "note",
                    "initial_count": 5,  # Notes available on field
                    "mass": 0.235,  # kg
                    "material": "rubber_composite",
                    "diameter": 0.375,  # meters
                }
            ],
            "constraints": {
                "min_robot_size": 0.15,
                "max_robot_size": 1.20,
                "max_robot_height": 1.90,
                "max_robot_mass": 70.0,  # kg
            }
        }


class Field2025Definition:
    """REEFSCAPE 2025 field definition.
    
    2025 FRC Game: REEFSCAPE
    Field dimensions: 16.54m x 8.21m (54 x 27 feet)
    
    Key elements:
    - Coral reef structures
    - Algae removal zones
    - Barge zones
    """
    
    FIELD_LENGTH = 16.54
    FIELD_WIDTH = 8.21
    
    @staticmethod
    def get_field_definition() -> Dict[str, Any]:
        """Get complete field definition."""
        return {
            "year": 2025,
            "game": "REEFSCAPE",
            "field_dimensions": {
                "length": Field2025Definition.FIELD_LENGTH,
                "width": Field2025Definition.FIELD_WIDTH,
            },
            "elements": [
                # Reefs (left)
                {
                    "name": "reef_left",
                    "type": "reef",
                    "pose": {
                        "x": 2.5,
                        "y": 4.105,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 2.0,
                        "width": 2.0,
                    },
                    "material": "plastic",
                    "mass": 50.0,
                },
                # Reefs (right)
                {
                    "name": "reef_right",
                    "type": "reef",
                    "pose": {
                        "x": 14.04,
                        "y": 4.105,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 2.0,
                        "width": 2.0,
                    },
                    "material": "plastic",
                    "mass": 50.0,
                },
                # Barge (blue)
                {
                    "name": "barge_blue",
                    "type": "platform",
                    "pose": {
                        "x": 1.0,
                        "y": 0.5,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 0.0,
                    },
                    "dimensions": {
                        "length": 3.66,
                        "width": 2.74,
                    },
                    "material": "aluminum",
                    "mass": 100.0,
                    "alliance": "blue",
                },
                # Barge (red)
                {
                    "name": "barge_red",
                    "type": "platform",
                    "pose": {
                        "x": 15.54,
                        "y": 7.71,
                        "z": 0.0,
                        "roll": 0.0,
                        "pitch": 0.0,
                        "yaw": 3.14159,
                    },
                    "dimensions": {
                        "length": 3.66,
                        "width": 2.74,
                    },
                    "material": "aluminum",
                    "mass": 100.0,
                    "alliance": "red",
                },
            ],
            "game_pieces": [
                {
                    "type": "coral",
                    "initial_count": 12,
                    "mass": 0.567,  # kg
                    "material": "plastic",
                    "diameter": 0.178,
                }
            ],
            "constraints": {
                "min_robot_size": 0.15,
                "max_robot_size": 1.20,
                "max_robot_height": 1.90,
                "max_robot_mass": 70.0,
            }
        }


class Field2026Definition:
    """2026 FRC Field Definition - REBUILT

    I'm using placeholder values here for now because I'm lazy.
        
    Default dimensions: 16.54m x 8.21m (54 x 27 feet)
    """
    
    FIELD_LENGTH = 16.54
    FIELD_WIDTH = 8.21
    
    @staticmethod
    def get_field_definition() -> Dict[str, Any]:
        """Get complete field definition for 2026.
        
        Note: This is a template. Update with actual 2026 field specs.
        """
        return {
            "year": 2026,
            "game": "2026 Game REBUILT",
            "status": "template",
            "note": "Update with official 2026 field specification",
            "field_dimensions": {
                "length": Field2026Definition.FIELD_LENGTH,
                "width": Field2026Definition.FIELD_WIDTH,
            },
            "elements": [
                # TODO: Add 2026 field elements
                # Examples:
                # - Scoring zones
                # - Platforms
                # - Goals
                # - Perimeter walls
            ],
            "game_pieces": [
                # TODO: Add 2026 game pieces
                # Examples:
                # - Primary game piece
                # - Secondary game piece (if applicable)
            ],
            "constraints": {
                "min_robot_size": 0.15,
                "max_robot_size": 1.20,
                "max_robot_height": 1.90,
                "max_robot_mass": 70.0,
            }
        }


class FieldDefinitionManager:
    """Manages field definitions for different seasons."""
    
    SEASONS = {
        2024: Field2024Definition,
        2025: Field2025Definition,
    }
    
    @staticmethod
    def get_field_definition(year: int) -> Dict[str, Any]:
        """Get field definition for a given year.
        
        Args:
            year: Competition year (e.g., 2024)
        
        Returns:
            Field definition dictionary
        """
        if year not in FieldDefinitionManager.SEASONS:
            raise ValueError(f"No field definition for year {year}")
        
        season_class = FieldDefinitionManager.SEASONS[year]
        return season_class.get_field_definition()
    
    @staticmethod
    def save_field_definition(year: int, output_path: str) -> bool:
        """Save field definition to JSON file.
        
        Args:
            year: Competition year
            output_path: Path to save JSON
        
        Returns:
            True if saved successfully
        """
        try:
            field_def = FieldDefinitionManager.get_field_definition(year)
            
            output_file = Path(output_path)
            output_file.parent.mkdir(parents=True, exist_ok=True)
            
            with open(output_file, 'w') as f:
                json.dump(field_def, f, indent=2)
            
            print(f"✓ Saved 2024 field definition to {output_path}")
            return True
        except Exception as e:
            print(f"✗ Failed to save field definition: {e}")
            return False
    
    @staticmethod
    def load_field_definition(path: str) -> Dict[str, Any]:
        """Load field definition from JSON file.
        
        Args:
            path: Path to field definition JSON
        
        Returns:
            Field definition dictionary
        """
        try:
            with open(path, 'r') as f:
                return json.load(f)
        except Exception as e:
            print(f"✗ Failed to load field definition: {e}")
            return {}
    
    @staticmethod
    def list_available_years() -> List[int]:
        """Get list of years with available definitions."""
        return sorted(FieldDefinitionManager.SEASONS.keys())
