package br.com.lucaslprimo.bakingtime.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import br.com.lucaslprimo.bakingtime.MyIdlingResource;
import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Ingredient;
import br.com.lucaslprimo.bakingtime.data.Recipe;
import br.com.lucaslprimo.bakingtime.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity  implements NetworkUtils.RecipesCallback,RecipeListAdapter.OnClickListener{

    private final static String INSTANCE_RECIPES = "recipes_instance";
    public final static String EXTRA_RECIPE = "recipe";
    public final static int COLUMNS_TABLET_GRID = 2;

    private final static int ERROR_NO_INTERNET = 1;
    private final static int ERROR_FETCH_FAILED = 2;

    private Snackbar mSnackBar;

    @BindView(R.id.recycler_view_recipes) RecyclerView mRecyclerView;
    @BindView(R.id.loading_recipes) ProgressBar loadingRecipes;
    @BindView(R.id.text_widget_choose_recipe) TextView textChooseRecipe;
    @BindView(R.id.tv_error_message) TextView mErrorMessage;
    @BindView(R.id.error_view) LinearLayout mErrorView;
    @BindView(R.id.iv_error) ImageView mErrorImage;

    RecipeListAdapter mRecipeAdapter;
    Recipe[] mRecipeList;

    boolean isFromWidget = false;
    int widgetId;

    // The Idling Resource which will be null in production.
    @Nullable
    private MyIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new MyIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mIdlingResource!=null)
            mIdlingResource.setIdleState(false);

        Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

        mSnackBar = Snackbar.make(mRecyclerView,R.string.label_snack_text,Snackbar.LENGTH_INDEFINITE);
        mSnackBar.setAction(R.string.label_snack_try_again, new TryAgainInternetListener());
        mSnackBar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));

        if(getIntent().getExtras()!=null)
        {
            if(getIntent().getExtras().containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID))
            {
                isFromWidget = true;
                widgetId = getIntent().getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                textChooseRecipe.setVisibility(View.VISIBLE);

                if(getSupportActionBar()!=null)
                    getSupportActionBar().setTitle(getString(R.string.title_widget_setup));
            }else
            {
                textChooseRecipe.setVisibility(View.GONE);
            }
        }

        RecyclerView.LayoutManager layoutManager;

        if(getResources().getBoolean(R.bool.isTablet))
        {
            layoutManager = new GridLayoutManager(this,COLUMNS_TABLET_GRID);
        }else
        {
            layoutManager = new LinearLayoutManager(this);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipeAdapter = new RecipeListAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_RECIPES))
        {
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(INSTANCE_RECIPES);

            if(parcelables!=null) {
                mRecipeList = new Recipe[parcelables.length];

                for (int i = 0; i < parcelables.length; i++)
                    mRecipeList[i] = (Recipe) parcelables[i];
            }

            showData();
        }else {

            if(NetworkUtils.isOnline(this))
                NetworkUtils.requestRecipes(this, this, mIdlingResource);
            else
                showMessage(ERROR_NO_INTERNET);
        }
    }

    public class TryAgainInternetListener implements  View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            if(NetworkUtils.isOnline(MainActivity.this))
                NetworkUtils.requestRecipes(MainActivity.this, MainActivity.this ,mIdlingResource);
            else
                showMessage(ERROR_NO_INTERNET);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArray(INSTANCE_RECIPES, mRecipeList);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(INSTANCE_RECIPES, mRecipeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFinishRequest(Recipe[] recipeList) {


        if(recipeList!=null && recipeList.length>0)
        {
            mRecipeList = recipeList;
            showData();
        }else
        {
            showMessage(ERROR_FETCH_FAILED);
        }

    }

    void showData()
    {
        mErrorView.setVisibility(View.INVISIBLE);
        loadingRecipes.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecipeAdapter.setRecipeList(mRecipeList);
    }

    @Override
    public void onItemClick(Recipe recipe) {

        if(isFromWidget)
        {
            widgetSetup(recipe);

            finish();
        }else
        {
            Intent intent = new Intent(this,MasterStepsListActivity.class);
            intent.putExtra(EXTRA_RECIPE,recipe);
            startActivity(intent);
        }
    }

    void widgetSetup(Recipe recipe)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.recipe_name, recipe.getName());

        views.setTextViewText(R.id.text_ingredients,Ingredient.getStringListIngredients(recipe.getIngredientList(),this));

        Intent i =  new Intent(this,MainActivity.class);
        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        this.startActivity(i);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.layout_widget,pendingIntent);

        appWidgetManager.updateAppWidget(widgetId,views);

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, intent);
    }

    private void showMessage(int errorCode)
    {
        if(errorCode == ERROR_NO_INTERNET)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_perm_scan_wifi_black_24px));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_wifi));
            mErrorMessage.setText(R.string.error_no_internet);
            mSnackBar.setText(R.string.error_no_internet);

        }else
        if(errorCode == ERROR_FETCH_FAILED)
        {
            mErrorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_black_24px));
            mErrorImage.setContentDescription(getString(R.string.label_content_desc_error));
            mErrorMessage.setText(R.string.error_fetch_failed);
            mSnackBar.setText(R.string.error_fetch_failed_snack);
        }

        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        loadingRecipes.setVisibility(View.INVISIBLE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSnackBar.show();
            }
        }, 1000);
    }
}
