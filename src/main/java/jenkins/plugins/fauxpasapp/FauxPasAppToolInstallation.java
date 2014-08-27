package jenkins.plugins.fauxpasapp;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.EnvironmentSpecific;
import hudson.model.TaskListener;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.remoting.Callable;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolProperty;
import hudson.tools.ToolInstallation;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class FauxPasAppToolInstallation extends ToolInstallation implements NodeSpecific<FauxPasAppToolInstallation>, EnvironmentSpecific<FauxPasAppToolInstallation>
{
    @DataBoundConstructor
    public FauxPasAppToolInstallation( 
            String name, 
            String home,
            List<? extends ToolProperty<?>> properties
            )
    {
        super(Util.fixEmptyAndTrim(name), 
              FauxPasUtils.pathByRemovingTrailingSlash(Util.fixEmptyAndTrim(home)),
              properties);
    }

    public String getExecutable(Launcher launcher) throws IOException, InterruptedException
    {
        return launcher.getChannel().call(new Callable<String,IOException>()
        {
            @Override
            public String call() throws IOException
            {
                File fauxpasFile = new File(getHome(), "fauxpas");
                return fauxpasFile.exists() ? fauxpasFile.getPath() : null;
            }
        });
    }
    
    @Override
    public FauxPasAppToolInstallation forEnvironment(EnvVars environment)
    {
        return new FauxPasAppToolInstallation(getName(), getHome(), getProperties().toList());
    }

    @Override
    public FauxPasAppToolInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException
    {
        return new FauxPasAppToolInstallation(getName(), translateFor(node, log), getProperties().toList());
    }

    @Extension
    public static class FauxPasAppToolDescriptor extends ToolDescriptor<FauxPasAppToolInstallation>
    {
        @Override
        public String getDisplayName()
        {
            return "Faux Pas";
        }

        @Override
        public FauxPasAppToolInstallation[] getInstallations()
        {
            return locateMainDescriptor().getInstallations();
        }

        @Override
        public void setInstallations(FauxPasAppToolInstallation... installations)
        {
            locateMainDescriptor().setInstallations(installations);
        }
        
        private FauxPasAppDescriptor locateMainDescriptor()
        {
            return Hudson.getInstance().getDescriptorByType(FauxPasAppDescriptor.class);
        }

        @Override
        public FormValidation doCheckHome(@QueryParameter File value)
        {
            if (!Hudson.getInstance().hasPermission(Hudson.ADMINISTER))
                return FormValidation.ok();

            if (value.getPath().equals(""))
                return FormValidation.ok(); // can be blank for master configurations and overriden on nodes

            if (!value.isDirectory())
                return FormValidation.error("Path to 'fauxpas' must be the directory that contains the 'fauxpas' command. If this is your master and you will be overriding this value on a node you can leave this value blank.");

            File commandFile = new File(value, "fauxpas");
            if(!commandFile.exists())
                return FormValidation.warning( "Unable to locate 'fauxpas' in the provided home directory." );

            return FormValidation.ok();
        }
    }
}
