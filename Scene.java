import gmaths.Vec3;

/**
 * The Scene class represents
 * the whole scene and stores global
 * data such as lights
 */

public class Scene {

	private Light[] worldLights;
	private PointLight[] lampLights;

	private Room room;
	private Garden garden;

	public Scene(GL3 gl, Camera camera) {

		// Create the lights for our scene
		Light roomLight = new Light(gl);
		Vec3 sunColour = new Vec3(0.99216f,  0.98039f,  0.84314f);
		Light sun = new Light(gl,sunColour,sunColour,sunColour);

		// Store our lights
		worldLights = new Light[2];
		worldLights[0] = roomLight;
		worldLights[1] = sun;

		// Create the room for the scene
		room = new Room(gl,camera);
		worldLights[0] = room.getLight(); // TODO implement ABC
		lampLights = room.getLamps();

		// Create the garden
		garden = new Garden(gl, camera);
		worldLights[1] = garden.getLight(); // TODO implement ABC


	}
}
