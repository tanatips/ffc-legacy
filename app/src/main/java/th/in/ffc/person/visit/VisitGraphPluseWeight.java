package th.in.ffc.person.visit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import th.in.ffc.R;

import java.util.ArrayList;
import java.util.HashMap;

public class VisitGraphPluseWeight {
    private Context context;
    private XYSeries xySeries;
    private XYSeries xyFirstSpecialSeries;
    private XYSeries xySecondsSpecialSeries;
    private ArrayList<XYSeries> series;
    private ArrayList<String> dateXLabel;
    private XYMultipleSeriesRenderer xyreRenderer;
    private XYMultipleSeriesDataset dataSet;
    private ArrayList<String> TAG;
    private HashMap<Integer, Boolean> specialValue;
    private int xBound;

    XYSeriesRenderer rendererHilightValue;
    XYSeriesRenderer rendererfisrtValue;
    XYSeriesRenderer renderersecondsValue;
    XYSeriesRenderer rendererthirdValue;

    public VisitGraphPluseWeight(Context context) {
        this.context = context;
        dateXLabel = new ArrayList<String>();
        series = new ArrayList<XYSeries>();
        specialValue = new HashMap<Integer, Boolean>();
    }


    public void setSeriesGraph(ArrayList<ArrayList<String>> value, ArrayList<ArrayList<String>> date, ArrayList<String> TAG, ArrayList<String> setDate) {
        this.TAG = TAG;
        xyFirstSpecialSeries = null;
        int specialPosition = 0;
        dataSet = new XYMultipleSeriesDataset();
        xBound = setDate.size();
        if (!value.isEmpty() && !date.isEmpty()) {
            for (int i = 0; i < TAG.size(); i++) {
                ArrayList<String> tempDate = date.get(i);
                ArrayList<String> tempValue = value.get(i);
                String[] checkSpecialValue = tempValue.get(0).split("/");
                if (checkSpecialValue.length == 1) {
                    String[] tagLine = TAG.get(i).split("-");
                    xySeries = new XYSeries(tagLine[0], 1);
                    specialValue.put(specialPosition, false);
                    specialPosition++;
                } else {
                    String[] tagLine = TAG.get(i).split("-");
                    specialValue.put(specialPosition, true);
                    xyFirstSpecialSeries = new XYSeries(tagLine[1], 2);
                    specialPosition++;
                    specialValue.put(specialPosition, true);
                    xySecondsSpecialSeries = new XYSeries(tagLine[2], 3);
                    specialPosition++;
                }
                for (int j = 0; j < tempDate.size(); j++) {
                    if (xyFirstSpecialSeries != null) {
                        String[] valueSpecial = tempValue.get(j).split("/");
                        int position = findPosition(setDateAxisX(tempDate.get(j)));
                        if (position != -1) {
                            xyFirstSpecialSeries.add(position, Double.parseDouble(valueSpecial[0]));
                            xySecondsSpecialSeries.add(position, Double.parseDouble(valueSpecial[1]));
                        }
                    } else {
                        xySeries.add(j, Double.parseDouble(tempValue.get(j)));
                        if (!checkDate(setDateAxisX(tempDate.get(j)))) {
                            dateXLabel.add(setDateAxisX(tempDate.get(j)));
                        }
                    }
                }
                if (xyFirstSpecialSeries != null) {
                    series.add(xyFirstSpecialSeries);
                    series.add(xySecondsSpecialSeries);
                    xyFirstSpecialSeries = null;
                    xySecondsSpecialSeries = null;
                } else {
                    series.add(xySeries);
                }
            }
        }
/*		// single value
        if(series.size() ==1){
			dataSet.addSeries(series.get(0));
		}
		// duo value
		else if(series.size() ==2){
				dataSet.addSeries(0,series.get(0));
				dataSet.addSeries(1,series.get(1));
		}
		else{
			
		}*/
        setXYMultipleSeriesRenderer();
    }

    public void setDateHilight(String date) {
        xySeries = new XYSeries(context.getString(R.string.plusedate), 0);
        xySeries.add(findPosition(setDateAxisX(date)), -10);
        xySeries.add(findPosition(setDateAxisX(date)), 50);
        xySeries.add(findPosition(setDateAxisX(date)), 100);
        xySeries.add(findPosition(setDateAxisX(date)), 150);
        xySeries.add(findPosition(setDateAxisX(date)), 200);
        //	xySeries.add(findPosition(setDateAxisX(date)), 250);
        dataSet.addSeries(0, xySeries);
        for (int i = 0; i < series.size(); i++) {
            dataSet.addSeries(i + 1, series.get(i));
        }


        // XYSeriesRenderer for HilightValue;
        rendererHilightValue = new XYSeriesRenderer();
        rendererHilightValue.setColor(Color.parseColor("#FF33FF"));
        rendererHilightValue.setLineWidth(1);
        rendererHilightValue.setPointStrokeWidth(0);

        addXYMultipleSeriesRenderer();
    }

