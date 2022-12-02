import java.io.File;
import java.io.FileInputStream;
import com.jogamp.opengl.*;

import com.jogamp.opengl.util.texture.*;

public final class TextureLibrary {

  public static Texture loadTexture(GL3 gl, String filename) {
    return loadTexture(gl, filename, GL3.GL_REPEAT, GL3.GL_REPEAT);
  }


  // mip-mapping is included
  public static Texture loadTexture(GL3 gl3, String filename,
                                    int wrappingS, int wrappingT) {
    Texture t = null;
    try {
      File f = new File(filename);
      t = (Texture)TextureIO.newTexture(f, true);
      t.bind(gl3);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, wrappingS);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, wrappingT);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
      t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);

      gl3.glGenerateMipmap(GL3.GL_TEXTURE_2D);
    }
    catch(Exception e) {
      System.out.println("Error loading texture " + filename);
    }
    return t;
  }

}