/**
 * 
 */
package br.odb.liboldfart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import br.odb.gameutils.Color;
import junit.framework.Assert;

import org.junit.Test;

import br.odb.libstrip.TriangleMesh;
import br.odb.libstrip.Material;
import br.odb.libstrip.builders.GeneralTriangleFactory;

/**
 * @author Daniel "Monty" Monteiro
 *
 */
public class SimpleWavefrontOBJLoaderTest {

	private final String model1 = "o test\n"
			+ "usemtl Material\n"
			+ "#Vertices\n"
			+ "v 1.0 2.0 3.0\n"
			+ "v 2.0 3.0 4.0\n"
			+ "v 4.0 5.0 6.0\n"
			+ "v 7.0 8.0 9.0\n"
			+ "#Faces\n"
			+ "\n"
			+ "h 1 2\n"
			+ "f 1 2 3 4\n"
			+ "f 2 3 4\n"
			+ "f 1 2 4\n";
	
	private final String mat = "Kd 0.640000 0.640000 0.640000\n"
			+ "#material test\n"
			+ "\n"
			+ "newmtl Material\n" 
			+ "Ns 96.078431\n"
			+ "Ka 0.000000 0.000000 0.000000\n"
			+ "Kd 1.00000 0.50000 0.250000\n"
			+ "Ks 0.500000 0.500000 0.500000\n"
			+ "Ni 1.000000\n"
			+ "d 1.000000\n"
			+ "illum 2\n"
			+ "map_Kd tex.png";

	

	@Test
	public void testLoadMeshes() {
		WavefrontMaterialLoader matLoader = new WavefrontMaterialLoader();
		List<WavefrontMaterial> mats = matLoader.parseMaterials( inputStreamFromString( mat ) );

		SimpleWavefrontOBJLoader loader = new SimpleWavefrontOBJLoader( new GeneralTriangleFactory() );
		List<TriangleMesh> mesh = loader.loadMeshes( inputStreamFromString( model1 ), mats );
		
		Assert.assertEquals( 1, mesh.size() );
		Assert.assertEquals( "test", mesh.get( 0 ).name );

		mesh = loader.loadMeshes( inputStreamFromString( model1 ), null );
		
		Assert.assertEquals( 1, mesh.size() );
		Assert.assertEquals( "test", mesh.get( 0 ).name );

		mats = matLoader.parseMaterials( null );
		mesh = loader.loadMeshes( inputStreamFromString( "" ), mats );
		Assert.assertEquals( 0, mesh.size() );

	}
	
	@Test
	public void testNullMaterial() {
		SimpleWavefrontOBJLoader loader = new SimpleWavefrontOBJLoader( new GeneralTriangleFactory() );
		List<TriangleMesh> mesh = loader.loadMeshes( inputStreamFromString( model1 ), null );
		
		Assert.assertEquals( 1, mesh.size() );
		Assert.assertEquals( "test", mesh.get( 0 ).name );
		
		mesh = loader.loadMeshes( null, null );
		
		Assert.assertEquals( 0, mesh.size() );
	}
	
	
	@Test
	public void testMaterialParsing() {
		WavefrontMaterialLoader loader = new WavefrontMaterialLoader();
		List<WavefrontMaterial> mats = loader.parseMaterials( inputStreamFromString( mat ) );
		Assert.assertEquals( "Material", mats.get( 0 ).name );

		Assert.assertEquals( "tex.png", mats.get( 0 ).material.texture );
		Assert.assertEquals( new Color( 255, 127, 63 ), mats.get( 0 ).material.mainColor );
	}

	private InputStream inputStreamFromString(String str ) {		
		return new ByteArrayInputStream( str.getBytes() );
	}
}
