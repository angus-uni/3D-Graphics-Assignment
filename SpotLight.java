import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Vec3;

public class SpotLight extends Light {

  private Vec3 equation;

  public SpotLight(GL3 gl, Vec3 equation) {
    super(gl);
    this.equation = equation;

  }

  public SpotLight(GL3 gl) {
    this(gl, new Vec3(1,0.5f,0.2f));
  }


    public Vec3 equation()
  {
    return equation;
  }
}