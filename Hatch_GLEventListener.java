import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
  
public class Hatch_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Hatch_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    ceilingLight.dispose(gl);
    room.dispose(gl);
    table.dispose(gl);
    garden.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
   
  private boolean animation = false;
  private double savedTime = 0;
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }

  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Light ceilingLight, sun;
  private Texture[] texture;   // array of textures

  private Room room;
  private Table table;
  private Garden garden;

  private void loadTextures(GL3 gl) {
    texture = new Texture[9];
    texture[0] = TextureLibrary.loadTexture(gl, "textures/floor.jpg");
    texture[1] = TextureLibrary.loadTexture(gl, "textures/wall.jpg");
    texture[2] = TextureLibrary.loadTexture(gl, "textures/window.png");

    texture[3] = TextureLibrary.loadTexture(gl, "textures/tabletop.jpg");
    texture[4] = TextureLibrary.loadTexture(gl, "textures/table_legs.jpg");
    texture[5] = TextureLibrary.loadTexture(gl, "textures/egg.jpg");
    texture[6] = TextureLibrary.loadTexture(gl, "textures/egg_map.jpg");

  }


  private void initialise(GL3 gl) {
    createRandomNumbers();
    loadTextures(gl);

    ceilingLight = new Light(gl);
    ceilingLight.setCamera(camera);

    sun = new Light(gl);
    sun.setCamera(camera);

    room = new Room(gl, camera,ceilingLight, texture[0], texture[1], texture[2]);
    table  = new Table(gl, camera, ceilingLight, texture[3], texture[4], texture[5], texture[6]);
    garden = new Garden(gl, camera,ceilingLight);

    // Place the ceiling light on top of the room
    ceilingLight.setPosition(0,room.wallSize,0);
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    //Calculate & Render light position
    //ceilingLight.setPosition(getLightPosition());  // changing light position each frame
    ceilingLight.render(gl);

    // Render our office room
    room.setClouds(getCloudsPosition());
    room.render(gl);

    garden.setClouds(getCloudsPosition());
    garden.render(gl);

    // Render our table
    table.render(gl);

  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  private Vec2 getCloudsPosition() {
    double elapsedTime = getSeconds()-startTime;
    double t = elapsedTime*0.1;  // *0.1 slows it down a bit
    float offsetX = (float)(t - Math.floor(t));
    float offsetY = 0.0f;
    return new Vec2(offsetX, offsetY);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
}