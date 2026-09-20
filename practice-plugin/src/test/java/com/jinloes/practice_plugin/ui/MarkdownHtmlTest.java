package com.jinloes.practice_plugin.ui;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownHtmlTest {

    @Test
    void rendersInlineCodeSpans() {
        assertThat(MarkdownHtml.toHtml("Call `isBalanced(input)` first."))
                .isEqualTo("<p>Call <code>isBalanced(input)</code> first.</p>");
    }

    @Test
    void rendersAdjacentCodeSpansSeparately() {
        assertThat(MarkdownHtml.toHtml("The characters `(`, `)`, and `[`."))
                .as("statements write delimiter lists as back-to-back code spans")
                .isEqualTo("<p>The characters <code>(</code>, <code>)</code>, and <code>[</code>.</p>");
    }

    @Test
    void joinsHardWrappedParagraphLinesWithSpaces() {
        assertThat(MarkdownHtml.toHtml("Decide whether a string\nis properly nested."))
                .as("authored line breaks must not survive into the rendered paragraph")
                .isEqualTo("<p>Decide whether a string is properly nested.</p>");
    }

    @Test
    void rendersBulletsAfterALeadingLabelLine() {
        String markdown = """
                Contract
                - `input` is non-null and contains
                  only the six delimiters.
                - Returns true when balanced.""";
        assertThat(MarkdownHtml.toHtml(markdown))
                .as("a section label shares its block with the bullets that follow it")
                .isEqualTo("<p>Contract</p><ul>"
                        + "<li><code>input</code> is non-null and contains only the six delimiters.</li>"
                        + "<li>Returns true when balanced.</li>"
                        + "</ul>");
    }

    @Test
    void separatesBlocksOnBlankLines() {
        assertThat(MarkdownHtml.toHtml("First paragraph.\n\nSecond paragraph."))
                .isEqualTo("<p>First paragraph.</p><p>Second paragraph.</p>");
    }

    @Test
    void escapesHtmlSoStatementsCannotInjectMarkup() {
        assertThat(MarkdownHtml.toHtml("Compare `a < b && c > d`."))
                .as("angle brackets and ampersands appear in generic and boolean examples")
                .isEqualTo("<p>Compare <code>a &lt; b &amp;&amp; c &gt; d</code>.</p>");
    }

    @Test
    void escapesMarkupOutsideCodeSpans() {
        assertThat(MarkdownHtml.toHtml("Use <b> literally."))
                .isEqualTo("<p>Use &lt;b&gt; literally.</p>");
    }

    @Test
    void rendersEmptyInputAsEmptyHtml() {
        assertThat(MarkdownHtml.toHtml("   \n\n  ")).as("blank statement").isEmpty();
    }

    @Test
    void inlineRendersWithoutBlockMarkup() {
        assertThat(MarkdownHtml.inline("pairSum([1, 2], 3)"))
                .as("example inputs are placed inside an existing code element")
                .isEqualTo("pairSum([1, 2], 3)");
    }
}
