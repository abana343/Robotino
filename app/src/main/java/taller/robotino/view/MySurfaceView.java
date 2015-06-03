package taller.robotino.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import taller.robotino.activity.movimiento;
import taller.robotino.modelo.Comunicador;

/**
 * Created by Bastian on 03-06-2015.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    //referencia un thread que usaremos para dibujar.
    private MySurfaceThread thread;
    //variables que indican donde toco la pantalla
    public int touched_x, touched_y;
    //variable que indica si se esta tocando o no la pantalla
    public boolean touched;

    private movimiento mov;
    private float movx=0,movy=0;
    private boolean adentro=false;
    private int ejecutandose=0;



    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //usaremos esta clase como manejador
        getHolder().addCallback(this);
    }

    public void setMovimiento(movimiento mov)
    {
        try {
            this.mov= mov;
        }
        catch (Exception e){}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        this.thread = new MySurfaceThread(getHolder(),this);
        this.thread.setRuning(true);
        this.thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e("surfaceDestroyed","hilo detenido");
        Comunicador.setCamara(false);
        boolean retry = true;
        //el hilo se detendra.
        this.thread.setRuning(false);
        while(retry)
        {
            try
            {
                this.thread.join();
                retry=false;
            }
            catch (InterruptedException e){}
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        try
        {
            //se utiliza para dibujar
            int ancho = canvas.getWidth();
            int alto = canvas.getHeight();
            int anchoW = ancho/2;
            int altoH = alto/2;
            int radio;
            if(anchoW<=altoH)
            {
                radio=anchoW;
            }
            else
            {
                radio=altoH;
            }
/*
    Bitmap imgBoton_libre = BitmapFactory.decodeResource(getResources(),R.drawable.botonlibre);
        Bitmap imgBoton_puch = BitmapFactory.decodeResource(getResources(),R.drawable.botonpress);

        int anchobitmap= imgBoton_libre.getWidth();
        int altobitmap= imgBoton_puch.getHeight();
 */
            /// imagen del boton


            //pintamos el fondo de color blanco
            canvas.drawColor(Color.TRANSPARENT);

            //definimos circulo externo
            Paint pcirculo = new Paint();
            pcirculo.setColor(Color.BLUE);
            pcirculo.setStyle(Paint.Style.FILL);
            canvas.drawCircle(anchoW, altoH, radio, pcirculo);

            //circulo interno
            Paint pcirculointerno = new Paint();
            pcirculointerno.setColor(Color.BLACK);
            pcirculointerno.setStyle(Paint.Style.FILL);

            // circulo interno
            Paint imgboton = new Paint();
            imgboton.setAntiAlias(true);
            imgboton.setFilterBitmap(true);
            imgboton.setDither(true);

        /*
        validacion sobre el movimineto de la palanca, si estamos tocando la pantalla y en donde
        tocamos esta dentro del sector de la palanca la dibujaremso en donde toco, esto dara la censacion de movimiento.
        */
            this.movx=touched_x-anchoW;
            this.movy=touched_y-altoH;
            double hipo =Math.pow(movx,2)+ Math.pow(movy,2);
            hipo=Math.sqrt(hipo);
            if(touched && //si tocamos
                    //eje Y
                    hipo<radio
                    )
            {
                canvas.drawCircle(touched_x,touched_y,35,pcirculointerno);
                this.adentro=true;
            }
            else
            {
                canvas.drawCircle(anchoW,altoH,35,pcirculointerno);
                this.adentro=false;
            }
        }catch (Exception e)
        {}
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //eventos que suceden cuando se toca la pantalla.
        this.mover();
        touched_x =(int) event.getX();
        touched_y =(int) event.getY();
        //leemos el codigo de acción
        int action = event.getAction();
        Log.e("touched (x,y)","("+touched_x+","+touched_y+")");

        switch (action)
        {
            case MotionEvent.ACTION_DOWN://cuando se toca la pantalla
                Log.e("touchEven ACTION_DOWN","se toco la pantalla");
                touched=true;
                break;
            case MotionEvent.ACTION_MOVE://cuando se desplaza el dedo por la pantalla
                touched=true;
                Log.e("TouchEven ACTION_MOVE", "nos desplazamos por la pantalla");
                break;
            case MotionEvent.ACTION_UP://cuando levantamos el dedo de la pantalla
                touched=false;
                Log.e("TouchEven ACTION_UP", "ya no tocamos la pantalla");
                Log.e("parar","parado");
                mov.parar(this);
                ejecutandose=0;
                break;
            case MotionEvent.ACTION_CANCEL:
                touched=false;
                Log.e("TouchEven ACTION_CANCEL","");
                mov.parar(this);
                ejecutandose=0;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                touched=false;
                mov.parar(this);
                ejecutandose=0;
                break;
            default:
        }
        return true;
    }

    private void mover()
    {
        Log.e("----------","----");
        Log.e("x reducido",""+this.movx);
        Log.e("y reducido",""+this.movy);
        float div;
        if(this.adentro)
        {

            if(this.movx!=0)
            {
                div = this.movy/this.movx;
                Log.e("divicion="," "+div);
                if(div>0.41 && div<=2.41)
                {
                    if(this.movx<0)
                    {
                        if(ejecutandose!=1)
                        {
                            Log.e("mover en ","diagonal arriba iz");
                            mov.dNO(this);
                            ejecutandose=1;
                        }
                    }else
                    {
                        if(ejecutandose!=2)
                        {
                            Log.e("mover en ","diagonal abajo der");
                            mov.dSE(this);
                            ejecutandose=2;
                        }

                    }
                }
                //................
                if(div<=-0.41 && div>-2.41)
                {
                    if(this.movx<0)
                    {
                        if(ejecutandose!=3){
                            Log.e("mover en ","diagonal abajo iz");
                            mov.dSO(this);
                            ejecutandose=3;
                        }
                    }else
                    {
                        if(ejecutandose!=4) {
                            Log.e("mover en ", "diagonal arriba der");
                            mov.dNE(this);
                            ejecutandose=4;
                        }
                    }
                }
                //................
                if((div>-0.41 && div<=0) || (div<=0.41 && div>=0))
                {
                    if(this.movx<0)
                    {
                        if(ejecutandose!=5) {
                            Log.e("mover en ", "Izquierda");
                            mov.izquierda(this);
                            ejecutandose=5;
                        }
                    }else
                    {
                        if(ejecutandose!=6)
                        {
                            Log.e("mover en ","derecha");
                            mov.derecha(this);
                            ejecutandose=6;
                        }

                    }
                }
                //................
                if(div<= -2.41 || div> 2.41)
                {
                    if(this.movy<0)
                    {
                        if(ejecutandose!=7) {
                            Log.e("mover en ", "arriba");
                            mov.adelante(this);
                            ejecutandose=7;
                        }
                    }else
                    {
                        if(ejecutandose!=8) {
                            Log.e("mover en ", "abajo");
                            mov.atras(this);
                            ejecutandose=8;
                        }
                    }
                }

            }else
            {
                if(movy<0)
                {
                    if(ejecutandose!=7){
                        Log.e("mover en ","arriba");
                        mov.adelante(this);
                        ejecutandose=7;
                    }
                }
                else
                {
                    if(ejecutandose!=8) {
                        Log.e("mover en ", "abajo");
                        mov.atras(this);
                        ejecutandose=8;
                    }
                }
            }
        }

    }
}

