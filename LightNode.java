import com.jogamp.opengl.*;
import gmaths.Vec3;

public class LightNode extends SGNode {

	protected PointLight light;

	public LightNode(String name, PointLight l) {
		super(name);
		light = l;
	}

	public void setSize(Vec3 size){
		light.setSize(size);
	}

	public void draw(GL3 gl) {
		light.setPosition(worldTransform.getPosition());
		light.render(gl);
		for (int i=0; i<children.size(); i++) {
			children.get(i).draw(gl);
		}
	}

}