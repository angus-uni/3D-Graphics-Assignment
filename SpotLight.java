import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Vec3;

public class SpotLight extends Light {

  private Vec3 equation, direction;
  private float cutoff, outerCutoff;


  public SpotLight(GL3 gl, Vec3 direction, float cutoff, float outerCutoff, Vec3 equation) {
    super(gl);
    this.equation = equation;
    this.direction = direction;
    this.cutoff = cutoff;
    this.outerCutoff = outerCutoff;
  }

  public Vec3 getEquation()
  {
    return equation;
  }


  public float getCutoff()
  {
    return cutoff;
  }

  public float getOuterCutoff()
  {
    return outerCutoff;
  }

  public Vec3 getDirection()
  {
    return direction;
  }

}