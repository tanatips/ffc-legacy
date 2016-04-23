package th.in.ffc.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import org.osmdroid.DefaultResourceProxyImpl;
import th.in.ffc.R;

public class ResourceProxyImpl extends DefaultResourceProxyImpl {

    private final Context mContext;

    public ResourceProxyImpl(final Context pContext) {
        super(pContext);
        mContext = pContext;
    }

    @Override
    public String getString(final string pResId) {
        return "Google Maps";
    }

    @Override
    public Bitmap getBitmap(final bitmap pResId) {
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.house_green);
        /*try {
			final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
			return BitmapFactory.decodeResource(mContext.getResources(), res);
		} catch (final Exception e) {
			return super.getBitmap(pResId);
		}*/
    }

    @Override
    public Drawable getDrawable(final bitmap pResId) {
        return mContext.getResources().getDrawable(R.drawable.house_green);
		/*try {
			final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
			return mContext.getResources().getDrawable(res);
		} catch (final Exception e) {
			return super.getDrawable(pResId);
		}*/
    }
}
