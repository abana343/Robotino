package taller.robotino.modelo;

/**
 * Created by Bastian on 03-06-2015.
 */
public class Comunicador {


    /**
     * Created by Javier Aros on 14-05-2015.
     * Enviar objeto entre diferentes ativity
     */

        private static Object objeto = null;



        private static String IpWebService = "172.17.50.211";
        private static String IpRobotino = "172.26.201.3";
        public static final String NAMESPACE = "http://services.ws.rws/";

        //para parar el tred de la camara.

        public static boolean camara;
        private static int movimiento;

        public static String getIpRobotino() {
            return IpRobotino;
        }

        public static void setIpRobotino(String ipRobotino) {
            IpRobotino = ipRobotino;
        }

        public static String getIpWebService() {
            return IpWebService;
        }

        public static void setIpWebService(String ipWebService) {
            IpWebService = ipWebService;
        }

        public static void setObjeto(Object newObjeto) {
            objeto = newObjeto;
        }

        public static Object getObjeto() {
            return objeto;
        }

        public static String getNamespace(){
            return NAMESPACE;
        }

        public static void setMovimiento(int mov)
        {
            movimiento=mov;
        }
        public static int getMovimiento()
        {
            return movimiento;
        }

        public static void setCamara(boolean cam)
        {
            camara=cam;
        }

        public static boolean getCamara()
        {
            return camara;
        }



    }
