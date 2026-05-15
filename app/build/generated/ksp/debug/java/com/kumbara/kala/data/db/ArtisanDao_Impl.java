package com.kumbara.kala.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.kumbara.kala.data.model.Artisan;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ArtisanDao_Impl implements ArtisanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Artisan> __insertionAdapterOfArtisan;

  private final EntityDeletionOrUpdateAdapter<Artisan> __updateAdapterOfArtisan;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDailyFact;

  public ArtisanDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfArtisan = new EntityInsertionAdapter<Artisan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `artisan` (`id`,`name`,`village`,`yearsOfCraft`,`biography`,`phone`,`heritageTags`,`profileImagePath`,`lastDailyFact`,`lastDailyFactDate`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Artisan entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getVillage());
        statement.bindLong(4, entity.getYearsOfCraft());
        statement.bindString(5, entity.getBiography());
        statement.bindString(6, entity.getPhone());
        statement.bindString(7, entity.getHeritageTags());
        statement.bindString(8, entity.getProfileImagePath());
        statement.bindString(9, entity.getLastDailyFact());
        statement.bindString(10, entity.getLastDailyFactDate());
      }
    };
    this.__updateAdapterOfArtisan = new EntityDeletionOrUpdateAdapter<Artisan>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `artisan` SET `id` = ?,`name` = ?,`village` = ?,`yearsOfCraft` = ?,`biography` = ?,`phone` = ?,`heritageTags` = ?,`profileImagePath` = ?,`lastDailyFact` = ?,`lastDailyFactDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Artisan entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getVillage());
        statement.bindLong(4, entity.getYearsOfCraft());
        statement.bindString(5, entity.getBiography());
        statement.bindString(6, entity.getPhone());
        statement.bindString(7, entity.getHeritageTags());
        statement.bindString(8, entity.getProfileImagePath());
        statement.bindString(9, entity.getLastDailyFact());
        statement.bindString(10, entity.getLastDailyFactDate());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateDailyFact = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE artisan SET lastDailyFact = ?, lastDailyFactDate = ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insertArtisan(final Artisan artisan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfArtisan.insert(artisan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateArtisan(final Artisan artisan, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfArtisan.handle(artisan);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDailyFact(final String fact, final String date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDailyFact.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, fact);
        _argIndex = 2;
        _stmt.bindString(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDailyFact.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<Artisan> getArtisan() {
    final String _sql = "SELECT * FROM artisan WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"artisan"}, false, new Callable<Artisan>() {
      @Override
      @Nullable
      public Artisan call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfVillage = CursorUtil.getColumnIndexOrThrow(_cursor, "village");
          final int _cursorIndexOfYearsOfCraft = CursorUtil.getColumnIndexOrThrow(_cursor, "yearsOfCraft");
          final int _cursorIndexOfBiography = CursorUtil.getColumnIndexOrThrow(_cursor, "biography");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfHeritageTags = CursorUtil.getColumnIndexOrThrow(_cursor, "heritageTags");
          final int _cursorIndexOfProfileImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImagePath");
          final int _cursorIndexOfLastDailyFact = CursorUtil.getColumnIndexOrThrow(_cursor, "lastDailyFact");
          final int _cursorIndexOfLastDailyFactDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastDailyFactDate");
          final Artisan _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpVillage;
            _tmpVillage = _cursor.getString(_cursorIndexOfVillage);
            final int _tmpYearsOfCraft;
            _tmpYearsOfCraft = _cursor.getInt(_cursorIndexOfYearsOfCraft);
            final String _tmpBiography;
            _tmpBiography = _cursor.getString(_cursorIndexOfBiography);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpHeritageTags;
            _tmpHeritageTags = _cursor.getString(_cursorIndexOfHeritageTags);
            final String _tmpProfileImagePath;
            _tmpProfileImagePath = _cursor.getString(_cursorIndexOfProfileImagePath);
            final String _tmpLastDailyFact;
            _tmpLastDailyFact = _cursor.getString(_cursorIndexOfLastDailyFact);
            final String _tmpLastDailyFactDate;
            _tmpLastDailyFactDate = _cursor.getString(_cursorIndexOfLastDailyFactDate);
            _result = new Artisan(_tmpId,_tmpName,_tmpVillage,_tmpYearsOfCraft,_tmpBiography,_tmpPhone,_tmpHeritageTags,_tmpProfileImagePath,_tmpLastDailyFact,_tmpLastDailyFactDate);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getArtisanOnce(final Continuation<? super Artisan> $completion) {
    final String _sql = "SELECT * FROM artisan WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Artisan>() {
      @Override
      @Nullable
      public Artisan call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfVillage = CursorUtil.getColumnIndexOrThrow(_cursor, "village");
          final int _cursorIndexOfYearsOfCraft = CursorUtil.getColumnIndexOrThrow(_cursor, "yearsOfCraft");
          final int _cursorIndexOfBiography = CursorUtil.getColumnIndexOrThrow(_cursor, "biography");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfHeritageTags = CursorUtil.getColumnIndexOrThrow(_cursor, "heritageTags");
          final int _cursorIndexOfProfileImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImagePath");
          final int _cursorIndexOfLastDailyFact = CursorUtil.getColumnIndexOrThrow(_cursor, "lastDailyFact");
          final int _cursorIndexOfLastDailyFactDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastDailyFactDate");
          final Artisan _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpVillage;
            _tmpVillage = _cursor.getString(_cursorIndexOfVillage);
            final int _tmpYearsOfCraft;
            _tmpYearsOfCraft = _cursor.getInt(_cursorIndexOfYearsOfCraft);
            final String _tmpBiography;
            _tmpBiography = _cursor.getString(_cursorIndexOfBiography);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpHeritageTags;
            _tmpHeritageTags = _cursor.getString(_cursorIndexOfHeritageTags);
            final String _tmpProfileImagePath;
            _tmpProfileImagePath = _cursor.getString(_cursorIndexOfProfileImagePath);
            final String _tmpLastDailyFact;
            _tmpLastDailyFact = _cursor.getString(_cursorIndexOfLastDailyFact);
            final String _tmpLastDailyFactDate;
            _tmpLastDailyFactDate = _cursor.getString(_cursorIndexOfLastDailyFactDate);
            _result = new Artisan(_tmpId,_tmpName,_tmpVillage,_tmpYearsOfCraft,_tmpBiography,_tmpPhone,_tmpHeritageTags,_tmpProfileImagePath,_tmpLastDailyFact,_tmpLastDailyFactDate);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
