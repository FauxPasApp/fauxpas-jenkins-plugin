package jenkins.plugins.fauxpasapp.commands;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import java.io.OutputStream;

public interface BuildContext
{
    public FilePath getBuildFolder();

    public FilePath getWorkspace();
    
    public int waitForProcess(FilePath workingDirectory, ArgumentListBuilder command, OutputStream stdoutStream);
    
    public void log(String message);
}
