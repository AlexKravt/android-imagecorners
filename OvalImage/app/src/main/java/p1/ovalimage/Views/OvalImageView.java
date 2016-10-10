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
        initBitmapMask(context);
    }

    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmapMask(context);
    }

    public OvalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBitmapMask(context);
    }

    private void initBitmapMask(Context context)
    {
        mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.oval_fore4);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable(); //<-- Если есть картинка из ресурса, то получаем

        if (drawable == null) { //<-- Если нет, то выходим
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) { // <-- Или картинка не имеет размера, также выходим
            return;
        }

        Bitmap bitmap; //<-- создаем переменную
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && drawable instanceof VectorDrawable) //<-- Проверка версии SDK
        {
            //Получаем картинку
            ((VectorDrawable) drawable).draw(canvas);
            bitmap = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas_new = new Canvas();
            canvas_new.setBitmap(bitmap);
            drawable.draw(canvas_new);
        }
        else
        {
            //Получаем картинку
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }


        int width = getWidth(), height = getHeight();

        //Копируем картинку для вырезки квадратной части. Другими словами для crop
        /* Bitmap bitmap_new = bitmap.copy(Bitmap.Config.ARGB_8888, true);
           Bitmap cropBitmap =  getCroppedBitmap(bitmap_new, width);
           canvas.drawBitmap(cropBitmap, 0,0, null);*/


        Bitmap roundBitmap =  getRoundOval(bitmap, mask);//<-- Создаём картинку с наложением маски
       //Делаем канву из картинки для вывода на экран
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF rectF = new RectF(0, 0,width,width);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(roundBitmap, null, rectF, paint);

        roundBitmap.recycle(); // <-- Удаляем картинку после вывода

    }

    public static Bitmap getRoundOval(Bitmap original,Bitmap mask)
    {
        //Создание изображения с наложением маски
        Bitmap bitmap_new  = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(bitmap_new);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF rectF = new RectF(0, 0,mask.getWidth(), mask.getHeight());
            paint.setStyle(Paint.Style.FILL);
        //Сначала обрабатываем оригинал
            tempCanvas.drawBitmap(original, null, rectF, paint);
            paint.setXfermode(null);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //Затем накладываем маску
            tempCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);

        //Получаем рзультат
            tempCanvas.drawBitmap(bitmap_new, 0, 0, new Paint());

        // mask.recycle();
         original.recycle();// <-- если не используется в списках типа ListView и т.д. иначе закомментировать

        return bitmap_new;


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