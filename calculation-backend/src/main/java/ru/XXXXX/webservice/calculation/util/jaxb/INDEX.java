
package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.jaxb;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PERIOD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CHAPTER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ITEMS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ITEM" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PRESSMARK" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="RATE_TRANSPORT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="RATE_MATERIAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="RATE_MACHINE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="RATE_TOTAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="RATE_SALARY" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="RATE_EXTRA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "period",
        "chapter",
        "items"
})
@XmlRootElement(name = "INDEX")
public class INDEX {

    /**
     * auto generated field
     */
    @XmlElement(name = "PERIOD", required = true)
    protected String period;
    /**
     * auto generated field
     */
    @XmlElement(name = "CHAPTER", required = true)
    protected String chapter;
    /**
     * auto generated field
     */
    @XmlElement(name = "ITEMS", required = true)
    protected INDEX.ITEMS items;

    /**
     * Gets the value of the period property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPERIOD() {
        return period;
    }

    /**
     * Sets the value of the period property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPERIOD(String value) {
        this.period = value;
    }

    /**
     * Gets the value of the chapter property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCHAPTER() {
        return chapter;
    }

    /**
     * Sets the value of the chapter property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCHAPTER(String value) {
        this.chapter = value;
    }

    /**
     * Gets the value of the items property.
     *
     * @return possible object is
     * {@link INDEX.ITEMS }
     */
    public INDEX.ITEMS getITEMS() {
        return items;
    }

