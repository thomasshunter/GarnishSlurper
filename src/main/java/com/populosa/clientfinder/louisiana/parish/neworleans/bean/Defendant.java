package com.populosa.clientfinder.louisiana.parish.neworleans.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Defendant implements Serializable
{
    private String caseNumber;
    
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    
    private byte[] screepcap;
    private String originalWholeName            = "";

    private List<String> nameAddressFromWebSearch   = new ArrayList<String>();
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        
        out.append( "\n---------------------------" );
        out.append( "\n       Defendant           " );
        out.append( "\n---------------------------" );
        out.append( "\n caseNumber               =" + this.caseNumber );
        out.append( "\n---------------------------" );
        out.append( "\n name                     =" + this.name );
        out.append( "\n address1                 =" + this.address1 );
        out.append( "\n address2                 =" + this.address2 );
        out.append( "\n city                     =" + this.city );
        out.append( "\n state                    =" + this.state );
        out.append( "\n zip                      =" + this.zip );
        out.append( "\n---------------------------" );
        out.append( "\n originalWholeName        =" + this.originalWholeName );
        out.append( "\n nameAddressFromWebSearch =" + java.util.Arrays.toString( this.nameAddressFromWebSearch.toArray( new String[]{} ) ) );
        out.append( "\n---------------------------" );
        
        return out.toString();
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress1()
    {
        return address1;
    }

    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getAddress2()
    {
        return address2;
    }

    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public String getOriginalWholeName()
    {
        return originalWholeName;
    }

    public void setOriginalWholeName(String originalWholeName)
    {
        this.originalWholeName = originalWholeName;
    }

    public byte[] getScreepcap()
    {
        return screepcap;
    }

    public void setScreepcap(byte[] screepcap)
    {
        this.screepcap = screepcap;
    }

    public List<String> getNameAddressFromWebSearch()
    {
        return nameAddressFromWebSearch;
    }

    public void addNameAddressFromWebSearch( String nameAddressFromWebSearch )
    {
        this.nameAddressFromWebSearch.add( nameAddressFromWebSearch );
    }
}
