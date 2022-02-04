
  package org.ivoa.vodml.stdtypes2;

    
    
        
       import javax.persistence.*;
    
/**
* A real value with a unit.
*
* dataType:  RealQuantity
*
* @author generated by https://github.com/ivoa/vo-dml tools
*/
    @Embeddable


  @javax.xml.bind.annotation.XmlAccessorType( javax.xml.bind.annotation.XmlAccessType.NONE )  
  @javax.xml.bind.annotation.XmlType( name = "RealQuantity")
    
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:RealQuantity", type=org.ivoa.vodml.annotation.VodmlType.dataType)
 
       public  class RealQuantity extends Quantity {
      
    /** 
    * The real value of this quantity. : Attribute value : multiplicity 1
    *
    */
    
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:RealQuantity.value", type=org.ivoa.vodml.annotation.VodmlType.attribute)
 
        @Basic( optional = true ) // IMPL have changed the nullability to allow cases where it is embedded to be null - cannot override this easily in hibernate
        @Column( name = "value", nullable = false )
                            
    @javax.xml.bind.annotation.XmlElement( name = "value", required = true, type = Double.class)
    
    protected Double value;
        
      /**
       * Creates a new RealQuantity
       */
      public RealQuantity() {
        super();
      }
      
        /**
        * full parameter constructor.
        */
        public  RealQuantity (
          final Double value, final org.ivoa.vodml.stdtypes2.Unit unit
        )
        {
           
           this.value = value;
           
           this.unit = unit;
           
        }
        
        /**
        * Returns value Attribute
        * @return value Attribute
        */
        
        public Double getValue() {
        return (Double)this.value;
        }
        
        
        /**
        * Defines value Attribute
        * @param pValue value to set
        */
        public void setValue(final Double pValue) {
        this.value = pValue;
        }
        

        public RealQuantity withValue(final Double pValue) {
        this.value = pValue;
        return this;
        }

    
        /**
          A builder class for RealQuantity principally to be used in the functional builder pattern.
        */
        public static class RealQuantityBuilder {
           
           /**
           * The real value of this quantity.
           */
           public Double value;
                   
           /**
           * The unit of this  quantity.
           */
           public org.ivoa.vodml.stdtypes2.Unit unit;
                   

           private RealQuantityBuilder with (java.util.function.Consumer <RealQuantityBuilder> f)
           {
             f.accept(this);
             return this;
           }
           /**
             create a RealQuantity from this builder.
           */
           public RealQuantity create()
           {
             return new RealQuantity (
             value,unit
             );
           }
         }
         /**
           create a RealQuantity in functional builder style.
         */
         public static RealQuantity createRealQuantity (java.util.function.Consumer <RealQuantityBuilder> f)
         {
             return new RealQuantityBuilder().with(f).create();
         }
    



}
  