package br.com.lucaslprimo.bakingtime.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class Recipe implements Parcelable{

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_INGREDIENTS = "ingredients";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_SERVINGS = "servings";
    private static final String KEY_IMAGE = "image";

    private int id;
    private String name;
    private Ingredient[] ingredientList;
    private Step[] stepList;
    private int servings;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ingredient[] getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(Ingredient[] ingredientList) {
        this.ingredientList = ingredientList;
    }

    public Step[] getStepList() {
        return stepList;
    }

    public void setStepList(Step[] stepList) {
        this.stepList = stepList;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Recipe fromJson(JSONObject jsonObject)
    {
        Recipe recipe = null;

        try {
            recipe = new Recipe();
            recipe.setId(jsonObject.getInt(KEY_ID));
            recipe.setName(jsonObject.getString(KEY_NAME));

            JSONArray jsonArrayIngredients = jsonObject.getJSONArray(KEY_INGREDIENTS);
            recipe.ingredientList = new Ingredient[jsonArrayIngredients.length()];
            for(int i=0;i<jsonArrayIngredients.length();i++)
            {
                recipe.ingredientList[i] = Ingredient.fromJson(jsonArrayIngredients.getJSONObject(i));
            }

            JSONArray jsonArraySteps = jsonObject.getJSONArray(KEY_STEPS);
            recipe.stepList = new Step[jsonArraySteps.length()];
            for(int i=0;i<jsonArraySteps.length();i++)
            {
                recipe.stepList[i] = Step.fromJson(jsonArraySteps.getJSONObject(i));
            }

            recipe.setServings(jsonObject.getInt(KEY_SERVINGS));
            recipe.setImage(jsonObject.getString(KEY_IMAGE));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipe;
    }

    //Parcelable implementation
    private Recipe(Parcel in)
    {
        this.id = in.readInt();
        this.name = in.readString();
        this.ingredientList =  in.createTypedArray(Ingredient.CREATOR);
        this.stepList =   in.createTypedArray(Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public Recipe()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedArray(ingredientList,0);
        parcel.writeTypedArray(stepList,0);
        parcel.writeInt(servings);
        parcel.writeString(image);

    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>()
    {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
