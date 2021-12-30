
package org.ivoa.vodml.stdtypes2;
        
        import javax.persistence.*;
    


      /**
      *  an integer identifier.
      *  PrimitiveType IntIdentifier :
      *
      *  @author Paul Harrison
      */
      
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:IntIdentifier", type=org.ivoa.vodml.annotation.VodmlType.primitiveType)
 @Embeddable

    @javax.xml.bind.annotation.XmlType( name = "IntIdentifier")
  
      public class IntIdentifier  implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        /**  representation */
        @javax.xml.bind.annotation.XmlValue
        private int value;

        /**
         * Creates a new IntIdentifier Primitive Type instance, wrapping a base type.
         *
         * @param v 
         */
        public IntIdentifier(final int v) {
            this.value = v;
        }
        /**
         * no arg constructor.
         */
        protected IntIdentifier() {}

        /**
         * Return the representation of this primitive (value)
         * @return string representation of this primitive( value)
         */
        public final int value() {
            return this.value;
        }

        /**
         * Return the string representation of this primitive value
         * @see #value()
         * @return string representation of this primitive
         */
        @Override
        public final String toString() {
            return Integer.toString(value);
        }
              

      }
  