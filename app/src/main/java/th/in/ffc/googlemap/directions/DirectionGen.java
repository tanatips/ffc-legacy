package th.in.ffc.googlemap.directions;

import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;

public class DirectionGen extends AsyncTask<LatLng, String, ArrayList<LatLng>> {
    private ArrayList<LatLng> latlng;
    private directionCallBack callback;
    private ArrayList<String> directionBox;


    public static interface directionCallBack {
        public void getDirection(ArrayList<LatLng> latlng, ArrayList<String> directionBox);
    }

    public DirectionGen() {

    }

    public void setDirectionClick(directionCallBack callback) {
        this.callback = callback;
    }


    @Override
    protected ArrayList<LatLng> doInBackground(LatLng... params) {
        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
                + "origin=" + params[0].latitude + "," + params[0].longitude
                + "&destination=" + params[1].latitude + "," + params[1].longitude
                + "&sensor=false&units=metric&mode=driving&language=th";
        latlng = new ArrayList<LatLng>();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            InputStream in = response.getEntity().getContent();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in);
            latlng = getDirection(doc);
            directionBox = getList(doc);
            return latlng;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> result) {
        callback.getDirection(result, directionBox);
        super.onPostExecute(result);
    }

    public String getDurationText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDurationValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DurationValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getDistanceText(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    }

    public int getDistanceValue(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    }

    public String getStartAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getEndAddress(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    }

    public String getCopyRights(Document doc) {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    }

    public ArrayList<LatLng> getDirection(Document doc) {
        NodeList nl1, nl2, nl3;
        ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();
        nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node1 = nl1.item(i);
                nl2 = node1.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "start_location"));
                nl3 = locationNode.getChildNodes();
                Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
                double lat = Double.parseDouble(latNode.getTextContent());
                Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                double lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));

                locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "points"));
                ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
                for (int j = 0; j < arr.size(); j++) {
                    listGeopoints.add(new LatLng(arr.get(j).latitude, arr.get(j).longitude));
                }

                locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
                nl3 = locationNode.getChildNodes();
                latNode = nl3.item(getNodeIndex(nl3, "lat"));
                lat = Double.parseDouble(latNode.getTextContent());
                lngNode = nl3.item(getNodeIndex(nl3, "lng"));
                lng = Double.parseDouble(lngNode.getTextContent());
                listGeopoints.add(new LatLng(lat, lng));
            }
        }

        return listGeopoints;
    }

    private int getNodeIndex(NodeList nl, String nodename) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(nodename))
                return i;
        }
        return -1;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }

    public ArrayList<String> getList(Document doc) {
        ArrayList<String> directionList = new ArrayList<String>();
        double dis_met = 0;
        double time = 0;
        NodeList nl1 = doc.getElementsByTagName("step");
        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node11 = nl1.item(i);
                NodeList nl2 = node11.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "html_instructions"));
                Node disNode = nl2.item(getNodeIndex(nl2, "distance"));
                NodeList nl3 = disNode.getChildNodes();
                Node dis = nl3.item(getNodeIndex(nl3, "value"));
                dis_met = dis_met + Integer.parseInt(dis.getTextContent());

                Node timeNode = nl2.item(getNodeIndex(nl2, "duration"));
                NodeList nl4 = timeNode.getChildNodes();
                Node timenode = nl4.item(getNodeIndex(nl4, "value"));
                time = time + Integer.parseInt(timenode.getTextContent());

            }
        }
        dis_met = dis_met / 1000;
        time = time / 60;

        dis_met = Math.floor(dis_met * (Math.pow(10, 2)) / Math.pow(10, 2));
        time = Math.floor(time * (Math.pow(10, 2)) / Math.pow(10, 2));
        directionList.add("���зҧ " + dis_met + " ��.");
        directionList.add("��������� " + time + " �ҷ�");


        if (nl1.getLength() > 0) {
            for (int i = 0; i < nl1.getLength(); i++) {
                Node node11 = nl1.item(i);
                NodeList nl2 = node11.getChildNodes();

                Node locationNode = nl2.item(getNodeIndex(nl2, "html_instructions"));
                Node disNode = nl2.item(getNodeIndex(nl2, "distance"));
                NodeList nl3 = disNode.getChildNodes();
                Node dis = nl3.item(getNodeIndex(nl3, "text"));
                String disStr = dis.getTextContent();
                System.out.println("1.====" + disStr);
                String direc = locationNode.getTextContent();
                //System.out.println("1.===="+direc);
                direc = direc.replace("<b>", "");
                //System.out.println("2.===="+direc);
                direc = direc.replace("</b>", "");
                //System.out.println("3.===="+direc);
                direc = direc.replace("Head", "Start");
                direc = direc.replaceAll("(<div)(.*)(</div>)", "");

                direc = direc + "  " + disStr;
                System.out.println(direc);
                directionList.add(direc);


            }
        }
        return directionList;
    }

}
