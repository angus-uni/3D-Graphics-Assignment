import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * We use the poses class
 * to store all the poses
 * an object, currently it only
 * supports lamp poses
 * @author Angus Goody
 */

public class Poses {

	private LampPose[] poses;

	public Poses(LampPose start, LampPose pose2, LampPose pose3) {
		poses = new LampPose[]{start,pose2,pose3};

	}

	public LampPose getPose(int pose){
		// Get the pose for this lamp
		return poses[pose];
	}
}

