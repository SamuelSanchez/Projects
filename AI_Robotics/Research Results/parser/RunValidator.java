//package parser;

public class RunValidator{
   private String type;
   private int point;

   public RunValidator( String type, int point ){
      this.type = type;
      this.point = point;
   }

   public String getType(){
      return type;
   }

   public int getPointsNumber(){
      return point;
   }

   public boolean equals( Object obj ){
       if( obj == null && !(obj instanceof RunValidator) )
           return false;
       String type = ( (RunValidator) obj ).getType();
       int points  = ( (RunValidator) obj ).getPointsNumber();
       if( !this.type.equals(type) || point != points )
           return false;
       return true;
   }
}
