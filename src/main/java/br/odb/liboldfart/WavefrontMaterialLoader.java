package br.odb.liboldfart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.odb.libstrip.Material;
import br.odb.utils.Color;

public class WavefrontMaterialLoader {

	public static List< Material > parseMaterials(InputStream fis) {

		List<Material> materials = new ArrayList<Material>();
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

					subToken = line.split("[ ]+");

					if (line != null && line.length() > 0
							&& line.charAt(0) != '#') {

						opcode = subToken[0];

						if ("newmtl".equals( opcode )) {
							op1 = subToken[1];
							System.out
									.println(" reading definition for material: "
											+ op1);
							m = new Material(op1,null, null, null, null );
							materials.add(m);
						}

						if ("Kd".equals( opcode ) && m != null) {
							int r = (int) (255 * Float.parseFloat(subToken[1]));
							int g = (int) (255 * Float.parseFloat(subToken[2]));
							int b = (int) (255 * Float.parseFloat(subToken[3]));
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

		for ( Material mat : materials ) {
			System.out.println( ": " + mat );
		}
		
		return materials;
	}
}
