package br.odb.liboldfart.wavefront_obj;

import java.util.ArrayList;

import br.odb.libscene.Sector;
import br.odb.libstrip.Mesh;

public class WavefrontOBJExporter {

	public static String getWavefrontFor(Sector sector) {
		String toReturn = new String();

		Mesh mesh;
		ArrayList<Mesh> array = sector.getMeshes();

		for (int c = 0; c < 6; ++c) {

			mesh = array.get(c);
			toReturn += (mesh.toString());
			toReturn += ('\n');
		}

		return toReturn.toString();
	}
}
