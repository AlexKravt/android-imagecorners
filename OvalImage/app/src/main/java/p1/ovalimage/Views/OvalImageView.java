package p1.ovalimage.Views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import p1.ovalimage.R;

/**
 * Created by Alex on 09.08.2016.
 */
public class OvalImageView extends ImageView {
    private Bitmap mask;
    public OvalImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.oval_fore4);
    }

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.oval_fore4);
    }

    public OvalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.oval_fore4);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && drawable instanceof VectorDrawable) {
            ((VectorDrawable) drawable).draw(canvas);
            b = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas();
            c.setBitmap(b);
            drawable.draw(c);
        }
        else
        {

            b = ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

       /* Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0,0, null);*/


        Bitmap roundBitmap =  getRoundOval(bitmap, mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF rectF = new RectF(0, 0,w,w);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(roundBitmap, null, rectF, paint);

        roundBitmap.recycle();
       // mask.recycle();
    }

    public static Bitmap getRoundOval(Bitmap original,Bitmap mask)
    {
        // Bitmap mask = BitmapFactory.decodeResource(getResources(),R.drawable.oval_fore3);

        //You can change original image here and draw anything you want to be masked on it.
        Bitmap result  = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF rectF = new RectF(0, 0,mask.getWidth(), mask.getHeight());
            paint.setStyle(Paint.Style.FILL);
            tempCanvas.drawBitmap(original, null, rectF, paint);
            paint.setXfermode(null);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            tempCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);

            //Draw result after performing masking
            tempCanvas.drawBitmap(result, 0, 0, new Paint());


        // mask.recycle();
        // original.recycle();

        return result;


    }



    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2,
                sbmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }
}