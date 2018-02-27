package servidor;

import cliente.Scraping;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HandlerHTTPServer extends Observable implements Runnable{
    
        Socket comunicaCliente;
        OutputStream flujoSalida;
        DataOutputStream flujo;
        
    HandlerHTTPServer(Socket comunicaCliente){
        this.comunicaCliente = comunicaCliente;
    }
    
    public void run() {
        try {
            flujoSalida = comunicaCliente.getOutputStream();
            flujo = new DataOutputStream(flujoSalida);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            this.setChanged();
            this.notifyObservers(dateFormat.format(date)+" - "+comunicaCliente.getInetAddress().getHostAddress());
            this.clearChanged();
            String paginaWeb = "HTTP/1.0 200 OK\r\n Connection: close\r\nServer: ServidorWebModuloPSP v0\r\nContent-Type: text/html\r\n\r\n·"
                    /*+obtenerArchivoHTML("index.html")*/+scrapping();
            System.out.println(paginaWeb);
            flujo.writeBytes(paginaWeb);
            flujo.flush();
            //Se va a dormir el proceso para que no termine antes de que el 
            //cliente pueda procesar los datos.
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(HandlerHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            comunicaCliente.close();
        } catch (IOException e) {
        } catch (SecurityException e) {
        }
    }
    
    private String obtenerArchivoHTML(String nombre) {
	String resultado = "";
	BufferedReader in = null;
	try {
            in = new BufferedReader(new FileReader(nombre));
            try {
                    String linea=in.readLine();
                    while (linea!=null){
                            resultado+=linea;
                            linea=in.readLine();
                    }
                    in.close();
            } catch (IOException e) {
                    System.err.println("IOException occurred in server.");
            }
	} catch (FileNotFoundException e) {
		System.err.println("Could not open quote file.");
	}
	return resultado;
    }
    
    private String scrapping() {
        String resultado = "";
        try {
            System.out.println("scrapping http");
            String url = "https://elpais.com/";
            System.out.println("Fetching %s..."+ url);
            
            Document doc;
            doc = Jsoup.connect(url).get();
            Elements tit = doc.getElementsByTag("meta");

            for(Element link: tit){
                if(link.attr("itemprop").toString().equalsIgnoreCase("url")){
                    resultado = resultado + "<br/><img src=\"" + link.attr("content").toString()+"\"><br/>";
                }
            }
            System.out.println("SCRAP DONE");
        } catch (IOException ex) {
            Logger.getLogger(HandlerHTTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<br/><h1>IMÁGENES EL PAÍS<h1/>"+resultado;
    }

}
