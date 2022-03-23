package com.example.webviewsample.database

import androidx.room.*

@Dao
interface CodeDao {
    @Insert
    fun addCode(code: Code?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addListCode(codes: List<Code?>?)

    @Query("select * from Code where language_code = :languageCode")
    fun getCodes(languageCode: String?): List<Code?>?

    @Query("select * from Code where category_id = :categoryId and language_code = :languageCode")
    fun getCodeByCategoryId(categoryId: String?, languageCode: String?): List<Code?>?

    @Query("select * from Code where category_id = :categoryId and code_value = :codeValue and language_code = :languageCode")
    fun getCodeDescByValue(categoryId: String?, codeValue: String?, languageCode: String?): Code?

    @Query("select * from Code where category_id = :categoryId and code_desc = :codeDesc and language_code = :languageCode")
    fun getCodeValueByDesc(categoryId: String?, codeDesc: String?, languageCode: String?): Code?

    @Query("Delete from Code ")
    fun truncateCodes()

    @Delete
    fun deleteCode(Code: Code?)

    @Update
    fun updateCode(Code: Code?)
}