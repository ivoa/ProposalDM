
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

        /**  representation */
        @javax.xml.bind.annotation.XmlValue
        private Object value;

        /**
         * Creates a new Identifier Primitive Type instance, wrapping a base type.
         *
         * @param v 
         */
        public Identifier(final Object v) {
            this.value = v;
        }
        /**
         * no arg constructor.
         */
        protected Identifier() {}

        /**
         * Return the representation of this primitive (value)
         * @return string representation of this primitive( value)
         */
        public final Object value() {
            return this.value;
        }

        /**
         * Return the string representation of this primitive value
         * @see #value()
         * @return string representation of this primitive
         */
        @Override
        public final String toString() {
            return value().toString();
        }
              

      }
  