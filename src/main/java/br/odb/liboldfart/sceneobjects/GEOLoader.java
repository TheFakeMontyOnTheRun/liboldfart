package br.odb.liboldfart.sceneobjects;

import java.io.InputStream;
import java.util.ArrayList;

import br.odb.liboldfart.parser.FileFormatParser;
import br.odb.libscene.Sector;
import br.odb.libscene.World;
import br.odb.libstrip.GeneralPolygon;
import br.odb.libstrip.Mesh;
import br.odb.libstrip.MeshFactory;
import br.odb.utils.FileServerDelegate;

public class GEOLoader implements FileFormatParser {

	private World world;
	private FileServerDelegate fileServer;

	public World getWorld() {
		return world;
	}

	public void parseDocument(FileServerDelegate fServe) {
		fileServer = fServe;
	}

	public void prepareForPath(String path, MeshFactory factory ) {
		world = new World();
		world.internalize(path, false, fileServer, factory );
	}

	public ArrayList<Mesh> getGeometry() {
		ArrayList<Mesh> toReturn = null;
		Mesh mesh = null;
		GeneralPolygon face = null;
		Sector sector;

		if (world != null) {
			toReturn = new ArrayList<Mesh>();

			for (int c = 0; c < world.getTotalSectors(); ++c) {

				sector = world.getSector(c);

				if (!sector.isMaster() || c == 0)
					continue;

				mesh = new Mesh();
				toReturn.add(mesh);

				// N
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ0());
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ0());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);
				// L
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ1());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ1());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ0());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);
				// S
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ1());
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ1());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ1());
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ1());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);
				// W
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ1());
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ1());
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ0());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);
				// FLOOR
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY0(), sector.getZ1());
				mesh.addPoint(sector.getX0(), sector.getY0(), sector.getZ1());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);
				// CEILING
				face = new GeneralPolygon();
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ0());
				mesh.addPoint(sector.getX1(), sector.getY1(), sector.getZ1());
				mesh.addPoint(sector.getX0(), sector.getY1(), sector.getZ1());
				face.addIndex(0);
				face.addIndex(1);
				face.addIndex(2);
				face.addIndex(3);

				mesh.addFace(face);
			}
		}

		return toReturn;
	}

	@Override
	public void preBuffer(InputStream is) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFileServer(FileServerDelegate instance) {

		fileServer = instance;
	}
}
