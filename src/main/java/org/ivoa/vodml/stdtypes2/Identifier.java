
package org.ivoa.vodml.stdtypes2;
        
import javax.persistence.*;
    


      /**
      *  something that an identifier that can be used as a key for lookup of an entity that is *outside this datamodel*.
      *  PrimitiveType Identifier :
      *
      *  @author Paul Harrison
      */
      
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:Identifier", type=org.ivoa.vodml.annotation.VodmlType.primitiveType)
 @Embeddable

    @javax.xml.bind.annotation.XmlType( name = "Identifier")
  
      public abstract class Identifier  implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

       /**
         * no arg constructor.
         */
        protected Identifier() {}

 


      }
  