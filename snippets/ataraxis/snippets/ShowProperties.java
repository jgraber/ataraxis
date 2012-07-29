package ataraxis.snippets;

import java.util.*;

public class ShowProperties
{
   public static void main(String[] args)
   {
      Properties sysprops   = System.getProperties();
      Enumeration propnames = sysprops.propertyNames();
      while (propnames.hasMoreElements()) {
         String propname = (String)propnames.nextElement();
         System.out.println(
            propname + "=" + System.getProperty(propname)
         );
      }
   }
}