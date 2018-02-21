package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author dam203
 */
public class WikipediaJSON extends Observable implements Runnable{
    private final String url;

    public WikipediaJSON(String searchword) {
        url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + searchword;
    }
    
    @Override
    public void run() {
        String result = "";
        try {
            URL url_rate = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url_rate.openConnection();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader inputStreamReader =
                    new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader bufferedReader =
                    new BufferedReader(inputStreamReader, 8192);
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                String rateResult = parseResult(result);
                System.out.println(rateResult);
                this.setChanged();
                this.notifyObservers(rateResult);
                this.clearChanged();
            } else {
                System.out.println("Error in httpURLConnection.getResponseCode()!!!");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(WikipediaJSON.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WikipediaJSON.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(WikipediaJSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String parseResult(String result) throws JSONException {
        String parsedResult = "";
        JSONObject jsonObject = new JSONObject(result);
        JSONObject query = jsonObject.getJSONObject("query");
        JSONObject pages = query.getJSONObject("pages");
        JSONObject number = null;
        Iterator a = pages.keys();
        while(a.hasNext()) {
            number = pages.getJSONObject((String)a.next());
        }
        //titulo
        String title = number.getString("title");
        //contenido
        String extract = number.getString("extract");
        // ratios usd
        parsedResult = "<h1><center>"+title+"</center></h1>"+extract;
        return parsedResult;
    }
    
}
