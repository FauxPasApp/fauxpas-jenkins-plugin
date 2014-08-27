package jenkins.plugins.fauxpasapp;

import hudson.Plugin;
import java.io.IOException;
import org.xml.sax.SAXException;

public class PluginImpl extends Plugin
{
    public static final String SHORTNAME = "fauxpasapp-plugin";
	
    @Override
    public void start() throws IOException, SAXException { }
}
