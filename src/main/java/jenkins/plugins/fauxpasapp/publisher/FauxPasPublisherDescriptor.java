package jenkins.plugins.fauxpasapp.publisher;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;


public class FauxPasPublisherDescriptor extends BuildStepDescriptor<Publisher>
{
    public FauxPasPublisherDescriptor()
    {
        super(FauxPasPublisher.class);
        load();
    }
    
    @Override
    public Publisher newInstance(StaplerRequest arg0, JSONObject json) throws hudson.model.Descriptor.FormException
    {
        return new FauxPasPublisher();
    }
    
    @Override
    public String getDisplayName()
    {
        return "Publish Faux Pas checking results";
    }

    @Override
    public boolean isApplicable(@SuppressWarnings("rawtypes") Class<? extends AbstractProject> jobType)
    {
        if (!FreeStyleProject.class.isAssignableFrom(jobType))
        {
            System.err.println("Faux Pas ERROR: Expected FreeStyleProject but was: " + jobType + " at Publisher Descriptor");
            return false;
        }
        return true;
    }
}
