package jenkins.plugins.fauxpasapp.reports;

import hudson.model.AbstractBuild;

public class GraphPoint
{
    public GraphPoint(AbstractBuild<?, ?> build,
                      int totalDiagnosticsCount,
                      int errorDiagnosticsCount,
                      int warningDiagnosticsCount,
                      int concernDiagnosticsCount
                      )
    {
        super();
        this.build = build;
        this.totalDiagnosticsCount = totalDiagnosticsCount;
        this.errorDiagnosticsCount = errorDiagnosticsCount;
        this.warningDiagnosticsCount = warningDiagnosticsCount;
        this.concernDiagnosticsCount = concernDiagnosticsCount;
    }
    
    private AbstractBuild<?,?> build;
    public AbstractBuild<?, ?> getBuild() { return build; }
    public void setBuild(AbstractBuild<?, ?> build) { this.build = build; }

    private final int totalDiagnosticsCount;
    public int getTotalDiagnosticsCount() { return totalDiagnosticsCount; }

    private final int errorDiagnosticsCount;
    public int getErrorDiagnosticsCount() { return errorDiagnosticsCount; }
    
    private final int warningDiagnosticsCount;
    public int getWarningDiagnosticsCount() { return warningDiagnosticsCount; }
    
    private final int concernDiagnosticsCount;
    public int getConcernDiagnosticsCount() { return concernDiagnosticsCount; }
}
