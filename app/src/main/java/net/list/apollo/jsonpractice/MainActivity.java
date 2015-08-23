package net.list.apollo.jsonpractice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


public class MainActivity extends ActionBarActivity {
    TextView mTextView;
    TextView ingredients;
    TextView instructions;
    TextView recipetitle;
    String[] recipe_id_list;
    int curIndex;
    String imageUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        recipe_id_list = new String[]{"591946", "196149", "201809", "586891", "192433", "561511"};
        recipe_id_list = new String[]{"bacon_asparagus_quiche", "banana_milkshake", "beef_chili", "bourbon_chicken", "brownies", "chicken_bacon_alfredo", "chicken_cordon_bleu", "chicken_marsala", "chicken_noodle_soup", "chocolate_chip_cookies", "cornbread", "flank_steak", "horchata", "lasagna", "lemon_salmon", "oreo_truffles", "reuben_sandwich", "sushi", "sweet_tea"};
        curIndex = -1;
        super.onCreate(savedInstanceState);
        renderMainView();




    }

    public static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    private void renderMainView(){
        Random rn = new Random();
        int range = recipe_id_list.length;
        int index = rn.nextInt(range);
        while(index == curIndex){
            index = rn.nextInt(range);
        }

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;




        curIndex = index;
        String recipe_id = recipe_id_list[index];
        String[] cur_recipe_arr = (String[]) recipe_id.split("_");
        String cur_recipe = capitalizeString(strJoin(cur_recipe_arr, " "));
        Toast toast = Toast.makeText(context, cur_recipe, duration);
        toast.show();
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.mTextView);
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{"http://www.apollojain.me/recipes/" + recipe_id + ".xml"});
    }



    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

        public String getJSON(String address){
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            try{
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if(statusCode == 200){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                } else {
                    Log.e(MainActivity.class.toString(), "Failed JSON object");
                }
            }catch(ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected String doInBackground(String... urls) {
            return getJSON(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            try{

//                Toast toast = Toast.makeTe`xt(context, jsonObject.getString("date"), Toast.LENGTH_LONG);
//                toast.show();
//                String xml = "<resp><status>good</status><msg>hi</msg></resp>";

                InputSource source = new InputSource(new StringReader(result));

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;

                db = dbf.newDocumentBuilder();
                Document document = db.parse(source);

                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
//                    XPathExpression ingredientExpr = xpath.compile("/Recipe/Ingredients/Ingredient");

                    imageUrl = xpath.evaluate("/Recipe/ImageURL", document);
                    final String recipeInstructions = xpath.evaluate("/Recipe/Instructions", document);
                    final String recipeTitle = xpath.evaluate("/Recipe/Title", document);
                    int i = 1;
                    String recipeIngredientList = "";
                    while(true) {
//                        if(xpath.evaluate("/Recipe/Ingredients/Ingredient[" + Integer.toString(i) + "]/Name", document) != null){
//                            break;
//                        }
                        if(xpath.evaluate("/Recipe/Ingredients/Ingredient[" + Integer.toString(i) + "]/Name", document) == ""){
                            break;

                        }
                        recipeIngredientList += xpath.evaluate("/Recipe/Ingredients/Ingredient[" + Integer.toString(i) + "]/Name", document);
                        recipeIngredientList += "\n";
//                        Context context = getApplicationContext();
//                        int duration = Toast.LENGTH_LONG;
//                        Toast toast = Toast.makeText(context, recipeIngredient, duration);
                        i++;
                        if(i == 25){
                            break;
                        }
                    }
                    final String recipeIngredients = recipeIngredientList;
//                    NodeList ingredientNames = (NodeList) ingredientExpr.evaluate(document, XPathConstants.NODESET);
//                    NodeList recipeQuantities = (NodeList) displayQuantityExpr.evaluate(document, XPathConstants.NODESET);
//                    NodeList recipeUnits = (NodeList) unitsExpr.evaluate(document, XPathConstants.NODESET);
//                    Context context = getApplicationContext();
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context, recipeIngredients, duration);
//                    toast.show();

                    mTextView.setText(imageUrl);

                    DownloadImageTask dit = new DownloadImageTask((RelativeLayout) findViewById(R.id.bgLayout));
                            dit.execute(imageUrl);
                            Button button= (Button) findViewById(R.id.button);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                renderMainView();
                                }
                            });
                            Button button2= (Button) findViewById(R.id.button2);
                            button2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    showRecipeDetails(recipeTitle, recipeIngredients, recipeInstructions);
                                }
                    });




            } catch(Exception e){e.printStackTrace();}
        }

    }

    protected void showRecipeDetails(String recipeTitle, String recipeIngredients, String recipeInstructions){
        setContentView(R.layout.layout);
        recipetitle = (TextView) findViewById(R.id.recipeTitle);
        instructions = (TextView) findViewById(R.id.instructions);
        ingredients = (TextView) findViewById(R.id.ingredients);
        ingredients.setText(recipeIngredients);
        instructions.setText(recipeInstructions);
        recipetitle.setText(recipeTitle);
        Button backbutton= (Button) findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderMainView();
            }
        });

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        RelativeLayout layout;

        public DownloadImageTask(RelativeLayout layout) {
            this.layout = layout;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            BitmapDrawable ob = new BitmapDrawable(getResources(), result);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable( ob );
            } else {
                layout.setBackground( ob);
            }
        }
    }

}