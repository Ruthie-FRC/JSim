"""
AdvantageScope integration for JSim arena visualization.

Provides utilities to export arena state to NetworkTables format compatible
with AdvantageScope for real-time visualization of:
- Robot poses
- Game piece positions
- Field elements
- Material interactions

From Issue #45: Integration with AdvantageScope using Pose3d structs at 20ms+ refresh rate.
"""

import json
from typing import Dict, List, Any, Optional
from dataclasses import asdict
import logging

logger = logging.getLogger(__name__)


class NetworkTablesKeyFormat:
    """Standard NetworkTables key formats for ASC ope integration."""
    
    # Root path for JSim simulation
    ROOT = "/jsim"
    
    # Arena state
    ARENA_TIME = f"{ROOT}/arena/time"
    ARENA_PAUSED = f"{ROOT}/arena/paused"
    ARENA_FIELD_LENGTH = f"{ROOT}/arena/field_length"
    ARENA_FIELD_WIDTH = f"{ROOT}/arena/field_width"
    
    # Robots (team_number substitution)
    ROBOT_POSE = f"{ROOT}/robots/{{team}}/pose"
    ROBOT_VELOCITY = f"{ROOT}/robots/{{team}}/velocity"
    ROBOT_ACTIVE = f"{ROOT}/robots/{{team}}/active"
    
    # Game pieces (piece_id substitution)
    GAMEPIECE_POSE = f"{ROOT}/game_pieces/{{id}}/pose"
    GAMEPIECE_VELOCITY = f"{ROOT}/game_pieces/{{id}}/velocity"
    GAMEPIECE_TYPE = f"{ROOT}/game_pieces/{{id}}/type"
    GAMEPIECE_ACTIVE = f"{ROOT}/game_pieces/{{id}}/active"
    
    # Field elements (element_name substitution)
    ELEMENT_POSE = f"{ROOT}/field_elements/{{name}}/pose"
    ELEMENT_TYPE = f"{ROOT}/field_elements/{{name}}/type"


class AdvantageeScopeExporter:
    """Exports arena state to AdvantageScope-compatible formats."""
    
    @staticmethod
    def arena_to_networktables_dict(arena_state: Dict[str, Any]) -> Dict[str, Any]:
        """Convert arena state to NetworkTables-compatible dictionary.
        
        Args:
            arena_state: Arena state snapshot
        
        Returns:
            Dictionary with NT key-value pairs
        """
        nt_dict = {}
        
        # Arena state
        nt_dict[NetworkTablesKeyFormat.ARENA_TIME] = arena_state.get("time", 0.0)
        nt_dict[NetworkTablesKeyFormat.ARENA_PAUSED] = arena_state.get("paused", False)
        
        field = arena_state.get("field", {})
        nt_dict[NetworkTablesKeyFormat.ARENA_FIELD_LENGTH] = field.get("length", 0.0)
        nt_dict[NetworkTablesKeyFormat.ARENA_FIELD_WIDTH] = field.get("width", 0.0)
        
        # Robots
        for robot in arena_state.get("robots", []):
            team = robot.get("team_number")
            
            # Pose as array [x, y, z, roll, pitch, yaw]
            pose = robot.get("pose", {})
            pose_array = [
                pose.get("x", 0), pose.get("y", 0), pose.get("z", 0),
                pose.get("roll", 0), pose.get("pitch", 0), pose.get("yaw", 0)
            ]
            nt_dict[NetworkTablesKeyFormat.ROBOT_POSE.format(team=team)] = pose_array
            
            # Velocity as dict
            vel = robot.get("velocity", {})
            nt_dict[NetworkTablesKeyFormat.ROBOT_VELOCITY.format(team=team)] = {
                "x": vel.get("x", 0),
                "y": vel.get("y", 0),
                "z": vel.get("z", 0),
            }
            
            nt_dict[NetworkTablesKeyFormat.ROBOT_ACTIVE.format(team=team)] = robot.get("active", True)
        
        # Game pieces
        for piece in arena_state.get("game_pieces", []):
            piece_id = piece.get("id")
            
            pose = piece.get("pose", {})
            pose_array = [
                pose.get("x", 0), pose.get("y", 0), pose.get("z", 0),
                pose.get("roll", 0), pose.get("pitch", 0), pose.get("yaw", 0)
            ]
            nt_dict[NetworkTablesKeyFormat.GAMEPIECE_POSE.format(id=piece_id)] = pose_array
            
            vel = piece.get("velocity", {})
            nt_dict[NetworkTablesKeyFormat.GAMEPIECE_VELOCITY.format(id=piece_id)] = {
                "x": vel.get("x", 0),
                "y": vel.get("y", 0),
                "z": vel.get("z", 0),
            }
            
            nt_dict[NetworkTablesKeyFormat.GAMEPIECE_TYPE.format(id=piece_id)] = piece.get("type", "unknown")
            nt_dict[NetworkTablesKeyFormat.GAMEPIECE_ACTIVE.format(id=piece_id)] = piece.get("active", True)
        
        # Field elements
        for element in arena_state.get("field_elements", []):
            name = element.get("name")
            
            pose = element.get("pose", {})
            pose_array = [
                pose.get("x", 0), pose.get("y", 0), pose.get("z", 0),
                pose.get("roll", 0), pose.get("pitch", 0), pose.get("yaw", 0)
            ]
            nt_dict[NetworkTablesKeyFormat.ELEMENT_POSE.format(name=name)] = pose_array
            nt_dict[NetworkTablesKeyFormat.ELEMENT_TYPE.format(name=name)] = element.get("type", "unknown")
        
        return nt_dict
    
    @staticmethod
    def export_to_nt_json(arena_state: Dict[str, Any], output_path: str) -> bool:
        """Export arena state to JSON suitable for NetworkTables.
        
        Args:
            arena_state: Arena state snapshot
            output_path: Path to output JSON
        
        Returns:
            True if successful
        """
        try:
            nt_dict = AdvantageeScopeExporter.arena_to_networktables_dict(arena_state)
            
            with open(output_path, 'w') as f:
                json.dump(nt_dict, f, indent=2)
            
            logger.info(f"Exported {len(nt_dict)} NT entries to {output_path}")
            return True
        except Exception as e:
            logger.error(f"Failed to export to NT JSON: {e}")
            return False


