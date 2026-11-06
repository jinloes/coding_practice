package com.jinloes.system_design_plugin.ui;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.UIUtil;
import com.jinloes.system_design_plugin.web.PageState;

import java.awt.Color;
import java.awt.Font;

/** The current IDE look and feel as {@link PageState.Theme} CSS values. */
final class IdeTheme {
    private IdeTheme() {
    }

    static PageState.Theme current() {
        Font font = UIUtil.getLabelFont();
        return new PageState.Theme(isDark(), css(UIUtil.getPanelBackground()), css(UIUtil.getLabelForeground()),
                css(NamedColorUtil.getInactiveTextColor()), css(JBColor.border()),
                css(JBUI.CurrentTheme.Link.Foreground.ENABLED),
                "\"" + font.getFamily().replace("\"", "") + "\", system-ui, sans-serif", font.getSize());
    }

    static boolean isDark() {
        return !JBColor.isBright();
    }

    private static String css(Color color) {
        return "#" + ColorUtil.toHex(color);
    }
}
