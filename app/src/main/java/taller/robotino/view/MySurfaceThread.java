package taller.robotino.view;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Bastian on 03-06-2015.
 */
public class MySurfaceThread extends Thread
{
    private SurfaceHolder sh;
    private SurfaceView view;
    private boolean run;

    //el constructor recive los dos tipos de surface
    public MySurfaceThread(SurfaceHolder sh, SurfaceView view)
    {
        this.sh=sh;
        this.view=view;
        this.run=false;
    }

    //se utiliza para establecer si esta corriendo o no
    public void setRuning(boolean run)
    {
        this.run=run;
    }

    public void run()
    {
        //se instancia el canvas
        Canvas canvas;
        while(run)
        {
            canvas = null;
            try
            {
                //se define nulo el area de pintado
                canvas = sh.lockCanvas(null);
                //se utiliza el Synchronized  para asegurarce que no se este usando el objeto en ningun otro thread
                synchronized (sh)
                {
                    //se ejecuta el metodo para que dibuje pasandole en canvas
                    this.view.draw(canvas);
                }
            }
            finally
            {
                if(canvas != null)
                {
                    //se libera el canvas
                    sh.unlockCanvasAndPost(canvas);
                }
            }
        }

    }
}
