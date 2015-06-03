package taller.robotino.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import android.widget.ImageButton;
import android.widget.ImageView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


import taller.robotino.R;
import taller.robotino.modelo.Comunicador;
import taller.robotino.view.MySurfaceView;

public class movimiento extends Activity implements View.OnLongClickListener, View.OnTouchListener{


    private static final String NAMESPACE = "http://services.ws.rws/";
    private static String URL = "http://172.26.201.4:8080/WSR/Servicios";//no sirve localhost si no se usa el emulador propio de androidstudio
    private static String server="172.26.201.4";

    private MySurfaceView analog;
    private int mov;
    private ImageView iv;

    private ImageButton giroD;
    private ImageButton giroi;

    private int parar=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ocultamos el titulo
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //ocultamos la barra de notificaciones
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //se define que en ves de usar una vista de Layout se usa la vista entregada por MySurfaceView
        iniciar();


    }

    public void iniciar()
    {
        mov= Comunicador.getMovimiento();
        if(1 == mov)
        {
            setContentView(R.layout.movimiento_clasico);
            iv=(ImageView) findViewById(R.id.camara1);

        }
        else
        {
            setContentView(R.layout.activity_movimiento);
            iv=(ImageView) findViewById(R.id.camara);
            giroD = (ImageButton) findViewById(R.id.gd);
            giroD.setOnLongClickListener(this);

            giroi = (ImageButton) findViewById(R.id.gi);
            giroi.setOnLongClickListener(this);
            try {
                analog = (MySurfaceView) findViewById(R.id.analogo);
                analog.setMovimiento(this);
            }
            catch (Exception e){}
        }

        try {
        }catch (Exception e){}
        try {
            server = Comunicador.getIpWebService();
            URL= "http://"+server+":8080/WSR/Servicios";
        }catch (Exception e){}

        Log.e("server=",""+server);
        Comunicador.setCamara(true);
        pedirImagen2();
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movimiento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    public void pedirImagen2()
    {
        //esta parte se agrego para poder pedir la imagen sin necesidad de conectar ya que toma la ip del server del edittext
        //EditText editTextServer = (EditText) findViewById(R.id.editTextIpServer);
        // String localserver = editTextServer.getText().toString();//192.168.56.1
        //localserver = "192.168.1.40";
        final String URL = "http://"+server+":8080/WSR/Servicios";//no sirve localhost si no se usa el emulador propio de androidstudio
        //final String URL = "http://" + server+":8080/WSR/Servicios";//no sirve localhost si no se usa el emulador propio de androidstudio
        final String METHOD_NAME = "imagen";//mediaDatosSensores
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta;
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                //quiza poner ciclo aqui
                //Thread.sleep(100);
                try {
                    while (Comunicador.getCamara()) {
                        Thread.sleep(100);
                        try {
                            transportSE.call(SOAP_ACTION,envelope);
                            SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                            respuesta = resultado.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                byte[] decodedByte = Base64.decode(respuesta, 0);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                                iv.setImageBitmap(bitmap);

                                //Toast.makeText(MainActivity.this,String.valueOf(contador),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        nt.start();
    }

    public void parar(View view)
    {
        final String METHOD_NAME = "parar";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void adelante(View view)
    {
        final String METHOD_NAME = "N";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void atras(View view)
    {
        final String METHOD_NAME = "S";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void derecha(View view)
    {
        final String METHOD_NAME = "E";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void izquierda(View view)
    {
        final String METHOD_NAME = "O";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void dNE(View view)
    {
        final String METHOD_NAME = "NE";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void dSO(View view)
    {
        final String METHOD_NAME = "SO";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void dSE(View view)
    {
        final String METHOD_NAME = "SE";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void dNO(View view)
    {
        final String METHOD_NAME = "NO";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void gizquierda(View view)
    {
        final String METHOD_NAME = "giroIzquierda";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    public void gderecha(View view)
    {
        final String METHOD_NAME = "giroDerecha";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Thread nt = new Thread()
        {
            String respuesta = "Coneccion Fallida";
            @Override
            public void run()
            {
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE transportSE = new HttpTransportSE(URL);
                try {
                    transportSE.call(SOAP_ACTION,envelope);
                    SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
                    respuesta = resultado.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(movimiento.this,respuesta,Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        nt.start();
    }

    @Override
    public boolean onLongClick(View v)
    {
        switch (v.getId())
        {
            case R.id.gd:
                if(parar!=1)
                {
                    this.gderecha(v);
                    parar=1;
                }
                break;
            case R.id.gi:
                if(parar!=2)
                {
                    this.gizquierda(v);
                    parar=2;
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP://cuando levantamos el dedo de la pantalla
                Log.e("touchEven ACTION_DOWN", "se toco la pantalla");
                parar=0;
                this.parar(v);
                break;
        }
        return true;
    }
}
