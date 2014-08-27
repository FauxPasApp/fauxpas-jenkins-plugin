package jenkins.plugins.fauxpasapp.commands;

import hudson.FilePath;
import hudson.util.ArgumentListBuilder;
import jenkins.plugins.fauxpasapp.CommandExecutor;

import java.io.OutputStream;

public class FauxPasCheckCommand implements Command
{
    private String fauxPasCommandPath;
    public String getFauxPasCommandPath() { return fauxPasCommandPath; }
    public void setFauxPasCommandPath(String fauxPasCommandPath) { this.fauxPasCommandPath = fauxPasCommandPath; }

    private String xcodeprojPath;
    public String getXcodeprojPath() { return xcodeprojPath; }
    public void setXcodeprojPath(String xcodeprojPath) { this.xcodeprojPath = xcodeprojPath; }

    private String fauxPasConfigFilePath;
    public String getFauxPasConfigFilePath() { return fauxPasConfigFilePath; }
    public void setFauxPasConfigFilePath(String fauxPasConfigFilePath) { this.fauxPasConfigFilePath = fauxPasConfigFilePath; }

    private FilePath fauxPasOutputFilePath;
    public FilePath getFauxPasOutputFilePath() { return fauxPasOutputFilePath; }
    public void setFauxPasOutputFilePath(FilePath fauxPasOutputFilePath) { this.fauxPasOutputFilePath = fauxPasOutputFilePath; }
    
    private ArgumentListBuilder getArgumentList()
    {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(getFauxPasCommandPath());
        args.add("--configFile", getFauxPasConfigFilePath());
        args.add("--outputFormat", "json");
        args.add("check", getXcodeprojPath());
        return args;
    }
    
    @Override
    public int execute(BuildContext context) throws Exception
    {
        context.log("Output file path: " + getFauxPasOutputFilePath());
        
        OutputStream outputFileStream = getFauxPasOutputFilePath().write();
        int rc = context.waitForProcess(context.getWorkspace(), getArgumentList(), outputFileStream);
        outputFileStream.flush();
        outputFileStream.close();

        if (rc == CommandExecutor.SUCCESS)
            context.log("Faux Pas exit status: success");
        else
            context.log("Faux Pas exit status: error");
        
        return rc;
    }
    
    @Override
    public String toString()
    {
        return getArgumentList().toString();
    }
}
