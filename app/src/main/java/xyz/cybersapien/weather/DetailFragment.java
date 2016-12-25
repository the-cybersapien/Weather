package xyz.cybersapien.weather;


import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.ShareActionProvider;

import xyz.cybersapien.weather.data.WeatherContract.WeatherEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getName();

    private static final String FORECAST_SHARE_HASHTAG = " #WeatherApp";

    private ShareActionProvider shareActionProvider;
    private String forecastString;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {

            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WIND_SPEED = 6;
    private static final int COL_DEGREES = 7;
    private static final int COL_PRESSURE = 8;
    private static final int COL_WEATHER_ID = 9;


    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (forecastString!=null){
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData()==null){
            return null;
        }

        return new CursorLoader(getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()){
            return;
        }

        //Set date
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        TextView dateTextView = (TextView) getView().findViewById(R.id.detail_date_textview);
        dateTextView.setText(dateString);

        //Set day
        String dayString = Utility.getDayName(getContext(), data.getLong(COL_WEATHER_DATE));
        TextView dayTextView = (TextView) getView().findViewById(R.id.detail_day_textview);
        dayTextView.setText(dayString);

        //Set Weather description
        String weatherDescription = data.getString(COL_WEATHER_DESC);
        TextView detailDescView = (TextView) getView().findViewById(R.id.detail_desc_textview);
        detailDescView.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getContext());

        //Set Max Temprature
        String high = Utility.formatTemperature(getContext(),data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        TextView detailHighView = (TextView) getView().findViewById(R.id.detail_high_temp_textview);
        detailHighView.setText(high);

        //Set Min Temperature
        String low = Utility.formatTemperature(getContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        TextView detailLowView = (TextView) getView().findViewById(R.id.detail_low_temp_textview);
        detailLowView.setText(low);

        //Set Humidity
        String humidity = getContext().getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));
        TextView humidityView = (TextView) getView().findViewById(R.id.detail_humidity_textview);
        humidityView.setText(humidity);

        //Set pressure
        String pressure = getContext().getString(R.string.format_pressure, data.getFloat(COL_PRESSURE));
        TextView pressureView = (TextView) getView().findViewById(R.id.detail_pressure_textview);
        pressureView.setText(pressure);

        //Set Windspeed
        String windSpeed= Utility.getFormattedWind(getContext(), data.getFloat(COL_WIND_SPEED), data.getFloat(COL_DEGREES));
        TextView windView = (TextView) getView().findViewById(R.id.detail_wind_textview);
        windView.setText(windSpeed);

        //Set the weather art
        int weatherArt = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_ID));
        ImageView artView = (ImageView) getView().findViewById(R.id.detail_weather_art_imageview);
        artView.setImageResource(weatherArt);

        forecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high,low);
        if (shareActionProvider != null){
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
