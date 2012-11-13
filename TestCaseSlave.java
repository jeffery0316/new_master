/*
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
*/
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;

public class TestCaseSlave{

   public static void main(String[] args) throws UnknownHostException, IOException{
      new TestCaseSlave().go();
   }
                        
   public void go(){
      try{
         Socket s = new Socket();//new Socket("127.0.0.1",9000);
         InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 9001);
         s.connect(isa, 10000);
         RecvThread recv = new RecvThread(s);
         new Thread(recv).start();
   
         SendThread send = new SendThread(s);
         new Thread(send).start();
      }catch(UnknownHostException e){
         e.printStackTrace();
      }catch(IOException e){
         System.out.println("maybe master server is not initialized");
         //e.printStackTrace();
      }
   }

   class RecvThread implements Runnable{
      private Socket s = null;
      //private DataInputStream dis = null;
      private BufferedReader bf = null;
      public RecvThread(Socket s){
         this.s = s;
         try{
            bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //dis = new DataInputStream(s.getInputStream());
         }catch(IOException e){
            e.printStackTrace();
         }
      }
                                                   
      @Override 
      public void run() {
         String str = "";
         int r = 0, nread = 0;
         char[] buf = new char[5000];
         while(true){
            try{
               //System.out.println(buf[nread]);
               if((r = bf.read(buf, nread, 1))<=0) break;
               //System.out.println(buf[nread]);
               if(buf[nread++] == '\0') break;    
               
               //str = dis.readUTF();
               //if("bye".equals(str))break;
               //System.out.println("received message:"+str.toString());
            }catch(IOException e){
               e.printStackTrace();
            }
         }
         System.out.println("received message: "+new String(buf));
      }
   }
   class SendThread implements Runnable{
      private Socket s;
      private BufferedOutputStream bos;//DataOutputStream dos;
      public SendThread(Socket s){
         this.s = s;
         try{
            bos = new BufferedOutputStream(s.getOutputStream());
            //dos = new DataOutputStream(s.getOutputStream());
         }catch(IOException ioe){
            ioe.printStackTrace();
         }
      }

      @Override
      public void run(){
         String str = "";
         while(true){
            //System.out.println("input message: ");
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try{
               str = "123\0";//br.readLine();
               bos.write(str.getBytes());
               //dos.writeUTF(str);
               if("bye".equals(str)) break;
            }catch(IOException ioe){
               ioe.printStackTrace();
            }
         }
      }
   }
}

/*import java.net.*;
import java.io.*;
public class TestCase{
   private static int port = 9000;
   private static String address = "127.0.0.1";
   public static void main(String [] args){
      try{
         Socket s = new Socket();
         InetSocketAddress isa = new InetSocketAddress(address, port);
         s.connect(isa, 10000);
         BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
         String cmd = "cmd=query&language=all&recordSeconds=10&listNumber=20&method=3&searchAll=no&wavMid=507+519+531+536+533+536+537+536+536+538+540+540+539+541+541+573+568+571+572+569+574+583+583+584+587+593+607+619+622+622+622+616+619+621+620+613+604+590+589+590+588+583+577+571+568+568+570+572+570+569+571+571+572+569+571+570+571+573+573+578+591+593+591+591+588+569+563+565+569+568+567+570+569+568+590+589+590+588+583+577+571+568+568+570+572+570+569+571+571+572+569+571+570+569+571+571+572+569+571";
         bos.write(new String(cmd+"\0").getBytes());
         bos.flush();
         bos.close();
         s.close();
      }catch(IOException ioe){
         ioe.printStackTrace();
      }
   }
}*/
