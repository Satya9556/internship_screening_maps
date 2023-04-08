package com.example.maps;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationManager;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener;
import com.mapbox.maps.ViewAnnotationOptions;
//import com.mapbox.maps.viewannotation.ViewAnnotation;
//import com.mapbox.maps.viewannotation.ViewAnnotation;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;
    FloatingActionButton expand , street , satellite , dark;
    Boolean clicked = false;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private List<Point> coordinates;
    private GeoJsonSource geoJsonSource;
    Context context;
    int annotations;
    GesturesPlugin gesturesPlugin;
    PolylineAnnotationManager polylineAnnotationManager;
    PointAnnotationManager pointAnnotationManager;
    ViewAnnotationManager viewAnnotationManager;
    AnnotationPlugin annotationPlugin;
    PolylineAnnotation polylineAnnotation;
    ConstraintLayout parent_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expand = (FloatingActionButton) findViewById(R.id.addButton);
        satellite = (FloatingActionButton) findViewById(R.id.satelliteButton);
        street = (FloatingActionButton) findViewById(R.id.streetButton);
        dark = (FloatingActionButton) findViewById(R.id.nightButton);
        rotateOpen = (Animation) AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = (Animation) AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = (Animation) AnimationUtils.loadAnimation(this , R.anim.from_bottom_anim);
        toBottom = (Animation) AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButtonClicked();
            }
        });
        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS);
            }
        });
        street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
            }
        });
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.getMapboxMap().loadStyleUri(Style.DARK);
            }
        });

        coordinates = new ArrayList<>();
        annotations = coordinates.size();
        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        gesturesPlugin = GesturesUtils.getGestures(mapView);
        annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        polylineAnnotationManager = PolylineAnnotationManagerKt.createPolylineAnnotationManager(annotationPlugin, new AnnotationConfig());
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());
        viewAnnotationManager = mapView.getViewAnnotationManager();
        gesturesPlugin.addOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull Point point) {
//                Toast.makeText(MainActivity.this, point.latitude()+" "+point.longitude(), Toast.LENGTH_SHORT).show();
//                System.out.println(point.latitude()+" "+point.longitude());
                System.out.println(annotations);
                if(annotations < 2){
                    coordinates.add(point);
                    annotations = coordinates.size();
                    addAnnotationToMap(point);
                }else{
//                    System.out.println(coordinates);
                }
                if(annotations == 2){
//                    annotations = 0;
                    addLine(coordinates);
                }
                return false;
            }
        });
        gesturesPlugin.addOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public boolean onMapLongClick(@NonNull Point point) {
//                Toast.makeText(MainActivity.this, "Long click"+point.coordinates(), Toast.LENGTH_SHORT).show();
//                System.out.println(point.latitude()+" "+point.longitude());
                return false;
            }
        });

    }

    private void addButtonClicked() {
        setVisibility(clicked);
        setAnimations(clicked);
        clicked = !clicked;
    }
    private void setVisibility(Boolean clicked) {
        if(!clicked){
            street.setVisibility(View.VISIBLE);
            satellite.setVisibility(View.VISIBLE);
            dark.setVisibility(View.VISIBLE);
//            street.setEnabled(true);
//            satellite.setEnabled(true);
//            dark.setEnabled(true);

        }else{
            street.setVisibility(View.INVISIBLE);
            satellite.setVisibility(View.INVISIBLE);
            dark.setVisibility(View.INVISIBLE);
//            street.setEnabled(false);
//            satellite.setEnabled(false);
//            dark.setEnabled(false);
        }
    }
    private void setAnimations(Boolean clicked) {
        if(!clicked){
            street.startAnimation(fromBottom);
            satellite.startAnimation(fromBottom);
            dark.startAnimation(fromBottom);
            expand.startAnimation(rotateOpen);
        }else{
            street.startAnimation(toBottom);
            satellite.startAnimation(toBottom);
            dark.startAnimation(toBottom);
            expand.startAnimation(rotateClose);
        }
    }



    private void viewAnnotati(PointAnnotation point){
        ViewAnnotationOptions viewAnnotationOptions = new ViewAnnotationOptions.Builder()
                .anchor(ViewAnnotationAnchor.TOP_RIGHT)
                .geometry(point.getPoint())
                .build();
        View viewAnnotation = viewAnnotationManager.addViewAnnotation(R.layout.view_annotation , viewAnnotationOptions);
        TextView text = viewAnnotation.findViewById(R.id.textView);
        text.setText(getResources().getString(R.string.location, point.getPoint().latitude() , point.getPoint().longitude()));
        Button del = viewAnnotation.findViewById(R.id.button);
        System.out.println(point);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Point Removed", Toast.LENGTH_SHORT).show();
                coordinates.remove(point);
                annotations = coordinates.size();
                pointAnnotationManager.delete(point);
//                polylineAnnotationManager.delete(polylineAnnotation);
                function_to_remove(viewAnnotation);
            }
            public void function_to_remove(View anno){
//                if(anno != null) {
                    viewAnnotationManager.removeViewAnnotation(anno);
                    Toast.makeText(MainActivity.this, "removed......", Toast.LENGTH_SHORT).show();
//                    viewAnnotationManager.updateViewAnnotation(anno);
//                }
            }
        });
    }
    private void addLine(List<Point> coordinates) {
        PolylineAnnotationOptions polylineAnnotationOptions = new PolylineAnnotationOptions()
                .withPoints(coordinates)
                .withLineColor("#ee4e8b")
                .withLineWidth(5.0f);
        polylineAnnotation = polylineAnnotationManager.create(polylineAnnotationOptions);
    }

    private void addAnnotationToMap(Point point) {
        Bitmap icon = getBitmapFromVectorDrawable(this ,R.drawable.baseline_location_on_24);

// Set options for the resulting symbol layer.
        if(icon != null){
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(icon);
            PointAnnotation pointAnnotation =  pointAnnotationManager.create(pointAnnotationOptions);
            pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                @Override
                public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                    viewAnnotati(pointAnnotation);
                    return false;
                }
            });
        }

    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    public class MyPointAnnotationClickListener implements OnPointAnnotationClickListener {
        @Override
        public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
            return false;
        }
    }

}