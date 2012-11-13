import java.net.*;
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
}
