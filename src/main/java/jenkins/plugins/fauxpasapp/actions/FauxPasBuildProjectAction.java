package jenkins.plugins.fauxpasapp.actions;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jenkins.plugins.fauxpasapp.FauxPasUtils;
import jenkins.plugins.fauxpasapp.reports.FauxPasBuildGraph;
import jenkins.plugins.fauxpasapp.reports.GraphPoint;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class FauxPasBuildProjectAction implements Action
{
    public final AbstractProject<?,?> project;
    
    public FauxPasBuildProjectAction(AbstractProject<?,?> project)
    {
        super();
        this.project = project;
    }
    
    @Override
    public String getIconFileName()
    {
        return FauxPasUtils.getIconsPath() + "fauxpas-32x32.png";
    }
    
    public FauxPasBuildGraph getGraph()
    {
        return new FauxPasBuildGraph(getGraphPointsForLatestBuilds(project.getLastBuild()));
    }

    @Override
    public String getDisplayName()
    {
        return "Faux Pas Diagnostics Trend";
    }

    @Override
    public String getUrlName()
    {
        return "fauxPasDiagnosticsTrend";
    }

    private static final String DEFAULT_IMAGE = "/images/headless.png";
    
    public void doGraph(StaplerRequest request, StaplerResponse response) throws IOException
    {
        if (ChartUtil.awtProblemCause != null)
        {
            response.sendRedirect2(request.getContextPath() + DEFAULT_IMAGE);
            return;
        }

        getGraph().doPng(request, response);
    }
    
    public void doMap(StaplerRequest request, StaplerResponse response) throws IOException
    {
        getGraph().doMap(request, response);
    }
    
    public boolean buildDataExists()
    {
        List<GraphPoint> points = getGraphPointsForLatestBuilds(project.getLastBuild());
        return 0 < points.size();
    }
    
    private static List<GraphPoint> getGraphPointsForLatestBuilds(AbstractBuild<?,?> latestBuild)
    {
        int numberOfBuildsToGather = 20;
        
        List<GraphPoint> points = new ArrayList<GraphPoint>();
        if (latestBuild == null)
            return points;
        
        int gatheredBuilds = 0;
        for (AbstractBuild<?,?> build = latestBuild; build != null; build = build.getPreviousBuild())
        {
            if (numberOfBuildsToGather <= gatheredBuilds)
                return points;
            
            FauxPasBuildAction action = build.getAction(FauxPasBuildAction.class);
            if (action == null)
                continue;
            
            points.add(new GraphPoint(build,
                                      action.getTotalDiagnosticsCount(),
                                      action.getErrorDiagnosticsCount(),
                                      action.getWarningDiagnosticsCount(),
                                      action.getConcernDiagnosticsCount()
                                      ));
            gatheredBuilds++;
        }
        
        return points;
    }
}
