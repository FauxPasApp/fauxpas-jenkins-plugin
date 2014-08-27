package jenkins.plugins.fauxpasapp.actions;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.util.regex.Pattern;

import jenkins.plugins.fauxpasapp.FauxPasUtils;
import jenkins.plugins.fauxpasapp.diagnostics.FauxPasDiagnosticsSet;
import jenkins.plugins.fauxpasapp.diagnostics.FauxPasDiagnostic;

import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import org.json.JSONObject;
import org.json.JSONArray;

public class FauxPasBuildAction implements Action, StaplerProxy, ModelObject
{
    private final FilePath diagnosticsSetFilePath;
    private final AbstractBuild<?,?> build;
    
    public FauxPasBuildAction(AbstractBuild<?,?> build, FilePath diagnosticsSetFilePath)
    {
        this.build = build;
        this.diagnosticsSetFilePath = diagnosticsSetFilePath;

        FauxPasDiagnosticsSet diagsSet = loadDiagnosticsSet();
        if (diagsSet != null)
        {
            this.totalDiagnosticsCount = diagsSet.getDiagnosticsCount();
            this.errorDiagnosticsCount = diagsSet.getDiagnosticsCountForSeverityRange(FauxPasDiagnostic.SEVERITY_ERROR, 99);
            this.warningDiagnosticsCount = diagsSet.getDiagnosticsCountForSeverityRange(FauxPasDiagnostic.SEVERITY_WARNING, FauxPasDiagnostic.SEVERITY_ERROR);
            this.concernDiagnosticsCount = diagsSet.getDiagnosticsCountForSeverityRange(0, FauxPasDiagnostic.SEVERITY_WARNING);
        }
        else
        {
            this.totalDiagnosticsCount = 0;
            this.errorDiagnosticsCount = 0;
            this.warningDiagnosticsCount = 0;
            this.concernDiagnosticsCount = 0;
        }
    }

    private final int totalDiagnosticsCount;
    public int getTotalDiagnosticsCount() { return totalDiagnosticsCount; }

    private final int errorDiagnosticsCount;
    public int getErrorDiagnosticsCount() { return errorDiagnosticsCount; }
    
    private final int warningDiagnosticsCount;
    public int getWarningDiagnosticsCount() { return warningDiagnosticsCount; }
    
    private final int concernDiagnosticsCount;
    public int getConcernDiagnosticsCount() { return concernDiagnosticsCount; }
    
    public final FauxPasDiagnosticsSet loadDiagnosticsSet()
    {
        if (diagnosticsSetFilePath == null)
            return null;

        JSONObject obj;
        try {
            obj = new JSONObject(diagnosticsSetFilePath.readToString());
        } catch (Exception e) {
            System.err.println("Unable to read diagnostics set file: " + e.getMessage());
            return null;
        }

        FauxPasDiagnosticsSet diagsSet = new FauxPasDiagnosticsSet(build.number);

        FilePath workspacePath = this.build.getWorkspace();
        String rootFilePath = (workspacePath != null) ? workspacePath.getRemote() : null;

        JSONArray diagnosticsJsonArray = obj.getJSONArray("diagnostics");
        int diagnosticsJsonArrayLength = diagnosticsJsonArray.length();
        for (int i = 0; i < diagnosticsJsonArrayLength; i++)
        {
            diagsSet.addDiagnostic(new FauxPasDiagnostic(diagnosticsJsonArray.getJSONObject(i), rootFilePath));
        }

        return diagsSet;
    }
    
    
    @Override
    public String getIconFileName()
    {
        return FauxPasUtils.getIconsPath() + "fauxpas-32x32.png";
    }

    @Override
    public String getDisplayName()
    {
        return "Faux Pas Diagnostics";
    }

    public static final String BUILD_ACTION_URL_NAME = "fauxPasBuildAction";
    
    @Override
    public String getUrlName()
    {
        return BUILD_ACTION_URL_NAME;
    }

    @Override
    public Object getTarget()
    {
        return this;
    }
    
    private final Pattern APPROVED_REPORT_REQUEST_PATTERN = Pattern.compile("[^.\\\\/]*\\.html");

    public void doBrowse(StaplerRequest request, StaplerResponse response) throws IOException
    {
        String requestedPath = FauxPasUtils.pathByRemovingLeadingSlash(request.getRestOfPath());
        if (requestedPath == null)
            response.sendError(404);
    
        if (!APPROVED_REPORT_REQUEST_PATTERN.matcher(requestedPath).matches())
        {
            System.err.println("Someone is requesting unapproved content: " + requestedPath);
            response.sendError(404);
            return;
        }
        
        FilePath reportsPath = new FilePath(build.getRootDir());
        FilePath requestedFilePath = new FilePath(reportsPath, FauxPasUtils.pathByRemovingLeadingSlash(requestedPath));
        
        try
        {
            if (!requestedFilePath.exists())
            {
                System.err.println("Unable to locate report: " + request.getRestOfPath());
                response.sendError(404);
                return;
            }
            response.serveFile(request, requestedFilePath.toURI().toURL());
        }
        catch(Exception e)
        {
            System.err.println("FAILED TO SERVE FILE: " + request.getRestOfPath() + " -> " + e.getLocalizedMessage());
            response.sendError(500);
        }
    }
}
