package jenkins.plugins.fauxpasapp.commands;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.util.ArgumentListBuilder;
import hudson.model.TaskListener;

import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("rawtypes")
public class BuildContextImpl implements BuildContext
{
    private final AbstractBuild build;
    private final Launcher launcher;
    private final BuildListener listener;

    public BuildContextImpl(AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        super();
        this.build = build;
        this.launcher = launcher;
        this.listener = listener;
    }
    
    @Override
    public FilePath getWorkspace()
    {
        return build.getWorkspace();
    }

    public int waitForProcess(ProcStarter processStarter)
    {
        try {
            return processStarter.join();
        } catch (IOException e) {
            log("Error starting process: " + processStarter + "\n" + e.getMessage());
        } catch (InterruptedException e) {
            log("Process was interrupted: " + processStarter + "\n" + e.getMessage());
        }
        return 1;
    }

    @Override
    public void log(String message)
    {
        listener.getLogger().println(message);
    }

    @Override
    public FilePath getBuildFolder()
    {
        return new FilePath(build.getRootDir());
    }

    @Override
    public int waitForProcess(FilePath workingDirectory, ArgumentListBuilder command, OutputStream stdoutStream)
    {
        ProcStarter procStarter = launcher.launch();
        try {
            final EnvVars buildEnvironment = build.getEnvironment(TaskListener.NULL);
            procStarter.envs(buildEnvironment);
        } catch (IOException e) {
            log("Error getting environment: " + e.getMessage());
        } catch (InterruptedException e) {
            log("Interrupted when getting environment: " + e.getMessage());
        }

        procStarter.pwd(workingDirectory);
        procStarter.cmds(command);

        procStarter.stderr(listener.getLogger());

        if (stdoutStream != null)
            procStarter.stdout(stdoutStream);
        else
            procStarter.stdout(listener);
        
        log("EXECUTING COMMAND:" + procStarter.cmds());

        try {
            return procStarter.join();
        } catch (IOException e) {
            log("Error starting process: " + procStarter + "\nException: " + e.getMessage());
        } catch (InterruptedException e) {
            log("Process was interrupted: " + procStarter + "\nException: " + e.getMessage());
        }
        
        return 1; // ERROR
    }
}
