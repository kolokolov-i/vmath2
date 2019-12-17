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
import org.apache.commons.math3.complex.Complex;

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
//        colResult.setCellValueFactory(t -> t.getValue().resultProperty());
    }

    private Float[] xx,
            ff = {0f, 8f, 4f, 4f, 3f, 0f, -3f, -3f, -4f, -8f, 0f},
            //ff = {0f, -8f, -7f, -6f, -5f, -4f, -3f, -2f, -1f, 0f, 2f, 4f, 7f, 10f, 8f, 5f, 2f, 0f, -1f, 1f, 0f},
            //ff = {0f, 0f, 5f, 5f, 5f, 0f, 0f, -3f, -3f, -3f, 0f},
            //ff = {0f, 0f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 0f, 0f},
            rr;
    private int n = 10;//, fs = 2;//, nn = 10;
    private float x0 = 0;// xStep = 0.5f;
    private Complex aa[];
    private Complex res[];

    private void initData() {
        xx = new Float[n+1];
        rr = new Float[n+1];
        float curX = x0;
        for (int i = 0; i < n+1; i++) {
            xx[i] = curX;
            curX++;
        }
    }

    private void fillTable() {
        items = new ArrayList<>();
        for (int i = 0; i < n+1; i++) {
            items.add(new ValueItem(xx[i], ff[i]));
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
        aa = new Complex[n];
        for (int k = 0; k < n; k++) {
            Complex s = new Complex(0);
            for (int j = 0; j < n; j++) {
                float tf = ff[j];
                double ti = -2 * Math.PI * xx[j] * k / n;
                Complex t = new Complex(Math.cos(ti), Math.sin(ti));
                s = s.add(t.multiply(tf));
            }
            System.out.printf("A[%d] = %f + i* %f\n", k, s.getReal(), s.getImaginary());
            aa[k] = s;
        }
    }

    private void calcR() {
        int xi = 0;
        int mg = 5;
        res = new Complex[n*mg+1];
        float xStep = 1.f/mg;
        for (float x = 0; x < n; x+=xStep) {
            Complex tr = new Complex(0);
            int ki = 0;
            for (int k = 0; k < n/2; k++) {
                double ti = 2*Math.PI*x*k/n;
                Complex mul = aa[ki++].multiply(new Complex(Math.cos(ti), Math.sin(ti)));
                System.out.printf("AA %f + i * %f\n", mul.getReal(), mul.getImaginary());
                tr = tr.add(mul);
            }
            for (int k = -n/2; k < 0; k++) {
                double ti = 2*Math.PI*x*k/n;
                Complex mul = aa[ki++].multiply(new Complex(Math.cos(ti), Math.sin(ti)));
                System.out.printf("AA %f + i * %f\n", mul.getReal(), mul.getImaginary());
                tr = tr.add(mul);
            }
            float r = (float) tr.getReal()/n;
            res[xi++] = new Complex(x, r);
            System.out.printf("%f = %f\n", x, r);
            //rr[xi++] = r;
        }
    }

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
        for (Complex c : res){
            if(c == null) continue;
            seriesR.getData().add(new XYChart.Data(c.getReal(), c.getImaginary()));
        }
//        for (ValueItem item : items) {
//            if (item.getR() != null) {
//                seriesR.getData().add(new XYChart.Data(item.getI(), item.getR()));
//            }
//        }
        chart.getData().add(seriesR);
    }

    public class ValueItem {

        private StringProperty indexProperty;
        private StringProperty functionProperty;
        //private StringProperty resultProperty;

        private Float i;
        private Float f;
        //private Float r;

        public ValueItem(Float i, Float f) {
            indexProperty = new SimpleStringProperty(i == null ? "" : String.format("%2.1f", i));
            functionProperty = new SimpleStringProperty(f == null ? "" : String.format("%2.1f", f));
            //resultProperty = new SimpleStringProperty(r == null ? "" : String.format("%2.1f", r));
            this.i = i;
            this.f = f;
            //this.r = r;
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

//        public Float getR() {
//            return r;
//        }

//        public void setR(Float r) {
//            this.r = r;
//        }

        public StringProperty indexProperty() {
            return indexProperty;
        }

        public StringProperty functionProperty() {
            return functionProperty;
        }

//        public StringProperty resultProperty() {
//            return resultProperty;
//        }
    }
}
