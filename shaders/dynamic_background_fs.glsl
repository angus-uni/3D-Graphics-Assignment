#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;
in vec2 movingTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform sampler2D second_texture;
uniform vec3 viewPos;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};

uniform Light light;

struct Material {
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
};

uniform Material material;


void main() {

  // Because the cloud has transparent background we don't
  // need to mix the colours

  vec4 cloudTexColor = texture(second_texture, movingTexCoord);
  vec4 firstMix = texture(first_texture, aTexCoord);

  // Show only the cloud if it's not a transparent texel
  if(cloudTexColor.a > 0.1){
    firstMix = cloudTexColor;
  }

  // ambient mixed with
  vec3 ambient = light.ambient * material.ambient * firstMix.rgb;

  // diffuse
  vec3 norm = normalize(aNormal);
  vec3 lightDir = normalize(light.position - aPos);
  float diff = max(dot(norm, lightDir), 0.0);
  vec3 diffuse = light.diffuse * (diff * material.diffuse) * firstMix.rgb;

  // specular
  vec3 viewDir = normalize(viewPos - aPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  vec3 specular = light.specular * (spec * material.specular);

  vec3 result = ambient + diffuse + specular;
  fragColor = vec4(result, 1.0);
}