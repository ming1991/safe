  
package com.example.mobilesafe01.db;  
/** 
 * ClassName:LockDB <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Date:     2016年9月18日 下午9:06:16 <br/> 
 * @author   ming001 
 * @version       
 */
public interface LockDB {
    public int VERSION = 1;
    public String NAME = "lock.db";
    
    public interface LockTable {
        //create table lock(_id integer primary key,packageName varchar(20) unique)
        public String TABLE_NAME = "lock";
        public String ID = "_id";
        public String PACKAGE_NAME = "packageName";
        public String CREATE_SQL = "create table "+TABLE_NAME
                +"("+ID+" integer primary key,"
                +PACKAGE_NAME+" varchar(20) unique)";
    }
}
  