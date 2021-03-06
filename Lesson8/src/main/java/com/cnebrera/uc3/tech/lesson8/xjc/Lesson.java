//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.12.06 at 02:13:19 PM CET 
//


package com.cnebrera.uc3.tech.lesson8.xjc;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for lesson complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lesson">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lessonFrom" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="lessonTo" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="lessonName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lessonTeacher" type="{}fullTeacherInfo"/>
 *         &lt;element name="lessonMark" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lessonId" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lesson", propOrder = {
    "lessonFrom",
    "lessonTo",
    "lessonName",
    "lessonTeacher",
    "lessonMark"
})
public class Lesson {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar lessonFrom;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar lessonTo;
    @XmlElement(required = true)
    protected String lessonName;
    @XmlElement(required = true)
    protected FullTeacherInfo lessonTeacher;
    @XmlElement(required = true)
    protected BigInteger lessonMark;
    @XmlAttribute(name = "lessonId", required = true)
    protected BigInteger lessonId;

    /**
     * Gets the value of the lessonFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLessonFrom() {
        return lessonFrom;
    }

    /**
     * Sets the value of the lessonFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLessonFrom(XMLGregorianCalendar value) {
        this.lessonFrom = value;
    }

    /**
     * Gets the value of the lessonTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLessonTo() {
        return lessonTo;
    }

    /**
     * Sets the value of the lessonTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLessonTo(XMLGregorianCalendar value) {
        this.lessonTo = value;
    }

    /**
     * Gets the value of the lessonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLessonName() {
        return lessonName;
    }

    /**
     * Sets the value of the lessonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLessonName(String value) {
        this.lessonName = value;
    }

    /**
     * Gets the value of the lessonTeacher property.
     * 
     * @return
     *     possible object is
     *     {@link FullTeacherInfo }
     *     
     */
    public FullTeacherInfo getLessonTeacher() {
        return lessonTeacher;
    }

    /**
     * Sets the value of the lessonTeacher property.
     * 
     * @param value
     *     allowed object is
     *     {@link FullTeacherInfo }
     *     
     */
    public void setLessonTeacher(FullTeacherInfo value) {
        this.lessonTeacher = value;
    }

    /**
     * Gets the value of the lessonMark property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLessonMark() {
        return lessonMark;
    }

    /**
     * Sets the value of the lessonMark property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLessonMark(BigInteger value) {
        this.lessonMark = value;
    }

    /**
     * Gets the value of the lessonId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLessonId() {
        return lessonId;
    }

    /**
     * Sets the value of the lessonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLessonId(BigInteger value) {
        this.lessonId = value;
    }

}
