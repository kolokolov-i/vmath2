package superbro.vmath2;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button btnCalculate;

    @FXML
    private TableView<ValueItem> table;

    @FXML
    private TableColumn<ValueItem, String> colIndex;
    @FXML
    private TableColumn<ValueItem, String> colFunction;
    @FXML
    private TableColumn<ValueItem, String> colResult;

    @FXML
    private LineChart<Number, Number> chart;

    private List<ValueItem> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        fillTable();
        colIndex.setCellValueFactory(t -> t.getValue().indexProperty());
        colFunction.setCellValueFactory(t -> t.getValue().functionProperty());
        colResult.setCellValueFactory(t -> t.getValue().resultProperty());
    }

    private Float[] xx,
            ff = {0f, null, 8f, null, 4f, null, 4f, null, 3f, null, 0f, null, -3f, null, -3f, null, -4f, null, -8f, null, 0f},
            rr;
    private int n = 21, fs = 2, nf = 10;
    private float x0 = -5, xStep = 0.5f;
    private float aa[];

    private void initData() {
        xx = new Float[n];
        rr = new Float[n];
        float curX = x0;
        for (int i = 0; i < n; i++) {
            xx[i] = curX;
            curX += xStep;
        }
    }

    private void fillTable() {
        items = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            items.add(new ValueItem(xx[i], ff[i], rr[i]));
        }
        table.setItems(FXCollections.observableArrayList(items));
    }

    public void btnCalcAction(ActionEvent e) {
        calcA();
        calcR();
        fillTable();
        show();
    }

    private void calcA() {
        aa = new float[nf];
        for (int i = 0; i < nf; i++) {
            float s = 0;
            for (int j = 0; j < i; j++) {
                s += ff[j * fs] * Math.cos(2 * Math.PI * j * i * xx[j]);
            }
            aa[i] = s * 2 / n;
        }
    }
    /*
    double A(int j)
{
double S=0;
int ii;
for (int i=-n;i<n+1;i++)
	{

	S=S+Function(2*pi*double(i)/(2*n+1))*cos(2*pi*double(j)*double(i)/(2*n+1));
	}
if (j==0) return 1/double(2*n+1)*S;

return 2/double(2*n+1)*S;
}
     */

    private void calcR() {
        for (int i = 0; i < n; i++) {
            float s = 0;
            for (int j = 0; j < nf; j++) {
                s += aa[j] * Math.cos(xx[j]);
            }
            rr[i] = s;
        }
    }
    /*
    double Interpolate(double x)
{
double S=a[0];
for (int i=1;i<n;i++)
	{
	S=S+a[i]*cos(double(i)*x)+b[i]*sin(double(i)*x);
	}
return S;
}
     */

    private void show() {
        XYChart.Series seriesF = new XYChart.Series();

        seriesF.setName("Function");
        for (ValueItem item : items) {
            if (item.getF() != null) {
                seriesF.getData().add(new XYChart.Data(item.getI(), item.getF()));
            }
        }
        chart.getData().add(seriesF);
        XYChart.Series seriesR = new XYChart.Series();
        seriesR.setName("Result");
        for (ValueItem item : items) {
            if (item.getR() != null) {
                seriesR.getData().add(new XYChart.Data(item.getI(), item.getR()));
            }
        }
        chart.getData().add(seriesR);
    }

    public class ValueItem {

        private StringProperty indexProperty;
        private StringProperty functionProperty;
        private StringProperty resultProperty;

        private Float i;
        private Float f;
        private Float r;

        public ValueItem(Float i, Float f, Float r) {
            indexProperty = new SimpleStringProperty(i == null ? "" : String.format("%2.1f", i));
            functionProperty = new SimpleStringProperty(f == null ? "" : String.format("%2.1f", f));
            resultProperty = new SimpleStringProperty(r == null ? "" : String.format("%2.1f", r));
            this.i = i;
            this.f = f;
            this.r = r;
        }

        public Float getI() {
            return i;
        }

        public void setI(Float i) {
            this.i = i;
        }

        public Float getF() {
            return f;
        }

        public void setF(Float f) {
            this.f = f;
        }

        public Float getR() {
            return r;
        }

        public void setR(Float r) {
            this.r = r;
        }

        public StringProperty indexProperty() {
            return indexProperty;
        }

        public StringProperty functionProperty() {
            return functionProperty;
        }

        public StringProperty resultProperty() {
            return resultProperty;
        }
    }
}
