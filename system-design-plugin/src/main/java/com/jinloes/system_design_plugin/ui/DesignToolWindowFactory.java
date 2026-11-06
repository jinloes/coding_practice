package com.jinloes.system_design_plugin.ui;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingConstants;

public final class DesignToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory factory = ContentFactory.getInstance();
        if (!JBCefApp.isSupported()) {
            JBLabel label = new JBLabel("<html>System Design Practice requires JCEF (the embedded browser). "
                    + "Use an IDE runtime with JCEF support.</html>", SwingConstants.CENTER);
            label.setBorder(JBUI.Borders.empty(12));
            toolWindow.getContentManager().addContent(factory.createContent(label, "", false));
            return;
        }
        DesignController controller = new DesignController(project);
        var content = factory.createContent(controller.component(), "", false);
        content.setDisposer(controller);
        toolWindow.getContentManager().addContent(content);
    }
}
