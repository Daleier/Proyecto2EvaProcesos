/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dam203
 */
public class Scraping extends Observable implements Runnable {
    public final int NUMERO_TITULARES = 10;
    
    @Override
    public void run() {
        String url = "https://elpais.com/";
        System.out.println("Fetching %s..."+ url);

        Document doc;
        String result="";
        try {
            doc = Jsoup.connect(url).get();
            Elements tit = doc.getElementsByClass("articulo-titulo");
            Elements titulares = tit.select("a");
            Elements imagen = doc.getElementsByTag("meta");
            Iterator <Element> i = imagen.iterator();
            while(i.hasNext()){ //elimina las imagenes que no son de titular
                Element img = i.next();
                if(!img.attr("itemprop").toString().matches("url")){
                    i.remove();
                }else if(!img.attr("content").toString().contains(".jpg")){
                    i.remove();
                }
            }
            System.out.println(imagen.size());
            for(int a = 0; a < NUMERO_TITULARES; a++){
                try{
                    if(imagen.get(a).attr("itemprop").toString().equalsIgnoreCase("url") && imagen.get(a).attr("content").toString().contains(".jpg")){
                        result = result + "<br/><img src=\"" + imagen.get(a).attr("content").toString()+"\" height=\"175\" width=\"325\"><br/>";
                    }
                }catch(IndexOutOfBoundsException ex){}
                if(titulares.get(a).attr("href").toString().startsWith("http")){
                    result = result + "<br/><b><a href=\"" + titulares.get(a).attr("href").toString()+"\">"+titulares.get(a).text()+ "<a/><b/><br/>";
                    System.out.println("<br/><b><a href=\"" + titulares.get(a).attr("href").toString()+">"+titulares.get(a).text()+ "<a/><b/><br/>");
                }else{
                    result = result + "<br/><b><a href=\"https://elpais.com" + titulares.get(a).attr("href").toString()+"\">"+titulares.get(a).text()+ "<a/><b/><br/>";
                    System.out.println("<br/><b><a href=\"https://elpais.com" + titulares.get(a).attr("href").toString()+">"+titulares.get(a).text()+ "<a/><b/><br/>");
                }
            }
            this.setChanged();
            this.notifyObservers(result);
            this.clearChanged();
            System.out.println("SCRAP DONE");
        } catch (IOException ex) {
            Logger.getLogger(Scraping.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
