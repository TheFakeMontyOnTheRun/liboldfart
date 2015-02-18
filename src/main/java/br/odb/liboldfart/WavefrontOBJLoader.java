package br.odb.liboldfart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.odb.libstrip.GeneralPolygon;
import br.odb.libstrip.Material;
import br.odb.libstrip.Mesh;
import br.odb.utils.Color;
import br.odb.utils.math.Vec3;

public class WavefrontOBJLoader {

	private String buffer = "";
	final private ArrayList<Mesh> meshList = new ArrayList<Mesh>();
	final private HashMap< String, Material> materials = new HashMap<String, Material>();
	private Mesh mesh;
	private List< Vec3 > vertexes = new ArrayList< Vec3 >();
	private int lastIndex;
	private Material currentMaterial;

	private void parseMaterialList(InputStream fis) {

		Material[] list = parseMaterials(fis);

		for( Material m : list ) {
			System.out.println( "registering material:" + m.name );
			this.materials.put( m.name, m );
		}
	}

	private static Material[] parseMaterials(InputStream fis) {

		ArrayList<Material> materials = new ArrayList<Material>();
		Material[] toReturn;
		String line = "";
		String opcode;
		String op1;
		Material m = null;
		Color c = null;
		String[] subToken;
		
		try {

			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			while (bis.ready()) {
				try {
					line = bis.readLine();
					
					subToken = line.split( "[ ]+" );
					
					if (line != null && line.length() > 0
							&& line.charAt(0) != '#') {

						opcode = subToken[ 0 ];

						if (opcode.equals("newmtl")) {
							op1 = subToken[ 1 ];
							System.out
									.println(" reading definition for material: "
											+ op1);
							m = new Material(op1, "", "", "" );
							materials.add(m);
						}

						if (opcode.equals("Kd") && m != null) {
							int r = (int) (255 * Float.parseFloat(subToken[ 1 ]));
							int g = (int) (255 * Float.parseFloat(subToken[ 2 ]));
							int b = (int) (255 * Float.parseFloat(subToken[ 3 ]));
							c = new Color(r, g, b);
							m.mainColor.set(c);
							System.out.println("got color " + c
									+ " for material " + m.name);
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (materials.size() > 0) {
			System.out.println("materials: " + materials.size());
			for (int d = 0; d < materials.size(); ++d) {
				System.out.println("material(" + d + "): " + materials.get(d)
						+ " name = " + materials.get(d).name);
			}
			toReturn = new Material[materials.size()];
			materials.toArray(toReturn);
			return toReturn;
		} else
			return null;
	}



	private void parseLine(String line ) {
		Vec3 v;
		String[] subToken = line.split( "[ ]+" );
		
		switch (line.charAt(0)) {
		
		case 'u':
			
			currentMaterial = materials.get( subToken[ 1 ] );
			System.out.println("now using material: " + currentMaterial.name + " for mesh " + mesh.name );
			mesh.material = currentMaterial;
			break;

		case 'v':

			v = new Vec3(Float.parseFloat( subToken[ 1 ] ),
					Float.parseFloat( subToken[ 3 ] ),
					Float.parseFloat( subToken[ 2 ] ) );


			
			vertexes.add(v);

			System.out.println("v: " + v);
			break;
			
		case 'o':
			System.out.println("reading " + line.substring(2));
			lastIndex += vertexes.size();
			mesh = new Mesh(line.substring(2));
			meshList.add(mesh);
			vertexes.clear();
			break;
			
		case 'f':
			String token = "";
			String last = line.substring(1).trim();
			GeneralPolygon poly = new GeneralPolygon();

			while (last.length() > 0) {

				if (last.indexOf(' ') != -1) {
					token = last.substring(0, last.indexOf(' ')).trim();
					last = last.substring(last.indexOf(' ') + 1).trim();
				} else {
					token = last.trim();
					last = "";
				}

				System.out.println(":" + token);
				int integer = Integer.parseInt(token);
				poly.addIndex(integer - lastIndex - 1);
			}

			System.out.println("---");
			mesh.faces.add(poly);

			break;
		}
	}

	
	public ArrayList<Mesh> loadMeshes( String meshName, InputStream fis, InputStream materialData) {
		buffer = " ";
		String mybuffer = "";
		String line;
		parseMaterialList(materialData);

		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));

			while (bis.ready()) {
				line = bis.readLine();
				if (line != null && line.length() > 0 && line.charAt(0) != '#')
					mybuffer += line + "\n";
			}

		} catch (Exception e) {

		}

		buffer = mybuffer;
		
		lastIndex = 0;
		mesh = new Mesh(meshName);
		meshList.add(mesh);
		currentMaterial = null;

		while (buffer.length() > 0) {
			line = buffer.substring(0, buffer.indexOf('\n'));
			buffer = buffer.substring(buffer.indexOf('\n') + 1);
			parseLine( line );
		}
		
		return meshList;
	}
}
