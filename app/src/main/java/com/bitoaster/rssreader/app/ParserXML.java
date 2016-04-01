package com.bitoaster.rssreader.app;


import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ParserXML extends AsyncTask<String, Void, Channel> {
    @Override
    protected Channel doInBackground(String... params) {
        List<Item> items = new ArrayList<Item>();
        final String CONN = "CONNECTE";
        final String TRY = "TRY";
        try{
            /* ouverture de l'URL */
            Log.d(TRY, "try");
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d(CONN, "connecte");
            /* construction du parser */
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Log.d("FACT", "factory");
            DocumentBuilder builder = factory.newDocumentBuilder();
            Log.d("BUILD", "build");
            Log.d("A", String.valueOf(connection.getResponseCode()));
            Document doc = builder.parse(connection.getInputStream());
            /* recuperation des donnees */
            Log.d("DONN", "recupDonne");
            NodeList nodes = doc.getElementsByTagName("item");
            NodeList cTitle = doc.getElementsByTagName("title");
            NodeList cDescription = doc.getElementsByTagName("description");
            NodeList cLink = doc.getElementsByTagName("link");
            NodeList cLastBuildDate = doc.getElementsByTagName("pubDate");
            Log.d("DEBUT", "debut for");
            for(int i = 0; i<nodes.getLength(); i++){
                Element element = (Element) nodes.item(i);
                NodeList titles = element.getElementsByTagName("title");
                NodeList descriptions = element.getElementsByTagName("description");
                NodeList enclosures = element.getElementsByTagName("enclosure");
                NodeList links = element.getElementsByTagName("link");
                NodeList pubDates = element.getElementsByTagName("pubDate");
                Element title = (Element)titles.item(0);
                Element description = (Element)descriptions.item(0);
                Element link = (Element)links.item(0);
                Element pubDate = (Element)pubDates.item(0);
                if(enclosures.getLength()>0) {
                    Element enclosure = (Element) enclosures.item(0);
                    items.add(new Item(title.getTextContent(), description.getTextContent(), enclosure.getTextContent(), link.getTextContent(), pubDate.getTextContent()));
                }
                else{
                    items.add(new Item(title.getTextContent(), description.getTextContent(), "", link.getTextContent(), pubDate.getTextContent()));

                }
                Log.d("FIN", "fin for");
            }
            Channel channel;
            if(cLastBuildDate.getLength()>0) {
                channel = new Channel(cTitle.item(0).getTextContent(), cDescription.item(0).getTextContent(), params[0], cLastBuildDate.item(0).getTextContent(), items);// sinon
            }
            else{
                channel = new Channel(cTitle.item(0).getTextContent(), cDescription.item(0).getTextContent(), params[0], items.get(0).getPubDate().toString(), items);// sinon
            }
            return channel;

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
