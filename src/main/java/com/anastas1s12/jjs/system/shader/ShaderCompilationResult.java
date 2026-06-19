package com.anastas1s12.jjs.system.shader;

public record ShaderCompilationResult(boolean success, String errorMessage, int errorLine) {
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public int getErrorLine() { return errorLine; }
}