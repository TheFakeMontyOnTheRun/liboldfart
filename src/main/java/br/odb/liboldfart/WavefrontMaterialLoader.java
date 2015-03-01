package br.odb.liboldfart;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.odb.libstrip.Material;
import br.odb.utils.Color;

public class WavefrontMaterialLoader {

	public void parseLine(String line) {

		Color c = null;
		String[] subToken;

		

		String opcode;
		String op1;

		if (line == null || line.length() == 0 || line.charAt(0) == '#') {
			return;
		}

		subToken = line.split("[ ]+");

		opcode = subToken[0];

		if ("newmtl".equals(opcode)) {
			op1 = subToken[1];
			System.out.println(" reading definition for material: " + op1);
			currentMaterial = new Material(op1, null, null, null, null);
			materials.add(currentMaterial);
		}

		if ("Kd".equals(opcode) && currentMaterial != null) {
			int r = (int) (255 * Float.parseFloat(subToken[1]));
			int g = (int) (255 * Float.parseFloat(subToken[2]));
			int b = (int) (255 * Float.parseFloat(subToken[3]));
			c = new Color(r, g, b);
			currentMaterial.mainColor.set(c);

			System.out.println("got color " + c + " for material " + currentMaterial.name);
		}

	}

	Material currentMaterial = null;
	private final List<Material> materials = new ArrayList<Material>();

	public List<Material> parseMaterials(InputStream fis) {

		try {

			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			while (bis.ready()) {
				parseLine(bis.readLine());
			}

		} catch (Exception e) {
		}

		for (Material mat : materials) {
			System.out.println(": " + mat);
		}

		return materials;
	}
}
