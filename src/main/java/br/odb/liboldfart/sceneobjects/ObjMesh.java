/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.odb.liboldfart.sceneobjects;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import br.odb.liboldfart.wavefront_obj.WavefrontOBJLoader;
import br.odb.libscene.SceneObject3D;
import br.odb.libstrip.Material;
import br.odb.libstrip.Mesh;
import br.odb.libstrip.MeshFactory;
import br.odb.utils.FileServerDelegate;
import br.odb.utils.math.Vec3;

/**
 * 
 * @author daniel
 */
public class ObjMesh extends SceneObject3D {
	/*
	 * public static ObjMesh makeCubeObjMesh() { ObjMesh cube = new ObjMesh();
	 * cube.internalize( EngineActivity.getInstanceResources().openRawResource(
	 * R.raw.cube), null);
	 * 
	 * return cube; }
	 */
	private Material currentMaterial;
	private HashMap<String, Material> materialList;
	Mesh mesh;
	public ArrayList<Vec3> verteces;

	public ObjMesh() {
		super();
		materialList = new HashMap<String, Material>();
		verteces = new ArrayList<Vec3>();
		currentMaterial = null;
		new Random();
	}

	public ObjMesh(ObjMesh other) {
		this(other.getMeshes());
	}

	public ObjMesh(ArrayList<Mesh> meshes) {
		this();

		for (Mesh m : meshes)
			addMesh(new Mesh(m));
	}

	public ObjMesh(String detail, FileServerDelegate server, MeshFactory factory) {
		this();

		try {
			internalize(server.openAsInputStream(detail), server, factory);
		} catch (IOException irrelevant) {
		}
	}

	public ObjMesh(InputStream meshStream, MeshFactory factory) {

		internalize(meshStream, null, factory);
	}

	private Material getMaterialByName(String name) {
		return materialList.get(name);
	}

	String getSubToken(String main, int token) {
		String work = main;
		String toreturn = null;
		while (token > -1) {
			if (token == 0) {
				if (work.indexOf(' ') != -1) {
					toreturn = work.substring(0, work.indexOf(' '));
				} else {
					toreturn = work;
				}
			}

			if (work.indexOf(' ') != -1) {
				work = work.substring(work.indexOf(' ') + 1);
			}

			token--;
		}

		return toreturn;
	}

	public void internalize(InputStream filecontent, FileServerDelegate server,
			MeshFactory factory) {

		String Sub = null;
		int pos = 0;
		char head;

		try {
			while (filecontent.available() != 0) {
				head = (char) filecontent.read();
				if (pos == 0 || head == '\n') {
					if (Sub != null && Sub.length() > 0) {
						Parse(Sub, server, factory);
					}
					Sub = new String();
				}
				if (head != '\n') {
					Sub += head;
				}
				pos++;
			}
		} catch (IOException irrelevant) {
		}

	}

	private void Parse(String Sub, FileServerDelegate server,
			MeshFactory factory) {

		Vec3 origin = getPosition();
		switch (Sub.charAt(0)) {
		case 'v':
			verteces.add(new Vec3(origin.x
					+ Float.parseFloat(getSubToken(Sub, 1)), origin.y
					+ Float.parseFloat(getSubToken(Sub, 2)), origin.z
					+ Float.parseFloat(getSubToken(Sub, 3))));
			break;
		case 'm': {

			Material[] mats = null;
			try {
				mats = WavefrontOBJLoader.parseMaterials(server
						.openAsInputStream(getSubToken(Sub, 1)));
			} catch (IOException irrelevant) {
			}
			if (mats != null)
				for (int c = 0; c < mats.length; ++c)
					materialList.put(mats[c].getName(), mats[c]);

		}
			break;
		case 'u':
			currentMaterial = getMaterialByName(getSubToken(Sub, 1));

			break;
		case 'o':

			if (mesh != null)
				mesh.preBuffer();

			mesh = factory.emptyMeshNamed(getSubToken(Sub, 1));
			addMesh(mesh);
			break;

		case 'f':
			Vec3 v1 = verteces.get(Integer.parseInt(getSubToken(Sub, 1)) - 1);
			Vec3 v2 = verteces.get(Integer.parseInt(getSubToken(Sub, 2)) - 1);
			Vec3 v3 = verteces.get(Integer.parseInt(getSubToken(Sub, 3)) - 1);
			{
				int color = 0;

				if (currentMaterial != null)
					color = currentMaterial.getMainColor().getARGBColor();

				mesh.addFace(factory.makeTrig(v1.x, v1.y, v1.z, v2.x, v2.y,
						v2.z, v3.x, v3.y, v3.z, color,
						MeshFactory.DEFAULT_LIGHT_VECTOR));
			}
			break;
		}
	}

	public void destroy() {

		if ( currentMaterial != null ) {
			
			currentMaterial.destroy();
		}
		
		if ( materialList != null ) {
			
			materialList.clear();
		}
		materialList = null;
		
		if ( mesh != null ) {
			
			mesh.destroy();
			mesh = null;
		}
		
		if ( verteces != null ) {
			
			verteces.clear();
			verteces = null;
		}
	}
}
