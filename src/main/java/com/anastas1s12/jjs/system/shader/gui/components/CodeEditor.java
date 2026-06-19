package com.anastas1s12.jjs.system.shader.gui.components;

import com.anastas1s12.jjs.config.SWBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.regex.*;

/**
 * GLSL Code Editor with syntax highlighting.
 *
 * Features:
 * - Syntax highlighting for GLSL keywords, types, functions
 * - Line numbers
 * - Auto-indentation
 * - Error highlighting
 * - Basic search/replace
 */
public class CodeEditor extends AbstractWidget {
    private static final int LINE_HEIGHT = 12;
    private static final int CHAR_WIDTH = 6;
    private static final int PADDING = 4;

    // GLSL Syntax patterns
    private static final Pattern KEYWORD_PATTERN = Pattern.compile(
            "\\b(void|int|float|bool|vec2|vec3|vec4|mat2|mat3|mat4|sampler2D|" +
                    "if|else|for|while|do|break|continue|return|discard|struct|uniform|in|out|inout|" +
                    "layout|location|flat|smooth|noperspective|patch|sample|invariant|precise)\\b");
    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
            "\\b(main|sin|cos|tan|asin|acos|atan|pow|exp|log|sqrt|abs|sign|floor|ceil|fract|" +
                    "mod|min|max|clamp|mix|step|smoothstep|length|distance|dot|cross|normalize|" +
                    "reflect|refract|texture|texture2D|textureLod|dFdx|dFdy|fwidth|gl_Position|" +
                    "gl_FragColor|gl_FragCoord|gl_VertexID|gl_InstanceID)\\b");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+\\.?\\d*\\b");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("//.*$|/\\*[\\s\\S]*?\\*/", Pattern.MULTILINE);
    private static final Pattern PREPROCESSOR_PATTERN = Pattern.compile("^\\s*#.*$", Pattern.MULTILINE);
    private static final Pattern STRING_PATTERN = Pattern.compile("\".*?\"");

    // Colors
    private static final int COLOR_KEYWORD = 0xFF569CD6;
    private static final int COLOR_FUNCTION = 0xFFDCDCAA;
    private static final int COLOR_NUMBER = 0xFFB5CEA8;
    private static final int COLOR_COMMENT = 0xFF6A9955;
    private static final int COLOR_PREPROCESSOR = 0xFF9B9B9B;
    private static final int COLOR_STRING = 0xFFCE9178;
    private static final int COLOR_DEFAULT = 0xFFD4D4D4;
    private static final int COLOR_LINE_NUMBER = 0xFF858585;
    private static final int COLOR_LINE_NUMBER_BG = 0xFF1E1E1E;
    private static final int COLOR_CURSOR = 0xFFFFFFFF;
    private static final int COLOR_SELECTION = 0xFF264F78;
    private static final int COLOR_ERROR = 0xFFFF0000;
    private static final int COLOR_ERROR_BG = 0x33FF0000;

    private String text = "";
    private final List<String> lines = new ArrayList<>();
    private int cursorLine = 0;
    private int cursorColumn = 0;
    private int scrollOffset = 0;
    private int errorLine = -1;

    private final java.util.function.Consumer<String> onChange;
    private final Runnable onCompile;

    // Undo/Redo
    private final Deque<EditAction> undoStack = new ArrayDeque<>();
    private final Deque<EditAction> redoStack = new ArrayDeque<>();

    public CodeEditor(int x, int y, int width, int height,
                      java.util.function.Consumer<String> onChange,
                      Runnable onCompile) {
        super(x, y, width, height, Component.empty());
        this.onChange = onChange;
        this.onCompile = onCompile;
        setText("");
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        parseLines();
        cursorLine = 0;
        cursorColumn = 0;
        scrollOffset = 0;
        errorLine = -1;
    }

    public String getText() {
        return text;
    }

    private void parseLines() {
        lines.clear();
        if (text.isEmpty()) {
            lines.add("");
            return;
        }
        String[] split = text.split("\\n", -1);
        Collections.addAll(lines, split);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // Background
        graphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF1E1E1E);

        int lineNumberWidth = SWBConfig.LINE_NUMBERS.get() ? 40 : 0;
        int contentX = getX() + lineNumberWidth + PADDING;
        int contentWidth = width - lineNumberWidth - PADDING * 2;

        // Line numbers
        if (SWBConfig.LINE_NUMBERS.get()) {
            graphics.fill(getX(), getY(), getX() + lineNumberWidth, getY() + height, COLOR_LINE_NUMBER_BG);
            for (int i = 0; i < visibleLines(); i++) {
                int lineIdx = scrollOffset + i;
                if (lineIdx >= lines.size()) break;
                int y = getY() + i * LINE_HEIGHT + PADDING;
                String num = String.valueOf(lineIdx + 1);
                int numX = getX() + lineNumberWidth - CHAR_WIDTH * num.length() - PADDING;
                graphics.drawString(Minecraft.getInstance().font, num, numX, y, COLOR_LINE_NUMBER);
            }
        }

        // Code content with syntax highlighting
        for (int i = 0; i < visibleLines(); i++) {
            int lineIdx = scrollOffset + i;
            if (lineIdx >= lines.size()) break;

            int y = getY() + i * LINE_HEIGHT + PADDING;
            String line = lines.get(lineIdx);

            // Error highlight
            if (lineIdx == errorLine) {
                graphics.fill(contentX, y - 1, contentX + contentWidth, y + LINE_HEIGHT, COLOR_ERROR_BG);
            }

            // Render syntax highlighted line
            renderHighlightedLine(graphics, line, contentX, y);
        }

        // Cursor
        if (isFocused()) {
            int cursorX = contentX + cursorColumn * CHAR_WIDTH;
            int cursorY = getY() + (cursorLine - scrollOffset) * LINE_HEIGHT + PADDING;
            if (cursorLine >= scrollOffset && cursorLine < scrollOffset + visibleLines()) {
                graphics.fill(cursorX, cursorY, cursorX + 2, cursorY + LINE_HEIGHT, COLOR_CURSOR);
            }
        }
    }

    private void renderHighlightedLine(GuiGraphics graphics, String line, int x, int y) {
        if (!SWBConfig.SYNTAX_HIGHLIGHTING.get()) {
            graphics.drawString(Minecraft.getInstance().font, line, x, y, COLOR_DEFAULT);
            return;
        }

        // Simple syntax highlighting using regex matches
        List<Token> tokens = tokenize(line);
        int currentX = x;
        for (Token token : tokens) {
            graphics.drawString(Minecraft.getInstance().font, token.text, currentX, y, token.color);
            currentX += token.text.length() * CHAR_WIDTH;
        }
    }

    private List<Token> tokenize(String line) {
        List<Token> tokens = new ArrayList<>();
        if (line.isEmpty()) return tokens;

        // This is a simplified tokenizer - production would use a proper lexer
        int pos = 0;
        while (pos < line.length()) {
            // Check for comment
            if (line.substring(pos).startsWith("//")) {
                tokens.add(new Token(line.substring(pos), COLOR_COMMENT));
                break;
            }

            // Check for preprocessor
            if (pos == 0 && line.trim().startsWith("#")) {
                tokens.add(new Token(line, COLOR_PREPROCESSOR));
                break;
            }

            // Find next token
            int nextPos = pos + 1;
            String remaining = line.substring(pos);

            Matcher kw = KEYWORD_PATTERN.matcher(remaining);
            Matcher fn = FUNCTION_PATTERN.matcher(remaining);
            Matcher num = NUMBER_PATTERN.matcher(remaining);
            Matcher str = STRING_PATTERN.matcher(remaining);

            boolean matched = false;

            if (kw.find() && kw.start() == 0) {
                tokens.add(new Token(kw.group(), COLOR_KEYWORD));
                nextPos = pos + kw.end();
                matched = true;
            } else if (fn.find() && fn.start() == 0) {
                tokens.add(new Token(fn.group(), COLOR_FUNCTION));
                nextPos = pos + fn.end();
                matched = true;
            } else if (num.find() && num.start() == 0) {
                tokens.add(new Token(num.group(), COLOR_NUMBER));
                nextPos = pos + num.end();
                matched = true;
            } else if (str.find() && str.start() == 0) {
                tokens.add(new Token(str.group(), COLOR_STRING));
                nextPos = pos + str.end();
                matched = true;
            }

            if (!matched) {
                // Add single character as default
                tokens.add(new Token(line.substring(pos, nextPos), COLOR_DEFAULT));
            }

            pos = nextPos;
        }

        return mergeAdjacent(tokens);
    }

    private List<Token> mergeAdjacent(List<Token> tokens) {
        List<Token> merged = new ArrayList<>();
        for (Token token : tokens) {
            if (!merged.isEmpty() && merged.get(merged.size() - 1).color == token.color) {
                Token last = merged.get(merged.size() - 1);
                last.text += token.text;
            } else {
                merged.add(new Token(token.text, token.color));
            }
        }
        return merged;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!isFocused()) return false;

        insertCharacter(codePoint);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> moveCursor(-1, 0);
            case GLFW.GLFW_KEY_RIGHT -> moveCursor(1, 0);
            case GLFW.GLFW_KEY_UP -> moveCursor(0, -1);
            case GLFW.GLFW_KEY_DOWN -> moveCursor(0, 1);
            case GLFW.GLFW_KEY_HOME -> cursorColumn = 0;
            case GLFW.GLFW_KEY_END -> cursorColumn = lines.get(cursorLine).length();
            case GLFW.GLFW_KEY_BACKSPACE -> deleteCharacter();
            case GLFW.GLFW_KEY_DELETE -> deleteForward();
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> insertNewline();
            case GLFW.GLFW_KEY_TAB -> insertTab();
            case GLFW.GLFW_KEY_Z -> {
                if ((modifiers & 0x02) != 0) { // Ctrl
                    if ((modifiers & 0x01) != 0) { // Shift
                        redo();
                    } else {
                        undo();
                    }
                    return true;
                }
            }
            case GLFW.GLFW_KEY_F5 -> {
                onCompile.run();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void insertCharacter(char c) {
        saveUndo();
        String line = lines.get(cursorLine);
        String newLine = line.substring(0, cursorColumn) + c + line.substring(cursorColumn);
        lines.set(cursorLine, newLine);
        cursorColumn++;
        updateText();
    }

    private void insertNewline() {
        saveUndo();
        String line = lines.get(cursorLine);
        String newLine = line.substring(cursorColumn);
        lines.set(cursorLine, line.substring(0, cursorColumn));
        cursorLine++;
        cursorColumn = 0;

        // Auto-indent
        if (SWBConfig.AUTO_INDENT.get()) {
            int indent = getIndentLevel(lines.get(cursorLine - 1));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) sb.append("    ");
            newLine = sb.toString() + newLine;
            cursorColumn = sb.length();
        }

        lines.add(cursorLine, newLine);
        updateText();
    }

    private void insertTab() {
        saveUndo();
        String line = lines.get(cursorLine);
        String newLine = line.substring(0, cursorColumn) + "    " + line.substring(cursorColumn);
        lines.set(cursorLine, newLine);
        cursorColumn += 4;
        updateText();
    }

    private int getIndentLevel(String line) {
        int level = 0;
        for (char c : line.toCharArray()) {
            if (c == '{') level++;
            if (c == '}') level--;
        }
        return Math.max(0, level);
    }

    private void deleteCharacter() {
        if (cursorColumn > 0) {
            saveUndo();
            String line = lines.get(cursorLine);
            lines.set(cursorLine, line.substring(0, cursorColumn - 1) + line.substring(cursorColumn));
            cursorColumn--;
            updateText();
        } else if (cursorLine > 0) {
            saveUndo();
            String prevLine = lines.get(cursorLine - 1);
            cursorColumn = prevLine.length();
            lines.set(cursorLine - 1, prevLine + lines.get(cursorLine));
            lines.remove(cursorLine);
            cursorLine--;
            updateText();
        }
    }

    private void deleteForward() {
        String line = lines.get(cursorLine);
        if (cursorColumn < line.length()) {
            saveUndo();
            lines.set(cursorLine, line.substring(0, cursorColumn) + line.substring(cursorColumn + 1));
            updateText();
        }
    }

    private void moveCursor(int dx, int dy) {
        cursorColumn += dx;
        cursorLine += dy;

        // Clamp
        cursorLine = Math.max(0, Math.min(cursorLine, lines.size() - 1));
        cursorColumn = Math.max(0, Math.min(cursorColumn, lines.get(cursorLine).length()));

        // Scroll if needed
        if (cursorLine < scrollOffset) {
            scrollOffset = cursorLine;
        } else if (cursorLine >= scrollOffset + visibleLines()) {
            scrollOffset = cursorLine - visibleLines() + 1;
        }
    }

    private void updateText() {
        text = String.join("\n", lines);
        onChange.accept(text);
    }

    private void saveUndo() {
        undoStack.push(new EditAction(text, cursorLine, cursorColumn));
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        EditAction action = undoStack.pop();
        redoStack.push(new EditAction(text, cursorLine, cursorColumn));
        text = action.text;
        cursorLine = action.line;
        cursorColumn = action.column;
        parseLines();
        onChange.accept(text);
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        EditAction action = redoStack.pop();
        undoStack.push(new EditAction(text, cursorLine, cursorColumn));
        text = action.text;
        cursorLine = action.line;
        cursorColumn = action.column;
        parseLines();
        onChange.accept(text);
    }

    private int visibleLines() {
        return (height - PADDING * 2) / LINE_HEIGHT;
    }

    public void highlightError(int line) {
        this.errorLine = line;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    private static class Token {
        String text;
        int color;
        Token(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

    private record EditAction(String text, int line, int column) {}
}