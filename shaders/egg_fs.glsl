#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform vec3 viewPos;

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight {
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};


struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform Light light;
int NR_POINT_LIGHTS = 2;
uniform PointLight pointLights[2];

uniform Material material;

vec3 CalcWorldLight(Light worldLight, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    // ambient
    vec3 ambient = worldLight.ambient * material.ambient * texture(first_texture, aTexCoord).rgb;

    // diffuse
    vec3 lightDir = normalize(worldLight.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = worldLight.diffuse * (diff * material.diffuse) * texture(first_texture, aTexCoord).rgb;

    // specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = worldLight.specular * (spec * material.specular);

    return (ambient + diffuse + specular);
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);

    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    // combine results
    vec3 ambient = light.ambient * material.ambient * texture(first_texture, aTexCoord).rgb;
    vec3 diffuse  = light.diffuse  * diff * material.diffuse * texture(first_texture, aTexCoord).rgb;
    vec3 specular = light.specular * spec * material.specular * texture(first_texture, aTexCoord).rgb;

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}


void main() {

    vec3 viewDir = normalize(viewPos - aPos);
    vec3 norm = normalize(aNormal);

    vec3 result = CalcWorldLight(light, norm, aPos, viewDir);

    // Go though our spot lights
    for(int i = 0; i < NR_POINT_LIGHTS; i++)
        result += CalcPointLight(pointLights[i], norm, aPos, viewDir);

    fragColor = vec4(result, 1.0);
}