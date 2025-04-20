package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfTokenDao {
    private Context mContext;

    public SfTokenDao(Context context) {
        this.mContext = context;
    }
    public static Uri getTokenDaoUriAppend(String name) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfToken.CONTENT_URI,name);
    }
    public List<SfToken> getAllTokens() {
        List<SfToken> tokens = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = getTokenDaoUriAppend("list");

        String[] projection = {
                ScreeningFormProvider.SfToken.ID,
                ScreeningFormProvider.SfToken.TOKEN_AUTH,
                ScreeningFormProvider.SfToken.TOKEN_CLAIM,
                ScreeningFormProvider.SfToken.CREATED_DATE,
                ScreeningFormProvider.SfToken.UPDATED_DATE
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                tokens.add(getTokenFromCursor(cursor));
            }
            cursor.close();
        }
        return tokens;
    }

    public SfToken getTokenById(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = ScreeningFormProvider.SfToken.CONTENT_URI;

        String[] projection = {
                ScreeningFormProvider.SfToken.ID,
                ScreeningFormProvider.SfToken.TOKEN_AUTH,
                ScreeningFormProvider.SfToken.TOKEN_CLAIM,
                ScreeningFormProvider.SfToken.CREATED_DATE,
                ScreeningFormProvider.SfToken.UPDATED_DATE
        };

        String selection = ScreeningFormProvider.SfToken.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            SfToken token = getTokenFromCursor(cursor);
            cursor.close();
            return token;
        }
        return null;
    }

    public Uri insert(SfToken token) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ScreeningFormProvider.SfToken.TOKEN_AUTH, token.getTokenAuth());
        values.put(ScreeningFormProvider.SfToken.TOKEN_CLAIM, token.getTokenClaim());
        values.put(ScreeningFormProvider.SfToken.CREATED_DATE, token.getCreatedDate());
        values.put(ScreeningFormProvider.SfToken.UPDATED_DATE, token.getUpdatedDate());

        return resolver.insert(ScreeningFormProvider.SfToken.CONTENT_URI, values);
    }

    public int update(SfToken token) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ScreeningFormProvider.SfToken.TOKEN_AUTH, token.getTokenAuth());
        values.put(ScreeningFormProvider.SfToken.TOKEN_CLAIM, token.getTokenClaim());
        values.put(ScreeningFormProvider.SfToken.UPDATED_DATE, token.getUpdatedDate());

        String selection = ScreeningFormProvider.SfToken.ID + " = ?";
        String[] selectionArgs = {String.valueOf(token.getId())};

        return resolver.update(
                ScreeningFormProvider.SfToken.CONTENT_URI,
                values,
                selection,
                selectionArgs);
    }

    public int delete(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        String selection = ScreeningFormProvider.SfToken.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return resolver.delete(
                ScreeningFormProvider.SfToken.CONTENT_URI,
                selection,
                selectionArgs);
    }

    private SfToken getTokenFromCursor(Cursor cursor) {
        SfToken token = new SfToken();
        token.setId((int) cursor.getLong(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfToken.ID)));
        token.setTokenAuth(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfToken.TOKEN_AUTH)));
        token.setTokenClaim(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfToken.TOKEN_CLAIM)));
        token.setCreatedDate(cursor.getLong(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfToken.CREATED_DATE)));
        token.setUpdatedDate(cursor.getLong(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfToken.UPDATED_DATE)));
        return token;
    }
    public void insertDefaultTokenIfEmpty() {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = getTokenDaoUriAppend("list");

        // Check if table is empty
        Cursor cursor = resolver.query(uri, new String[]{ScreeningFormProvider.SfToken.ID}, null, null, null);
        boolean isEmpty = true;

        if (cursor != null) {
            isEmpty = cursor.getCount() == 0;
            cursor.close();
        }

        // Insert default token if table is empty
        if (isEmpty) {
            SfToken defaultToken = new SfToken();
            defaultToken.setTokenAuth("34913796-e515-4b33-9656-6a2eb64ef569");
            defaultToken.setTokenClaim("0b749fef-b348-4072-94f6-2a62cce6f7ac");
            defaultToken.setCreatedDate(System.currentTimeMillis());
            defaultToken.setUpdatedDate(System.currentTimeMillis());

            insert(defaultToken);
        }
    }
}