package br.odb.liboldfart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.odb.libstrip.GeneralPolygon;
import br.odb.libstrip.Material;
import br.odb.libstrip.Mesh;
import br.odb.utils.Color;
import br.odb.utils.FileServerDelegate;
import br.odb.utils.math.Vec3;

public class WavefrontOBJLoader {

	private String buffer;
	private ArrayList<Mesh> meshList;
	private ArrayList<Material> materialList;
	private Mesh mesh;
	private int lastIndex;
	private Material currentMaterial;
	private InputStream materialData;
	public String currentPath;

	public WavefrontOBJLoader() {
		buffer = "";
		meshList = null;
		materialList = null;
	}

	private void parseMaterialList(InputStream fis) {

		Material[] materials = parseMaterials(fis);

		for (int c = 0; c < materials.length; ++c)
			materialList.add(materials[c]);
	}

	public static Material[] parseMaterials(InputStream fis) {

		ArrayList<Material> materials = new ArrayList<Material>();
		Material[] toReturn;
		String line = "";
		String opcode;
		String op1;
		Material m = null;
		Color c = null;

		try {

			BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
			while (bis.ready()) {
				try {
					line = bis.readLine();
					if (line != null && line.length() > 0
							&& line.charAt(0) != '#') {

						opcode = getSubToken(line, 0);

						if (opcode.equals("newmtl")) {
							op1 = getSubToken(line, 1);
							System.out
									.println(" reading definition for material: "
											+ op1);
							m = new Material(op1);
							materials.add(m);
						}

						if (opcode.equals("Kd") && m != null) {
							int r = (int) (255 * Float.parseFloat(getSubToken(
									line, 1)));
							int g = (int) (255 * Float.parseFloat(getSubToken(
									line, 2)));
							int b = (int) (255 * Float.parseFloat(getSubToken(
									line, 3)));
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

	public void loadMeshes(String meshName, FileServerDelegate fServer) {

		String line;
		lastIndex = 0;
		meshList = new ArrayList<Mesh>();
		materialList = new ArrayList<Material>();
		mesh = new Mesh(meshName);
		meshList.add(mesh);
		currentMaterial = null;

		while (buffer.length() > 0) {
			line = buffer.substring(0, buffer.indexOf('\n'));
			buffer = buffer.substring(buffer.indexOf('\n') + 1);
			parseLine(line, fServer);
		}
	}

	static String getSubToken(String main, int token) {

		String work = main;
		String toreturn = null;
		while (token > -1) {

			if (token == 0) {
				if (work.indexOf(' ') != -1)
					toreturn = work.substring(0, work.indexOf(' '));
				else
					toreturn = work;
			}

			if (work.indexOf(' ') != -1)
				work = work.substring(work.indexOf(' ') + 1);

			token--;
		}

		return toreturn;
	}

	private Material getMaterialByName(String name) {
		for (int c = 0; c < materialList.size(); ++c) {
			if (materialList.get(c).name.equals(name))
				return materialList.get(c);
		}
		return null;
	}

	private void parseLine(String line, FileServerDelegate fServer) {
		Vec3 v;
		switch (line.charAt(0)) {
		case 'm':
			System.out.println("reading materials from: "
					+ getSubToken(line, 1));

			if (materialData != null) {
				System.out.println("reading from stream");
				parseMaterialList(materialData);
			} else
				try {
					parseMaterialList(fServer.openAsInputStream(currentPath
							+ getSubToken(line, 1)));
				} catch (IOException e) {
				}

			break;
		case 'u':
			currentMaterial = getMaterialByName(getSubToken(line, 1));
			System.out.println("now using material: " + getSubToken(line, 1));
			mesh.material = currentMaterial;
			break;

		case 'v':

			v = new Vec3(Float.parseFloat(getSubToken(line, 1)),
					Float.parseFloat(getSubToken(line, 3)),
					Float.parseFloat(getSubToken(line, 2)));

			mesh.points.add(v);

			System.out.println("v: " + v);

			break;
		case 'o':
			System.out.println("reading " + line.substring(2));
			lastIndex += mesh.points.size();
			mesh = new Mesh(line.substring(2));
			meshList.add(mesh);
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

	public ArrayList<Mesh> getGeometry() {
		return meshList;
	}

	public void preBuffer(InputStream fis, InputStream materialData) {
		buffer = " ";
		String mybuffer = "";
		String line;
		this.materialData = materialData;
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
	}
}
