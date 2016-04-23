package th.in.ffc.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import org.osmdroid.DefaultResourceProxyImpl;
import th.in.ffc.R;

public class UserResourceProxyImpl extends DefaultResourceProxyImpl {

    private final Context mContext;

    public UserResourceProxyImpl(final Context pContext) {
        super(pContext);
        mContext = pContext;
    }

    @Override
    public String getString(final string pResId) {
        return "Google Maps";
    }

    @Override
    public Bitmap getBitmap(final bitmap pResId) {
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.me);
    }

    @Override
    public Drawable getDrawable(final bitmap pResId) {
        return mContext.getResources().getDrawable(R.drawable.me);
    }
}
