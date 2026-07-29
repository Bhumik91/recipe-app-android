package com.example.recipeapp.storage.savedrecipes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SavedRecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE recipeId = :recipeId AND userId = :userId")
    suspend fun delete(recipeId: Int, userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_recipes WHERE recipeId = :recipeId AND userId = :userId)")
    suspend fun isSaved(recipeId: Int, userId: String): Boolean

    @Query("SELECT * FROM saved_recipes WHERE userId = :userId ORDER BY savedAt DESC")
    fun observeSavedRecipes(userId: String): Flow<List<SavedRecipeEntity>>
}
