package edu.feri.jager.soslokator.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.feri.jager.soslokator.structure.MyGeoPoint;


/**
 * Razred DirectionPathData pošlje zahtevo na eno izmed spletnih storitev google maps, 
 * ter iz vrnjene strukture le-te izlošèi potrebne podatke namenjene za nadaljno obdelavo.
 * @author Jager
 *
 */
public class DirectionPathData {
	private final String URL_START = new String("http://maps.google.com/maps?f=d&hl=en&saddr=");
	private final String URL_MIDDLE = new String("&daddr=");
	private final String URL_END = new String("&ie=UTF8&0&om=0&output=kml");

	private final String DATA_TAG = new String("LineString");
	private final String PAIR_SEPERATOR = new String(" ");
	private final String COORD_SPERATOR = new String(",");
	private final String GET_METHOD = new String("GET");



	public List<List<MyGeoPoint>> getDirectionData(MyGeoPoint startPoint, MyGeoPoint endPoint) {
		try {
			URL url = new URL(parseUrl(startPoint, endPoint));
			InputStream inputStream = getURLStream(url);

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(inputStream);
			NodeList lineStringNodelist = document.getElementsByTagName(DATA_TAG);

			List<List<MyGeoPoint>> listDirr = new ArrayList<List<MyGeoPoint>>();

			for (int i = 0; i < lineStringNodelist.getLength(); i++) {
				Node lineStringNode = lineStringNodelist.item(i);
				NodeList coordinatesNodeList = lineStringNode.getChildNodes();

				for (int j = 0; j < coordinatesNodeList.getLength(); j++) {
					Node coordinatesNode = coordinatesNodeList.item(j);
					NodeList value = coordinatesNode.getChildNodes();

					listDirr.add(parseText(value.item(0).getNodeValue()));
				}
			}
			return listDirr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private InputStream getURLStream(URL url) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod(GET_METHOD);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.connect();

		return urlConnection.getInputStream();
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	private List<MyGeoPoint> parseText(String text) {
		String[] pairs = text.split(PAIR_SEPERATOR);
		StringBuilder[] coordinates = new StringBuilder[pairs.length];

		List<MyGeoPoint> vecPairs = new ArrayList<MyGeoPoint>();

		int pos = 0;

		for(String pair : pairs) {

			String[] coord = pair.split(COORD_SPERATOR);

			vecPairs.add(new MyGeoPoint(new Double(coord[1]), new Double(coord[0])));

			StringBuilder stringBuilder = new StringBuilder(coord[1]);
			stringBuilder.append(COORD_SPERATOR);
			stringBuilder.append(coord[0]);

			coordinates[pos] = stringBuilder;
			pos++;
		}

		return vecPairs;
	}

	/**
	 * 
	 * @param startLatitude
	 * @param startLongitude
	 * @param endLatitude
	 * @param endLongitude
	 * @return
	 */
	private String parseUrl(MyGeoPoint startPoint, MyGeoPoint endPoint) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(URL_START);
		stringBuilder.append(parseCoordinates(startPoint));
		stringBuilder.append(URL_MIDDLE);
		stringBuilder.append(parseCoordinates(endPoint));
		stringBuilder.append(URL_END);

		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private StringBuilder parseCoordinates(MyGeoPoint geoPoint) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(geoPoint.getLatitude());
		stringBuilder.append(COORD_SPERATOR);
		stringBuilder.append(geoPoint.getLongitude());

		return stringBuilder;
	}
}
