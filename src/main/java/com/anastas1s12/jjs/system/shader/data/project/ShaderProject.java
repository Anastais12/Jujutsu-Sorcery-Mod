package com.anastas1s12.jjs.system.shader.data.project;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a complete shader project with all source files and metadata.
 */
public class ShaderProject {
    private String name;
    private String vertexShader;
    private String fragmentShader;
    private String jsonDefinition;
    private Map<String, Object> properties;
    private long createdAt;
    private long modifiedAt;

    // Default vertex shader template
    private static final String DEFAULT_VERTEX = """
        #version 150
        
        in vec2 Position;
        in vec2 UV0;
        
        out vec2 texCoord;
        
        void main() {
            gl_Position = vec4(Position, 0.0, 1.0);
            texCoord = UV0;
        }
        """;

    // Default fragment shader template
    private static final String DEFAULT_FRAGMENT = """
        #version 150
        
        uniform sampler2D DiffuseSampler;
        
        in vec2 texCoord;
        out vec4 fragColor;
        
        void main() {
            vec4 color = texture(DiffuseSampler, texCoord);
            fragColor = color;
        }
        """;

    // Default JSON definition
    private static final String DEFAULT_JSON = """
        {
            "targets": ["swap"],
            "passes": [
                {
                    "name": "custom",
                    "intarget": "minecraft:main",
                    "outtarget": "swap",
                    "uniforms": []
                },
                {
                    "name": "blit",
                    "intarget": "swap",
                    "outtarget": "minecraft:main"
                }
            ]
        }
        """;

    public ShaderProject(String name) {
        this.name = name;
        this.vertexShader = DEFAULT_VERTEX;
        this.fragmentShader = DEFAULT_FRAGMENT;
        this.jsonDefinition = DEFAULT_JSON;
        this.properties = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = this.createdAt;

        // Default properties
        properties.put("renderPass", "post");
        properties.put("blendMode", "alpha");
        properties.put("priority", 100);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVertexShader() { return vertexShader; }
    public void setVertexShader(String vertexShader) {
        this.vertexShader = vertexShader;
        this.modifiedAt = System.currentTimeMillis();
    }

    public String getFragmentShader() { return fragmentShader; }
    public void setFragmentShader(String fragmentShader) {
        this.fragmentShader = fragmentShader;
        this.modifiedAt = System.currentTimeMillis();
    }

    public String getJsonDefinition() { return jsonDefinition; }
    public void setJsonDefinition(String jsonDefinition) {
        this.jsonDefinition = jsonDefinition;
        this.modifiedAt = System.currentTimeMillis();
    }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperty(String key, Object value) {
        properties.put(key, value);
        this.modifiedAt = System.currentTimeMillis();
    }

    public ShaderProject duplicate(String newName) {
        ShaderProject copy = new ShaderProject(newName);
        copy.vertexShader = this.vertexShader;
        copy.fragmentShader = this.fragmentShader;
        copy.jsonDefinition = this.jsonDefinition;
        copy.properties = new HashMap<>(this.properties);
        return copy;
    }
}