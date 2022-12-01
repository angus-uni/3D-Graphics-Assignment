import java.io.File;
import java.io.IOException;
import com.jogamp.common.util.IOUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.*;

public class Cubemap {

	private static final String[] suffixes = { "posx", "negx", "posy", "negy", "posz", "negz" };
	private static final int[] targets = { GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
			GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
			GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
			GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
			GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
			GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };

	public static Texture loadFromStreams(GL3 gl,
	                                      ClassLoader scope,
	                                      String basename,
	                                      String suffix, boolean mipmapped){
		Texture cubemap = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);

		try {
			for (int i = 0; i < suffixes.length; i++) {
				String resourceName = basename + suffixes[i] + "." + suffix;
				TextureData data = TextureIO.newTextureData(GLContext.getCurrentGL().getGLProfile(), scope.getResourceAsStream(resourceName),
						mipmapped,
						IOUtil.getFileSuffix(resourceName));
				cubemap.updateImage(gl, data, targets[i]);
			}

		}
		catch(Exception e) {
			System.out.println("Error loading cube map");
		}

		return cubemap;
	}
}