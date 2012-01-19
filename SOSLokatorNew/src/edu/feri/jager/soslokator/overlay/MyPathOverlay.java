package edu.feri.jager.soslokator.overlay;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MyPathOverlay extends Overlay {

	private final int MIN_STROKE_WIDTH = new Integer(1);
	private final int MAX_STROKE_WIDTH = new Integer(4);

	private Point pixelStartPoint;
	private Point pixelEndPoint;	
	private GeoPoint startPoint;
	private GeoPoint endPoint;
	private Paint paint;

	public MyPathOverlay(GeoPoint startPoint, GeoPoint endPoint, int color, 
			int strokeWidth) {
		setPixelStartPoint(new Point());
		setPixelEndPoint(new Point());
		setStartPoint(startPoint);
		setEndPoint(endPoint);

		setPaint(new Paint());
		getPaint().setColor(color);
		getPaint().setAntiAlias(true);

		if(strokeWidth < MIN_STROKE_WIDTH) {
			getPaint().setStrokeWidth(MIN_STROKE_WIDTH);
		}
		else if(strokeWidth > MAX_STROKE_WIDTH) {
			getPaint().setStrokeWidth(MAX_STROKE_WIDTH);
		}
		else {
			getPaint().setStrokeWidth(strokeWidth);
		}
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow == false) {
			Projection projection = mapView.getProjection();
			projection.toPixels(getStartPoint(), getPixelStartPoint());
			projection.toPixels(getEndPoint(), getPixelEndPoint());

			canvas.drawLine(getPixelStartPointX(), getPixelStartPointY(), 
					getPixelEndPointX(), getPixelEndPointY(), getPaint());
		}

		super.draw(canvas, mapView, shadow);
	}

	public Paint getPaint() {
		return paint;
	}
	public GeoPoint getEndPoint() {
		return endPoint;
	}
	public Point getPixelEndPoint() {
		return pixelEndPoint;
	}
	public GeoPoint getStartPoint() {
		return startPoint;
	}
	public Point getPixelStartPoint() {
		return pixelStartPoint;
	}
	public float getPixelEndPointX() {
		return new Float(getPixelEndPoint().x);
	}
	public float getPixelEndPointY() {
		return new Float(getPixelEndPoint().y);
	}
	public float getPixelStartPointX() {
		return new Float(getPixelStartPoint().x);
	}
	public float getPixelStartPointY() {
		return new Float(getPixelStartPoint().y);
	}
	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	public void setEndPoint(GeoPoint endPoint) {
		this.endPoint = endPoint;
	}
	public void setStartPoint(GeoPoint startPoint) {
		this.startPoint = startPoint;
	}
	public void setPixelEndPoint(Point pixelEndPoint) {
		this.pixelEndPoint = pixelEndPoint;
	}
	public void setPixelStartPoint(Point pixelStartPoint) {
		this.pixelStartPoint = pixelStartPoint;
	}
}