import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import javafx.scene.chart.XYChart;
import org.mariuszgromada.math.mxparser.*;

public class GraphPlotter extends Application {
    private static String expression;

    public static void launch(Class<? extends Application> appClass, String expr) {
        expression = expr;
        Application.launch(appClass);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Graph Plotter");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Graph of " + expression);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("y = " + expression);

        Expression e = new Expression("f(x) = " + expression);
        for (double x = -10; x <= 10; x += 0.1) {
            e.setArgumentValue("x", x);
            double y = e.calculate();
            if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                series.getData().add(new XYChart.Data<>(x, y));
            }
        }

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}