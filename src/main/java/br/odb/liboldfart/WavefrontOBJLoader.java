package br.odb.liboldfart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.odb.libstrip.GeneralTriangle;
import br.odb.libstrip.Material;
import br.odb.libstrip.GeneralTriangleMesh;
import br.odb.libstrip.builders.GeneralTriangleFactory;
import br.odb.utils.Color;
import br.odb.utils.math.Vec3;

public class WavefrontOBJLoader {

	final private GeneralTriangleFactory factory;
	final private List<GeneralTriangleMesh> meshList = new ArrayList<>();
	final private Map<String, Material> materials = new HashMap<String, Material>();
	final private List<Vec3> vertexes = new ArrayList<Vec3>();

	private GeneralTriangleMesh currentMesh;
	private Material currentMaterial;

	private static Material NEUTRAL_MATERIAL = new Material( null, new Color( 0xFFFFFFFF ), null, null, null );

	private void parseLine(String line) {

		if (line == null || line.length() == 0 || line.charAt(0) == '#') {
			return;
		}
		
		String[] subToken = line.split("[ ]+");

		switch (line.charAt(0)) {
		
		case 'u':

			if ( materials.containsKey( subToken[1] ) ) {
				currentMaterial = materials.get(subToken[1]);
				System.out.println("now using material: " + currentMaterial.name
						+ " for mesh " + currentMesh.name);				
			} else {
				currentMaterial = NEUTRAL_MATERIAL;
			}
			break;

		case 'v':
			Vec3 v = new Vec3(Float.parseFloat(subToken[1]),
					Float.parseFloat(subToken[3]),
					Float.parseFloat(subToken[2]));

			vertexes.add(v);

			System.out.println("v: " + v);
			break;

		case 'o':
			System.out.println("reading " + line.substring(2));
			currentMesh = new GeneralTriangleMesh(line.substring(2));
			meshList.add(currentMesh);
			break;

		case 'f':
			
			GeneralTriangle poly = new GeneralTriangle();
			List<Vec3> temporary = new ArrayList<>();
			
			for ( int c = 1; c < subToken.length; ++c ) {
				System.out.println(":" + Integer.parseInt( subToken[ c ] ));
				temporary.add(vertexes.get( Integer.parseInt( subToken[ c ] ) - 1 ) );
			}
			
			if (temporary.size() >= 3) {
				
				poly = factory.makeTrig(temporary.get(0).x, temporary.get(0).y,
						temporary.get(0).z, temporary.get(1).x,
						temporary.get(1).y, temporary.get(1).z,
						temporary.get(2).x, temporary.get(2).y,
						temporary.get(2).z,
						currentMaterial, null);
				
				currentMesh.faces.add(poly);
			} 
			
			if (temporary.size() >= 4) {
				
				poly = factory.makeTrig(temporary.get(3).x, temporary.get(3).y,
						temporary.get(3).z, temporary.get(1).x,
						temporary.get(1).y, temporary.get(1).z,
						temporary.get(2).x, temporary.get(2).y,
						temporary.get(2).z,
						currentMaterial, null);
				
				currentMesh.faces.add(poly);				
			}

			System.out.println("---");	
			break;
			
			default:
				return;
		}
	}

	public WavefrontOBJLoader(GeneralTriangleFactory factory) {
		this.factory = factory;
	}

	public List<GeneralTriangleMesh> loadMeshes(InputStream fis, List<Material> materialList) {
		
		if ( materialList != null ) {
			for ( Material m : materialList ) {
				materials.put( m.name, m );
			}			
			currentMaterial = null;
		}

		currentMesh = new GeneralTriangleMesh("");
		meshList.add(currentMesh);		

		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));

			while (bis.ready()) {
				parseLine(bis.readLine());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		clearEmptyMeshes( meshList );

		return meshList;
	}

	private void clearEmptyMeshes(List<GeneralTriangleMesh> meshList ) {
		List<GeneralTriangleMesh> toRemove = new ArrayList<GeneralTriangleMesh>();

		for (GeneralTriangleMesh mesh : meshList) {
			if (mesh.faces.size() == 0) {
				toRemove.add(mesh);
			}
		}

		for (GeneralTriangleMesh mesh : toRemove) {
			meshList.remove(mesh);
		}		
	}
}
