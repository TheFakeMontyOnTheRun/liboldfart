/**
 * 
 */
package br.odb.liboldfart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.odb.libstrip.GeneralTriangleMesh;
import br.odb.libstrip.Material;
import br.odb.libstrip.builders.GeneralTriangleFactory;

/**
 * @author Daniel "Monty" Monteiro
 *
 */
public class WavefrontOBJLoaderTest {

	String model1 = "o test\n"
			+ "usemtl Material\n"
			+ "#Vertices\n"
			+ "v 1.0 2.0 3.0\n"
			+ "v 2.0 3.0 4.0\n"
			+ "v 4.0 5.0 6.0\n"
			+ "v 7.0 8.0 9.0\n"
			+ "#Faces\n"
			+ "f 1 2 3 4\n"
			+ "f 2 3 4\n"
			+ "f 1 2 4\n";
	
	String mat = "newmtl Material\n"
			+ "Ns 96.078431\n"
			+ "Ka 0.000000 0.000000 0.000000\n"
			+ "Kd 0.640000 0.640000 0.640000\n"
			+ "Ks 0.500000 0.500000 0.500000\n"
			+ "Ni 1.000000\n"
			+ "d 1.000000\n"
			+ "illum 2";
	
	
	/**
	 * Test method for {@link br.odb.liboldfart.WavefrontOBJLoader#loadMeshes(java.lang.String, java.io.InputStream, java.io.InputStream)}.
	 */
	@Test
	public void testLoadMeshes() {
		List<Material> mats = WavefrontMaterialLoader.parseMaterials( inputStreamFromString( mat ) );

		WavefrontOBJLoader loader = new WavefrontOBJLoader( new GeneralTriangleFactory() );
		List<GeneralTriangleMesh> mesh = loader.loadMeshes( inputStreamFromString( model1 ), mats );
		
		Assert.assertEquals( 1, mesh.size() );
		Assert.assertEquals( "test", mesh.get( 0 ).name );		
	}
	
	@Test
	public void testNullMaterial() {
		WavefrontOBJLoader loader = new WavefrontOBJLoader( new GeneralTriangleFactory() );
		List<GeneralTriangleMesh> mesh = loader.loadMeshes( inputStreamFromString( model1 ), null );
		
		Assert.assertEquals( 1, mesh.size() );
		Assert.assertEquals( "test", mesh.get( 0 ).name );
	}
	
	
	@Test
	public void testMaterialParsing() {
		List<Material> mats = WavefrontMaterialLoader.parseMaterials( inputStreamFromString( mat ) );
		Assert.assertEquals( "Material", mats.get( 0 ).name );
	}

	private InputStream inputStreamFromString(String str ) {		
		return new ByteArrayInputStream( str.getBytes() );
	}
}