class AdvantageKitVisualizer:
    """Generates code for AdvantageKit integration with JSim.
    
    Produces Java code that can be used in robot code to visualize
    JSim simulation data in AdvantageScope.
    """
    
    @staticmethod
    def generate_note_visualizer_java(
        output_path: str,
        game_year: int = 2024
    ) -> bool:
        """Generate Java code for note/game piece visualization.
        
        Based on:
        https://github.com/Mechanical-Advantage/AdvantageKit/blob/main/example_projects/kitbot_2024/src/main/java/frc/robot/util/NoteVisualizer.java
        
        Args:
            output_path: Path to output Java file
            game_year: FRC game year
        
        Returns:
            True if successful
        """
        code = f'''package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import org.littletonrobotics.junction.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Auto-generated visualizer for JSim game pieces in {game_year}.
 * Publishes game piece poses to AdvantageScope via NetworkTables.
 */
public class GamePieceVisualizer {{
    
    private static final String NT_ROOT = "/jsim/game_pieces";
    
    /**
     * Update game piece visualization in AdvantageScope.
     * 
     * Call this at ~20ms intervals (in robotPeriodic or main loop).
     * 
     * @param gamePieces List of game piece poses
     */
    public static void updateGamePieces(List<Pose3d> gamePieces) {{
        // Create array of poses for AdvantageScope
        Pose3d[] poses = gamePieces.toArray(new Pose3d[0]);
        
        // Logger automatically publishes to NetworkTables
        Logger.recordOutput(NT_ROOT + "/poses", poses);
    }}
    
    /**
     * Update robot poses for visualization.
     * Automatically handled by WPILib odometry, but can be custom.
     */
    public static void updateRobotPose(Pose3d robotPose) {{
        Logger.recordOutput("/jsim/robot/pose", robotPose);
    }}
    
    /**
     * Example: Create a pose from JSim data
     */
    public static Pose3d fromJSimData(
        double x, double y, double z,
        double roll, double pitch, double yaw
    ) {{
        return new Pose3d(
            new Translation3d(x, y, z),
            new Rotation3d(roll, pitch, yaw)
        );
    }}
}}
'''
        
        try:
            with open(output_path, 'w') as f:
                f.write(code)
            logger.info(f"Generated AdvantageKit visualizer: {output_path}")
            return True
        except Exception as e:
            logger.error(f"Failed to generate visualizer: {e}")
            return False
    
    @staticmethod
    def generate_pose3d_updater_java(output_path: str) -> bool:
        """Generate Java code for updating Pose3d in robot code.
        
        Args:
            output_path: Path to output Java file
        
        Returns:
            True if successful
        """
        code = '''package frc.robot.util;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.DoubleArrayTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Utility for updating Pose3d via NetworkTables from JSim.
 * 
 * Subscribes to JSim-published poses and updates robot state.
 * Thread-safe for high-frequency updates (20ms+).
 */
public class JSImPose3dUpdater implements Runnable {{
    
    private static final String NT_PREFIX = "/jsim";
    private NetworkTable table;
    private DoubleArrayTopic poseTopic;
    private volatile Pose3d currentPose;
    
    public JSImPose3dUpdater() {{
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        table = inst.getTable(NT_PREFIX);
        
        // Subscribe to robot pose updates
        poseTopic = table.getDoubleArrayTopic("robot/pose");
        currentPose = new Pose3d();
    }}
    
    /**
     * Update pose from NetworkTables.
     * Expected format: [x, y, z, roll, pitch, yaw]
     */
    public void updatePose() {{
        double[] poseArray = poseTopic.getEntry(new double[6]).get();
        
        if (poseArray.length == 6) {{
            currentPose = new Pose3d(
                new Translation3d(poseArray[0], poseArray[1], poseArray[2]),
                new Rotation3d(poseArray[3], poseArray[4], poseArray[5])
            );
        }}
    }}
    
    /**
     * Get current pose (thread-safe).
     */
    public synchronized Pose3d getPose() {{
        return currentPose;
    }}
    
    @Override
    public void run() {{
        // High-frequency update loop (50Hz)
        while (!Thread.currentThread().isInterrupted()) {{
            updatePose();
            
            try {{
                Thread.sleep(20);  // 20ms cycle
            }} catch (InterruptedException e) {{
                Thread.currentThread().interrupt();
                break;
            }}
        }}
    }}
}}
'''
        
        try:
            with open(output_path, 'w') as f:
                f.write(code)
            logger.info(f"Generated Pose3d updater: {output_path}")
            return True
        except Exception as e:
            logger.error(f"Failed to generate updater: {e}")
            return False
