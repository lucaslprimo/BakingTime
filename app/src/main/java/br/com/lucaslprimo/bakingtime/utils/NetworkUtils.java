package br.com.lucaslprimo.bakingtime.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;


import br.com.lucaslprimo.bakingtime.data.Recipe;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class NetworkUtils {

    private static final String URL_RECIPES = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public static void requestRecipes(Context context, final RecipesCallback recipesCallback)
    {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_RECIPES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(!response.isEmpty())
                        {
                            try {

                                JSONArray jsonArray =new JSONArray(response);
                                Recipe[] recipeList = new Recipe[jsonArray.length()];

                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    recipeList[i] = Recipe.fromJson(jsonArray.getJSONObject(i));
                                }

                                recipesCallback.onFinishRequest(recipeList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                recipesCallback.onFinishRequest(null);
                            }
                        }else
                            recipesCallback.onFinishRequest(null);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recipesCallback.onFinishRequest(null);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface RecipesCallback{
        void onFinishRequest(Recipe[] recipeList);
    }
}
