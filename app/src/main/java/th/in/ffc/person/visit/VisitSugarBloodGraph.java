package th.in.ffc.person.visit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import th.in.ffc.R;

import java.util.ArrayList;

public class VisitSugarBloodGraph {
    XYMultipleSeriesDataset dataSet;
    XYMultipleSeriesRenderer xyreRenderer;
    XYSeriesRenderer rendererfisrtValue;
    ArrayList<String> labelDate;
    String[] types;
    XYMultipleSeriesDataset dataset;
    XYMultipleSeriesRenderer multiRenderer;
    int xBound;
    Context context;
    ArrayList<XYSeries> listXYSeries;
    boolean defaultChart;

    public VisitSugarBloodGraph(Context context) {
        this.context = context;
        defaultChart = true;
        labelDate = new ArrayList<String>();
        listXYSeries = new ArrayList<XYSeries>();

    }

    public void setGraph(ArrayList<String> value, ArrayList<String> date, ArrayList<String> tag) {
        defaultChart = false;
        String TAG[] = tag.get(0).split("-");
        XYSeries series = new XYSeries(TAG[0]);
        XYSeries nomalLine = new XYSeries(context.getString(R.string.sugargraphnomalline));
        xBound = value.size();

        for (int i = 0; i < value.size(); i++) {
            double values = Double.parseDouble(value.get(i));
            series.add(i + 1, values);

            labelDate.add(setDateAxisX(date.get(i)));
        }
        nomalLine.add(-1, 100);
        nomalLine.add(xBound + 5, 100);
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(nomalLine);
        dataset.addSeries(series);

        XYSeriesRenderer valueRenderer = new XYSeriesRenderer();
        valueRenderer.setColor(Color.RED);
        valueRenderer.setPointStyle(PointStyle.CIRCLE);
        valueRenderer.setFillPoints(true);
        valueRenderer.setLineWidth(2);
        valueRenderer.setDisplayChartValues(true);
        valueRenderer.setChartValuesTextSize(20);

        XYSeriesRenderer normalLineRenderer = new XYSeriesRenderer();
        normalLineRenderer.setColor(Color.parseColor("#00AA00"));
        normalLineRenderer.setFillPoints(true);
        normalLineRenderer.setLineWidth(2);

        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.addSeriesRenderer(normalLineRenderer);
        multiRenderer.addSeriesRenderer(valueRenderer);

        for (int i = 0; i < labelDate.size(); i++) {
            multiRenderer.addXTextLabel(i + 1, labelDate.get(i));
        }

        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle(context.getString(R.string.sugargraphtitle));
        multiRenderer.setXTitle(context.getString(R.string.monthyear));
        multiRenderer.setYTitle(TAG[1]);
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setBarSpacing(3);
        multiRenderer.setLabelsTextSize(15);

        multiRenderer.setAxisTitleTextSize(20);
        multiRenderer.setXLabelsPadding(10);
        multiRenderer.setChartTitleTextSize(20);
        multiRenderer.setMargins(new int[]{30, 70, 30, 70});
        multiRenderer.setPanLimits(new double[]{-1, xBound + 1, 0, 200});
        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(xBound + 1);
        multiRenderer.setYAxisMax(200);
        multiRenderer.setYAxisMin(50);
        multiRenderer.setYLabelsAlign(Align.RIGHT);
        multiRenderer.setYLabelsColor(0, Color.RED);
        multiRenderer.setLegendTextSize(17);
        types = new String[]{LineChart.TYPE, BarChart.TYPE};
    }

    public void setDefaultGraph(String title) {
        defaultChart = true;
        dataSet = new XYMultipleSeriesDataset();
        XYSeries xySeries = new XYSeries(context.getString(R.string.weightkilo));
        xySeries.add(0, 0);
        dataSet.addSeries(xySeries);
        xyreRenderer = new XYMultipleSeriesRenderer();
        rendererfisrtValue = new XYSeriesRenderer();
        rendererfisrtValue.setColor(Color.BLUE);
        xyreRenderer.addSeriesRenderer(rendererfisrtValue);
        xyreRenderer.setYLabelsAlign(Align.RIGHT);
        xyreRenderer.setYLabelsColor(0, Color.BLUE);
        xyreRenderer.setYAxisMax(200);
        xyreRenderer.setYAxisMin(50);
        xyreRenderer.setShowGridY(true);
        xyreRenderer.setXTitle(context.getString(R.string.month));
        xyreRenderer.setMargins(new int[]{30, 70, 0, 70});
        xyreRenderer.setChartTitle(title);
        xyreRenderer.setChartTitleTextSize(20);
        xyreRenderer.setShowGrid(true);
        xyreRenderer.setShowGridX(true);
        xyreRenderer.setGridColor(Color.BLACK);
        xyreRenderer.setAxisTitleTextSize(20);
        xyreRenderer.setLabelsTextSize(15);
        xyreRenderer.setXLabels(0);
        xyreRenderer.setShowGrid(true);
        xyreRenderer.setShowGridX(true);
        xyreRenderer.setPanLimits(new double[]{-1, xBound + 1, 0, 200});
        xyreRenderer.setXTitle(context.getString(R.string.month));
        xyreRenderer.setXAxisMin(0);
        xyreRenderer.setXAxisMax(xBound + 1);
        xyreRenderer.setXLabelsPadding(30);
        xyreRenderer.setZoomButtonsVisible(true);
        xyreRenderer.setLegendTextSize(15);
    }

    public GraphicalView getGraph() {
        GraphicalView graphView;
        if (!defaultChart) {
            graphView = (GraphicalView) ChartFactory.getCombinedXYChartView(context, dataset, multiRenderer, types);
        } else {
            graphView = (GraphicalView) ChartFactory.getLineChartView(context, dataSet, xyreRenderer);
        }
        return graphView;
    }

    private String setDateAxisX(String date) {
        String temps[] = date.split("-");
        int month = Integer.parseInt(temps[1]);
        int day = Integer.parseInt(temps[2]);
        date = numberLessThenTen(month) + "/" + numberLessThenTen(day);
        return date;
    }

    private String numberLessThenTen(int number) {
        String numbercheck = null;
        if (number < 10) {
            numbercheck = "0" + number;
        } else numbercheck = "" + number;
        return numbercheck;
    }
}
