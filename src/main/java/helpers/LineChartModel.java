package helpers;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class LineChartModel {

    public static LineChart lineChart (){
        CategoryAxis categoryAxis = new CategoryAxis();
        NumberAxis numberAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<String, Number>(categoryAxis, numberAxis);
        return lineChart;
    }

}
