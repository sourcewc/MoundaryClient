package com.team.moundary.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.team.moundary.R;

import java.util.HashMap;

/**
 * Created by pyoinsoo on 2016-05-12.
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    HashMap<Marker, POI> poiResolver;
    View contentView;
    TextView addressView;

    public MyInfoWindowAdapter(Context context, HashMap<Marker,POI> poiResolver) {
        contentView = LayoutInflater.from(context).inflate(R.layout.map_view_info_window, null);
        addressView = (TextView)contentView.findViewById(R.id.text_address);
        this.poiResolver = poiResolver;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        POI poi = poiResolver.get(marker);
        addressView.setText(poi.getAddress());
        return contentView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        POI poi = poiResolver.get(marker);
        addressView.setText(poi.getAddress());
        return contentView;
    }
}
