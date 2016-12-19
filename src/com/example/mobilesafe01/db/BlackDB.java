
package com.example.mobilesafe01.db;

/**
 * ClassName:BlackDBConstant <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2016年9月7日 上午12:48:06 <br/>
 * 
 * @author ming001
 * @version
 */
public interface BlackDB {
    String NAME = "black.db";
    int VERSION = 1;

    public interface BlackTable {
        // "create table blacklist(_id integer primary key,num varchar(20) unique,type varchar(5))";//保证号码的唯一性
        String TABLE_NAME = "blacklist";
        String C_ID = "_id";
        String C_NUM = "num";
        String C_TYPE = "type";
        String CREATE_SQL = "create table " + TABLE_NAME + "(" 
                + C_ID + " integer primary key,"
                + C_NUM + " varchar(20) unique," 
                + C_TYPE + " varchar(5))";
    }

}
