import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import gmaths.Mat4;
import gmaths.Vec3;

/**
 * A class that extends a light,
 * and will have a directional beam
 * of light
 * @author Angus Goody
 */
public class SpotLight extends Light {

  private Vec3 equation, direction;
  private float cutoff, outerCutoff;


  public SpotLight(GL3 gl,float cutoff, float outerCutoff, Vec3 equation) {
    super(gl);
    this.equation = equation;
    this.cutoff = cutoff;
    this.outerCutoff = outerCutoff;

    // Set an initial direction
    this.direction = new Vec3(1,0,0);
  }


  public Vec3 getEquation()
  {
    return equation;
  }

  @Override
  public void render(GL3 gl, Mat4 worldTransform) {
    super.render(gl, worldTransform);
    // Fetch the direction from the world matrix
    this.direction = worldTransform.getXDirection();
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