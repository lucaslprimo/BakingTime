package br.com.lucaslprimo.bakingtime.ui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Recipe;
import br.com.lucaslprimo.bakingtime.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity  implements NetworkUtils.RecipesCallback,RecipeListAdapter.OnClickListener{

    private final static String INSTANCE_RECIPES = "recipes_instance";
    public final static String EXTRA_RECIPE = "recipe";
    public final static int COLUMNS_TABLET_GRID = 2;

    @BindView(R.id.recycler_view_recipes) RecyclerView mRecyclerView;
    @BindView(R.id.loading_recipes) ProgressBar loadingRecipes;
    RecipeListAdapter mRecipeAdapter;
    Recipe[] mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

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
            mRecipeList = (Recipe[]) savedInstanceState.getParcelableArray(INSTANCE_RECIPES);
            showData();
        }else
            NetworkUtils.requestRecipes(this,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArray(INSTANCE_RECIPES, mRecipeList);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onFinishRequest(Recipe[] recipeList) {

        mRecipeList = recipeList;
        showData();
    }

    void showData()
    {
        loadingRecipes.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecipeAdapter.setRecipeList(mRecipeList);
    }

    @Override
    public void onItemClick(Recipe recipe) {

        Intent intent = new Intent(this,MasterStepsListActivity.class);
        intent.putExtra(EXTRA_RECIPE,recipe);
        startActivity(intent);
    }
}
