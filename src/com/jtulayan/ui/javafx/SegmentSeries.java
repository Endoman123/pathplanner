package com.jtulayan.ui.javafx;

import jaci.pathfinder.Trajectory;
import javafx.scene.chart.XYChart;

public class SegmentSeries {
    Trajectory trajectory;

    public SegmentSeries(Trajectory t) {
        trajectory = t;
    }

    public XYChart.Series<Double, Double> getPositionSeries() {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

        for (int i = 0; i < trajectory.segments.length; i++) {
            XYChart.Data<Double, Double> data = new XYChart.Data<>();

            data.setXValue(trajectory.get(i).x);
            data.setYValue(trajectory.get(i).y);

            series.getData().add(data);
        }
        return series;
    }

    public XYChart.Series<Double, Double> getVelocitySeries() {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

        for (int i = 0; i < trajectory.segments.length; i++) {
            XYChart.Data<Double, Double> data = new XYChart.Data<>();

            data.setXValue(trajectory.get(i).dt * i);
            data.setYValue(trajectory.get(i).velocity);

            series.getData().add(data);
        }
        return series;
    }
}
