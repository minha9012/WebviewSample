package com.example.webviewsample.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Code")
class Code : Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "serial_no") // sequence
    var serialNo = 0

    @ColumnInfo(name = "category_id") // varchar(50) 코드그룹코드
    var categoryId: String? = null

    @ColumnInfo(name = "category_desc") // varchar(400) 코드그룹명
    var categoryDesc: String? = null

    @ColumnInfo(name = "language_code") // varchar(2) 표기언어
    var languageCode: String? = null

    @ColumnInfo(name = "code_value") // varchar(200) 코드값
    var codeValue: String? = null

    @ColumnInfo(name = "code_short_desc") // varchar(500) 코드값약어
    var codeShortDesc: String? = null

    @ColumnInfo(name = "code_desc") // varchar(500) 코드값약어
    var codeDesc: String? = null

    @ColumnInfo(name = "sort_seq") // numeric(30) 정렬순서
    var sortSeq: Int? = null

    override fun toString(): String {
        return "Code [ categoryID : $categoryId" +
                ", categoryDesc : $categoryDesc" +
                ", codeValue : $codeValue" +
                ", codeShortDesc : $codeShortDesc" +
                ", codeDesc : $codeDesc" +
                ", languageCode : $languageCode" +
                ", sortSeq : $sortSeq" +
                " ]"
    }
}