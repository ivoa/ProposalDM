
  package org.ivoa.vodml.stdtypes2;

    
    
        
       import javax.persistence.*;
    
/**
* Meant to represent the value of a numerical physical quantity.  
              Can be mapped in VOTables to FIELDrefs and PARAM(ref)s, in which case the @unit attribute of those VOTable elements is assumed to be mapped to the attribute on the Quantity.
              Ths is only allowed for these predefined ivoa types.
*
* dataType:  Quantity
*
* @author generated by https://github.com/ivoa/vo-dml tools
*/
    @Embeddable
@MappedSuperclass


  @javax.xml.bind.annotation.XmlAccessorType( javax.xml.bind.annotation.XmlAccessType.NONE )  
  @javax.xml.bind.annotation.XmlType( name = "Quantity")
    
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:Quantity", type=org.ivoa.vodml.annotation.VodmlType.dataType)
 
       public abstract class Quantity  {
      
    /** 
    * The unit of this  quantity. : Attribute unit : multiplicity 0..1
    *
    */
    
 @org.ivoa.vodml.annotation.VoDml(ref="ivoa:Quantity.unit", type=org.ivoa.vodml.annotation.VodmlType.attribute)
 
        @Basic( optional = true )
        @Column( name = "unit", nullable = true )
                            
    @javax.xml.bind.annotation.XmlElement( name = "unit", required = false, type = org.ivoa.vodml.stdtypes2.Unit.class)
    
    protected org.ivoa.vodml.stdtypes2.Unit unit;
        
      /**
       * Creates a new Quantity
       */
      public Quantity() {
        super();
      }
      
        /**
        * full parameter constructor.
        */
        public  Quantity (
          final org.ivoa.vodml.stdtypes2.Unit unit
        )
        {
           
           this.unit = unit;
           
        }
        
        /**
        * Returns unit Attribute
        * @return unit Attribute
        */
        
        public org.ivoa.vodml.stdtypes2.Unit getUnit() {
        return (org.ivoa.vodml.stdtypes2.Unit)this.unit;
        }
        
        
        /**
        * Defines unit Attribute
        * @param pUnit value to set
        */
        public void setUnit(final org.ivoa.vodml.stdtypes2.Unit pUnit) {
        this.unit = pUnit;
        }
        

        public Quantity withUnit(final org.ivoa.vodml.stdtypes2.Unit pUnit) {
        this.unit = pUnit;
        return this;
        }

    



}
  