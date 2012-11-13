import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Master{
   private int socketNum = 0;
   public Master() throws Exception{
   }

   public static void main(String [] args){
      ExecutorService es = Executors.newFixedThreadPool(3);
      es.execute(new WebInvoker());//if here we add queue into constructor
      //es.execute(new SlaveInvoker());
      //es.shutdown();
   }
}

/**
 * this class is used to handle the web part, and
 * each request will store in the requestArray
 */
class WebInvoker implements Runnable{

   /**
    * The socket which handle input request
    * , and try to use default port to open socket
    */
   private ServerSocket server;

   /**
    * This is default port
    */
   private final int masterPort = 9000;
   private ArrayList<Request> requestArray = new ArrayList<Request>();
   private final int BUF_SIZE = 5000;
   private Socket clifd;
   private char[] buf;
   private char terminalChar = '\0';
   
   /**
    * Constructor initialize some parameter
    */
   public WebInvoker(){
      System.out.printf("Server run on port:%d\n", masterPort);
      buf = new char[BUF_SIZE];
      try{
         server = new ServerSocket(masterPort);
      }catch(Exception e){
         e.printStackTrace();
      }
   }

   /**
    * run method will implements handling information
    */ 
   public void run(){
      System.out.println("get client information...");
      
      /* initialize ssrvManager */
      SlaveInvoker ssrvManager = new SlaveInvoker();
      ssrvManager.execute();
      while(true){
         try{
            /* accept ther request */
            clifd = server.accept();

            /* decode the request into data stream */
            String req = this.receive(clifd);
            System.out.println("get request string is: "+req);
            System.out.println("client " + clifd.getInetAddress().getHostAddress() + " connected");
            
            /* temporarily saves the request into requestArray */
            //this.addRequest(new Request(req));

            /* transfer the data to SlaveInvoker */
            ssrvManager.renewRequest(new Request(req));
         }catch(IOException ioe){
            ioe.printStackTrace();
         }
      }      
   }

   /**
    * read stream from client using BufferedReader and InputReader
    * return the string type request
    */
   public String receive(Socket s){
      int r = 0, nread = 0;
      try{
         BufferedReader bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
         while(true){
            System.out.println(buf[nread]);
            if((r = bf.read(buf, nread, 1))<=0) return null;
            //System.out.println(buf[nread]);
            if(buf[nread++] == terminalChar) break;        
         }
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
      return (nread <=1) ? null : new String(buf, 0, nread-1);
      /*
      if(nread <= 1)
         return null;
      else
         return new String(buf, 0, nread-1);
      */
   }

   public void addRequest(Request request){
      requestArray.add(request);
      System.out.println("the queue is"+ requestArray.size());
   }
   
   public boolean removeRequest(){
      if(requestArray.size()>0){
         requestArray.remove(1);
         return true;
      }else{
         return false;
      }
   }

   public synchronized void computePitch(Socket s){
      
   }
/*
   class RequestDispatcher{
      public RequestDispatcher(){
      }
   }
*/
}


/**
 * this class is used to check the slave server alive or not,
 * for example, i set the checkPeriod = 30 and this thread will check
 * each slave server in each 30 seconds
 */ 
class SlaveInvoker{
   private int checkPeriod = 500;
   private ServerSocket slaveService;
   private BufferedOutputStream bos;
   private ArrayList<Request> requestList = new ArrayList<Request>();
   private ArrayList<Socket> slaveList = new ArrayList<Socket>();
   public SlaveInvoker(){
      try{
         slaveService = new ServerSocket(9001);
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
   }

   public void broadcastMessage(String cmd){
      for(Socket so: slaveList){
         try{
            bos = new BufferedOutputStream(so.getOutputStream());
            bos.write(new String(cmd+"\0").getBytes());
            bos.flush();
            bos.close();
         }catch(IOException ioe){
            ioe.printStackTrace();
         }
      }
   }
   
   /**
    * this method is used to renew the request array if there comes 
    * new request
    */
   public void renewRequest(Request reqArr){
      this.requestList.add(reqArr);
   }

   /**
    * if the slave server want to come in, 
    * the SlaveInvoker will let it in and add it into ArrayList
    * However, it will add until the request finished
    */ 
   public void cyclingCheck()throws InterruptedException, IOException{
      while(true){
         Socket slavefd = slaveService.accept();
         this.addSlave(slavefd);
         //Thread.sleep(checkPeriod);
      }
   }
   
   public void execute(){
      try{
         /* here use multithread again*/
         // thread1 is cyclingCheck
         System.out.println("This is SlaveInvoker test");
         Thread checkThread = new Thread(new Runnable(){
            public void run(){
               try{
                  cyclingCheck();
                  System.out.println("cyclingCheck");
                  Thread.sleep(1000);
               }catch(InterruptedException ie){
                  ie.printStackTrace();
               }catch(IOException ioe){
                  ioe.printStackTrace();
               }
            }
         });

         // thread2 is broadcastMessage
         Thread broadcastThread = new Thread(new Runnable(){
            public void run(){
               try{
                  broadcastMessage("getSongNumber");
                  System.out.println("broadCastMessage");
                  Thread.sleep(1000);
               }catch(InterruptedException ie){
                  ie.printStackTrace();
               }
            }
         });
         checkThread.start();
         broadcastThread.start();
      }catch(Exception e){
         e.printStackTrace();
      }
   }

   /**
    * each of socket is saved in the ArrayList, 
    * if one of them is lost, we can delete it
    */
   public void addSlave(Socket s){
      slaveList.add(s);
   }

   /**
    * here just plan to delete the slave server when it
    * is lost, and just remove it
    */
   public boolean removeSlave(int index){
      slaveList.remove(index);
      return true; 
   }
   
   public void testData(){
      //this.slaveList.add(new Socket().connect( new InetSocketAddress("127", ) ));
   }
}
