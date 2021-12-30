
package org.ivoa.vodml.stdtypes2;
        
        import javax.persistence.*;
    


      /**
      *  a string identifier.
      *  PrimitiveType StringIdentifier :
      *
      *  @author Paul Harrison
      */
      
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:StringIdentifier", type=org.ivoa.vodml.annotation.VodmlType.primitiveType)
 @Embeddable

    @javax.xml.bind.annotation.XmlType( name = "StringIdentifier")
  
      public class StringIdentifier  implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        /**  representation */
        @javax.xml.bind.annotation.XmlValue
        private String value;

        /**
         * Creates a new StringIdentifier Primitive Type instance, wrapping a base type.
         *
         * @param v 
         */
        public StringIdentifier(final String v) {
            this.value = v;
        }
        /**
         * no arg constructor.
         */
        protected StringIdentifier() {}

        /**
         * Return the representation of this primitive (value)
         * @return string representation of this primitive( value)
         */
        public final String value() {
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
  