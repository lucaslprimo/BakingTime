package br.com.lucaslprimo.bakingtime.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class Ingredient implements Parcelable{

    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_MEASURE = "measure";
    private static final String KEY_INGREDIENT = "ingredient";

    private int quantity;
    private String measure;
    private String ingredient;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public static Ingredient fromJson(JSONObject jsonObject)
    {
        Ingredient ingredient = null;

        try {

            ingredient = new Ingredient();
            ingredient.setQuantity(jsonObject.getInt(KEY_QUANTITY));
            ingredient.setMeasure(jsonObject.getString(KEY_MEASURE));
            ingredient.setIngredient(jsonObject.getString(KEY_INGREDIENT));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return  ingredient;
    }

    //Parcelable implementation
    private Ingredient(Parcel in)
    {
        this.ingredient = in.readString();
        this.measure = in.readString();
        this.quantity = in.readInt();
    }

    public Ingredient()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ingredient);
        parcel.writeString(measure);
        parcel.writeInt(quantity);
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>()
    {
        @Override
        public Ingredient createFromParcel(Parcel parcel) {
            return new Ingredient(parcel);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
