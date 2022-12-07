import gmaths.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/**
 * The class to handle the different
 * poses a lamp can take
 */
public class LampPose {

	public static Mat4 noTransform = new Mat4(1);

	private Mat4 baseTransform, armTransform, headTransform;
	/**
	 *
	 * @param gl
	 * @param baseTransform
	 * @param armTransform
	 * @param headTransform
	 */
	public LampPose(Mat4 baseTransform, Mat4 armTransform, Mat4 headTransform) {
		this.baseTransform = baseTransform;
		this.armTransform = armTransform;
		this.headTransform = headTransform;

	}

	public Mat4 getBaseTransform(){
		return baseTransform;
	}

	public Mat4 getArmTransform(){
		return armTransform;
	}

	public Mat4 getHeadTransform(){
		return headTransform;
	}
}
