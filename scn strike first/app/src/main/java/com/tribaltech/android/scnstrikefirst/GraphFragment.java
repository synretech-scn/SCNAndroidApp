package com.tribaltech.android.scnstrikefirst;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tribaltech.android.util.AppConstants;
import com.tribaltech.android.util.CommonUtil;
import com.tribaltech.android.util.Data;
import com.tribaltech.android.util.FilterItem;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rmn.androidscreenlibrary.ASSL;

public class GraphFragment extends Fragment implements OnClickListener, UserStats.Filterable, AppConstants {

    private static final int SERIES_NR = 2;

    /**
     * Colors to be used for the pie slices.
     */
    private static int[] COLORS = new int[]{Color.GREEN, Color.CYAN,
            Color.MAGENTA, Color.YELLOW, Color.GRAY, Color.WHITE, Color.RED};
    private static String[] COLORS_RGB = new String[]{"#7798BF", "#f45b5b",
            "#8085e9", "#8d4654", "#939920", "#ff0066", "#eeaaee", "#55BF3B",
            "#DF5353", "#7798BF", "#111632", "fea721"};
    private static int[] colors = {Color.parseColor("#ffffff"),
            Color.parseColor("#0076FE")/*Blue*/, Color.parseColor("#FFF226") /*Yellow*/};
    /** The ScreenMain series that will include all the data. */
    // private CategorySeries mSeries = new CategorySeries("");
    /** The ScreenMain renderer for the ScreenMain dataset. */
    // private DefaultRenderer mRenderer = new DefaultRenderer();
    /**
     * The chart view that displays the data.
     */
    // private GraphicalView mChartView;
    // private GraphicalView mBarGraphView;
    // private GraphicalView mLineGraphView;

    private LinearLayout chartLayout;
    Random rand = new Random();

    String strikeSpareData = "";
    String highScoreData = "";

    private Spinner tabDropDown;

    private FilterItem filterItem;

