package com.jinloes.practice_plugin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the small Markdown subset exercise statements actually use into the HTML that Swing's
 * {@link javax.swing.text.html.HTMLEditorKit} understands: paragraphs, {@code -} bullet lists, and
 * {@code `inline code`}.
 *
 * <p>Statements are hard-wrapped in their manifests. A single newline inside a paragraph or bullet
 * is a soft break and is joined with a space, so the rendered text wraps to the panel width instead
 * of keeping the authored line breaks.
 */
final class MarkdownHtml {

    private static final Pattern CODE = Pattern.compile("`([^`]+)`");

    private MarkdownHtml() {
    }

    /** Renders {@code markdown} as an HTML fragment. The result is never wrapped in {@code <html>}. */
    static String toHtml(String markdown) {
        StringBuilder html = new StringBuilder();
        List<String> block = new ArrayList<>();
        for (String line : markdown.strip().lines().toList()) {
            if (line.isBlank()) {
                appendBlock(block, html);
                block.clear();
            } else {
                block.add(line);
            }
        }
        appendBlock(block, html);
        return html.toString();
    }

    /** Escapes {@code text} and renders its inline code spans, without any block markup. */
    static String inline(String text) {
        String escaped = escape(text);
        Matcher matcher = CODE.matcher(escaped);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(out, Matcher.quoteReplacement("<code>" + matcher.group(1) + "</code>"));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    /**
     * A block is an optional leading paragraph followed by an optional bullet list, because
     * statements write a section label directly above its bullets with no blank line between them.
     */
    private static void appendBlock(List<String> lines, StringBuilder html) {
        if (lines.isEmpty()) {
            return;
        }
        List<String> paragraph = new ArrayList<>();
        List<StringBuilder> items = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.strip();
            if (trimmed.startsWith("- ")) {
                items.add(new StringBuilder(trimmed.substring(2).strip()));
            } else if (items.isEmpty()) {
                paragraph.add(trimmed);
            } else {
                items.get(items.size() - 1).append(' ').append(trimmed);
            }
        }
        if (!paragraph.isEmpty()) {
            html.append("<p>").append(inline(String.join(" ", paragraph))).append("</p>");
        }
        if (!items.isEmpty()) {
            html.append("<ul>");
            for (StringBuilder item : items) {
                html.append("<li>").append(inline(item.toString())).append("</li>");
            }
            html.append("</ul>");
        }
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
