package com.jinloes.system_design_plugin.web;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts the restricted Markdown subset used in problem statements, hints, and concept guides
 * into escaped HTML fragments for the bundled JCEF tool-window page: {@code ##} subheadings
 * ({@code <h4>}), paragraphs ({@code <p>}), {@code -} bullet lists ({@code <ul><li>}), and
 * {@code `inline code`} ({@code <code>}).
 *
 * <p>Statements are hard-wrapped in their manifests. A single newline inside a paragraph or bullet
 * is a soft break and is joined with a space, so the rendered text wraps to the panel width instead
 * of keeping the authored line breaks.
 */
public final class MarkdownHtml {

    private static final Pattern CODE = Pattern.compile("`([^`]+)`");

    private MarkdownHtml() {
    }

    /** Renders {@code markdown} as an HTML fragment. The result is never wrapped in {@code <html>}. */
    public static String toHtml(String markdown) {
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

    /**
     * Renders only the opening block of {@code markdown} (before the first blank line), used for realistic
     * ambiguous-prompt practice where candidates gather scope before revealing detailed requirements.
     */
    public static String firstParagraphHtml(String markdown) {
        List<String> firstBlock = new ArrayList<>();
        for (String line : markdown.strip().lines().toList()) {
            if (line.isBlank()) {
                break;
            }
            firstBlock.add(line);
        }
        StringBuilder html = new StringBuilder();
        appendBlock(firstBlock, html);
        return html.toString();
    }

    /** Escapes {@code text} and renders its inline code spans, without any block markup. */
    public static String inline(String text) {
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
     * A block may contain {@code ##} subheadings, paragraphs, and {@code -} bullet lists, because
     * statements and guides often write a heading or label directly above bullets with no blank line.
     */
    private static void appendBlock(List<String> lines, StringBuilder html) {
        if (lines.isEmpty()) {
            return;
        }
        List<String> paragraph = new ArrayList<>();
        List<StringBuilder> items = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.strip();
            if (trimmed.startsWith("## ")) {
                flush(paragraph, items, html);
                html.append("<h4>").append(inline(trimmed.substring(3).strip())).append("</h4>");
            } else if (trimmed.startsWith("- ")) {
                items.add(new StringBuilder(trimmed.substring(2).strip()));
            } else if (items.isEmpty()) {
                paragraph.add(trimmed);
            } else {
                items.get(items.size() - 1).append(' ').append(trimmed);
            }
        }
        flush(paragraph, items, html);
    }

    private static void flush(List<String> paragraph, List<StringBuilder> items, StringBuilder html) {
        if (!paragraph.isEmpty()) {
            html.append("<p>").append(inline(String.join(" ", paragraph))).append("</p>");
            paragraph.clear();
        }
        if (!items.isEmpty()) {
            html.append("<ul>");
            for (StringBuilder item : items) {
                html.append("<li>").append(inline(item.toString())).append("</li>");
            }
            html.append("</ul>");
            items.clear();
        }
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
