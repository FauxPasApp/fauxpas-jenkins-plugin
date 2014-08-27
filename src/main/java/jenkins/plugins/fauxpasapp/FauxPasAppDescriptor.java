package jenkins.plugins.fauxpasapp;

import hudson.CopyOnWrite;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;

import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class FauxPasAppDescriptor extends BuildStepDescriptor<Builder>
{
    @CopyOnWrite
    private volatile FauxPasAppToolInstallation[] installations = new FauxPasAppToolInstallation[0];
    
    public FauxPasAppDescriptor()
    {
        super(FauxPasBuilder.class);
        load();
    }
    
    public FauxPasAppToolInstallation.FauxPasAppToolDescriptor getToolDescriptor()
    {
        return ToolInstallation.all().get(FauxPasAppToolInstallation.FauxPasAppToolDescriptor.class);
    }
    
    public FauxPasAppToolInstallation[] getInstallations()
    {
        return installations;
    }
    
    public FauxPasAppToolInstallation getNamedInstallation(String name)
    {
        if (name == null)
            return null;
        
        for (FauxPasAppToolInstallation installation : installations)
        {
            if (name.equals(installation.getName()))
                return installation;
        }
        return null;
    }

    public FauxPasAppToolInstallation getDefaultInstallation()
    {
        if (installations.length == 0)
            return new FauxPasAppToolInstallation("Default", "/usr/local/bin", null);
        return installations[0];
    }
    
    public void setInstallations(FauxPasAppToolInstallation[] installations)
    {
        this.installations = installations;
        save();
    }

    public FormValidation doCheckProjectFilePath(@QueryParameter String value) throws IOException, ServletException
    {
        if (value.length() == 0)
            return FormValidation.error("You must provide a path to the Xcode project to check.");
        return FormValidation.ok();
    }

    public FormValidation doCheckConfigFilePath(@QueryParameter String value) throws IOException, ServletException
    {
        if (value.length() == 0)
            return FormValidation.error("You must provide a path to a Faux Pas configuration file.");
        return FormValidation.ok();
    }
    
    @Override
    public String getDisplayName()
    {
        return "Faux Pas";
    }

    @Override
    public boolean configure(StaplerRequest request, JSONObject formData) throws FormException
    {
        save();
        return super.configure(request, formData);
    }
    
    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType)
    {
        return FreeStyleProject.class.isAssignableFrom(jobType);
    }
}
