import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;


/**
 * The Scene class represents
 * the whole scene and manages
 * global objects such as world lights
 * @author Angus Goody
 */

public class Scene {

	private Light[] worldLights;

	private Room room;
	private Garden garden;
	private Shader multiShader;
	private double startTime;


	public Scene(GL3 gl, Camera camera) {

		// Time
		startTime = getSeconds();

		// Create the lights for our scene (sun should be slightly yellow)
		Vec3 whiteLight = new Vec3(1,1,1);
		Light roomLight = new Light(gl, Vec3.multiply(whiteLight, 0.2f),Vec3.multiply(whiteLight, 0.3f),whiteLight,
				new Vec3(0.3f));

		// Colour the user see's when they look at the sun
		Vec3 sunActualColour = new Vec3(1, 0.92f, 0.07f);
		// Colour of the light rays
		Vec3 sunColour = new Vec3(1,  0.98f,  0.78f);
		Light sun = new Light(gl,Vec3.multiply(sunColour, 0.3f),Vec3.multiply(sunColour, 0.6f),whiteLight,
				new Vec3(0.8f), sunActualColour);


		// Setup lights
		roomLight.setCamera(camera);
		sun.setCamera(camera);


		// Store our world lights
		worldLights = new Light[2];
		worldLights[0] = roomLight;
		worldLights[1] = sun;

		// Store the new shaders to handle multiple world lights
		multiShader = new Shader(gl, "shaders/tt_vs.glsl", "shaders/new_fs.glsl");

		// Create the room for the scene (this should be illuminated by the room and the sun
		room = new Room(gl,camera, worldLights, multiShader);

		// Create the garden (the garden should not be illuminated by the room light)
		garden = new Garden(gl, camera, sun);


	}

	private double getSeconds() {
		return System.currentTimeMillis()/1000.0;
	}


	public void render(GL3 gl) {
		// Render the world lights
		for (Light worldLight : worldLights) {
			worldLight.render(gl);
		}

		double elapsedTime = startTime - getSeconds();
		room.render(gl, elapsedTime);
		garden.render(gl, elapsedTime);


	}

	public void dispose(GL3 gl) {
		// Dispose the world lights
		for (Light worldLight : worldLights) {
			worldLight.dispose(gl);
		}

		room.dispose(gl);
		garden.dispose(gl);
	}

	public void toggleLight(int index)
	{
		worldLights[index].toggle();
	}

	public void toggleLamp(int i) {
		room.toggleLamp(i);
	}

	public void animateLamp(int lampNumber, int pose) {
		room.animateLamp(lampNumber, pose,startTime - getSeconds());
	}
}
