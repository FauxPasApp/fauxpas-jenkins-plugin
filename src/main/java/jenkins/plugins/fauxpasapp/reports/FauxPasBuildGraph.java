package jenkins.plugins.fauxpasapp.reports;

import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.renderer.category.StackedAreaRenderer;

public class FauxPasBuildGraph extends Graph
{
    private final List<GraphPoint> points;
    
    public FauxPasBuildGraph(List<GraphPoint> points)
    {
        super(Calendar.getInstance(), 350, 150);
        this.points = points;
    }

    private static final Color RED    = new Color(0xEF, 0x29, 0x29);
    private static final Color YELLOW = new Color(0xCC, 0xCC, 0x00);
    private static final Color CYAN   = new Color(0x5F, 0xAB, 0xD4);

    @Override
    protected JFreeChart createGraph()
    {
        DataSetBuilder<Row, NumberOnlyBuildLabel> dataSetBuilder = new DataSetBuilder<Row, NumberOnlyBuildLabel>();
        for (GraphPoint point : points)
        {
            dataSetBuilder.add(point.getConcernDiagnosticsCount(), CONCERN_ROW, new NumberOnlyBuildLabel(point.getBuild()));
            dataSetBuilder.add(point.getWarningDiagnosticsCount(), WARNING_ROW, new NumberOnlyBuildLabel(point.getBuild()));
            dataSetBuilder.add(point.getErrorDiagnosticsCount(), ERROR_ROW, new NumberOnlyBuildLabel(point.getBuild()));
        }

        final JFreeChart chart = ChartFactory.createStackedAreaChart(
            null, // chart title
            null, // category axis label
            "Diagnostics", // value axis label
            dataSetBuilder.build(), // dataset
            PlotOrientation.VERTICAL, // orientation
            false, // include legend
            true, // tooltips
            true // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        
        CategoryAxis domainAxis = new ShiftedCategoryAxis("Build Number");
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);
  
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        StackedAreaRenderer renderer = new StackedAreaRenderer2();
        plot.setRenderer(renderer);
        renderer.setSeriesPaint(2, RED);
        renderer.setSeriesPaint(1, YELLOW);
        renderer.setSeriesPaint(0, CYAN);

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));
        return chart;
    }

    private static class Row implements Comparable<Row>
    {
        private final String tag;
        private final int number;

        public Row(String tag, int number)
        {
            this.tag = tag;
            this.number = number;
        }
        
        @Override
        public String toString()
        {
            return tag;
        }

        @Override
        public int hashCode()
        {
            return number;
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other) return true;
            if (other == null) return false;
            if (getClass() != other.getClass()) return false;
            return ((Row)other).number == this.number;
        }

        @Override
        public int compareTo(Row other)
        {
            return number == other.number ? 0 : number < other.number ? 1 : -1;
        }
    }

    private static final Row ERROR_ROW   = new Row("Error", 0);
    private static final Row WARNING_ROW = new Row("Warning", 1);
    private static final Row CONCERN_ROW = new Row("Concern", 2);
}
