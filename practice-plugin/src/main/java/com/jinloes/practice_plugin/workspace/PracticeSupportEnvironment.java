package com.jinloes.practice_plugin.workspace;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

@Service(Service.Level.PROJECT)
public final class PracticeSupportEnvironment implements Disposable {
    private static final String OWNED_SDK_PREFIX = "Algorithm Practice IDE JDK ";
    private final Project project;
    private final Set<RunnerAndConfigurationSettings> temporaryConfigurations = new LinkedHashSet<>();
    private Module module;
    private Sdk sdk;
    private Sdk ownedFallbackSdk;
    private Sdk previousProjectSdk;
    private boolean managesProjectSdk;

    public PracticeSupportEnvironment(Project project) {
        this.project = project;
    }

    public static PracticeSupportEnvironment get(Project project) {
        return project.getService(PracticeSupportEnvironment.class);
    }

    public synchronized Module module() throws ExecutionException {
        if (module != null && !module.isDisposed()) {
            ensureProjectSdk();
            return module;
        }
        try {
            ApplicationManager.getApplication().runWriteAction(() -> {
                Module existing = ModuleManager.getInstance(project).findModuleByName(moduleName());
                if (existing != null && !existing.isDisposed()) {
                    module = existing;
                    sdk = ModuleRootManager.getInstance(existing).getSdk();
                } else {
                    sdk = selectSdk();
                    module = ModuleManager.getInstance(project).newNonPersistentModule(
                            moduleName(),
                            StdModuleTypes.JAVA.getId());
                }
            });
            if (sdk == null) {
                sdk = selectSdk();
            }
            ModuleRootModificationUtil.setModuleSdk(module, sdk);
            ensureProjectSdk();
            VerificationWorkspace.cleanupAbandoned();
            return module;
        } catch (IllegalArgumentException exception) {
            throw new ExecutionException(exception.getMessage(), exception);
        }
    }

    public synchronized Path javaHome() throws ExecutionException {
        module();
        if (sdk == null || sdk.getHomePath() == null) {
            throw new ExecutionException("No usable Java 17 or newer SDK is available for scratch practice.");
        }
        return Path.of(sdk.getHomePath());
    }

    public synchronized void track(RunnerAndConfigurationSettings settings) {
        temporaryConfigurations.add(settings);
    }

    public synchronized boolean cleanupAbandonedArtifacts() {
        if (project.getService(com.jinloes.practice_plugin.run.PracticeRunner.class).isRunning()) {
            return false;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            VerificationWorkspace.cleanupAbandoned();
            removeAbandonedFallbackSdks();
        });
        return true;
    }

    @Override
    public synchronized void dispose() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            RunManager manager = RunManager.getInstance(project);
            temporaryConfigurations.forEach(manager::removeConfiguration);
            temporaryConfigurations.clear();
            if (module != null && !module.isDisposed()) {
                ModuleManager.getInstance(project).disposeModule(module);
            }
            if (managesProjectSdk && ProjectRootManager.getInstance(project).getProjectSdk() == sdk) {
                ProjectRootManager.getInstance(project).setProjectSdk(previousProjectSdk);
            }
            if (ownedFallbackSdk != null) {
                ProjectJdkTable.getInstance().removeJdk(ownedFallbackSdk);
            }
            module = null;
            sdk = null;
            ownedFallbackSdk = null;
            previousProjectSdk = null;
            managesProjectSdk = false;
        });
    }

    private void ensureProjectSdk() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            ProjectRootManager roots = ProjectRootManager.getInstance(project);
            if (roots.getProjectSdk() == null) {
                previousProjectSdk = null;
                roots.setProjectSdk(sdk);
                managesProjectSdk = true;
            }
        });
    }

    private Sdk selectSdk() {
        removeAbandonedFallbackSdks();
        String ownedNamePrefix = OWNED_SDK_PREFIX + ProcessHandle.current().pid() + "-";
        for (Sdk candidate : ProjectJdkTable.getInstance().getAllJdks()) {
            if (candidate.getName().startsWith(ownedNamePrefix) && isUsableJavaSdk(candidate)) {
                ownedFallbackSdk = candidate;
                return candidate;
            }
        }
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (isUsableJavaSdk(projectSdk)) {
            return projectSdk;
        }
        for (Sdk candidate : ProjectJdkTable.getInstance().getAllJdks()) {
            if (isUsableJavaSdk(candidate)) {
                return candidate;
            }
        }
        Path ideJdk = Path.of(System.getProperty("java.home"));
        if (!JavaSdk.getInstance().isValidSdkHome(ideJdk.toString())) {
            throw new IllegalArgumentException("The IDE runtime is not a full Java 17 or newer JDK.");
        }
        Sdk fallback = JavaSdk.getInstance().createJdk(
                OWNED_SDK_PREFIX + ProcessHandle.current().pid() + "-"
                        + Integer.toUnsignedString(project.getLocationHash().hashCode()),
                ideJdk.toString(),
                false);
        if (!isUsableJavaSdk(fallback)) {
            throw new IllegalArgumentException("The IDE runtime is not a Java 17 or newer JDK.");
        }
        ProjectJdkTable.getInstance().addJdk(fallback);
        ownedFallbackSdk = fallback;
        return fallback;
    }

    private String moduleName() {
        return "Algorithm Practice Scratch Support " + Integer.toUnsignedString(project.getLocationHash().hashCode());
    }

    private static void removeAbandonedFallbackSdks() {
        ProjectJdkTable table = ProjectJdkTable.getInstance();
        for (Sdk candidate : table.getAllJdks()) {
            if (!candidate.getName().startsWith(OWNED_SDK_PREFIX)) {
                continue;
            }
            String suffix = candidate.getName().substring(OWNED_SDK_PREFIX.length());
            int separator = suffix.indexOf('-');
            if (separator <= 0) {
                continue;
            }
            try {
                long pid = Long.parseLong(suffix.substring(0, separator));
                if (ProcessHandle.of(pid).map(ProcessHandle::isAlive).orElse(false)) {
                    continue;
                }
                table.removeJdk(candidate);
            } catch (NumberFormatException ignored) {
                // Unrecognized SDK names remain untouched.
            }
        }
    }

    private static boolean isUsableJavaSdk(Sdk candidate) {
        if (candidate == null || candidate.getHomePath() == null || candidate.getSdkType() != JavaSdk.getInstance()) {
            return false;
        }
        JavaSdkVersion version = JavaSdk.getInstance().getVersion(candidate);
        return version != null && version.isAtLeast(JavaSdkVersion.JDK_17);
    }
}
