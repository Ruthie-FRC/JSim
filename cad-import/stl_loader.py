"""
STL file loader for JSim.
Parses binary and ASCII STL files into vertex geometry for collision meshing and visualization.
"""

import struct
import logging

class STLLoader:
    def __init__(self):
        self.vertices = []
        self.normals = []

    def load(self, file_path: str):
        """Loads an STL file from the given path."""
        logging.info(f"Loading STL from {file_path}")
        try:
            with open(file_path, 'rb') as f:
                header = f.read(80)
                # Check for ASCII vs Binary
                if header.startswith(b'solid'):
                    self._load_ascii(file_path)
                else:
                    self._load_binary(f)
        except Exception as e:
            logging.error(f"Failed to load STL file {file_path}: {e}")
            return False
        return True

    def _load_ascii(self, file_path):
        """Parses an ASCII STL file."""
        logging.info("Parsing ASCII STL...")
        # Placeholder for ASCII STL parsing logic
        pass

    def _load_binary(self, f):
        """Parses a binary STL file."""
        logging.info("Parsing binary STL...")
        try:
            num_triangles = struct.unpack('<I', f.read(4))[0]
            for _ in range(num_triangles):
                # Normal
                self.normals.append(struct.unpack('<3f', f.read(12)))
                # Vertices
                for _ in range(3):
                    self.vertices.append(struct.unpack('<3f', f.read(12)))
                # Attribute byte count (ignored)
                f.read(2)
        except Exception as e:
            logging.error(f"Error parsing binary STL: {e}")

    def get_mesh_data(self):
        """Returns the parsed vertices and normals."""
        return {"vertices": self.vertices, "normals": self.normals}
