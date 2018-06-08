package com.mobilelearning.student.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilelearning.student.model.User;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Taofik Muhammad on 22/10/2016.
 */
public class DBUser extends SQLiteAssetHelper {
    private static final String DATABASE_NAME="mlearning.sqlite";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="user";
    private static final String KEY_ID="id_user";
    private static final String KEY_USERNAME="username";
    private static final String KEY_FULL_NAME="full_name";
    private static final String KEY_EMAIL="email";
    private static final String KEY_TYPE="user_type";

    public DBUser(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //untuk cek apakah data ada isinya atau tidak
    public boolean isNull()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM "+TABLE_NAME+"";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        db.close();
        if(icount>0)
        {
            return false;
        }else
        {
            return true;
        }
    }

    //untuk simpan data
    public void save(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID, user.getUserId());
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_FULL_NAME,user.getFullName());
        values.put(KEY_EMAIL,user.getEmail());
        values.put(KEY_TYPE,user.getUserType());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //untuk mendapatkan data
    public User findUser()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_ID,KEY_USERNAME,KEY_EMAIL,KEY_FULL_NAME,KEY_TYPE},null,null,null,null,null);

        User u=new User();
        if (cursor!=null)
        {
            cursor.moveToFirst();
            u.setUserId(cursor.getInt(0));
            u.setUsername(cursor.getString(1));
            u.setEmail(cursor.getString(2));
            u.setFullName(cursor.getString(3));
            u.setUserType(cursor.getInt(4));
        }else
        {
            u.setUserId(0);
            u.setUsername("");
            u.setEmail("");
            u.setFullName("");
            u.setUserType(0);
        }
        cursor.close();
        db.close();

        return u;
    }

    //untuk bersihkan atau hapus semua data
    public void delete()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    //untuk update data belanjaan berdasarkan pada nama toko
    public void update(User user)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_FULL_NAME,user.getFullName());
        values.put(KEY_EMAIL,user.getEmail());
        values.put(KEY_TYPE,user.getUserType());

        String where = ""+KEY_ID+"=?";
        String[] whereArgs = new String[] {String.valueOf(user.getUserId())};
        db.update(TABLE_NAME,values, where, whereArgs);
        db.close();
    }
}
