package jenkins.plugins.fauxpasapp.diagnostics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FauxPasDiagnosticsSet
{
    public FauxPasDiagnosticsSet(int buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    private final int buildNumber;
    public int getBuildNumber() { return buildNumber; }
    
    public Set<FauxPasDiagnostic> diagnostics = new HashSet<FauxPasDiagnostic>();
    
    public boolean addDiagnostic(FauxPasDiagnostic diagnostic)
    {
        return diagnostics.add(diagnostic);
    }
    
    public void addDiagnostics(Collection<FauxPasDiagnostic> diagnostics)
    {
        this.diagnostics.addAll(diagnostics);
    }

    public int getDiagnosticsCount()
    {
        return diagnostics.size();
    }

    public int getDiagnosticsCountForSeverityRange(int minSeverityInclusive, int maxSeverityExclusive)
    {
        int count = 0;
        for (FauxPasDiagnostic diag : diagnostics)
        {
            if (minSeverityInclusive <= diag.severity && diag.severity < maxSeverityExclusive)
                count++;
        }
        return count;
    }

    public List<FauxPasDiagnostic> getDiagnostics()
    {
        return new ArrayList<FauxPasDiagnostic>(diagnostics);
    }
}
