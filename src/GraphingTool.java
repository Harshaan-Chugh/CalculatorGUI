import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class GraphingTool extends JFrame {
    private final JTextField functionInput;
    private final JTextField rangeStartInput;
    private final JTextField rangeEndInput;
    private final JPanel chartContainer;

    public GraphingTool() {
        setTitle("Graphing Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Function:"));
        functionInput = new JTextField("sin(x)", 15);
        inputPanel.add(functionInput);

        inputPanel.add(new JLabel("Domain Start:"));
        rangeStartInput = new JTextField("-10", 5);
        inputPanel.add(rangeStartInput);

        inputPanel.add(new JLabel("Domain End:"));
        rangeEndInput = new JTextField("10", 5);
        inputPanel.add(rangeEndInput);

        JButton plotButton = new JButton("Plot");
        plotButton.addActionListener(_ -> plotGraph());
        inputPanel.add(plotButton);

        chartContainer = new JPanel(new BorderLayout());

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);

        setVisible(true);
    }

    private void plotGraph() {
        String functionText = functionInput.getText();
        double rangeStart = Double.parseDouble(rangeStartInput.getText());
        double rangeEnd = Double.parseDouble(rangeEndInput.getText());

        XYSeries series = new XYSeries("Graph");

        Expression e = new ExpressionBuilder(functionText).variable("x").build();

        for (double x = rangeStart; x <= rangeEnd; x += 0.1) {
            e.setVariable("x", x);
            double y = e.evaluate();
            series.add(x, y);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Graph",
            "X-Axis",
            "Y-Axis",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis());
        plot.setRangeAxis(new NumberAxis());

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        chartContainer.removeAll();
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
    }

    public static void main(String[] args) {
        new GraphingTool();
    }
}