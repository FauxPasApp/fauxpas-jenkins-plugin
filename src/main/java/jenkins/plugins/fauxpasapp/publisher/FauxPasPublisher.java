package jenkins.plugins.fauxpasapp.publisher;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.logging.Logger;

import jenkins.plugins.fauxpasapp.FauxPasUtils;
import jenkins.plugins.fauxpasapp.actions.FauxPasBuildAction;
import jenkins.plugins.fauxpasapp.actions.FauxPasBuildProjectAction;

public class FauxPasPublisher extends Recorder
{
    public FauxPasPublisher()
    {
        super();
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project)
    {
        return new FauxPasBuildProjectAction(project);
    }

    @Extension
    public static final FauxPasPublisherDescriptor DESCRIPTOR = new FauxPasPublisherDescriptor();
    @Override
    public FauxPasPublisherDescriptor getDescriptor() { return DESCRIPTOR; }
    
    @Override
    public BuildStepMonitor getRequiredMonitorService()
    {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException
    {
        listener.getLogger().println("Publishing Faux Pas checking results");

        // Copy report JSON file to master
        //
        FilePath reportOutputFilePath = FauxPasUtils.getFauxPasReportWorkspaceOutputFilePath(build); 
        FilePath reportMasterOutputFilePath = FauxPasUtils.getFauxPasReportFilePath(build);
        reportOutputFilePath.copyTo(reportMasterOutputFilePath);

        // Record a build action
        //
        final FauxPasBuildAction buildAction = new FauxPasBuildAction(build, reportMasterOutputFilePath);
        build.getActions().add(buildAction);

        return true;
    }
}
