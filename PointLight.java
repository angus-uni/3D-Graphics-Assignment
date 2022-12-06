import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PointLight extends Light {

  private float constant, linear, quadratic;

  public PointLight(GL3 gl) {
    super(gl);

    // TODO parameters
    constant = 1.0f;
    linear = 0.5f;
    quadratic = 0.2f;

  }

  public Vec3 equation()
  {
    return new Vec3(quadratic, linear, constant);
  }
}