    public void addXYMultipleSeriesRenderer() {
        if (series.size() == 1) {
            xyreRenderer = new XYMultipleSeriesRenderer(2);
            xyreRenderer.addSeriesRenderer(rendererHilightValue);
            xyreRenderer.addSeriesRenderer(rendererfisrtValue);
        } else {
            xyreRenderer = new XYMultipleSeriesRenderer(4);
            xyreRenderer.addSeriesRenderer(rendererHilightValue);
            xyreRenderer.addSeriesRenderer(rendererfisrtValue);
            xyreRenderer.addSeriesRenderer(renderersecondsValue);
            xyreRenderer.addSeriesRenderer(rendererthirdValue);
        }

        xyreRenderer.setXLabelsColor(Color.WHITE);
        for (int i = 0; i < dateXLabel.size(); i++) {
            String detail[] = dateXLabel.get(i).split("/");
            xyreRenderer.addXTextLabel(i, detail[0] + "/" + detail[1]);
        }

        xyreRenderer.setMargins(new int[]{30, 70, 30, 70});
        xyreRenderer.setChartTitle(context.getString(R.string.plusegraphtitle));
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
        xyreRenderer.setXAxisMax(xBound);
        xyreRenderer.setXLabelsPadding(30);
        xyreRenderer.setZoomButtonsVisible(true);
        xyreRenderer.setLegendTextSize(17);
        xyreRenderer.setLegendHeight(85);
        if (series.size() == 1) setSingleYLabel();
        else if (series.size() == 2) setDuoTLabel();
        else setSpecialLabel();

    }

    public void setXYMultipleSeriesRenderer() {
        // XYSeriesRenderer for fisrtValue;
        rendererfisrtValue = new XYSeriesRenderer();
        rendererfisrtValue.setColor(Color.BLUE);
        rendererfisrtValue.setLineWidth(5);
        rendererfisrtValue.setDisplayChartValues(true);
        rendererfisrtValue.setPointStrokeWidth(3);
        rendererfisrtValue.setChartValuesTextSize(15);
        rendererfisrtValue.setPointStyle(PointStyle.CIRCLE);

        // XYSeriesRenderer for secondsValue;
        renderersecondsValue = new XYSeriesRenderer();
        renderersecondsValue.setDisplayChartValues(true);
        renderersecondsValue.setColor(Color.RED);
        renderersecondsValue.setLineWidth(5);
        renderersecondsValue.setPointStrokeWidth(3);
        renderersecondsValue.setChartValuesTextSize(15);
        renderersecondsValue.setPointStyle(PointStyle.CIRCLE);

        // XYSeriesRenderer for thirdValue;
        rendererthirdValue = new XYSeriesRenderer();
        rendererthirdValue.setDisplayChartValues(true);
        rendererthirdValue.setColor(Color.parseColor("#00AA00"));
        rendererthirdValue.setLineWidth(5);
        rendererthirdValue.setPointStrokeWidth(3);
        rendererthirdValue.setChartValuesTextSize(15);
        rendererthirdValue.setPointStyle(PointStyle.CIRCLE);

        if (series.size() == 1) {
            xyreRenderer = new XYMultipleSeriesRenderer(2);
        } else if (series.size() == 2) {
            xyreRenderer = new XYMultipleSeriesRenderer(3);
        } else {
            xyreRenderer = new XYMultipleSeriesRenderer(4);
        }

    }

    private void setSingleYLabel() {
        for (int i = 0; i < TAG.size(); i++) {
            String[] tagLine = TAG.get(i).split("-");
            if (tagLine.length == 3) {
                xyreRenderer.setYTitle(tagLine[0], i);
            } else xyreRenderer.setYTitle(tagLine[0], i);
        }
        xyreRenderer.setYLabelsAlign(Align.RIGHT, 0);
        xyreRenderer.setYLabelsColor(0, Color.BLUE);
        xyreRenderer.setYAxisMax(200, 0);
        xyreRenderer.setYAxisMin(50, 0);
        xyreRenderer.setYLabelsAlign(Align.RIGHT, 1);
        xyreRenderer.setYLabelsColor(1, Color.BLUE);
        xyreRenderer.setYAxisMax(200, 1);
        xyreRenderer.setYAxisMin(50, 1);
    }

