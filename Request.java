class Request{
   private int beginRange, endRange, useRest, listNumber;
   private String cmd, method, language, database, wavMid; 
   public Request(){
      beginRange = 0;
      endRange = 10000;
      useRest = 0;
      listNumber = 15;
      method = "linear_scaling";
      language = "All";
      database = "MIR";
      wavMid = "";
   }
   public Request(String req){
      System.out.println("constructor: req");
      processRequest(req);
   }
   public void setCmd(String cmd){
      this.cmd = cmd;
   }
   public void setMethod(String method){
      this.method = method;
   }
   public void setLanguage(String language){
      this.language = language;
   }
   public void setDatabase(String database){
      this.database = database;
   }
   public void setWavMid(String wavMid){
      this.wavMid = wavMid;
   }
   public static void processRequest(String rawData){
      /* here needs to parse raw string into each token */
      for(String str : rawData.split("&")){
         System.out.println(str);
      }
   }
}
