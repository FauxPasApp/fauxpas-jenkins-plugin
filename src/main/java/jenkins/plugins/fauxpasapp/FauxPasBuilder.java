package jenkins.plugins.fauxpasapp;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Computer;
import hudson.tasks.Builder;

import java.io.IOException;

import jenkins.plugins.fauxpasapp.commands.BuildContextImpl;
import jenkins.plugins.fauxpasapp.commands.FauxPasCheckCommand;

import org.kohsuke.stapler.DataBoundConstructor;

public class FauxPasBuilder extends Builder
{
    @Extension
    public static final FauxPasAppDescriptor DESCRIPTOR = new FauxPasAppDescriptor();
    @Override
    public FauxPasAppDescriptor getDescriptor() { return DESCRIPTOR; }

    private String configFilePath;
    public String getConfigFilePath() { return configFilePath; }

    private String projectFilePath;
    public String getProjectFilePath() { return projectFilePath; }

    @DataBoundConstructor
    public FauxPasBuilder(String projectFilePath, String configFilePath)
    {
        this.projectFilePath = Util.fixEmptyAndTrim(projectFilePath);
        this.configFilePath = Util.fixEmptyAndTrim(configFilePath);
    }

    @Override
    public boolean perform(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException
    {
        FauxPasAppToolInstallation fauxPasInstallation = DESCRIPTOR.getDefaultInstallation();
        if (fauxPasInstallation == null)
        {
            listener.fatalError("Unable to locate the Faux Pas installation.");
            return false;
        }

        fauxPasInstallation = fauxPasInstallation.forNode(Computer.currentComputer().getNode(), listener);
        fauxPasInstallation = fauxPasInstallation.forEnvironment(build.getEnvironment(listener));
        
        FauxPasCheckCommand fauxpasCommand = new FauxPasCheckCommand();
        fauxpasCommand.setFauxPasOutputFilePath(FauxPasUtils.getFauxPasReportWorkspaceOutputFilePath(build));
        fauxpasCommand.setXcodeprojPath(getProjectFilePath());
        fauxpasCommand.setFauxPasConfigFilePath(getConfigFilePath());
        
        try
        {
            String path = fauxPasInstallation.getExecutable(launcher);
            if (path == null)
            {
                listener.fatalError("Unable to locate 'fauxpas' command within '" + fauxPasInstallation.getHome() + "' as configured in installation named '" + fauxPasInstallation.getName() + "' in the global config.");
                return false;
            }
            fauxpasCommand.setFauxPasCommandPath(path);
        }
        catch (Exception e)
        {
            listener.fatalError("Unable to locate 'fauxpas' command within '" + fauxPasInstallation.getHome() + "' as configured in installation named '" + fauxPasInstallation.getName() + "' in the global config.", e);
            return false;
        }
        
        int rc = CommandExecutor.execute(fauxpasCommand).withContext(new BuildContextImpl(build, launcher, listener));
        return rc == CommandExecutor.SUCCESS;
    }
}

