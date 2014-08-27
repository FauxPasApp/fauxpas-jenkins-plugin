package jenkins.plugins.fauxpasapp.diagnostics;

import org.json.JSONObject;
import org.json.JSONException;

import jenkins.plugins.fauxpasapp.FauxPasUtils;

public class FauxPasDiagnostic
{
    public static final int SEVERITY_ERROR = 9;
    public static final int SEVERITY_WARNING = 5;
    public static final int SEVERITY_CONCERN = 3;

    private String baseFilePath;

    public String ruleShortName;
    public String ruleName;
    public String ruleNameHTML;
    public String ruleDescription;
    public String ruleDescriptionHTML;
    public String ruleWarning;
    public String ruleWarningHTML;
    public String info;
    public String infoHTML;
    public String identifier;
    public String filePath;
    public String context;
    public String fileSnippet;
    public String impact;
    public String severityDescription;
    public int severity;
    public String confidenceDescription;
    public int confidence;
    public int startLine;
    public int startUTF16UnitColumn;
    public int startUTF16UnitOffset;
    public int endLine;
    public int endUTF16UnitColumn;
    public int endUTF16UnitOffset;

    private String getJSONString(JSONObject obj, String key)
    {
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }
    private int getJSONInt(JSONObject obj, String key)
    {
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }
    
    public FauxPasDiagnostic()
    {
    }
    
    public FauxPasDiagnostic(JSONObject obj, String baseFilePath)
    {
        this.baseFilePath = baseFilePath;

        this.ruleShortName = getJSONString(obj, "ruleShortName");
        this.ruleName = getJSONString(obj, "ruleName");
        this.ruleDescription = getJSONString(obj, "ruleDescription");
        this.ruleWarning = getJSONString(obj, "ruleWarning");
        this.info = getJSONString(obj, "info");
        this.identifier = getJSONString(obj, "identifier");
        this.filePath = getJSONString(obj, "file");
        this.context = getJSONString(obj, "context");
        this.fileSnippet = getJSONString(obj, "fileSnippet");
        this.impact = getJSONString(obj, "impact");
        this.severityDescription = getJSONString(obj, "severityDescription");
        this.severity = getJSONInt(obj, "severity");
        this.confidenceDescription = getJSONString(obj, "confidenceDescription");
        this.confidence = getJSONInt(obj, "confidence");
        
        JSONObject extentObj = obj.getJSONObject("extent");
        if (extentObj != null)
        {
            JSONObject extentStartObj = extentObj.getJSONObject("start");
            JSONObject extentEndObj = extentObj.getJSONObject("end");
            if (extentStartObj != null && extentEndObj != null)
            {
                this.startLine = getJSONInt(extentStartObj, "line");
                this.startUTF16UnitColumn = getJSONInt(extentStartObj, "utf16Column");
                this.startUTF16UnitOffset = getJSONInt(extentStartObj, "utf16Offset");
                this.endLine = getJSONInt(extentEndObj, "line");
                this.endUTF16UnitColumn = getJSONInt(extentEndObj, "utf16Column");
                this.endUTF16UnitOffset = getJSONInt(extentEndObj, "utf16Offset");
            }
        }
        
        JSONObject htmlObj = obj.getJSONObject("html");
        if (htmlObj != null)
        {
            this.ruleNameHTML = getJSONString(htmlObj, "ruleName");
            this.ruleDescriptionHTML = getJSONString(htmlObj, "ruleDescription");
            this.ruleWarningHTML = getJSONString(htmlObj, "ruleWarning");
            this.infoHTML = getJSONString(htmlObj, "info");
        }
    }


    public String getSeverityCSSColorString()
    {
        if (SEVERITY_ERROR <= this.severity)
            return "#C7A5A5";
        else if (SEVERITY_WARNING <= this.severity)
            return "#C7C1A5";
        return "#A5BAC7";
    }

    public String getShortFilePath()
    {
        return FauxPasUtils.pathByTrimmingPrefix(filePath, baseFilePath);
    }

    public boolean hasFilePositionInfo()
    {
        return (0 < this.startUTF16UnitOffset || 0 < this.endUTF16UnitOffset);
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result
                + ((ruleShortName == null) ? 0 : ruleShortName.hashCode());
        result = prime * result
                + ((info == null) ? 0 : info.hashCode());
        result = prime * result
                + ((filePath == null) ? 0 : filePath.hashCode());
        result = prime * result
                + ((impact == null) ? 0 : impact.hashCode());
        result = prime * result
                + severity;
        result = prime * result
                + confidence;
        result = prime * result
                + startUTF16UnitOffset;
        result = prime * result
                + endUTF16UnitOffset;

        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        FauxPasDiagnostic other = (FauxPasDiagnostic) obj;

        if (ruleShortName == null) {
            if (other.ruleShortName != null)
                return false;
        } else if (!ruleShortName.equals(other.ruleShortName))
            return false;

        if (info == null) {
            if (other.info != null)
                return false;
        } else if (!info.equals(other.info))
            return false;

        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;

        if (impact == null) {
            if (other.impact != null)
                return false;
        } else if (!impact.equals(other.impact))
            return false;

        if (severity != other.severity)
            return false;

        if (confidence != other.confidence)
            return false;

        if (startUTF16UnitOffset != other.startUTF16UnitOffset)
            return false;

        if (endUTF16UnitOffset != other.endUTF16UnitOffset)
            return false;

        return true;
    }
}
