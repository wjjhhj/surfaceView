package com.example.surfaceview.surface_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.surfaceview.R;

import java.lang.reflect.Type;

public class SurfaceViewPan extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    private Thread t;//用于绘制的线程
    private boolean isRunning;//控制线程的开关

    private String[] mStrs=new String[]{"单反相机","IPAD","恭喜发财","IPHONE","服装一套","恭喜发财"};
    private int[] mImgs=new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f015,
            R.drawable.iphone,R.drawable.meizi,R.drawable.f040};
    private Bitmap[] mImageBitmaps;//图片对应的Bitmap
    private int[] mColor=new int[]{0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01};//盘的颜色
private int itemCount=6;//盘块的数量
    private RectF mRange;//整个盘块的范围
    private int mRadius;//盘块的直径
    private Paint mArcPaint;//绘制盘块的画笔
    private Paint mTextPaint;//绘制文字的画笔

    private double mSped;//转盘的速度
    private int mStartAngle=0;//起始角度
    private boolean isShouldEnd;//判断是否点击了停止按钮
    private int mCenter;//中心位置
    private  Bitmap mBgBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg2);
    private  int mPadding;//我们的padding以paddingLeft为准
    private float mTextSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());
    public SurfaceViewPan(Context context) {
        this(context,null);
    }
    public SurfaceViewPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();
        mHolder.addCallback(this);

        //获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding=getPaddingLeft();
        //设置直径
        mRadius=width-2*mPadding;
        //初始化中点
        mCenter=width/2;
        setMeasuredDimension(width,width);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //绘制盘块的画笔
        mArcPaint=new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        //绘制文字的画笔
        mTextPaint=new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);
        //初始化盘块的绘制范围
        mRange=new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);
        //初始化图片
        mImageBitmaps=new Bitmap[itemCount];
        for (int i=0;i<itemCount;i++){
            mImageBitmaps[i]=BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }
        isRunning=true;
        t=new Thread( this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
      isRunning=false;
    }

    @Override
    public void run() {
        //不断进行绘制
        while (isRunning){
        long start=System.currentTimeMillis();
        draw();
        long end=System.currentTimeMillis();
        if (end-start<50){
            try {
                Thread.sleep(50-(end-start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    }
    /**
     * 不断绘制
     */
    private void draw() {
        try {
            mCanvas=mHolder.lockCanvas();
            if (mCanvas!=null){
           //draw something
                //绘制背景
                drawBg();
                //绘制盘块
            float tmpAngle=mStartAngle;
            float sweepAngle=360/itemCount;
            for (int i=0;i<itemCount;i++){
                mArcPaint.setColor(mColor[i]);
                mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);
                //绘制文本
                drawText(tmpAngle,sweepAngle,mStrs[i]);
             drawIcon(tmpAngle,mImageBitmaps[i]);
                tmpAngle+=sweepAngle;
             if (isShouldEnd){
                 mSped-=1;
             }
             if (mSped<0){
                 mSped=0;
                 isShouldEnd=false;
             }
            }
            mStartAngle+=mSped;
            }
        }catch (Exception e){

        }finally {
            if (mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }
    //点击旋转
    public void luckyStar(){
        mSped=50;
        isShouldEnd=false;
    }
    public void luckyEnd(){
        isShouldEnd=true;
    }
    /**
     * 转盘是否在旋转
     *
     */
    public boolean isStar(){
        return mSped!=0;
    }
    public boolean isShouldEnd(){
        return isShouldEnd;
    }
    /**
     * 绘制盘块内的图片
     * @param tmpAngle
     * @param mImageBitmap
     */
    private void drawIcon(float tmpAngle, Bitmap mImageBitmap) {
        //设置图片的宽度
        int imgWidth=mRadius/8;
        //起始角度
        float angle= (float) ((tmpAngle+360/itemCount/2)*Math.PI/180);
        int x= (int) (mCenter+mRadius/2/2*Math.cos(angle));
        int y= (int) (mCenter+mRadius/2/2*Math.sin(angle));

        //确定图片的位置
        Rect rect=new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth,y+imgWidth/2);

        mCanvas.drawBitmap(mImageBitmap,null,rect,null);

    }

    /**
     * 绘制文本
     * @param tmpAngle 起始角度
     * @param sweepAngle 结束角度
     * @param mStr 绘制文字
     */
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path mPath=new Path();
        mPath.addArc(mRange,tmpAngle,sweepAngle);
        float textWith=mTextPaint.measureText(mStr);
        int hOffset= (int) (mRadius*Math.PI/itemCount/2-textWith/2);//水平偏移量
        int vOffset=mRadius/6/2;//垂直偏移量
        mCanvas.drawTextOnPath(mStr,mPath,hOffset,vOffset,mTextPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,
                getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);
    }

}
