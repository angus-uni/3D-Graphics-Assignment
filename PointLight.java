import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PointLight extends Light {

  private Vec3 equation;

  public PointLight(GL3 gl, Vec3 equation) {
    super(gl);
    this.equation = equation;

  }

  public PointLight(GL3 gl) {
    this(gl, new Vec3(1,0.5f,0.2f));
  }


    public Vec3 equation()
  {
    return equation;
  }
}