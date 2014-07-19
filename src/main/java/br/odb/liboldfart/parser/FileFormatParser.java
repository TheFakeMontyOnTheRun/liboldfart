package br.odb.liboldfart.parser;

import java.io.InputStream;

import br.odb.libstrip.MeshFactory;
import br.odb.utils.FileServerDelegate;
// TODO: this sucks. Refactor.
//=============================================================================
/**
 * @author Daniel "Monty" Monteiro
 */
public interface FileFormatParser extends GeometryDecoder {
	// -----------------------------------------------------------------------------
	public void parseDocument(FileServerDelegate fServer);

	// -----------------------------------------------------------------------------
	public void preBuffer(InputStream is);

	public void prepareForPath(String path, MeshFactory factory );

	// -----------------------------------------------------------------------------
	public void setFileServer(FileServerDelegate instance);
}
// =============================================================================