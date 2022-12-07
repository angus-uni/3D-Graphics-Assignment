import com.jogamp.opengl.*;
import gmaths.Vec3;

/**
 * A class to allow lights
 * to be added to a scene graph
 * @author Angus Goody
 */
public class LightNode extends SGNode {

	protected Light light;

	public LightNode(String name, Light l) {
		super(name);
		light = l;
	}

	public void draw(GL3 gl) {
		light.render(gl, worldTransform);
		for (int i=0; i<children.size(); i++) {
			children.get(i).draw(gl);
		}
	}

}