    TextView errorMsg;
    Map<Integer, String> cache = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.graph_fragment, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 720,
                1196, true);
        errorMsg = (TextView) view.findViewById(R.id.errorMsg);
        chartLayout = (LinearLayout) view.findViewById(R.id.chart);
        tabDropDown = (Spinner) view.findViewById(R.id.graphSpinner);
        List<String> gameModes = new ArrayList<String>();
        gameModes.add("Average Score");
        gameModes.add("High Score");
        gameModes.add("Open/Spare/Strike Graph");
        gameModes.add("Score Distribution");
        gameModes.add("Multi Pin Spare Conversion");
        gameModes.add("Split Conversion");
        gameModes.add("Oil Pattern");
        gameModes.add("Ball Type Spare-Strike Percentage");
        gameModes.add("Single Pin Spare Conversion");
        gameModes.add("First Ball Type Average Score");

        filterItem = CommonUtil.getFilter(getActivity());

        final EditText selectedMode = (EditText) view
                .findViewById(R.id.graph_view);
        GoBowling.CustomAdapter<String> countriesAdapter = new GoBowling.CustomAdapter<String>(
                getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, gameModes);
        countriesAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tabDropDown.setAdapter(countriesAdapter);
        tabDropDown
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {
                        selectedMode.setText(tabDropDown.getItemAtPosition(
                                position).toString());

                        // for (int i = 0; i < chartLayout.getChildCount(); i++)
                        // {
                        // View child = chartLayout.getChildAt(i);
                        // if (child != null) {
                        // chartLayout.removeView(child);
                        // }
                        // }
                        chartLayout.removeAllViews();
                        errorMsg.setVisibility(View.GONE);
                        if (position == 7) {
                            errorMsg.setText("No Ball name added.");
                        } else {
                            errorMsg.setText("You have no stats. Please play more games.");
                        }

                        String cachedJson = cache.get(position);
                        boolean useCache = false;
                        if (cachedJson != null && !cachedJson.isEmpty()) {
                            useCache = true;
                        }

                        switch (position) {
                            case AVERAGE_SCORE:
                                if (useCache) {
                                    averageScoreGraphParse(cachedJson);
                                } else {
                                    averageScoreGraphCall(filterItem);
                                }
                                break;

                            case HIGH_SCORE:
                                if (useCache) {
                                    highScoreGraphParse(cachedJson);
                                } else {
                                    highScoreGraphCall(filterItem);
                                }
                                break;

                            case STRIKE_SPARE:
                                if (useCache) {
                                    strikeSpareGraphParse(cachedJson);
                                } else {
                                    strikeSpareGraphCall(filterItem);
                                }
                                break;

                            case SCORE_DISTRIBUTION:
                                if (useCache) {
                                    scoreDistributionParse(cachedJson);
                                } else {
                                    scoreDistributionCall(filterItem);
                                }
                                break;

                            case MULTIPIN_SPARE:
                                if (useCache) {
                                    multiSpareSplitParse("multipin", cachedJson);
                                } else {
                                    multiSpareSplitCall("multiPin", filterItem);
                                }
                                break;

                            case SPLIT_SPARE:
                                if (useCache) {
                                    multiSpareSplitParse("split", cachedJson);
                                } else {
                                    multiSpareSplitCall("split", filterItem);
                                }
                                break;

                            case OIL_PATTERN:
                                if (useCache) {
                                    oilPatternGraphParse(cachedJson);
                                } else {
                                    oilPatternGraphCall(filterItem);
                                }
                                break;

                            case STRIKE_SPARE_FILTER:
                                if (useCache) {
                                    strikeSpareGraphByFilterParse(cachedJson);
                                } else {
                                    strikeSpareGraphByFilterCall(filterItem);
                                }
                                break;

                            case SINGLE_PIN:
                                if (useCache) {
                                    singlePinGraphParse(cachedJson);
                                } else {
                                    singlePinGraphCall(filterItem);
                                }
                                break;

                            case AVERAGE_BALL_TYPE:
                                if (useCache) {
                                    averageScoreGraphByBallTypeParse(cachedJson);
                                } else {
                                    averageScoreGraphByBallTypeCall(filterItem);
                                }
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonUtil.loading_box_stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    private XYMultipleSeriesDataset getDemoDataset(List<Integer> dataList,
                                                   String type) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        for (int i = 0; i < 1; i++) {
            XYSeries series = new XYSeries(type);
            for (int k = 0; k < dataList.size(); k++) {
                series.add(k + 1, dataList.get(k));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYMultipleSeriesDataset getBarDemoDataset(List<int[]> dataList) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        for (int i = 0; i < dataList.get(0).length; i++) {
            String label;
            if (i == 0) {
                label = "Strike";
            } else if (i == 1) {
                label = "Spare";
            } else {
                label = "Open";
            }
            CategorySeries series = new CategorySeries(label);
            for (int k = 0; k < dataList.size(); k++) {
                series.add(dataList.get(k)[i]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    private XYMultipleSeriesDataset getMultiDemoDataset(List<int[]> dataList,
                                                        String type) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        for (int i = 0; i < dataList.get(0).length; i++) {
            String label = "";
            if (type.equalsIgnoreCase("average")) {
                label = "Average Score";
            } else {
                if (i == 0) {
                    label = type.equalsIgnoreCase("multiPin") ? "Multi Pin"
                            : (type.equalsIgnoreCase("split") ? "Split"
                            : "Single Pin");
                } else if (i == 1) {
                    label = (type.equalsIgnoreCase("multiPin") ? "Multi Pin"
                            : (type.equalsIgnoreCase("split") ? "Split"
                            : "Single Pin"))
                            + (!type.equalsIgnoreCase("split") ? " Spare Conversion"
                            : " Conversion");
                }
            }
            CategorySeries series = new CategorySeries(label);
            for (int k = 0; k < dataList.size(); k++) {
                series.add(dataList.get(k)[i]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    private void setBarRendererSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setChartTitle("");
        renderer.setXTitle("");
        renderer.setYTitle("");
        renderer.setXAxisMin(0.5);
        // renderer.setXAxisMax(10.5);
        renderer.setYAxisMin(0);
        renderer.setXAxisMax(7);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        // renderer.setBackgroundColor(Color.RED);
        int series = renderer.getSeriesRendererCount();
        renderer.setBarSpacing(series == 1 ? 1 : 0.5);
        renderer.setGridColor(Color.GREEN);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(false, false);
        renderer.setZoomButtonsVisible(false);
        renderer.setStartAngle(180);
        renderer.setLabelsTextSize(22 * ASSL.Xscale());
        renderer.setLegendTextSize(22 * ASSL.Xscale());

        // renderer.setMargins(new int[] { 50, 50, 25, 22 });
    }

    public XYMultipleSeriesRenderer getBarDemoRenderer(List<String> xLabels,
                                                       double maxY, int seriesCount) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16 * ASSL.Xscale());
        // renderer.setYLabels(20);
        renderer.setXLabels(0);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(maxY);
        renderer.setXAxisMax(5);
        int ctr = 0;

        boolean skipVal = false;
        if (xLabels.get(1).isEmpty()) {
            skipVal = true;
        }

        for (String label : xLabels) {
            renderer.addXTextLabel(++ctr, label);
        }
        renderer.setChartTitleTextSize(20 * ASSL.Xscale());
        renderer.setLabelsTextSize(15 * ASSL.Xscale());
        renderer.setLegendTextSize(15 * ASSL.Xscale());
        renderer.setMargins(new int[]{(int) (20 * ASSL.Xscale()),
                (int) (50 * ASSL.Xscale()), (int) (40 * ASSL.Xscale()),
                (int) (40 * ASSL.Xscale())});
        SimpleSeriesRenderer r;
        for (int i = 0; i < seriesCount; i++) {
            r = new SimpleSeriesRenderer();
            // r.setColor(Color.BLUE);
            // r.setColor(Color.rgb(247,180, 68));
            r.setColor(colors[i]);
            r.setDisplayChartValues(!skipVal);
            r.setChartValuesTextSize(20 * ASSL.Xscale());
            r.setChartValuesSpacing(10 * ASSL.Xscale());
            r.setChartValuesTextAlign(Align.RIGHT);
            renderer.addSeriesRenderer(r);
        }

        return renderer;
    }

    private XYMultipleSeriesRenderer getDemoRenderer(List<String> xLabels,
                                                     double maxY) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setXLabels(0);
        // renderer.setXAxisMin(5);
        // renderer.setYAxisMin(0);
        // renderer.setXAxisMax(40);
        renderer.setYAxisMax(maxY);
        renderer.setXAxisMax(8);
        int ctr = 0;
        for (String label : xLabels) {
            renderer.addXTextLabel(++ctr, label);
        }
        renderer.setAxisTitleTextSize(16 * ASSL.Xscale());
        renderer.setChartTitleTextSize(20 * ASSL.Xscale());
        renderer.setLabelsTextSize(15 * ASSL.Xscale());
        renderer.setLegendTextSize(15 * ASSL.Xscale());
        renderer.setPointSize(5f);
        renderer.setMargins(new int[]{(int) (20 * ASSL.Xscale()),
                (int) (50 * ASSL.Xscale()), (int) (40 * ASSL.Xscale()),
                (int) (40 * ASSL.Xscale())});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setPointStyle(PointStyle.CIRCLE);
        r.setColor(colors[1]);
        r.setLineWidth(3f * ASSL.Xscale());
        r.setFillPoints(true);
        r.setDisplayChartValues(true);
        r.setChartValuesTextSize(22 * ASSL.Xscale());
        r.setChartValuesSpacing(10 * ASSL.Xscale());
        renderer.addSeriesRenderer(r);

        renderer.setPanEnabled(true, false);

        renderer.setAxesColor(Color.DKGRAY);
        renderer.setLabelsColor(Color.LTGRAY);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        renderer.setZoomEnabled(false);
        renderer.setZoomEnabled(false, false);
        renderer.setZoomButtonsVisible(false);
        renderer.setStartAngle(180);
        // renderer.setDisplayValues(true);
        renderer.setLabelsTextSize(22 * ASSL.Xscale());
        renderer.setLegendTextSize(22 * ASSL.Xscale());
        return renderer;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.filter: {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, 1);
                break;
            }

            default:
                break;
        }
    }

    private void strikeSpareGraphCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetSpare_StrikeGraph?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(STRIKE_SPARE, response);
                        strikeSpareGraphParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }


    private void strikeSpareGraphParse(String response) {
        try {
            strikeSpareData = response;
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<int[]> dataList = new ArrayList<int[]>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("filterName"));
                if (obj.getInt("strike") > maxValue) {
                    maxValue = obj.getInt("strike");
                }
                if (obj.getInt("spare") > maxValue) {
                    maxValue = obj.getInt("spare");
                }
                if (obj.getInt("open") > maxValue) {
                    maxValue = obj.getInt("open");
                }
                dataList.add(new int[]{obj.getInt("strike"),
                        obj.getInt("spare"), obj.getInt("open")});
            }

            if (xLabel.size() == 1) {
                xLabel.add("");
                dataList.add(new int[]{0, 0, 0});
            }
            XYMultipleSeriesRenderer renderer = getBarDemoRenderer(
                    xLabel, 1.2 * maxValue, 3);
            setBarRendererSettings(renderer);
            GraphicalView mBarGraphView;
            mBarGraphView = ChartFactory.getBarChartView(
                    getActivity(), getBarDemoDataset(dataList),
                    renderer, Type.DEFAULT);
            chartLayout.addView(mBarGraphView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void strikeSpareGraphByFilterCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetSpare_StrikeGraphByBallType?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(STRIKE_SPARE_FILTER, response);
                        strikeSpareGraphByFilterParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void strikeSpareGraphByFilterParse(String response) {
        try {
            strikeSpareData = response;
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<int[]> dataList = new ArrayList<int[]>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("filterName"));
                if (obj.getInt("strikePercentage") > maxValue) {
                    maxValue = obj.getInt("strikePercentage");
                }
                if (obj.getInt("sparePercentage") > maxValue) {
                    maxValue = obj.getInt("sparePercentage");
                }
                dataList.add(new int[]{
                        obj.getInt("strikePercentage"),
                        obj.getInt("sparePercentage")});
            }
            if (xLabel.size() == 1) {
                xLabel.add("");
                dataList.add(new int[]{0, 0});
            }
            XYMultipleSeriesRenderer renderer = getBarDemoRenderer(
                    xLabel, 100, 2);
            setBarRendererSettings(renderer);
            GraphicalView mBarGraphView;
            mBarGraphView = ChartFactory.getBarChartView(
                    getActivity(), getBarDemoDataset(dataList),
                    renderer, Type.DEFAULT);
            chartLayout.addView(mBarGraphView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void singlePinGraphCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetSinglePinGraph?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(SINGLE_PIN, response);
                        singlePinGraphParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void singlePinGraphParse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<int[]> dataList = new ArrayList<int[]>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            JSONObject obj = array.getJSONObject(0);
            for (int i = 1; i <= 10; i++) {
                xLabel.add("Pin " + i);
                if (obj.getInt("singlePinChancecount" + i) > maxValue) {
                    maxValue = obj
                            .getInt("singlePinChancecount" + i);
                }
                dataList.add(new int[]{

                        obj.getInt("singlePinChancecount" + i),
                        obj.getInt("singlePincount" + i)});
            }
            if (xLabel.size() == 1) {
                xLabel.add("");
                dataList.add(new int[]{0, 0});
            }

            XYMultipleSeriesRenderer renderer = getBarDemoRenderer(
                    xLabel, 1.2 * maxValue, 2);
            setBarRendererSettings(renderer);
            GraphicalView mBarGraphView;
            mBarGraphView = ChartFactory.getBarChartView(
                    getActivity(),
                    getMultiDemoDataset(dataList, "single"),
                    renderer, Type.DEFAULT);
            chartLayout.addView(mBarGraphView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void multiSpareSplitCall(final String type, FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetMultipinAndSplit_SplitConversionGraph?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(type.equals("multipin") ? MULTIPIN_SPARE : SPLIT_SPARE, response);
                        multiSpareSplitParse(type, response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void multiSpareSplitParse(String type, String response) {
        try {
            strikeSpareData = response;
            JSONArray multiJson = new JSONArray(response);

            multiJson = multiJson.getJSONArray(type
                    .equals("multiPin") ? 0 : 1);

            if (multiJson.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<int[]> dataList = new ArrayList<int[]>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < multiJson.length(); i++) {
                if (multiJson.getJSONObject(i).getString(
                        type + "Text") != null
                        && !multiJson.getJSONObject(i)
                        .getString(type + "Text")
                        .equals("null")) {
                    xLabel.add(multiJson.getJSONObject(i)
                            .getString(type + "Text"));
                    if (multiJson.getJSONObject(i).getInt(
                            type + "Chances") > maxValue) {
                        maxValue = multiJson.getJSONObject(i)
                                .getInt(type + "Chances");
                    }
                    dataList.add(new int[]{
                            multiJson.getJSONObject(i).getInt(
                                    type + "Chances"),
                            multiJson.getJSONObject(i).getInt(
                                    type)});
                }
            }
            if (type.equals("multiPin")) {
//								Collections.sort(dataList,
//										new Comparator<int[]>() {
//
//											@Override
//											public int compare(int[] lhs,
//													int[] rhs) {
//												return rhs[1] - lhs[1];
//											}
//										});
            }

            if (xLabel.size() == 1) {
                xLabel.add("");
                dataList.add(new int[]{0, 0});
            }

            XYMultipleSeriesRenderer renderer = getBarDemoRenderer(
                    xLabel, 1.2 * maxValue, 2);
            setBarRendererSettings(renderer);
            GraphicalView mBarGraphView;
            mBarGraphView = ChartFactory.getBarChartView(
                    getActivity(),
                    getMultiDemoDataset(dataList, type),
                    renderer, Type.DEFAULT);
            chartLayout.addView(mBarGraphView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void averageScoreGraphCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetAverageScoreGraph?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(AVERAGE_SCORE, response);
                        averageScoreGraphParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void averageScoreGraphParse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<Integer> dataList = new ArrayList<Integer>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("startDate"));
                if (obj.getInt("avgScore") > maxValue) {
                    maxValue = obj.getInt("avgScore");
                }
                dataList.add(obj.getInt("avgScore"));
            }
            GraphicalView mLineGraphView;
            mLineGraphView = ChartFactory.getLineChartView(
                    getActivity(),
                    getDemoDataset(dataList, "Average Score"),
                    getDemoRenderer(xLabel, 1.2 * maxValue));
            chartLayout.addView(mLineGraphView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void averageScoreGraphByBallTypeCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT * 2);

        client.get(
                Data.baseUrl
                        + "UserStat/GetAverageScoreGraphByBallType?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(AVERAGE_BALL_TYPE, response);
                        averageScoreGraphByBallTypeParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void averageScoreGraphByBallTypeParse(String response) {
        // CommonUtil.loading_box_stop();
        // try {
        // JSONArray array = new JSONArray(response);
        // if (array.length() == 0) {
        // errorMsg.setVisibility(View.VISIBLE);
        // return;
        // }
        // List<int[]> dataList = new ArrayList<int[]>();
        // List<String> xLabel = new ArrayList<String>();
        // int maxValue = 0;
        // for (int i = 0; i < array.length(); i++) {
        // JSONObject obj = array.getJSONObject(i);
        // xLabel.add(obj.getString("bawlingBallName"));
        // if (obj.getInt("avgScore") > maxValue) {
        // maxValue = obj.getInt("avgScore");
        // }
        // dataList.add(new int[] { obj.getInt("avgScore") });
        // }
        //
        // if (xLabel.size() == 1) {
        // xLabel.add("");
        // dataList.add(new int[] { 0 });
        // }
        //
        // XYMultipleSeriesRenderer renderer =
        // getBarDemoRenderer(
        // xLabel, 1.2 * maxValue, 1);
        // setBarRendererSettings(renderer);
        // GraphicalView mBarGraphView;
        // mBarGraphView = ChartFactory.getBarChartView(
        // getActivity(),
        // getMultiDemoDataset(dataList, "average"),
        // renderer, Type.DEFAULT);
        // chartLayout.addView(mBarGraphView);

        // =====================================
        CommonUtil.loading_box_stop();
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<Integer> dataList = new ArrayList<Integer>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("bawlingBallName"));
                if (obj.getInt("avgScore") > maxValue) {
                    maxValue = obj.getInt("avgScore");
                }
                dataList.add(obj.getInt("avgScore"));
            }
            GraphicalView mLineGraphView;
            mLineGraphView = ChartFactory.getLineChartView(
                    getActivity(),
                    getDemoDataset(dataList, "Average Score"),
                    getDemoRenderer(xLabel, 1.2 * maxValue));
            chartLayout.addView(mLineGraphView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highScoreGraphCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetHighestScoreGraph_ByUser?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(HIGH_SCORE, response);
                        highScoreGraphParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void highScoreGraphParse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<Integer> dataList = new ArrayList<Integer>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("startDate"));
                if (obj.getInt("highestScores") > maxValue) {
                    maxValue = obj.getInt("highestScores");
                }
                dataList.add(obj.getInt("highestScores"));
            }
            GraphicalView mLineGraphView;
            mLineGraphView = ChartFactory.getLineChartView(
                    getActivity(),
                    getDemoDataset(dataList, "High Score"),
                    getDemoRenderer(xLabel, 1.2 * maxValue));
            chartLayout.addView(mLineGraphView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void oilPatternGraphCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetOilPatternGraph_ByUser?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(OIL_PATTERN, response);
                        oilPatternGraphParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void oilPatternGraphParse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }
            List<Integer> dataList = new ArrayList<Integer>();
            List<String> xLabel = new ArrayList<String>();
            int maxValue = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                xLabel.add(obj.getString("oilPattern"));
                if (obj.getInt("avgScore") > maxValue) {
                    maxValue = obj.getInt("avgScore");
                }
                dataList.add(obj.getInt("avgScore"));
            }
            GraphicalView mLineGraphView;
            mLineGraphView = ChartFactory.getLineChartView(
                    getActivity(),
                    getDemoDataset(dataList, "Oil Pattern"),
                    getDemoRenderer(xLabel, 1.2 * maxValue));
            chartLayout.addView(mLineGraphView);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void scoreDistributionCall(FilterItem filterItem) {

        errorMsg.setVisibility(View.GONE);
        if (!CommonUtil.is_loading_showing()) {
            CommonUtil.loading_box(getActivity(), "Please wait...");
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(CommonUtil.TIMEOUT);

        client.get(
                Data.baseUrl
                        + "UserStat/GetScoreDistributionGraph?token="
                        + CommonUtil.getAccessToken(getActivity()).replaceAll(
                        "[+]", "%2B") + "&apiKey=" + Data.apiKey
                        + CommonUtil.getFilterString(filterItem),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        CommonUtil.loading_box_stop();
                        cache.put(SCORE_DISTRIBUTION, response);
                        scoreDistributionParse(response);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.v("exception ", e.toString() + "");
                        CommonUtil.commonGameErrorDialog(getActivity(),
                                "An error occured.Please try again.");
                        CommonUtil.loading_box_stop();

                    }
                });
    }

    private void scoreDistributionParse(String response) {
        try {

            JSONObject json = new JSONObject(response);
            List<Integer> dataList = new ArrayList<Integer>();
            List<String> xLabel = new ArrayList<String>();
            xLabel.add("Games < 150");
            xLabel.add("151 < Games < 175");
            xLabel.add("176 < Games < 200");
            xLabel.add("201 < Games < 225");
            xLabel.add("226 < Games < 250");
            xLabel.add("251 < Games < 299");
            xLabel.add("Perfect Score Percentage");

            dataList.add(json.getInt("gamesLessThan150"));
            dataList.add(json.getInt("gamesBetween151To175"));
            dataList.add(json.getInt("gamesBetween176To200"));
            dataList.add(json.getInt("gamesBetween201To225"));
            dataList.add(json.getInt("gamesBetween226To250"));
            dataList.add(json.getInt("gamesBetween251To299"));
            dataList.add(json.getInt("gamesPerfectScore"));

            int sum = 0;
            for (int data : dataList) {
                sum += data;
            }
            if (sum == 0) {
                errorMsg.setVisibility(View.VISIBLE);
                return;
            }

            final DefaultRenderer mRenderer = new DefaultRenderer();

            final CategorySeries mSeries = new CategorySeries(
                    "");
            mRenderer.setZoomEnabled(false);
            mRenderer.setZoomButtonsVisible(false);
            mRenderer.setStartAngle(180);
            mRenderer.setDisplayValues(true);
            mRenderer.setLabelsTextSize(22 * ASSL.Xscale());
            mRenderer.setLegendTextSize(22 * ASSL.Xscale());
            for (int i = 0; i < xLabel.size(); i++) {
                mSeries.add(xLabel.get(i), dataList.get(i));
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(COLORS[(mSeries
                        .getItemCount() - 1) % COLORS.length]);
                mRenderer.addSeriesRenderer(renderer);
            }
            mRenderer.setPanEnabled(false);
            mRenderer.setMargins(new int[]{
                    (int) (0 * ASSL.Xscale()),
                    (int) (0 * ASSL.Xscale()),
                    (int) (80 * ASSL.Xscale()),
                    (int) (0 * ASSL.Xscale())});

            final GraphicalView mChartView;
            mChartView = ChartFactory.getPieChartView(
                    getActivity(), mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mChartView
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SeriesSelection seriesSelection = mChartView
                                    .getCurrentSeriesAndPoint();
                            if (seriesSelection == null) {

                            } else {
                                for (int i = 0; i < mSeries
                                        .getItemCount(); i++) {
                                    mRenderer
                                            .getSeriesRendererAt(
                                                    i)
                                            .setHighlighted(
                                                    i == seriesSelection
                                                            .getPointIndex());
                                }
                                mChartView.repaint();
                                Toast.makeText(
                                        getActivity(),
                                        "Value="
                                                + seriesSelection
                                                .getValue(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
            chartLayout.addView(mChartView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filter(FilterItem filterItem) {
        this.filterItem = filterItem;
        chartLayout.removeAllViews();
        errorMsg.setVisibility(View.GONE);
        int position = tabDropDown.getSelectedItemPosition();
        errorMsg.setVisibility(View.GONE);
        if (position == 7) {
            errorMsg.setText("No Ball name added.");
        } else {
            errorMsg.setText("You have no stats. Please play more games.");
        }
        switch (position) {
            case AVERAGE_SCORE:
                averageScoreGraphCall(filterItem);
                break;

            case HIGH_SCORE:
                highScoreGraphCall(filterItem);
                break;

            case STRIKE_SPARE:
                strikeSpareGraphCall(filterItem);
                break;

            case SCORE_DISTRIBUTION:
                scoreDistributionCall(filterItem);
                break;

            case MULTIPIN_SPARE:
                multiSpareSplitCall("multiPin", filterItem);
                break;

            case SPLIT_SPARE:
                multiSpareSplitCall("split", filterItem);
                break;

            case OIL_PATTERN:
                oilPatternGraphCall(filterItem);
                break;

            case STRIKE_SPARE_FILTER:
                strikeSpareGraphByFilterCall(filterItem);
                break;

            case SINGLE_PIN:
                singlePinGraphCall(filterItem);
                break;

            case AVERAGE_BALL_TYPE:
                averageScoreGraphByBallTypeCall(filterItem);
                break;

            default:
                break;
        }
    }


}
