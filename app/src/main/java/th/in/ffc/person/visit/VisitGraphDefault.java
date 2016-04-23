package th.in.ffc.person.visit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import th.in.ffc.R;

import java.util.ArrayList;

public class VisitGraphDefault {
    ArrayList<String> labelDate;
    int xBound;
    XYMultipleSeriesRenderer xyreRenderer;
    XYSeriesRenderer rendererfisrtValue;
    XYSeries xySeries;
    XYMultipleSeriesDataset dataSet;
    Context context;

    public VisitGraphDefault(Context context) {
        this.context = context;
        dataSet = new XYMultipleSeriesDataset();
        xyreRenderer = new XYMultipleSeriesRenderer();
        labelDate = new ArrayList<String>();

    }

    public void setGraph(ArrayList<String> value, ArrayList<String> date,
                         ArrayList<String> tag) {
        String TAG[] = tag.get(0).split("-");
        xBound = value.size();
        xySeries = new XYSeries(TAG[0]);
        for (int i = 0; i < value.size(); i++) {
            double values = Double.parseDouble(value.get(i));
            xySeries.add(i, values);
            labelDate.add(setDateAxisX(date.get(i)));
        }
        dataSet.addSeries(xySeries);
        setRenderer();
    }

    public void setDefaultGraph(String title) {
        dataSet = new XYMultipleSeriesDataset();
        xySeries = new XYSeries(context.getString(R.string.weightkilo));
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
        xyreRenderer.setXTitle(context.getString(R.string.monthyear));


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
        xyreRenderer.setXTitle(context.getString(R.string.monthyear));
        xyreRenderer.setXAxisMin(0);
        xyreRenderer.setXAxisMax(xBound + 1);
        xyreRenderer.setXLabelsPadding(30);
        xyreRenderer.setZoomButtonsVisible(true);
        xyreRenderer.setLegendTextSize(15);
    }

    private void setRenderer() {
        xyreRenderer = new XYMultipleSeriesRenderer();
        rendererfisrtValue = new XYSeriesRenderer();
        rendererfisrtValue.setColor(Color.BLUE);
        rendererfisrtValue.setLineWidth(5);
        rendererfisrtValue.setDisplayChartValues(true);
        rendererfisrtValue.setPointStrokeWidth(3);
        rendererfisrtValue.setChartValuesTextSize(15);
        rendererfisrtValue.setPointStyle(PointStyle.CIRCLE);
        xyreRenderer.addSeriesRenderer(rendererfisrtValue);

        xyreRenderer.setYLabelsAlign(Align.RIGHT);
        xyreRenderer.setYLabelsColor(0, Color.BLUE);
        xyreRenderer.setYAxisMax(200);
        xyreRenderer.setYAxisMin(50);

        xyreRenderer.setShowGridY(true);
        xyreRenderer.setYTitle(context.getString(R.string.sugargraph));
        xyreRenderer.setXTitle(context.getString(R.string.monthyear));
        xyreRenderer.setLegendTextSize(20);
        xyreRenderer.setXLabelsColor(Color.WHITE);
        for (int i = 0; i < labelDate.size(); i++) {
            String detail[] = labelDate.get(i).split("/");
            xyreRenderer.addXTextLabel(i, detail[0] + "/" + detail[1]);
        }
        xyreRenderer.setMargins(new int[]{30, 70, 0, 70});
        xyreRenderer.setChartTitle(context.getString(R.string.sugargraphtitle));
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
        xyreRenderer.setXTitle(context.getString(R.string.monthyear));
        xyreRenderer.setXAxisMin(0);
        xyreRenderer.setXAxisMax(xBound + 1);
        xyreRenderer.setXLabelsPadding(30);
        xyreRenderer.setZoomButtonsVisible(true);
    }

    public GraphicalView getGraph() {
        return ChartFactory.getLineChartView(context, dataSet, xyreRenderer);
    }

    private String setDateAxisX(String date) {
        String temps[] = date.split("-");
        int year = Integer.parseInt(temps[0]);
        int month = Integer.parseInt(temps[1]);
        int day = Integer.parseInt(temps[2]);
        date = day + "/" + month + "/" + year;
        return date;
    }


}
