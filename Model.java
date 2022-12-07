import gmaths.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;

public class Model {

  private Mesh mesh;
  private Texture textureId1;
  private Texture textureId2;
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light[] worldLights;
  private SpotLight[] spotLights;

  public Model(GL3 gl, Camera camera, Light[] worldLights, SpotLight[] spotLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1, Texture textureId2) {

    // Setup instance variables
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.worldLights = worldLights;
    this.spotLights = spotLights;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }


  public Model(GL3 gl, Camera camera, Light[] worldLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1) {
    this(gl, camera, worldLights, null, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, Light[] worldLights, SpotLight[] spotLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1) {
    this(gl, camera, worldLights, spotLights, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, Light[] worldLights, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1, Texture textureId2) {
    this(gl, camera, worldLights, null, shader, material, modelMatrix, mesh, textureId1, null);
  }

  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1, Texture textureId2) {
    this(gl, camera, new Light[]{light}, null, shader, material, modelMatrix, mesh, textureId1, textureId2);
  }

  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, Texture textureId1) {
    this(gl, camera, new Light[]{light}, null, shader, material, modelMatrix, mesh, textureId1, null);
  }



  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }

  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  public void setWorldLights(Light[] worldLights) {
    this.worldLights = worldLights;
  }

  public void setSpotLights(SpotLight[] spotLights) {
    this.spotLights = spotLights;
  }


  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

    shader.setVec3(gl, "viewPos", camera.getPosition());

    // ============ Set the lighting for each world light =================

    // Only render a uniform array if we need to so we can reuse this with other shaders

    // TODO maybe extend shader class to handle more lights?
    if (worldLights.length > 1) {
      for (int i = 0; i < worldLights.length; i++) {
        Light currentLight = worldLights[i];
        shader.setVec3(gl, String.format("worldLights[%s].position", i), currentLight.getPosition());
        shader.setVec3(gl, String.format("worldLights[%s].ambient", i), currentLight.getMaterial().getAmbient());
        shader.setVec3(gl, String.format("worldLights[%s].diffuse", i), currentLight.getMaterial().getDiffuse());
        shader.setVec3(gl, String.format("worldLights[%s].specular", i), currentLight.getMaterial().getSpecular());

      }
    }else{
      Light light = worldLights[0];
      shader.setVec3(gl, "light.position", light.getPosition());
      shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
      shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
      shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());
    }

    // ============ Set the lighting for each point light =================

    if (spotLights != null)
    {
      for (int i = 0; i < spotLights.length; i++) {
        SpotLight currentLight = spotLights[i];

        System.out.println(currentLight.getDirection());

        shader.setVec3(gl, String.format("SpotLights[%s].position", i), currentLight.getPosition());
        shader.setVec3(gl, String.format("SpotLights[%s].ambient", i), currentLight.getMaterial().getAmbient());
        shader.setVec3(gl, String.format("SpotLights[%s].diffuse", i), currentLight.getMaterial().getDiffuse());
        shader.setVec3(gl, String.format("SpotLights[%s].specular", i), currentLight.getMaterial().getSpecular());

        // Set the point light attributes
        shader.setVec3(gl, String.format("SpotLights[%s].direction", i), currentLight.getDirection());
        shader.setFloat(gl, String.format("SpotLights[%s].cutOff", i), currentLight.getCutoff());
        shader.setFloat(gl, String.format("SpotLights[%s].outerCutOff", i), currentLight.getOuterCutoff());

        Vec3 equation = currentLight.getEquation();
        shader.setFloat(gl, String.format("SpotLights[%s].quadratic", i), equation.x);
        shader.setFloat(gl, String.format("SpotLights[%s].linear", i), equation.y);
        shader.setFloat(gl, String.format("SpotLights[%s].constant", i), equation.z);

      }
    }


    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      textureId1.bind(gl);  // uses JOGL Texture class
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      textureId2.bind(gl);  // uses JOGL Texture class
    }
    mesh.render(gl);
  }

  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }

  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) textureId1.destroy(gl);
    if (textureId2!=null) textureId2.destroy(gl);
  }

}