    /**
     * Sets the value of the items property.
     *
     * @param value allowed object is
     *              {@link INDEX.ITEMS }
     */
    public void setITEMS(INDEX.ITEMS value) {
        this.items = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ITEM" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="PRESSMARK" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="RATE_TRANSPORT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="RATE_MATERIAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="RATE_MACHINE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="RATE_TOTAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="RATE_SALARY" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="RATE_EXTRA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "item"
    })
    public static class ITEMS {

        /**
         * auto generated field
         */
        @XmlElement(name = "ITEM", required = true)
        protected List<INDEX.ITEMS.ITEM> item;

        /**
         * Gets the value of the item property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getITEM().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link INDEX.ITEMS.ITEM }
         * @return object
         */
        public List<INDEX.ITEMS.ITEM> getITEM() {
            if (item == null) {
                item = new ArrayList<INDEX.ITEMS.ITEM>();
            }
            return this.item;
        }


        /**
         * <p>Java class for anonymous complex type.
         * <p>
         * <p>The following schema fragment specifies the expected content contained within this class.
         * <p>
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="PRESSMARK" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="RATE_TRANSPORT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="RATE_MATERIAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="RATE_MACHINE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="RATE_TOTAL" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="RATE_SALARY" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="RATE_EXTRA" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_EXTRA_OVERHEAD" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_EXTRA_PROFIT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="BASE_EXTRA_WINTER_RISE_PRICE_MAT" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "pressmark",
                "ratetransport",
                "ratematerial",
                "ratemachine",
                "overhead",
                "extraoverhead",
                "profit",
                "extraprofit",
                "winterriseprice",
                "extrawinterriseprice",
                "winterrisepricemat",
                "extrawinterrisepricemat",
                "ratetotal",
                "ratesalary",
                "rateextra",
                "baseoverhead",
                "baseextraoverhead",
                "baseprofit",
                "baseextraprofit",
                "basewinterriseprice",
                "baseextrawinterriseprice",
                "basewinterrisepricemat",
                "baseextrawinterrisepricemat"
        })
        public static class ITEM {

            /**
             * auto generated field
             */
            @XmlElement(name = "PRESSMARK", required = true)
            protected String pressmark;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_TRANSPORT")
            protected BigDecimal ratetransport;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_MATERIAL")
            protected BigDecimal ratematerial;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_MACHINE")
            protected BigDecimal ratemachine;
            /**
             * auto generated field
             */
            @XmlElement(name = "OVERHEAD")
            protected BigDecimal overhead;
            /**
             * auto generated field
             */
            @XmlElement(name = "EXTRA_OVERHEAD")
            protected BigDecimal extraoverhead;
            /**
             * auto generated field
             */
            @XmlElement(name = "PROFIT")
            protected BigDecimal profit;
            /**
             * auto generated field
             */
            @XmlElement(name = "EXTRA_PROFIT")
            protected BigDecimal extraprofit;
            /**
             * auto generated field
             */
            @XmlElement(name = "WINTER_RISE_PRICE")
            protected BigDecimal winterriseprice;
            /**
             * auto generated field
             */
            @XmlElement(name = "EXTRA_WINTER_RISE_PRICE")
            protected BigDecimal extrawinterriseprice;
            /**
             * auto generated field
             */
            @XmlElement(name = "WINTER_RISE_PRICE_MAT")
            protected BigDecimal winterrisepricemat;
            /**
             * auto generated field
             */
            @XmlElement(name = "EXTRA_WINTER_RISE_PRICE_MAT")
            protected BigDecimal extrawinterrisepricemat;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_TOTAL")
            protected BigDecimal ratetotal;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_SALARY")
            protected BigDecimal ratesalary;
            /**
             * auto generated field
             */
            @XmlElement(name = "RATE_EXTRA")
            protected BigDecimal rateextra;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_OVERHEAD")
            protected BigDecimal baseoverhead;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_EXTRA_OVERHEAD")
            protected BigDecimal baseextraoverhead;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_PROFIT")
            protected BigDecimal baseprofit;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_EXTRA_PROFIT")
            protected BigDecimal baseextraprofit;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_WINTER_RISE_PRICE")
            protected BigDecimal basewinterriseprice;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_EXTRA_WINTER_RISE_PRICE")
            protected BigDecimal baseextrawinterriseprice;
            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_WINTER_RISE_PRICE_MAT")
            protected BigDecimal basewinterrisepricemat;

            /**
             * auto generated field
             */
            @XmlElement(name = "BASE_EXTRA_WINTER_RISE_PRICE_MAT")
            protected BigDecimal baseextrawinterrisepricemat;

            /**
             * Gets the value of the pressmark property.
             *
             * @return possible object is
             * {@link String }
             */
            public String getPRESSMARK() {
                return pressmark;
            }

            /**
             * Sets the value of the pressmark property.
             *
             * @param value allowed object is
             *              {@link String }
             */
            public void setPRESSMARK(String value) {
                this.pressmark = value;
            }

            /**
             * Gets the value of the ratetransport property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATETRANSPORT() {
                return ratetransport;
            }

            /**
             * Sets the value of the ratetransport property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATETRANSPORT(BigDecimal value) {
                this.ratetransport = value;
            }

            /**
             * Gets the value of the ratematerial property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATEMATERIAL() {
                return ratematerial;
            }

            /**
             * Sets the value of the ratematerial property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATEMATERIAL(BigDecimal value) {
                this.ratematerial = value;
            }

            /**
             * Gets the value of the ratemachine property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATEMACHINE() {
                return ratemachine;
            }

            /**
             * Sets the value of the ratemachine property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATEMACHINE(BigDecimal value) {
                this.ratemachine = value;
            }

            /**
             * Gets the value of the overhead property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getOVERHEAD() {
                return overhead;
            }

            /**
             * Sets the value of the overhead property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setOVERHEAD(BigDecimal value) {
                this.overhead = value;
            }

            /**
             * Gets the value of the extraoverhead property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getEXTRAOVERHEAD() {
                return extraoverhead;
            }

            /**
             * Sets the value of the extraoverhead property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setEXTRAOVERHEAD(BigDecimal value) {
                this.extraoverhead = value;
            }

            /**
             * Gets the value of the profit property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getPROFIT() {
                return profit;
            }

            /**
             * Sets the value of the profit property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setPROFIT(BigDecimal value) {
                this.profit = value;
            }

            /**
             * Gets the value of the extraprofit property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getEXTRAPROFIT() {
                return extraprofit;
            }

            /**
             * Sets the value of the extraprofit property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setEXTRAPROFIT(BigDecimal value) {
                this.extraprofit = value;
            }

            /**
             * Gets the value of the winterriseprice property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getWINTERRISEPRICE() {
                return winterriseprice;
            }

            /**
             * Sets the value of the winterriseprice property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setWINTERRISEPRICE(BigDecimal value) {
                this.winterriseprice = value;
            }

            /**
             * Gets the value of the extrawinterriseprice property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getEXTRAWINTERRISEPRICE() {
                return extrawinterriseprice;
            }

            /**
             * Sets the value of the extrawinterriseprice property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setEXTRAWINTERRISEPRICE(BigDecimal value) {
                this.extrawinterriseprice = value;
            }

            /**
             * Gets the value of the winterrisepricemat property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getWINTERRISEPRICEMAT() {
                return winterrisepricemat;
            }

            /**
             * Sets the value of the winterrisepricemat property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setWINTERRISEPRICEMAT(BigDecimal value) {
                this.winterrisepricemat = value;
            }

            /**
             * Gets the value of the extrawinterrisepricemat property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getEXTRAWINTERRISEPRICEMAT() {
                return extrawinterrisepricemat;
            }

            /**
             * Sets the value of the extrawinterrisepricemat property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setEXTRAWINTERRISEPRICEMAT(BigDecimal value) {
                this.extrawinterrisepricemat = value;
            }

            /**
             * Gets the value of the ratetotal property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATETOTAL() {
                return ratetotal;
            }

            /**
             * Sets the value of the ratetotal property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATETOTAL(BigDecimal value) {
                this.ratetotal = value;
            }

            /**
             * Gets the value of the ratesalary property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATESALARY() {
                return ratesalary;
            }

            /**
             * Sets the value of the ratesalary property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATESALARY(BigDecimal value) {
                this.ratesalary = value;
            }

            /**
             * Gets the value of the rateextra property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getRATEEXTRA() {
                return rateextra;
            }

            /**
             * Sets the value of the rateextra property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setRATEEXTRA(BigDecimal value) {
                this.rateextra = value;
            }

            /**
             * Gets the value of the baseoverhead property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEOVERHEAD() {
                return baseoverhead;
            }

            /**
             * Sets the value of the baseoverhead property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEOVERHEAD(BigDecimal value) {
                this.baseoverhead = value;
            }

            /**
             * Gets the value of the baseextraoverhead property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEEXTRAOVERHEAD() {
                return baseextraoverhead;
            }

            /**
             * Sets the value of the baseextraoverhead property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEEXTRAOVERHEAD(BigDecimal value) {
                this.baseextraoverhead = value;
            }

            /**
             * Gets the value of the baseprofit property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEPROFIT() {
                return baseprofit;
            }

            /**
             * Sets the value of the baseprofit property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEPROFIT(BigDecimal value) {
                this.baseprofit = value;
            }

            /**
             * Gets the value of the baseextraprofit property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEEXTRAPROFIT() {
                return baseextraprofit;
            }

            /**
             * Sets the value of the baseextraprofit property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEEXTRAPROFIT(BigDecimal value) {
                this.baseextraprofit = value;
            }

            /**
             * Gets the value of the basewinterriseprice property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEWINTERRISEPRICE() {
                return basewinterriseprice;
            }

            /**
             * Sets the value of the basewinterriseprice property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEWINTERRISEPRICE(BigDecimal value) {
                this.basewinterriseprice = value;
            }

            /**
             * Gets the value of the baseextrawinterriseprice property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEEXTRAWINTERRISEPRICE() {
                return baseextrawinterriseprice;
            }

            /**
             * Sets the value of the baseextrawinterriseprice property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEEXTRAWINTERRISEPRICE(BigDecimal value) {
                this.baseextrawinterriseprice = value;
            }

            /**
             * Gets the value of the basewinterrisepricemat property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEWINTERRISEPRICEMAT() {
                return basewinterrisepricemat;
            }

            /**
             * Sets the value of the basewinterrisepricemat property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEWINTERRISEPRICEMAT(BigDecimal value) {
                this.basewinterrisepricemat = value;
            }

            /**
             * Gets the value of the baseextrawinterrisepricemat property.
             *
             * @return possible object is
             * {@link BigDecimal }
             */
            public BigDecimal getBASEEXTRAWINTERRISEPRICEMAT() {
                return baseextrawinterrisepricemat;
            }

            /**
             * Sets the value of the baseextrawinterrisepricemat property.
             *
             * @param value allowed object is
             *              {@link BigDecimal }
             */
            public void setBASEEXTRAWINTERRISEPRICEMAT(BigDecimal value) {
                this.baseextrawinterrisepricemat = value;
            }

        }

    }

}
