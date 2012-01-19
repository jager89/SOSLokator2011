package edu.feri.jager.soslokator.overlay;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyPositionOverlay extends Overlay {
	private final int mRadius = 8;
	private Location location;
	private String text = "";
	
	public MyPositionOverlay() {
		super();
		text = "VAŠA LOKACIJA";
	}
	
	public MyPositionOverlay(String text) {
		super();
		this.text = text;
	}
/**
 * @author Jager
 * @param text1
 */
	public MyPositionOverlay(Location location, String text) {
		super();
		this.text = text;
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		return false;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();

		if ((location != null) && (shadow == false)) {
			Double latitude = location.getLatitude() * 1E6;
			Double longitude = location.getLongitude() * 1E6;
			GeoPoint geoPoint; 
			geoPoint = new 
			GeoPoint(latitude.intValue(), longitude.intValue());

			Point point = new Point();
			projection.toPixels(geoPoint, point);

			RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius);

			Paint paint = new Paint();
			paint.setARGB(250, 255, 255, 255);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);

			Paint backPaint = new Paint();
			backPaint.setARGB(175, 50, 50, 50);
			backPaint.setAntiAlias(true);

			RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 105, point.y + mRadius);

			canvas.drawOval(oval, paint);
			canvas.drawRoundRect(backRect, 5, 5, backPaint);
			canvas.drawText(text, point.x + 2 * mRadius + 2, point.y, paint);
		}
		super.draw(canvas, mapView, shadow);
	}
}