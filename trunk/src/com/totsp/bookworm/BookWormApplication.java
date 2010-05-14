package com.totsp.bookworm;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.totsp.bookworm.data.DataHelper;
import com.totsp.bookworm.data.DataImageHelper;
import com.totsp.bookworm.data.IBookDataSource;
import com.totsp.bookworm.model.Book;

import java.util.ArrayList;

public class BookWormApplication extends Application {

   boolean debugEnabled;

   SharedPreferences prefs;
   IBookDataSource bookDataSource;
   DataHelper dataHelper;
   DataImageHelper dataImageHelper;

   Book selectedBook;

   String lastSearchTerm;
   int lastSearchListPosition;
   int lastMainListPosition;
   ArrayList<Book> bookCacheList;

   @Override
   public void onCreate() {
      super.onCreate();
      if (debugEnabled) {
         Log.d(Constants.LOG_TAG, "APPLICATION onCreate");
      }

      prefs = PreferenceManager.getDefaultSharedPreferences(this);
      dataHelper = new DataHelper(this);
      dataImageHelper = new DataImageHelper(this);

      establishBookDataSourceFromProvider();
   }

   private void establishBookDataSourceFromProvider() {
      String className =
               prefs.getString("dataproviderpref",
                        "com.totsp.bookworm.data.GoogleBookDataSource");
      Log.i(Constants.LOG_TAG,
               "establishing book data provider using class name - "
                        + className);
      try {
         Class<?> clazz = Class.forName(className);
         // NOTE - validate that clazz is of IBookDataSource type?
         bookDataSource = (IBookDataSource) clazz.newInstance();
      } catch (ClassNotFoundException e) {
         Log.e(Constants.LOG_TAG, e.getMessage(), e);
      } catch (IllegalAccessException e) {
         Log.e(Constants.LOG_TAG, e.getMessage(), e);
      } catch (InstantiationException e) {
         Log.e(Constants.LOG_TAG, e.getMessage(), e);
      }
   }

   // so that onSaveInstanceState/onRestoreInstanceState can use with just saved title
   public void establishSelectedBook(final long id) {
      selectedBook = dataHelper.selectBook(id);
   }

   @Override
   public void onTerminate() {
      // not guaranteed to be called
      dataHelper.cleanup();
      selectedBook = null;
      super.onTerminate();
   }
}
