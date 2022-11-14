
package org.ivoa.vodml.stdtypes2;
        
        import javax.persistence.*;
    


      /**
      *  an identifier that can be used as a key to look up in an IVOA registry - see https://www.ivoa.net/documents/IVOAIdentifiers/.
      *  PrimitiveType Ivorn :
      *
      *  @author Paul Harrison
      */
      
 @org.ivoa.vodml.annotation.VoDml(id="ivoa:Ivorn", role=org.ivoa.vodml.annotation.VodmlRole.primitiveType)
 @Embeddable

    @javax.xml.bind.annotation.XmlType( name = "Ivorn")
  
      public class Ivorn  implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        /**  representation */
        @javax.xml.bind.annotation.XmlValue
        private String value;

        /**
         * Creates a new Ivorn Primitive Type instance, wrapping a base type.
         *
         * @param v 
         */
        public Ivorn(final String v) {
            this.value = v;
        }
        /**
         * no arg constructor.
         */
        protected Ivorn() {}

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
  