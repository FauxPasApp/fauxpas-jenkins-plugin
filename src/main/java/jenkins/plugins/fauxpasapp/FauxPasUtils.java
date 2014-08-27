package jenkins.plugins.fauxpasapp;

import hudson.FilePath;
import hudson.model.AbstractBuild;


public class FauxPasUtils
{
    public static String getIconsPath()
    {
        return "/plugin/" + PluginImpl.SHORTNAME + "/icons/";
    }

    public static final String REPORT_OUTPUT_FILENAME = "diagnostics_set.json";

    public static FilePath getFauxPasReportWorkspaceOutputFilePath(AbstractBuild<?,?> build)
    {
        if (build == null)
            return null;
        return new FilePath(build.getWorkspace(), REPORT_OUTPUT_FILENAME);
    }

    public static FilePath getFauxPasReportFilePath(AbstractBuild<?,?> build)
    {
        if (build == null)
            return null;
        return new FilePath(new FilePath(build.getRootDir()), REPORT_OUTPUT_FILENAME);
    }

    public static String pathByRemovingLeadingSlash(String path)
    {
        if (path == null)
            return null;
        if (!path.startsWith("/"))
            return path.trim();
        return path.substring(1).trim();
    }

    public static String pathByRemovingTrailingSlash(String path)
    {
        if (path == null)
            return "";
        if (path.endsWith("/"))
            return path.substring(0, path.length() - 1);
        return path;
    }

    public static String pathByTrimmingPrefix(String path, String prefixPath)
    {
        if (prefixPath == null)
            return path;
        if (path.startsWith(prefixPath))
            return path.substring(prefixPath.length());
        return path;
    }
}