    private void setDuoTLabel() {
        //set YAxisDetail
        for (int i = 0; i < TAG.size(); i++) {
            String[] tagLine = TAG.get(i).split("-");
            if (tagLine.length == 3) {
                Log.d("TEST", "TEST");
                xyreRenderer.setYTitle(tagLine[0], i);
            } else xyreRenderer.setYTitle(tagLine[1], i);
        }
        xyreRenderer.setYAxisColor(Color.RED);
        xyreRenderer.setYLabelsAlign(Align.RIGHT, 0);
        xyreRenderer.setYAxisAlign(Align.LEFT, 0);
        xyreRenderer.setYLabelsColor(0, Color.BLUE);
        xyreRenderer.setBarWidth(30);
        xyreRenderer.setYLabelsAlign(Align.LEFT, 1);
        xyreRenderer.setYLabelsColor(1, Color.RED);
        xyreRenderer.setYAxisAlign(Align.RIGHT, 1);
        xyreRenderer.setLabelsTextSize(15);
        xyreRenderer.setYAxisMin(50, 0);
        xyreRenderer.setYAxisMax(200, 0);
        xyreRenderer.setYAxisMin(50, 1);
        xyreRenderer.setYAxisMax(200, 1);

    }

    private void setSpecialLabel() {
        xyreRenderer.setLabelsTextSize(15);
        for (int i = 0; i < TAG.size(); i++) {
            String[] tagLine = TAG.get(i).split("-");
            if (tagLine.length == 3) {
                xyreRenderer.setYTitle(tagLine[0], 2);
            } else xyreRenderer.setYTitle(tagLine[1], 1);
        }
        // special value ALIGN LEFT
        if (specialValue.get(0)) {
            xyreRenderer.setYAxisAlign(Align.RIGHT, 1);
            xyreRenderer.setYLabelsAlign(Align.LEFT, 1);
            xyreRenderer.setYLabelsColor(1, Color.GREEN);

            xyreRenderer.setYAxisAlign(Align.LEFT, 2);
            xyreRenderer.setYLabelsAlign(Align.RIGHT, 2);
            xyreRenderer.setYLabelsColor(2, Color.RED);

            xyreRenderer.setYAxisAlign(Align.LEFT, 3);
            xyreRenderer.setYLabelsAlign(Align.RIGHT, 3);
            xyreRenderer.setYLabelsColor(3, Color.BLUE);

        }
        // special VALUE ALIGN RIGHT
        else {
            xyreRenderer.setYAxisAlign(Align.RIGHT, 0);
            xyreRenderer.setYLabelsAlign(Align.LEFT, 0);
            xyreRenderer.setYLabelsColor(0, Color.RED);

            xyreRenderer.setYAxisAlign(Align.LEFT, 1);
            xyreRenderer.setYLabelsAlign(Align.RIGHT, 1);
            xyreRenderer.setYLabelsColor(1, Color.BLUE);

            xyreRenderer.setYAxisAlign(Align.RIGHT, 2);
            xyreRenderer.setYLabelsAlign(Align.LEFT, 2);
            xyreRenderer.setYLabelsColor(2, Color.GREEN);

            xyreRenderer.setYAxisAlign(Align.RIGHT, 3);
            xyreRenderer.setYLabelsAlign(Align.LEFT, 3);
            xyreRenderer.setYLabelsColor(3, Color.RED);

        }
        xyreRenderer.setYAxisMin(20, 0);
        xyreRenderer.setYAxisMax(200, 0);
        xyreRenderer.setYAxisMin(20, 1);
        xyreRenderer.setYAxisMax(200, 1);
        xyreRenderer.setYAxisMin(20, 2);
        xyreRenderer.setYAxisMax(200, 2);
        xyreRenderer.setYAxisMin(20, 3);
        xyreRenderer.setYAxisMax(200, 3);

    }


    public GraphicalView getGraph() {
        return ChartFactory.getLineChartView(context, dataSet, xyreRenderer);
    }


    private String setDateAxisX(String date) {
        String temps[] = date.split("-");
        int year = Integer.parseInt(temps[0]);
        int month = Integer.parseInt(temps[1]);
        int day = Integer.parseInt(temps[2]);
        date = numberLessThenTen(month) + "/" + numberLessThenTen(day) + "/" + year;
        return date;
    }

    private boolean checkDate(String date) {
        boolean haveDate = false;
        if (dateXLabel.equals(date)) {
            haveDate = true;
        }
        if (dateXLabel.isEmpty()) {
            haveDate = false;
        }
        return haveDate;
    }

    private int findPosition(String date) {
        int position = -1;
        for (int i = 0; i < dateXLabel.size(); i++) {
            if (dateXLabel.get(i).equals(date)) {
                position = i;
                break;
            }
        }
        return position;
    }

    private String numberLessThenTen(int number) {
        String numbercheck = null;
        if (number < 10) {
            numbercheck = "0" + number;
        } else numbercheck = "" + number;
        return numbercheck;
    }


}
